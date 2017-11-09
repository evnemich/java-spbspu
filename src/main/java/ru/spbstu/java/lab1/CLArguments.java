package ru.spbstu.java.lab1;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CLArguments {

    private static final Map<String, String> args = new TreeMap<>();

    /**
     * Identifying command line arguments in case of no description
     *
     * @param arguments command line arguments
     * @throws InvalidArgumentException If the arguments are wrong
     * @throws IOException              If an I/O error occurs
     */
    private static void identifyCLArguments(String[] arguments) throws InvalidArgumentException, IOException {
        if (arguments.length != 3)
            throw new InvalidArgumentException(new String[]{"Wrong arguments count"});
        for (String arg : arguments) {
            if (!Files.exists(Paths.get(arg))) {
                args.put("output", arg);
            } else {
                if (Files.lines(Paths.get(arg)).allMatch(x -> x.contains("=") || x.length() == 0)) {
                    args.put("config", arg);
                    continue;
                }
                args.put("input", arg);
            }
        }
        validateCLArguments();
    }

    /**
     * Let the keys be
     * -i input file
     * -o output file
     * -c config file
     *
     * @param arguments command line arguments
     * @throws IOException              If an I/O error occurs
     * @throws InvalidArgumentException If the arguments are wrong
     */
    static void parseCLArguments(String[] arguments) throws IOException, InvalidArgumentException {
        for (String arg : arguments) {
            switch (arg.substring(0, 2)) {
                case "-i":
                    args.put("input", arg.substring(3));
                    break;
                case "-o":
                    args.put("output", arg.substring(3));
                    break;
                case "-c":
                    args.put("config", arg.substring(3));
                    break;
                default:
                    break;
            }
        }
        try {
            validateCLArguments();
        } catch (InvalidArgumentException e) {
            identifyCLArguments(arguments);
        }
    }

    /**
     * Just provides API to work with internal map
     *
     * @param key key to get filename from the map
     * @return Path of this file
     */
    public static Path getPath(String key) {
        return Paths.get(args.get(key));
    }

    /**
     * Validating CLArguments
     * {@code input} and {@code config} must exist
     * {@code output} mustn't exist
     *
     * @throws InvalidArgumentException If any of arguments is wrong
     */
    private static void validateCLArguments() throws InvalidArgumentException, IOException {
        List<String> ia = new ArrayList<>();
        String[] params = {"input", "output", "config"};

        for (String param : params) {
            if (!args.containsKey(param))
                ia.add(param);
        }
        if (ia.size() > 0) {
            throw new InvalidArgumentException(ia.toArray(new String[]{}));
        }

        try {
            Path output = getPath("output");
            Files.createFile(output);
            for (String param : params) {
                if (Files.notExists(getPath(param))) {
                    ia.add(param);
                }
            }
            Files.delete(output);
        } catch (FileAlreadyExistsException e) {
            ia.add("output");
        }
        if (ia.size() > 0) {
            throw new InvalidArgumentException(ia.toArray(new String[]{}));
        }
    }
}
