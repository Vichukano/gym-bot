package ru.vichukano.gym.bot.dao.google.disk;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.vichukano.gym.bot.UserDao;
import ru.vichukano.gym.bot.model.Exercise;
import ru.vichukano.gym.bot.model.SavedUser;
import ru.vichukano.gym.bot.model.Training;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class UserGoogleDiskDao implements UserDao {
    private static final String FILE_TYPE = ".xlsx";
    private static final String NEW = "new";
    private final Drive driveService;
    private final String path;

    @SneakyThrows
    @Override
    public void saveOrUpdate(SavedUser user) {
        log.trace("Start to save for: {}", user);
        Optional<File> file = getByFileName(user.getId() + user.getName());
        XSSFWorkbook workbook;
        if (file.isPresent()) {
            workbook = (XSSFWorkbook) WorkbookFactory.create(file.get());
        } else {
            workbook = new XSSFWorkbook();
        }
        Sheet sheet = workbook.createSheet("Training " + (workbook.getNumberOfSheets() + 1) + " " + lastTrainDate(user.getTrainings()));
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 6000);
        Row header = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 14);
        font.setBold(true);
        headerStyle.setFont(font);
        Cell exerciseCell = header.createCell(0);
        exerciseCell.setCellValue("Exercise");
        exerciseCell.setCellStyle(headerStyle);
        Cell weightCell = header.createCell(1);
        weightCell.setCellValue("Weight");
        weightCell.setCellStyle(headerStyle);
        Cell repsCell = header.createCell(2);
        repsCell.setCellValue("Reps");
        repsCell.setCellStyle(headerStyle);
        List<Exercise> exercises = fromLastTraining(user.getTrainings());
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
        String fileName = path + user.getId() + user.getName() + FILE_TYPE;
        try (var out = new FileOutputStream(fileName)) {
            workbook.write(out);
        } catch (IOException e) {
            log.error("Exception while saving for: {}", user, e);
        }
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(user.getId() + user.getName() + FILE_TYPE);
        fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");
        FileContent mediaContent = new FileContent("text/xlsx", new File(fileName));
        com.google.api.services.drive.model.File toDrive = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
        log.trace("Finish saving for: {}", user);
        Files.deleteIfExists(Paths.get(fileName));
        log.debug("Successfully execute file to Google Drive: {}", toDrive);
    }

    @SneakyThrows
    @Override
    public Optional<File> getByFileName(String fileName) {
        log.info("Start to search by: {}", fileName + FILE_TYPE);
        FileList fileList = driveService.files().list().setQ("name='" + fileName + FILE_TYPE + "'").execute();
        log.info("Found files: {}", fileList);
        if (fileList.getFiles().isEmpty()) {
            log.warn("Can't find file by name: {}", fileName);
            return Optional.empty();
        }
        com.google.api.services.drive.model.File file = fileList.getFiles().get(0);
        try (var out = new FileOutputStream(path + fileName + FILE_TYPE)) {
            driveService.files().export(file.getId(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").executeMediaAndDownloadTo(out);
        } catch (IOException e) {
            log.error("Exception while download and saving file with name: {}", fileName, e);
            return Optional.empty();
        }
        log.info("Successfully download file with name: {}", file + fileName + FILE_TYPE);
        return Optional.of(new File(path + fileName + FILE_TYPE));
    }

    private String lastTrainDate(Collection<Training> trainings) {
        return Stream.ofNullable(trainings)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(Training::getTime)
                .filter(Objects::nonNull)
                .map(LocalDateTime::toLocalDate)
                .filter(Objects::nonNull)
                .map(Objects::toString)
                .reduce((first, second) -> second)
                .orElse(LocalDate.now().toString());
    }

    private List<Exercise> fromLastTraining(Collection<Training> trainings) {
        return Stream.ofNullable(trainings)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .reduce((first, second) -> second)
                .map(Training::getExercises)
                .orElse(Collections.emptyList());
    }

    private void makeBold(Cell cell, final Workbook workbook) {
        CellStyle bold = workbook.createCellStyle();
        bold.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        bold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        bold.setFont(font);
        cell.setCellStyle(bold);
    }
}
