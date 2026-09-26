import app.ConsoleApplication;
import app.UniversitySystem;
import graph.CampusRouteGraph;
import hashing.StudentHashTable;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import queue.ServiceQueue;
import queue.ServiceRequest;
import searching.SearchAlgorithms;
import sorting.SortingAlgorithms;
import stack.Action;
import stack.ActionStack;
import student.Student;
import student.StudentLinkedList;
import tree.AVLTree;
import tree.StudentBST;

/** Dependency-free regression tests: custom structures are tested through their APIs. */
public final class TestRunner {
    @FunctionalInterface
    private interface Test { void run() throws Exception; }

    private static int assertions;
    private static int suites;
    private static int seed = 123456789;

    private TestRunner() { }

    public static void main(String[] args) throws Exception {
        run("Student validation and normalization", TestRunner::validation);
        run("Singly linked list mutations", TestRunner::linkedList);
        run("FIFO queue and LIFO stack", TestRunner::queueAndStack);
        run("Hash collisions, deletion and resizing", TestRunner::hashTable);
        run("BST deletion and inorder traversal", TestRunner::binaryTree);
        run("AVL rotations and randomized insert/delete", TestRunner::avlTree);
        run("Linear/binary search and merge sorting", TestRunner::searchAndSort);
        run("Graph traversal, cycles and vertex/road removal", TestRunner::graph);
        run("Coordinated records, requests and history", TestRunner::integration);
        run("Randomized index synchronization", TestRunner::randomizedIntegration);
        run("All 16 console options and invalid input", TestRunner::consoleWorkflow);
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

    private static String id(int number) { return "S" + String.format(java.util.Locale.ROOT, "%04d", number); }

    private static void ids(Student[] actual, String... expected) {
        check(actual.length == expected.length, "Student count mismatch");
        for (int i = 0; i < expected.length; i++) {
            check(actual[i].getStudentId().equals(expected[i]), "Unexpected student at index " + i);
        }
    }

    private static void names(String[] actual, String... expected) {
        check(actual.length == expected.length, "Traversal length mismatch");
        for (int i = 0; i < expected.length; i++) {
            check(actual[i].equals(expected[i]), "Unexpected traversal at index " + i + ": " + actual[i]);
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

    private static void hashTable() {
        StudentHashTable table = new StudentHashTable(17);
        // These IDs deliberately collide for the documented polynomial hash.
        for (String id : new String[] {"AA", "AR", "RA", "RR"}) { check(table.insert(student(id)), "Collision insertion"); }
        check(table.describeBuckets().contains("RR -> RA -> AR -> AA -> null"), "Separate chaining collision");
        check(!table.insert(student("aa")), "Reject duplicate ID");
        check(table.delete("AR") != null && table.search("RA") != null, "Delete chain middle");
        check(table.delete("RR") != null && table.search("RA") != null, "Delete chain head");
        check(table.delete("AA") != null && table.size() == 1, "Delete chain tail");
        Student replacement = student("RA", "Updated", 3.9);
        check(table.update(replacement) && table.search("ra") == replacement, "Hash update");
        check(table.delete("ZZ") == null, "Missing deletion");
        for (int i = 0; i < 300; i++) { table.insert(student(id(i))); }
        check(table.capacity() > 17 && table.size() == 301, "Resize preserves size");
        for (int i = 0; i < 300; i++) { check(table.search(id(i)) != null, "Rehash preserves entries"); }
        for (int i = 1; i < 300; i += 2) { check(table.delete(id(i)) != null, "Delete after rehash"); }
        for (int i = 0; i < 300; i++) { check((table.search(id(i)) != null) == (i % 2 == 0), "Correct remaining keys"); }
        rejects(() -> new StudentHashTable(0));
    }

    private static void binaryTree() {
        StudentBST tree = new StudentBST();
        check(tree.isEmpty() && tree.delete("A") == null, "Empty BST");
        for (String id : new String[] {"D", "B", "F", "A", "C", "E", "G"}) { tree.insert(student(id)); }
        ids(tree.inorder(), "A", "B", "C", "D", "E", "F", "G");
        check(!tree.insert(student("D")), "Duplicate tree key");
        check(tree.delete("D").getStudentId().equals("D"), "Delete two-child root");
        tree.delete("A");
        tree.delete("B");
        ids(tree.inorder(), "C", "E", "F", "G");
        Student replacement = student("F", "New Name", 2.5);
        check(tree.update(replacement) && tree.search("f") == replacement, "Tree update");
        check(tree.search("Z") == null && !tree.update(student("Z")), "Missing tree key");
    }

    private static int random(int bound) {
        seed ^= seed << 13;
        seed ^= seed >>> 17;
        seed ^= seed << 5;
        return (seed & Integer.MAX_VALUE) % bound;
    }

    private static void avlTree() {
        String[][] rotations = {{"C", "B", "A"}, {"A", "B", "C"}, {"C", "A", "B"}, {"A", "C", "B"}};
        for (String[] order : rotations) {
            AVLTree tree = new AVLTree();
            for (String id : order) { tree.insert(student(id)); }
            check(tree.height() == 2 && tree.isValidAvl(), "LL/RR/LR/RL rotation");
            ids(tree.inorder(), "A", "B", "C");
        }
        AVLTree tree = new AVLTree();
        int[] order = new int[400];
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
            tree.insert(student(id(i)));
            check(tree.isValidAvl() && tree.size() == i + 1, "Ascending insertion stays balanced");
        }
        check(tree.height() <= 12, "AVL logarithmic height");
        for (int i = order.length - 1; i > 0; i--) {
            int other = random(i + 1);
            int temporary = order[i]; order[i] = order[other]; order[other] = temporary;
        }
        for (int i = 0; i < order.length; i++) {
            check(tree.delete(id(order[i])) != null, "Randomized AVL deletion");
            check(tree.isValidAvl() && tree.size() == order.length - i - 1, "Deletion rebalances");
        }
        check(tree.height() == 0 && tree.inorder().length == 0, "AVL emptied");
        for (int value : order) {
            tree.insert(student(id(value)));
            check(tree.isValidAvl(), "Randomized insertion rebalances");
        }
        for (int i = 0; i < order.length; i++) {
            check(tree.search(id(i)) != null, "All AVL keys searchable");
        }
    }

    private static void searchAndSort() {
        Student[] input = {student("C", "Zara", 3.5), student("B", "ahmed", 4), student("A", "Ahmed", 3.5), student("D", "Ali", 2.0)};
        ids(SortingAlgorithms.sortById(input), "A", "B", "C", "D");
        ids(SortingAlgorithms.sortByName(input), "A", "B", "D", "C");
        ids(SortingAlgorithms.sortByGpa(input), "B", "A", "C", "D");
        ids(input, "C", "B", "A", "D");
        check(SearchAlgorithms.linearSearchById(input, " a ") == input[2], "Linear ID search");
        check(SearchAlgorithms.binarySearchById(input, "d") == input[3], "Binary search sorts its own copy");
        ids(SearchAlgorithms.linearSearchByName(input, " AHMED "), "B", "A");
        ids(SearchAlgorithms.binarySearchByName(input, "AHMED"), "A", "B");
        check(SearchAlgorithms.binarySearchById(input, "Z") == null, "Binary ID miss");
        check(SearchAlgorithms.linearSearchById(input, "Z") == null, "Linear ID miss");
        ids(SearchAlgorithms.binarySearchByName(input, "Nobody"));
        ids(SearchAlgorithms.linearSearchByName(input, "Nobody"));
        ids(SortingAlgorithms.sortByGpa(new Student[0]));
        check(SearchAlgorithms.binarySearchById(new Student[0], "A") == null, "Empty binary search");
        ids(SearchAlgorithms.binarySearchByName(new Student[0], "Ahmed"));
        rejects(() -> SortingAlgorithms.sortByName(new Student[] {null}));
    }

    private static void graph() {
        CampusRouteGraph graph = new CampusRouteGraph();
        check(graph.describeConnections().contains("No campus locations"), "Empty graph");
        rejects(() -> graph.bfs("A"));
        for (String name : new String[] {"A", "B", "C", "D", "E", "F"}) { graph.addLocation(name); }
        check(!graph.addLocation(" a "), "Case-insensitive vertex uniqueness");
        graph.addRoad("A", "B", 10);
        graph.addRoad("A", "C", 20);
        graph.addRoad("B", "D", 30);
        graph.addRoad("C", "D", 40);
        graph.addRoad("D", "E", 50);
        check(!graph.addRoad("B", "A", 11), "Duplicate undirected road");
        check(graph.distanceBetween("b", "a") == 10, "Symmetric road weight");
        names(graph.bfs("a"), "A", "B", "C", "D", "E");
        names(graph.dfs("A"), "A", "B", "D", "C", "E");
        names(graph.bfs("F"), "F");
        names(graph.dfs("F"), "F");
        rejects(() -> graph.addRoad("A", "A", 10));
        rejects(() -> graph.addRoad("A", "Z", 10));
        rejects(() -> graph.addRoad("A", "F", 0));
        rejects(() -> graph.addRoad("A", "F", Double.NaN));
        rejects(() -> graph.addRoad("A", "F", Double.POSITIVE_INFINITY));
        check(graph.roadCount() == 5, "Invalid changes leave graph intact");
        check(graph.removeLocation("B") && graph.roadCount() == 3 && graph.size() == 5, "Remove vertex and incident roads");
        check(!graph.describeConnections().contains("B"), "No dangling roads after vertex deletion");
        names(graph.bfs("A"), "A", "C", "D", "E");
        check(graph.removeRoad("C", "A") && !graph.removeRoad("A", "C"), "Symmetric edge removal");
        names(graph.bfs("A"), "A");
        check(Double.isInfinite(graph.distanceBetween("A", "C")), "Both directions removed");
        check(graph.addRoad("A", "C", 5), "Re-add removed edge");
        graph.addLocation("B");
        graph.addRoad("E", "B", 3);
        names(graph.dfs("A"), "A", "C", "D", "E", "B");
        for (String name : graph.locationNames()) { graph.removeLocation(name); }
        check(graph.size() == 0 && graph.roadCount() == 0, "Remove all vertices");
        for (int i = 0; i < 30; i++) { graph.addLocation("L" + i); }
        for (int i = 1; i < 30; i++) { graph.addRoad("L" + (i - 1), "L" + i, i); }
        check(graph.bfs("L0").length == 30 && graph.dfs("L0").length == 30, "Vertex array expands");
        graph.removeLocation("L15");
        check(graph.bfs("L0").length == 15 && graph.bfs("L29").length == 14, "Compaction preserves traversal indexes");
    }

    private static void integration() {
        UniversitySystem system = new UniversitySystem();
        check(system.indexesConsistent(), "Initially consistent");
        Student original = student("A");
        system.addStudent(original);
        rejects(() -> system.addStudent(student("a")));
        Student updated = student("A", "Changed", 3.8);
        system.updateStudent(updated);
        check(system.indexesConsistent() && system.allStudents()[0] == updated
                && system.studentsById()[0] == updated && system.searchByHash("a") == updated, "All indexes updated");
        ServiceRequest first = system.addRequest("A", "Transcript", "Copy");
        ServiceRequest second = system.addRequest("A", "Letter", "Letter");
        check(!first.getRequestId().equals(second.getRequestId()), "Unique request IDs");
        rejects(() -> system.addRequest("Z", "Letter", "Letter"));
        rejects(() -> system.deleteStudent("A"));
        check(system.indexesConsistent() && system.findStudent("A") == updated, "Pending request prevents orphaning");
        check(system.processNextRequest() == first && system.processNextRequest() == second, "Integrated FIFO");
        check(system.deleteStudent("A") == updated && system.indexesConsistent(), "Delete across all indexes");
        check(system.findStudent("A") == null && system.studentsById().length == 0, "Deleted key absent");
        rejects(() -> system.updateStudent(original));
        check(system.peekAction().getDescription().equals("Deleted student A"), "Deletion logged");
        system.popAction();
        check(system.findStudent("A") == null, "Popping history does not undo");
        UniversitySystem demo = new UniversitySystem();
        demo.loadDemoData();
        check(demo.allStudents().length == 4 && demo.locationNames().length == 7 && demo.indexesConsistent(), "Demo fixture");
        names(demo.traverse("Main Gate", true), "Main Gate", "Library", "Engineering Faculty", "Computer Laboratory", "Cafeteria", "Lecture Hall", "Hostel");
        rejects(demo::loadDemoData);
    }

    private static void randomizedIntegration() {
        UniversitySystem system = new UniversitySystem();
        Student[] expected = new Student[50];
        for (int turn = 0; turn < 600; turn++) {
            int key = random(expected.length);
            if (expected[key] == null) {
                expected[key] = student(id(key), "Name " + turn, (turn % 41) / 10.0);
                system.addStudent(expected[key]);
            } else if (random(2) == 0) {
                system.deleteStudent(id(key));
                expected[key] = null;
            } else {
                expected[key] = student(id(key), "Updated " + turn, (turn % 41) / 10.0);
                system.updateStudent(expected[key]);
            }
            check(system.indexesConsistent(), "Indexes agree after mutation " + turn);
            int count = 0;
            Student[] sorted = system.studentsById();
            for (int i = 0; i < expected.length; i++) {
                check(system.findStudent(id(i)) == expected[i], "Model agrees with lookup");
                if (expected[i] != null) { check(sorted[count++] == expected[i], "Model agrees with tree order"); }
            }
            check(count == sorted.length && count == system.allStudents().length, "No extra records");
        }
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
                "1", "s1",
                "2", "S1", "Updated Student", "", "", "", "", "", "", "3.9",
                "4", "1", "S1", "2", "Updated Student", "3", "S1", "4", "updated student", "5", "6", "7", "0",
                "5", "2", "1", "MISSING", "1", "S1", "Transcript", "Printed copy", "2", "0",
                "3", "S1", "6", "6",
                "7", "1", "2", "3", "0", "8", "9", "S1",
                "10", "Main Gate", "10", "Library", "10", "library",
                "12", "Main Gate", "Library", "Infinity", "-1", "150", "14",
                "15", "1", "Main Gate", "15", "2", "Library",
                "13", "Library", "Main Gate", "11", "Library", "3", "S1", "8", "16", "");
        String output = runConsole(system, script);
        String[] expected = {"Please enter a valid number.", "Choose a number from 1 to 16.", "Student name cannot be empty.",
                "Age must be between", "Enter a valid email", "Contact number may contain", "GPA must be a finite number",
                "Student added", "Student ID already exists", "Student updated", "Updated Student", "GPA ranking", "Hash table:",
                "Added to queue: REQ-1", "Process this student's waiting", "Processed: REQ-1", "No waiting service requests.",
                "Latest:", "Popped:", "AVL inorder traversal", "Campus location already exists.", "Distance must be a finite number",
                "BFS traversal: Main Gate -> Library", "DFS traversal: Library -> Main Gate", "campus road removed.",
                "Campus location and connected roads removed.", "Deleted student S1", "Goodbye!"};
        for (String text : expected) { check(output.contains(text), "Missing console output: " + text); }
        check(system.indexesConsistent() && system.allStudents().length == 0, "Console student mutations consistent");
        check(system.locationNames().length == 1 && system.waitingRequests().length == 0, "Console graph and queue final state");
    }

    private static void emptyConsole() throws Exception {
        String output = runConsole(new UniversitySystem(), "4\n0\n5\n2\n0\n6\n7\n1\n2\n0\n8\n9\nMISSING\n14\n15\n16\n");
        check(output.contains("No student records.") && output.contains("No waiting service requests.")
                && output.contains("No recent actions.") && output.contains("No campus locations.")
                && output.contains("Student record not found."), "Empty structures are handled");
        UniversitySystem system = new UniversitySystem();
        boolean closed = false;
        try { runConsole(system, "1\nS1\nName\n"); }
        catch (EOFException expected) { closed = true; }
        check(closed && system.allStudents().length == 0 && system.indexesConsistent(), "EOF cannot partially insert a student");
        system.addStudent(student("S1"));
        closed = false;
        try { runConsole(system, "2\nS1\nChanged\n"); }
        catch (EOFException expected) { closed = true; }
        check(closed && system.findStudent("S1").getName().equals("Student S1") && system.indexesConsistent(), "EOF cannot partially update");
    }
}
