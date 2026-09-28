package app;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import student.Student;

/** Line-based input avoids Scanner's mixed-token/newline pitfalls. */
public final class ConsoleInput {
    @FunctionalInterface
    private interface Parser<T> { T parse(String value); }

    private final BufferedReader reader;
    private final PrintStream out;

    public ConsoleInput(BufferedReader reader, PrintStream out) {
        this.reader = reader;
        this.out = out;
    }

    private String readLine(String prompt) throws IOException {
        out.print(prompt);
        out.flush();
        String line = reader.readLine();
        if (line == null) { throw new EOFException("Input closed"); }
        return line.trim();
    }

    private <T> T readValue(String label, T existing, Parser<T> parser) throws IOException {
        while (true) {
            String value = readLine(label + (existing == null ? "" : " [" + existing + "]") + ": ");
            if (value.isEmpty() && existing != null) { return existing; }
            try {
                return parser.parse(value);
            } catch (NumberFormatException exception) {
                out.println("Please enter a valid number.");
            } catch (IllegalArgumentException exception) {
                out.println(exception.getMessage());
            }
        }
    }

    public String text(String label) throws IOException { return text(label, null); }
    public String text(String label, String existing) throws IOException {
        return readValue(label, existing, value -> Student.requireText(value, label));
    }

    public String studentId() throws IOException {
        return readValue("Student ID", null, Student::normalizeId);
    }

    public int choice(String label, int minimum, int maximum) throws IOException {
        return readValue(label, null, value -> {
            int parsed = Integer.parseInt(value);
            if (parsed < minimum || parsed > maximum) {
                throw new IllegalArgumentException("Choose a number from " + minimum + " to " + maximum + ".");
            }
            return parsed;
        });
    }

    public int age(Integer existing) throws IOException {
        return readValue("Age", existing, value -> Student.validateAge(Integer.parseInt(value)));
    }

    public double gpa(Double existing) throws IOException {
        return readValue("GPA (0.00-4.00)", existing, value -> Student.validateGpa(Double.parseDouble(value)));
    }

    public String email(String existing) throws IOException {
        return readValue("Email", existing, Student::validateEmail);
    }

    public String contact(String existing) throws IOException {
        return readValue("Contact number", existing, Student::validateContact);
    }

}
