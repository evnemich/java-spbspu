package ru.spbstu.java.lab1;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


public class Lab1 {
    /**
    * Raw file with arithmetic expressions
    */
    public static String input;
    /**
     * Output file with results of expressions
     */
    public static String output;
    /**
     * Configuration file with classname of expression parser
     */
    public static String config;


    public static void main(String[] args) {
        try {
            parseCLArguments(args);
            validateCLArguments();
            Function<String, Double> parser = getParser(getParserName());
            Files.write(Paths.get(output), getResult(parser).getBytes());
        } catch (InvalidArgumentException | IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            System.out.println("Wrong input arguments");
            e.printStackTrace();
        }
    }

    /**
     * Evaluates all expressions of {@code input}
     *
     * @param parser parser class instance
     * @return String of result
     * @throws IOException if an I/O error occurs
     */
    private static String getResult(Function<String, Double> parser) throws IOException {
        List<String> strings = Files.readAllLines(Paths.get(input));
        StringBuilder outputData = new StringBuilder();
        for (String s : strings) {
            outputData.append(parser.apply(s));
            outputData.append("\n");
        }
        return outputData.toString();
    }

    /**
     * Opens {@code config} file and gets parser class name from it
     *
     * @return parser class name
     * @throws IOException              If an I/O error occurs
     * @throws InvalidArgumentException If config file has more than 1 line
     */
    private static String getParserName() throws IOException, InvalidArgumentException {
        List<String> lines = Files.readAllLines(Paths.get(config));
        if (lines.size() > 1)
            throw new InvalidArgumentException(new String[]{"Wrong config file"});
        return lines.get(0);
    }

    /**
     * Loads parser class
     *
     * @param parserName full name of parser class
     * @return instance of parser class
     * @throws ClassNotFoundException If there is no class with such name
     * @throws InstantiationException If unable to create instance
     * @throws IllegalAccessException If class is java.lang.Class
     */
    private static Function<String, Double> getParser(String parserName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class aClass = Class.forName(parserName);
        if (!Arrays.asList(aClass.getInterfaces()).contains(Function.class))
            throw new ClassNotFoundException();
        return ((Class<? extends Function<String, Double>>) aClass).newInstance();
    }

    /**
     * Let keys be
     * -i input file
     * -o output file
     * -c config file
     *
     * @param args
     */
    private static void parseCLArguments(String[] args) {
        for (String arg : args) {
            switch (arg.substring(0, 2)) {
                case "-i":
                    input = arg.substring(3);
                    break;
                case "-o":
                    output = arg.substring(3);
                    break;
                case "-c":
                    config = arg.substring(3);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Validating CLArguments
     * {@code input} and {@code config} must exist
     * {@code output} mustn't exist
     *
     * @throws InvalidArgumentException If any of arguments is wrong
     */
    private static void validateCLArguments() throws InvalidArgumentException {
        if (input == null || output == null || config == null) {
            throw new InvalidArgumentException(new String[]{"Wrong arguments"});
        }
        boolean result = Files.exists(Paths.get(input));
        result = result && Files.exists(Paths.get(config));
        result = result && !Files.exists(Paths.get(output));

        if (!result) {
            throw new InvalidArgumentException(new String[]{"Wrong arguments"});
        }
    }
}
