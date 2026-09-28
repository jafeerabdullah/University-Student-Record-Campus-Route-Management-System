package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;
import queue.ServiceRequest;
import stack.Action;
import student.Student;

/** Exact assignment menu, with additional operations grouped in submenus. */
public final class ConsoleApplication {
    private final UniversitySystem system;
    private final ConsoleInput input;
    private final PrintStream out;

    public ConsoleApplication(UniversitySystem system, BufferedReader reader, PrintStream out) {
        this.system = system;
        this.input = new ConsoleInput(reader, out);
        this.out = out;
    }

    public void run() throws IOException {
        out.println("Records are kept in memory for this session.");
        out.println("Options 8-15 will become available in a later update.");
        while (true) {
            showMenu();
            int choice = input.choice("Select an option", 1, 16);
            try {
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> updateStudent();
                    case 3 -> deleteStudent();
                    case 4 -> recordTools();
                    case 5 -> serviceRequests();
                    case 6 -> processRequest();
                    case 7 -> historyTools();
                    case 8, 9 -> out.println("Tree and hash searching features are not available yet.");
                    case 10, 11, 12, 13, 14, 15 -> out.println("Campus route features are not available yet.");
                    case 16 -> { out.println("Goodbye!"); return; }
                    default -> throw new IllegalStateException("Unexpected validated menu choice.");
                }
            } catch (IllegalArgumentException exception) {
                out.println("Unable to complete operation: " + exception.getMessage());
            }
        }
    }

    private void showMenu() {
        out.println();
        out.println("============================================================");
        out.println(" UNIVERSITY STUDENT RECORD AND CAMPUS ROUTE");
        out.println(" MANAGEMENT SYSTEM");
        out.println("============================================================");
        out.println("1. Add Student Record");
        out.println("2. Update Student Record");
        out.println("3. Delete Student Record");
        out.println("4. Display All Records using Linked List");
        out.println("5. Add Service Request to Queue");
        out.println("6. Process Next Service Request");
        out.println("7. Display Recent Actions using Stack");
        out.println("8. Display Students using BST/AVL");
        out.println("9. Search Student using Hashing");
        out.println("10. Add Campus Location");
        out.println("11. Remove Campus Location");
        out.println("12. Add Campus Connection/Road");
        out.println("13. Remove Campus Connection/Road");
        out.println("14. Display Campus Connections");
        out.println("15. Traverse Campus Locations using BFS or DFS");
        out.println("16. Exit");
    }

    private void addStudent() throws IOException {
        String id = input.studentId();
        if (system.findStudent(id) != null) {
            out.println("Student ID already exists: " + id);
            return;
        }
        system.addStudent(readStudent(id, null));
        out.println("Student added to linked list.");
    }

    private void updateStudent() throws IOException {
        String id = input.studentId();
        Student existing = system.findStudent(id);
        if (existing == null) { out.println("Student record not found."); return; }
        showStudent(existing);
        out.println("Press Enter to keep a current value. Student ID remains " + id + ".");
        system.updateStudent(readStudent(id, existing));
        out.println("Student updated in linked list.");
    }

    private Student readStudent(String id, Student old) throws IOException {
        String name = input.text("Student name", old == null ? null : old.getName());
        int age = input.age(old == null ? null : old.getAge());
        String gender = input.text("Gender", old == null ? null : old.getGender());
        String programme = input.text("Degree programme", old == null ? null : old.getDegreeProgramme());
        String email = input.email(old == null ? null : old.getEmail());
        String contact = input.contact(old == null ? null : old.getContactNumber());
        String address = input.text("Address", old == null ? null : old.getAddress());
        double gpa = input.gpa(old == null ? null : old.getGpa());
        return new Student(id, name, age, gender, programme, email, contact, address, gpa);
    }

    private void deleteStudent() throws IOException {
        Student removed = system.deleteStudent(input.studentId());
        out.println("Deleted student " + removed.getStudentId() + " from linked list.");
    }

    private void recordTools() throws IOException {
        showStudents(system.allStudents());
        while (true) {
            out.println("\nRecord tools");
            out.println("1. Search Student ID using Linked List");
            out.println("0. Return to Main Menu");
            int choice = input.choice("Select a record tool", 0, 1);
            if (choice == 0) { return; }
            showStudent(system.searchStudent(input.studentId()));
        }
    }

    private void showStudent(Student student) {
        if (student == null) { out.println("Student record not found."); return; }
        out.println("------------------------------------------------------------");
        out.println("Student ID       : " + student.getStudentId());
        out.println("Student Name     : " + student.getName());
        out.println("Age              : " + student.getAge());
        out.println("Gender           : " + student.getGender());
        out.println("Degree Programme : " + student.getDegreeProgramme());
        out.println("Email            : " + student.getEmail());
        out.println("Contact Number   : " + student.getContactNumber());
        out.println("Address          : " + student.getAddress());
        out.printf(Locale.ROOT, "GPA              : %.2f%n", student.getGpa());
    }

    private void showStudents(Student[] students) {
        out.println("\nStudent records in linked list insertion order");
        out.println("------------------------------------------------------------");
        if (students.length == 0) { out.println("No student records."); return; }
        for (Student student : students) { showStudent(student); }
        out.println("Total students: " + students.length);
    }

    private void serviceRequests() throws IOException {
        while (true) {
            out.println("\n1. Add Service Request");
            out.println("2. Display Waiting Requests");
            out.println("0. Return to Main Menu");
            int choice = input.choice("Select a queue operation", 0, 2);
            if (choice == 0) { return; }
            if (choice == 2) { showWaitingRequests(); continue; }
            String id = input.studentId();
            if (system.findStudent(id) == null) { out.println("Student record not found."); continue; }
            ServiceRequest request = system.addRequest(id, input.text("Request type"), input.text("Description"));
            out.println("Added to queue: " + request);
            showWaitingRequests();
        }
    }

    private void processRequest() {
        ServiceRequest request = system.processNextRequest();
        out.println(request == null ? "No waiting service requests." : "Processed: " + request);
    }

    private void showWaitingRequests() {
        ServiceRequest[] waiting = system.waitingRequests();
        out.println("Waiting requests (FIFO, next request first)");
        if (waiting.length == 0) { out.println("No waiting service requests."); }
        for (ServiceRequest request : waiting) { out.println(request); }
    }

    private void historyTools() throws IOException {
        showHistory();
        while (true) {
            out.println("\n1. Peek Latest Action");
            out.println("2. Pop Latest Action (removes history only; does not undo)");
            out.println("3. Display Recent Actions");
            out.println("0. Return to Main Menu");
            int choice = input.choice("Select a stack operation", 0, 3);
            if (choice == 0) { return; }
            if (choice == 3) { showHistory(); continue; }
            Action action = choice == 1 ? system.peekAction() : system.popAction();
            out.println(action == null ? "No recent actions." : (choice == 1 ? "Latest: " : "Popped: ") + action);
        }
    }

    private void showHistory() {
        Action[] actions = system.recentActions();
        out.println("Recent actions (newest first)");
        if (actions.length == 0) { out.println("No recent actions."); }
        for (Action action : actions) { out.println(action); }
    }

}
