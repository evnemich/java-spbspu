package ru.spbstu.java.lab1.pipelines;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.function.Function;

/**
 * Allowed operations: -+/*()
 */
public class Counter implements Function<String, Double> {
    @Override
    public Double apply(String s) {
        Double result = null;
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        try {
            result = Double.valueOf(engine.eval(s).toString());
//            System.out.println(s + " = " + result);
        } catch (ScriptException e) {
            System.out.println("Wrong Expression");
            e.printStackTrace();
        }
        return result;
    }
}
