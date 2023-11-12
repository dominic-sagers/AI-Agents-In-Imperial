package Group12.Imperial.gamelogic.rl;

import java.io.File;
import java.nio.file.FileSystems;

import org.python.util.PythonInterpreter;


public class JavaToPython{
    public static void main(String[] args) {
        PythonInterpreter interpreter = new PythonInterpreter();

        int[] inputArray = new int[] {1, 2, 3, 4, 5};

        File file = new File("Imperial//src//main//java//Group12//Imperial//gamelogic//rl//DQNModel.py");
        String completePath = file.getAbsolutePath();

        interpreter.set("inputArray", inputArray);

        interpreter.execfile(completePath);

        int x = (interpreter.get("x")).asInt();

        System.out.println(x);
        
    }
}