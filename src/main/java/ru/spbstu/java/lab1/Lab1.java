package ru.spbstu.java.lab1;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;


public class Lab1 {
    public static void main(String[] args) {
        removeOutput();
        try {
            CLArguments.parseCLArguments(args);
            List<String> input = Files.readAllLines(CLArguments.getPath("input"));
            try {
                Path config = CLArguments.getPath("config");
                List output = Pipeline.parsePipeline(config).acceptAllStages(input);
                Files.write(CLArguments.getPath("output"), getResult(output).getBytes());
            } catch (NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (InvalidArgumentException | IOException | ClassNotFoundException |
                IllegalAccessException | InstantiationException | ClassCastException e) {
            System.out.println("Wrong input arguments");
            e.printStackTrace();
        }
    }

    /**
     * Needed just to
     */
    private static void removeOutput() {
        try {
            if (Files.exists(Paths.get("o.txt")))
                Files.delete(Paths.get("o.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Evaluates all expressions of {@code input}
     * <p>
     * ///* @param parser parser class instance
     *
     * @return String of result
     */
    private static String getResult(List data) {
        StringBuilder outputData = new StringBuilder();
        for (Object line : data) {
            outputData.append(line);
            outputData.append("\n");
        }
        return outputData.toString();
    }
}
