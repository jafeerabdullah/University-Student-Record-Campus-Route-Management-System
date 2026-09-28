package app;

import queue.ServiceQueue;
import queue.ServiceRequest;
import stack.Action;
import stack.ActionStack;
import student.Student;
import student.StudentLinkedList;

/** Coordinates the student, queue and stack contributions currently merged into main. */
public final class UniversitySystem {
    private final StudentLinkedList students = new StudentLinkedList();
    private final ServiceQueue requests = new ServiceQueue();
    private final ActionStack history = new ActionStack();
    private long nextRequestId = 1;

    private void log(String description) { history.push(new Action(description)); }

    public void addStudent(Student student) {
        if (student == null) { throw new IllegalArgumentException("Student cannot be null."); }
        if (students.search(student.getStudentId()) != null) {
            throw new IllegalArgumentException("Student ID already exists: " + student.getStudentId());
        }
        students.add(student);
        log("Added student " + student.getStudentId());
    }

    public void updateStudent(Student student) {
        if (student == null) { throw new IllegalArgumentException("Student cannot be null."); }
        requireStudent(student.getStudentId());
        students.update(student);
        log("Updated student " + student.getStudentId());
    }

    public Student deleteStudent(String studentId) {
        Student removed = requireStudent(studentId);
        if (requests.hasStudent(studentId)) {
            throw new IllegalArgumentException("Process this student's waiting service requests before deleting the record.");
        }
        students.delete(studentId);
        log("Deleted student " + removed.getStudentId());
        return removed;
    }

    public Student findStudent(String studentId) { return students.search(studentId); }

    private Student requireStudent(String studentId) {
        Student found = findStudent(studentId);
        if (found == null) { throw new IllegalArgumentException("Student record not found: " + studentId); }
        return found;
    }

    public Student searchStudent(String studentId) {
        Student found = students.search(studentId);
        log("Linked list search for student " + Student.normalizeId(studentId) + (found == null ? " (not found)" : " (found)"));
        return found;
    }

    public Student[] allStudents() { return students.toArray(); }

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

    /** Optional sample students. A normal launch starts with empty structures. */
    public void loadDemoData() {
        if (!students.isEmpty() || !requests.isEmpty() || !history.isEmpty()) {
            throw new IllegalArgumentException("Demo data can only be loaded into a fresh system.");
        }
        addStudent(new Student("0001", "Abdullah", 21, "Male", "BAIT", "jafeerabdullah4g@gmail.com", "0770671752", "Kattankudy", 3.75));
        addStudent(new Student("0002", "Asra", 22, "Female", "IT", "mohammedasra577@gmail.com", "0779184849", "Eravur", 3.50));
        addStudent(new Student("0003", "Dilsath", 20, "Male", "Computer Science", "dilsathmohamed90@gmail.com", "0704875725", "Polonnauruwa", 3.90));
        addStudent(new Student("0004", "Nifra", 21, "Female", "Computer Science", "nifra@example.com", "0775691305", "Polonnaruwa", 3.60));
        log("Loaded four sample students");
    }
}
