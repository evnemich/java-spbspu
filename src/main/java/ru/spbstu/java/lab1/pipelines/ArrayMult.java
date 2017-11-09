package ru.spbstu.java.lab1.pipelines;

import java.util.function.Function;

/**
 * Simple pipeline operation
 */
public class ArrayMult implements Function<Double[], Double> {
    @Override
    public Double apply(Double[] x) {
        double result = 1;
        for (Double y : x) {
            result *= y;
        }
        return result;
    }
}
