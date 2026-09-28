import app.ConsoleApplication;
import app.UniversitySystem;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import queue.ServiceQueue;
import queue.ServiceRequest;
import stack.Action;
import stack.ActionStack;
import student.Student;
import student.StudentLinkedList;

/** Dependency-free regression tests: custom structures are tested through their APIs. */
public final class TestRunner {
    @FunctionalInterface
    private interface Test { void run() throws Exception; }

    private static int assertions;
    private static int suites;

    private TestRunner() { }

    public static void main(String[] args) throws Exception {
        run("Student validation and normalization", TestRunner::validation);
        run("Singly linked list mutations", TestRunner::linkedList);
        run("FIFO queue and LIFO stack", TestRunner::queueAndStack);
        run("Student, queue and stack integration", TestRunner::integration);
        run("Four requested sample students", TestRunner::demoData);
        run("Available menu operations and pending features", TestRunner::consoleWorkflow);
        run("Empty structures and interrupted input", TestRunner::emptyConsole);
        System.out.println("PASS: " + suites + " suites, " + assertions + " assertions.");
    }

    private static void run(String name, Test test) throws Exception {
        test.run();
        suites++;
        System.out.println("PASS: " + name);
    }

    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) { throw new AssertionError(message); }
    }

    private static void rejects(Runnable action) {
        boolean rejected = false;
        try { action.run(); }
        catch (IllegalArgumentException expected) { rejected = true; }
        check(rejected, "Expected validation rejection");
    }

    private static Student student(String id) { return student(id, "Student " + id, 3.0); }

    private static Student student(String id, String name, double gpa) {
        return new Student(id, name, 21, "Unspecified", "IT", "student@example.com", "+94 77 123 4567", "Colombo", gpa);
    }

    private static void ids(Student[] actual, String... expected) {
        check(actual.length == expected.length, "Student count mismatch");
        for (int i = 0; i < expected.length; i++) {
            check(actual[i].getStudentId().equals(expected[i]), "Unexpected student at index " + i);
        }
    }

    private static void validation() {
        check(student(" 23da2-0001 ").getStudentId().equals("23DA2-0001"), "Normalize ID");
        check(student("A", "Zero", 0).getGpa() == 0, "Minimum GPA");
        check(student("B", "Four", 4).getGpa() == 4, "Maximum GPA");
        rejects(() -> student(" "));
        rejects(() -> student("A B"));
        rejects(() -> student("A", " ", 3));
        rejects(() -> student("A", "Name", -0.1));
        rejects(() -> student("A", "Name", 4.1));
        rejects(() -> student("A", "Name", Double.NaN));
        rejects(() -> student("A", "Name", Double.POSITIVE_INFINITY));
        rejects(() -> Student.validateAge(0));
        rejects(() -> Student.validateAge(121));
        rejects(() -> Student.validateEmail("bad@email"));
        rejects(() -> Student.validateContact("1234"));
        rejects(() -> Student.validateContact("abcdefghi"));
        rejects(() -> Student.requireText(null, "Field"));
    }

    private static void linkedList() {
        StudentLinkedList list = new StudentLinkedList();
        check(list.isEmpty() && list.delete("A") == null, "Empty list");
        Student a = student("A");
        Student b = student("B");
        Student c = student("C");
        check(list.add(a) && list.add(b) && list.add(c), "Append records");
        check(!list.add(student("a")) && list.size() == 3, "Duplicate rejected");
        ids(list.toArray(), "A", "B", "C");
        Student replacement = student("B", "Updated", 4);
        check(list.update(replacement) && list.search("b") == replacement, "Update middle");
        check(!list.update(student("Z")), "Update missing");
        check(list.delete("B") == replacement, "Delete middle");
        check(list.delete("C") == c, "Delete tail");
        check(list.add(c), "Append after tail removal");
        check(list.delete("A") == a && list.delete("C") == c && list.isEmpty(), "Delete head and sole record");
        check(list.add(b) && list.search("B") == b && list.size() == 1, "Reuse empty list");
        Student[] copy = list.toArray();
        copy[0] = null;
        check(list.search("B") == b, "Snapshots do not expose nodes");
    }

    private static void queueAndStack() {
        ServiceQueue queue = new ServiceQueue();
        check(queue.dequeue() == null && queue.peek() == null, "Empty queue");
        ServiceRequest first = new ServiceRequest("R1", "A", "Transcript", "Printed copy");
        ServiceRequest second = new ServiceRequest("R2", "B", "Letter", "Enrollment letter");
        queue.enqueue(first);
        queue.enqueue(second);
        check(queue.peek() == first && queue.size() == 2, "Peek preserves queue");
        check(queue.toArray()[1] == second && queue.hasStudent("b"), "Queue traversal");
        check(queue.dequeue() == first && queue.dequeue() == second && queue.isEmpty(), "FIFO");
        queue.enqueue(first);
        check(queue.dequeue() == first && queue.isEmpty(), "Rear resets on empty");
        rejects(() -> queue.enqueue(null));
        ActionStack stack = new ActionStack();
        check(stack.pop() == null && stack.peek() == null, "Empty stack");
        Action added = new Action("Added student A");
        Action updated = new Action("Updated student A");
        stack.push(added);
        stack.push(updated);
        check(stack.peek() == updated && stack.size() == 2, "Peek preserves stack");
        check(stack.toArray()[0] == updated && stack.toArray()[1] == added, "History newest first");
        check(stack.pop() == updated && stack.pop() == added && stack.isEmpty(), "LIFO");
        rejects(() -> stack.push(null));
    }

    private static void integration() {
        UniversitySystem system = new UniversitySystem();
        Student original = student("A");
        system.addStudent(original);
        rejects(() -> system.addStudent(student("a")));
        Student updated = student("A", "Updated", 3.8);
        system.updateStudent(updated);
        check(system.allStudents().length == 1 && system.findStudent("a") == updated,
                "Linked list receives replacement record");
        check(system.searchStudent("A") == updated, "Linked list search finds student");
        check(system.searchStudent("Z") == null, "Missing search returns null");
        check(system.peekAction().getDescription().contains("Linked list search"), "Search action logged");
        ServiceRequest first = system.addRequest("A", "Transcript", "Printed copy");
        ServiceRequest second = system.addRequest("A", "Letter", "Enrollment letter");
        check(!first.getRequestId().equals(second.getRequestId()), "Unique request IDs");
        rejects(() -> system.addRequest("Z", "Letter", "Copy"));
        rejects(() -> system.deleteStudent("A"));
        check(system.findStudent("A") == updated, "Pending requests preserve student record");
        check(system.processNextRequest() == first && system.processNextRequest() == second, "Integrated FIFO");
        check(system.processNextRequest() == null, "Empty request processing");
        check(system.deleteStudent("A") == updated && system.allStudents().length == 0, "Delete student");
        rejects(() -> system.updateStudent(original));
        rejects(() -> system.deleteStudent("A"));
        check(system.popAction().getDescription().equals("Deleted student A"), "Deletion logged");
        check(system.findStudent("A") == null, "Popping history does not undo data");
    }

    private static void demoData() {
        UniversitySystem system = new UniversitySystem();
        system.loadDemoData();
        Student[] students = system.allStudents();
        ids(students, "0001", "0002", "0003", "0004");
        String[] names = {"Abdullah", "Asra", "Dilsath", "Nifra"};
        String[] programmes = {"BAIT", "IT", "Computer Science", "Computer Science"};
        double[] gpas = {3.75, 3.50, 3.90, 3.60};
        for (int i = 0; i < students.length; i++) {
            check(students[i].getName().equals(names[i])
                    && students[i].getDegreeProgramme().equals(programmes[i])
                    && students[i].getGpa() == gpas[i], "Requested sample " + names[i]);
        }
        check(system.waitingRequests().length == 0, "Demo starts with no requests");
        rejects(system::loadDemoData);
    }

    private static String runConsole(UniversitySystem system, String script) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            new ConsoleApplication(system, new BufferedReader(new StringReader(script)), out).run();
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    private static void consoleWorkflow() throws Exception {
        UniversitySystem system = new UniversitySystem();
        String script = String.join("\n",
                "bad", "0", "17", "1", "S1", "", "Test Student", "bad", "0", "21", "Unspecified", "IT",
                "bad-email", "test@example.com", "abc", "0771234567", "Colombo", "NaN", "5", "3.6",
                "1", "s1", "2", "S1", "Updated Student", "", "", "", "", "", "", "3.9",
                "4", "1", "s1", "1", "MISSING", "0",
                "5", "2", "1", "MISSING", "1", "S1", "Transcript", "Printed copy", "2", "0",
                "3", "S1", "6", "6", "7", "1", "2", "3", "0",
                "8", "9", "10", "11", "12", "13", "14", "15", "3", "S1", "16", "");
        String output = runConsole(system, script);
        String[] expected = {"Please enter a valid number.", "Choose a number from 1 to 16.",
                "Student name cannot be empty.", "Age must be between", "Enter a valid email",
                "Contact number may contain", "GPA must be a finite number", "Student added to linked list.",
                "Student ID already exists", "Student updated in linked list.", "Updated Student",
                "Search Student ID using Linked List", "Student record not found.", "Added to queue: REQ-1",
                "Process this student's waiting", "Processed: REQ-1", "No waiting service requests.",
                "Latest:", "Popped:", "Tree and hash searching features are not available yet.",
                "Campus route features are not available yet.", "Deleted student S1 from linked list.", "Goodbye!"};
        for (String value : expected) { check(output.contains(value), "Missing output: " + value); }
        check(system.allStudents().length == 0 && system.waitingRequests().length == 0, "Final console state");
        UniversitySystem demo = new UniversitySystem();
        demo.loadDemoData();
        int historySize = demo.recentActions().length;
        runConsole(demo, "8\n9\n10\n11\n12\n13\n14\n15\n16\n");
        check(demo.allStudents().length == 4 && demo.recentActions().length == historySize,
                "Pending menu choices do not modify data or history");
    }

    private static void emptyConsole() throws Exception {
        String output = runConsole(new UniversitySystem(), "4\n1\nMISSING\n0\n5\n2\n0\n6\n7\n1\n2\n0\n16\n");
        check(output.contains("No student records.") && output.contains("No waiting service requests.")
                && output.contains("Student record not found."), "Empty records and queue handled");
        String historyOutput = runConsole(new UniversitySystem(), "7\n1\n2\n0\n16\n");
        check(historyOutput.contains("No recent actions."), "Empty stack handled");
        UniversitySystem system = new UniversitySystem();
        boolean closed = false;
        try { runConsole(system, "1\nS1\nName\n"); }
        catch (EOFException expected) { closed = true; }
        check(closed && system.allStudents().length == 0, "EOF cannot partially add a student");
        system.addStudent(student("S1"));
        closed = false;
        try { runConsole(system, "2\nS1\nChanged\n"); }
        catch (EOFException expected) { closed = true; }
        check(closed && system.findStudent("S1").getName().equals("Student S1"), "EOF cannot partially update");
    }
}
