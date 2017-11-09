package ru.spbstu.java.lab1;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    /**
     *
     * @param   filePath  path of config file
     * @param   pipeline  pipeline to fill it with readed data
     * @throws IOException                  If an I/O error occurs
     * @throws InvalidArgumentException     If config file is corrupted
     */
    static void parseConfig(Path filePath, Pipeline pipeline) throws IOException, InvalidArgumentException {
        Pipeline.Stage e = new Pipeline.Stage();
        for (String line : Files.readAllLines(filePath)) {
            String[] split = line.split("=");
            if (split.length <= 1)
                continue;
            switch (split[0]){
                case "class":
                    if (e.getClassName() != null) {
                        if (e.getMethodName() == null)
                            throw new InvalidArgumentException(new String[]{"method not set"});
                        pipeline.add(e);
                        e = new Pipeline.Stage();
                    }
                    e.setClassName(split[1]);
                    break;
                case "method":
                    e.setMethodName(split[1]);
                    break;
                case "argType":
                    e.addParamType(split[1]);
                    break;
                case "arg":
                    try {
                        e.addParam(Double.valueOf(split[1]));
                    } catch (NumberFormatException ex) {
                        e.addParam(split[1]);
                    }
                    break;
            }
        }
        pipeline.add(e);
    }
}
