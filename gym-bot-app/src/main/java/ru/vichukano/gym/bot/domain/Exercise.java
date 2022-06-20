package ru.vichukano.gym.bot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum Exercise {
    BENCH_PRESS("/bench", "bench press"),
    SQUAT("/squat", "squat"),
    DEAD_LIFT("/dead_lift", "dead lift"),
    OVERHEAD_PRESS("/over_press", "overhead press"),
    DUMBBELLS_OVERHEAD_PRESS("/dum_over_press", "dumbbells overhead press"),
    DUMBBELLS_BENCH_PRESS("/dum_bench_press", "dumbbells bench press"),
    ABS("/abs", "exercises for abs"),
    PULL_UP("/pull_up", "pull ups"),
    PUSH_UP("/push_up", "push ups"),
    PUSH_UP_ON_BARS("/push_up_on_bars", "push ups on the uneven bars"),
    DUMBBELLS_BICEPS_CURL("/dumbbells_biceps_curl", "dumbbells biceps curls"),
    BARBELL_BECEPS_CURL("/barbell_biceps_curl", "burbell biceps curls"),
    HACK_SQUAT_MACHINE("/squat_machine", "hack squat machine"),
    LEG_PRESS_MACHINE("/leg_press_machine", "leq press machine");

    @Getter
    private final String command;
    @Getter
    private final String desc;

    public static String printAll() {
        return Arrays.stream(Exercise.values())
                .map(exe -> exe.getCommand() + " - " + exe.getDesc())
                .collect(Collectors.joining("\n"));
    }
}
