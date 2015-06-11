package ru.isa.ai.tests.python;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 09.06.2015
 * Time: 18:44
 */
public class PythonTest {
    public static void main(String[] args) throws FileNotFoundException, ScriptException {
        listEngines();

        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine pyEngine = mgr.getEngineByName("python");

        try {
            pyEngine.eval("print \"Python - Hello, world!\"");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        final PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("print \"Python - Hello, world!\"");

        PyObject result = interpreter.eval("2 + 3");
        System.out.println(result.toString());
    }

    public static void listEngines() {
        ScriptEngineManager mgr = new ScriptEngineManager();
        List<ScriptEngineFactory> factories =
                mgr.getEngineFactories();
        for (ScriptEngineFactory factory : factories) {
            System.out.println("ScriptEngineFactory Info");
            String engName = factory.getEngineName();
            String engVersion = factory.getEngineVersion();
            String langName = factory.getLanguageName();
            String langVersion = factory.getLanguageVersion();
            System.out.printf("\tScript Engine: %s (%s)\n",
                    engName, engVersion);
            List<String> engNames = factory.getNames();
            for (String name : engNames) {
                System.out.printf("\tEngine Alias: %s\n", name);
            }
            System.out.printf("\tLanguage: %s (%s)\n",
                    langName, langVersion);
        }
    }
}
