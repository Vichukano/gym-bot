package ru.vichukano.gym.bot;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.vichukano.gym.bot.model.Exercise;
import ru.vichukano.gym.bot.model.SavedUser;
import ru.vichukano.gym.bot.model.Training;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
public class UserExcelDao implements UserDao {
    private static final int WIDTH = 6000;
    private static final short FONT_SIZE_12 = 12;
    private static final short FONT_SIZE_14 = 14;
    private static final String FILE_TYPE = ".xlsx";
    private static final String NEW = "new";
    private final String path;

    @SneakyThrows
    @Override
    public void saveOrUpdate(SavedUser user) {
        log.trace("Start to save for: {}", user);
        Optional<File> file = getByFileName(user.getId() + user.getName());
        Workbook workbook = file.map(f -> {
            try {
                return WorkbookFactory.create(f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).orElse(new XSSFWorkbook());
        Sheet sheet = sheetForUser(workbook, user);
        List<Exercise> exercises = fromLastTraining(user.getTrainings());
        fillRows(sheet, exercises, workbook);
        String fileName = path + user.getId() + user.getName() + FILE_TYPE;
        try (var out = new FileOutputStream(fileName + NEW)) {
            workbook.write(out);
        } catch (IOException e) {
            log.error("Exception while saving for: {}", user, e);
        }
        Path p = Paths.get(fileName);
        Files.deleteIfExists(p);
        Files.move(Paths.get(fileName + NEW), p);
        log.trace("Finish saving for: {}", user);
    }

    @Override
    public Optional<File> getByFileName(String fileName) {
        var storeDir = new File(path);
        File[] files = storeDir.listFiles((dir, name) -> (fileName + FILE_TYPE).equals(name));
        if (Objects.isNull(files) || files.length == 0) {
            return Optional.empty();
        }
        return Optional.of(files[0]);
    }

    private Sheet sheetForUser(Workbook workbook, SavedUser user) {
        Sheet sheet = workbook.createSheet("Training " + (workbook.getNumberOfSheets() + 1) + " " + lastTrainDate(user.getTrainings()));
        sheet.setSelected(true);
        sheet.setColumnWidth(0, WIDTH);
        sheet.setColumnWidth(1, WIDTH);
        sheet.setColumnWidth(2, WIDTH);
        Row header = sheet.createRow(0);
        CellStyle headerStyle = headerStyle(workbook);
        Cell exerciseCell = header.createCell(0);
        exerciseCell.setCellValue("Exercise");
        exerciseCell.setCellStyle(headerStyle);
        Cell weightCell = header.createCell(1);
        weightCell.setCellValue("Weight");
        weightCell.setCellStyle(headerStyle);
        Cell repsCell = header.createCell(2);
        repsCell.setCellValue("Reps");
        repsCell.setCellStyle(headerStyle);
        workbook.setActiveSheet(workbook.getSheetIndex(sheet));
        workbook.setSelectedTab(workbook.getSheetIndex(sheet));
        return sheet;
    }

    private CellStyle headerStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints(FONT_SIZE_14);
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }

    private void fillRows(Sheet sheet, List<Exercise> exercises, Workbook workbook) {
        int position = 1;
        for (Exercise exercise : exercises) {
            List<BigDecimal> weights = exercise.getWeights();
            for (int j = 0; j < weights.size(); j++) {
                Row nextExerciseRow = sheet.createRow(position);
                Cell name = nextExerciseRow.createCell(0);
                name.setCellValue(exercise.getName());
                makeBold(name, workbook);
                Cell weight = nextExerciseRow.createCell(1);
                weight.setCellValue(weights.get(j).doubleValue());
                Cell reps = nextExerciseRow.createCell(2);
                reps.setCellValue(exercise.getReps().get(j));
                position++;
            }
        }
        Row nolRow = sheet.createRow(position);
        Cell nolName = nolRow.createCell(0);
        CellStyle headerStyle = headerStyle(workbook);
        nolName.setCellValue("NOL");
        nolName.setCellStyle(headerStyle);
        Cell nol = nolRow.createCell(1);
        final Integer repsSum = exercises.stream().map(Exercise::getReps).flatMap(Collection::stream).reduce(Integer::sum).orElse(0);
        nol.setCellValue(repsSum);
        Row tonsRow = sheet.createRow(++position);
        Cell tonsName = tonsRow.createCell(0);
        tonsName.setCellValue("Tonnage");
        tonsName.setCellStyle(headerStyle);
        final BigDecimal tonnage = exercises.stream().map(e -> {
            List<Integer> reps = e.getReps();
            List<BigDecimal> weights = e.getWeights();
            List<BigDecimal> tons = new ArrayList<>(weights.size());
            for (int i = 0; i < weights.size(); i++) {
                Integer rep = reps.get(i);
                BigDecimal weight = weights.get(i);
                final BigDecimal res;
                if (!BigDecimal.ZERO.equals(weight)) {
                    res = weight.multiply(BigDecimal.valueOf(rep));
                } else {
                    res = BigDecimal.valueOf(rep);
                }
                tons.add(res);
            }
            return tons;
        }).flatMap(Collection::stream).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        Cell tonnageCell = tonsRow.createCell(1);
        tonnageCell.setCellValue(tonnage.doubleValue());
    }

    private String lastTrainDate(Collection<Training> trainings) {
        return Stream.ofNullable(trainings).flatMap(Collection::stream).filter(Objects::nonNull).map(Training::getTime).filter(Objects::nonNull).map(LocalDateTime::toLocalDate).filter(Objects::nonNull).map(Objects::toString).reduce((first, second) -> second).orElse(LocalDate.now().toString());
    }

    private List<Exercise> fromLastTraining(Collection<Training> trainings) {
        return Stream.ofNullable(trainings).flatMap(Collection::stream).filter(Objects::nonNull).reduce((first, second) -> second).map(Training::getExercises).orElse(Collections.emptyList());
    }

    private void makeBold(Cell cell, final Workbook workbook) {
        CellStyle bold = workbook.createCellStyle();
        bold.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        bold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints(FONT_SIZE_12);
        font.setBold(true);
        bold.setFont(font);
        cell.setCellStyle(bold);
    }
}
