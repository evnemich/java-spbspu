package ru.spbstu.java.lab1.pipelines;

import java.util.function.BiFunction;

/**
 * Simple pipeline operation
 */
public class ToArray {
    public Double[] apply(Double a, Double b, Double c, Double d, Double e) {
        return new Double[]{a, b, c, d, e};
    }
}
