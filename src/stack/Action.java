package stack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import student.Student;

public final class Action {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String description;
    private final LocalDateTime timestamp;

    public Action(String description) {
        this.description = Student.requireText(description, "Action description");
        this.timestamp = LocalDateTime.now();
    }

    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp.format(FORMAT) + "] " + description;
    }
}
