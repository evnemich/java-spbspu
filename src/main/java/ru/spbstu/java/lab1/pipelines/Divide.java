package ru.spbstu.java.lab1.pipelines;

import java.util.function.BiFunction;

/**
 * Simple pipeline operation
 */
public class Divide implements BiFunction<Double, Double, Double> {
    @Override
    public Double apply(Double x, Double y) {
        return x / y;
    }
}
