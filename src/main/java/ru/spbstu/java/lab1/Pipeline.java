package ru.spbstu.java.lab1;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Pipeline {
    private List<Stage> pipeline;
    private AtomicInteger stage = new AtomicInteger();

    /**
     * Pipeline entry class with open API
     */
    public static class Stage {
        private String className;
        private String methodName;
        private List<String> paramTypes;
        private List<Object> params;
        private boolean prepared = false;
        private Object instance;
        private Method method;


        public List<Object> getParams() {
            return params;
        }

        public void addParam(Object param) {
            this.params.add(param);
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public List<String> getParamTypes() {
            return paramTypes;
        }

        public void addParamType(String param) {
            this.paramTypes.add(param);
        }

        public Stage() {
            this.paramTypes = new ArrayList<>();
            this.params = new ArrayList<>();
        }

        /**
         * Improve performance by invoking most of Reflection once
         */
        public void prepareStage() {
            if (prepared)
                return;
            try {
                Class<?> types[] = new Class<?>[paramTypes.size()];

                for (int i = 0; i < paramTypes.size(); i++) {
                    types[i] = Class.forName(paramTypes.get(i));
                }
                Class<?> aClass = Class.forName(className);
                if (Arrays.stream(aClass.getMethods()).noneMatch(x -> x.getName().equals(methodName))) {
                    throw new ClassNotFoundException();
                }
                instance = aClass.newInstance();
                method = aClass.getMethod(methodName, types);
                prepared = true;
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
                prepared = false;
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds stage to this pipeline
     *
     * @param stage Pipeline stage to add
     */
    public void add(Stage stage) {
        if (!pipeline.contains(stage))
            pipeline.add(stage);
    }

    /**
     * Private constructor to force using factory method
     */
    private Pipeline() {
        this.pipeline = new ArrayList<>();
    }

    /**
     * Pipeline factory method
     *
     * @param filePath path of config file
     * @return builded pipeline filled of data from (@code filePath)
     * @throws IOException              If an I/O error occurs
     * @throws InvalidArgumentException If config file is corrupted
     */
    public static Pipeline parsePipeline(Path filePath) throws IOException, InvalidArgumentException {
        Pipeline pipeline = new Pipeline();
        Config.parseConfig(filePath, pipeline);
        return pipeline;
    }

    /**
     * Run whole pipeline in single Thread
     *
     * @param arr list of input data
     * @return result List
     * @throws InvocationTargetException If config file is corrupted
     * @throws IllegalAccessException    If config file is corrupted
     */
    public List acceptAllStages(List arr) throws InvocationTargetException, IllegalAccessException {
        List<Object> result;
        for (stage.set(0); stage.get() < pipeline.size(); nextStage()) {
            result = new ArrayList<>();
            for (Object o : arr) {
                result.add(acceptStage(o));
            }
            arr = result;
            System.out.println("Pipeline stage " + (stage.get() + 1) + " finished \t" + getCurrentEntry().className + "\t" + getCurrentEntry().paramTypes);
        }
        return arr;
    }

    /**
     * Runs pipeline stage on single data object
     *
     * @param data single stage input
     * @return single stage resul
     * @throws IllegalAccessException    If config file is corrupted
     * @throws InvocationTargetException If config file is corrupted
     */
    private Object acceptStage(Object data) throws IllegalAccessException, InvocationTargetException {
        getCurrentEntry().prepareStage();
        Object o = getCurrentEntry().instance;
        Method method = getCurrentEntry().method;

        List<Object> params = new ArrayList<>();
        params.add(data);
        params.addAll(getCurrentEntry().getParams());

        return method.invoke(o, params.toArray());
    }

    /**
     * This method made just to make code cleaner
     *
     * @return current pipeline entry
     */
    private Stage getCurrentEntry() {
        return pipeline.get(stage.get());
    }

    /**
     * Incrementor
     */
    private void nextStage() {
        stage.addAndGet(1);
    }
}
