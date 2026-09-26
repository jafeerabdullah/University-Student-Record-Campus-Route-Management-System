package app;

import graph.CampusRouteGraph;
import hashing.StudentHashTable;
import queue.ServiceQueue;
import queue.ServiceRequest;
import searching.SearchAlgorithms;
import sorting.SortingAlgorithms;
import stack.Action;
import stack.ActionStack;
import student.Student;
import student.StudentLinkedList;
import tree.AVLTree;

/** Owns all mutable structures and keeps the list/hash/tree student indexes in sync. */
public final class UniversitySystem {
    private final StudentLinkedList students = new StudentLinkedList();
    private final StudentHashTable hashTable = new StudentHashTable();
    private final AVLTree tree = new AVLTree();
    private final ServiceQueue requests = new ServiceQueue();
    private final ActionStack history = new ActionStack();
    private final CampusRouteGraph campus = new CampusRouteGraph();
    private long nextRequestId = 1;

    private void log(String description) { history.push(new Action(description)); }

    public void addStudent(Student student) {
        if (student == null) { throw new IllegalArgumentException("Student cannot be null."); }
        if (hashTable.search(student.getStudentId()) != null) {
            throw new IllegalArgumentException("Student ID already exists: " + student.getStudentId());
        }
        students.add(student);
        hashTable.insert(student);
        tree.insert(student);
        log("Added student " + student.getStudentId());
    }

    public void updateStudent(Student student) {
        if (student == null) { throw new IllegalArgumentException("Student cannot be null."); }
        requireStudent(student.getStudentId());
        students.update(student);
        hashTable.update(student);
        tree.update(student);
        log("Updated student " + student.getStudentId());
    }

    public Student deleteStudent(String studentId) {
        Student removed = requireStudent(studentId);
        if (requests.hasStudent(studentId)) {
            throw new IllegalArgumentException("Process this student's waiting service requests before deleting the record.");
        }
        students.delete(studentId);
        hashTable.delete(studentId);
        tree.delete(studentId);
        log("Deleted student " + removed.getStudentId());
        return removed;
    }

    public Student findStudent(String studentId) { return hashTable.search(studentId); }

    private Student requireStudent(String studentId) {
        Student found = findStudent(studentId);
        if (found == null) { throw new IllegalArgumentException("Student record not found: " + studentId); }
        return found;
    }

    public Student searchByHash(String studentId) {
        Student found = hashTable.search(studentId);
        log("Hash search for student " + Student.normalizeId(studentId) + (found == null ? " (not found)" : " (found)"));
        return found;
    }

    public Student searchById(String id, boolean binary) {
        Student found = binary ? SearchAlgorithms.binarySearchById(students.toArray(), id)
                : SearchAlgorithms.linearSearchById(students.toArray(), id);
        log((binary ? "Binary" : "Linear") + " ID search for " + Student.normalizeId(id));
        return found;
    }

    public Student[] searchByName(String name, boolean binary) {
        Student[] found = binary ? SearchAlgorithms.binarySearchByName(students.toArray(), name)
                : SearchAlgorithms.linearSearchByName(students.toArray(), name);
        log((binary ? "Binary" : "Linear") + " name search for " + name.trim());
        return found;
    }

    public Student[] allStudents() { return students.toArray(); }
    public Student[] studentsById() { return tree.inorder(); }
    public Student[] studentsByName() { return SortingAlgorithms.sortByName(students.toArray()); }
    public Student[] studentsByGpa() { return SortingAlgorithms.sortByGpa(students.toArray()); }
    public String describeHashTable() { return hashTable.describeBuckets(); }

    public ServiceRequest addRequest(String studentId, String type, String description) {
        Student student = requireStudent(studentId);
        ServiceRequest request = new ServiceRequest("REQ-" + nextRequestId, student.getStudentId(), type, description);
        requests.enqueue(request);
        nextRequestId++;
        log("Queued " + request.getRequestId() + " for student " + student.getStudentId());
        return request;
    }

    public ServiceRequest processNextRequest() {
        ServiceRequest request = requests.dequeue();
        if (request != null) { log("Processed " + request.getRequestId() + " for student " + request.getStudentId()); }
        return request;
    }

    public ServiceRequest[] waitingRequests() { return requests.toArray(); }
    public Action[] recentActions() { return history.toArray(); }
    public Action peekAction() { return history.peek(); }
    public Action popAction() { return history.pop(); }

    public void addLocation(String name) {
        if (!campus.addLocation(name)) { throw new IllegalArgumentException("Campus location already exists."); }
        log("Added campus location " + name.trim());
    }

    public void removeLocation(String name) {
        if (!campus.removeLocation(name)) { throw new IllegalArgumentException("Campus location not found."); }
        log("Removed campus location " + name.trim() + " and its connected roads");
    }

    public void addRoad(String from, String to, double distance) {
        if (!campus.addRoad(from, to, distance)) { throw new IllegalArgumentException("Road already exists. Remove it before adding a new distance."); }
        log("Added campus road " + from.trim() + " <-> " + to.trim() + " (" + distance + " m)");
    }

    public void removeRoad(String from, String to) {
        if (!campus.removeRoad(from, to)) { throw new IllegalArgumentException("Campus road not found."); }
        log("Removed campus road " + from.trim() + " <-> " + to.trim());
    }

    public String[] locationNames() { return campus.locationNames(); }
    public String describeCampus() { return campus.describeConnections(); }

    public String[] traverse(String start, boolean breadthFirst) {
        String[] order = breadthFirst ? campus.bfs(start) : campus.dfs(start);
        log((breadthFirst ? "BFS" : "DFS") + " traversal from " + start.trim());
        return order;
    }

    /** Read-only diagnostic used by integration tests. */
    public boolean indexesConsistent() {
        if (students.size() != hashTable.size() || students.size() != tree.size() || !tree.isValidAvl()) { return false; }
        for (Student student : students.toArray()) {
            if (hashTable.search(student.getStudentId()) != student || tree.search(student.getStudentId()) != student) { return false; }
        }
        return true;
    }

    /** Fictional, optional sample data. A normal launch starts with empty structures. */
    public void loadDemoData() {
        if (!students.isEmpty() || campus.size() != 0 || !requests.isEmpty() || !history.isEmpty()) {
            throw new IllegalArgumentException("Demo data can only be loaded into a fresh system.");
        }
        addStudent(new Student("0001", "Abdullah", 21, "Male", "BAIT", "jafeerabdullah4g@gmail.com", "0771234567", "Colombo", 3.75));
        addStudent(new Student("0002", "Asra", 22, "Female", "IT", "mohammedasra577@gmail.com", "0772345678", "Kandy", 3.50));
        addStudent(new Student("0003", "Dilsath", 20, "Male", "Computer Science", "dilsathmohamm.com", "0773456789", "Galle", 3.90));
        addStudent(new Student("0004", "Nifra", 21, "Female", "Computer Science", "nifra@example.com", "0774567890", "Jaffna", 3.60));
        String[] names = {"Main Gate", "Library", "Engineering Faculty", "Computer Laboratory", "Lecture Hall", "Cafeteria", "Hostel"};
        for (String name : names) { addLocation(name); }
        addRoad("Main Gate", "Library", 150);
        addRoad("Main Gate", "Engineering Faculty", 300);
        addRoad("Library", "Computer Laboratory", 100);
        addRoad("Library", "Cafeteria", 120);
        addRoad("Engineering Faculty", "Lecture Hall", 80);
        addRoad("Computer Laboratory", "Lecture Hall", 90);
        addRoad("Lecture Hall", "Cafeteria", 140);
        addRoad("Cafeteria", "Hostel", 250);
        log("Loaded fictional demonstration data");
    }
}
