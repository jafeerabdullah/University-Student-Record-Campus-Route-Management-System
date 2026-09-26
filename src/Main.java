import app.ConsoleApplication;
import app.UniversitySystem;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/** Launch with --demo for fictional sample records and campus roads. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        if (args.length > 1 || (args.length == 1 && !args[0].equals("--demo"))) {
            System.out.println("Usage: java -cp out Main [--demo]");
            return;
        }
        UniversitySystem system = new UniversitySystem();
        if (args.length == 1) {
            system.loadDemoData();
            System.out.println("Loaded 4 fictional students, 7 campus locations and 8 roads.");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        try {
            new ConsoleApplication(system, reader, System.out).run();
        } catch (EOFException exception) {
            System.out.println("\nInput closed. Goodbye!");
        } catch (IOException exception) {
            System.err.println("Unable to read console input: " + exception.getMessage());
        }
    }
}
