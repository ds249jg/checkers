import consoleui.ConsoleUI;
import core.Field;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Field field = new Field();
        ConsoleUI console = new ConsoleUI(field);
        console.play();
    }
}
