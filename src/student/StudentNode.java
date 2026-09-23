package student;

/** Package-private links cannot be changed by callers of the list. */
final class StudentNode {
    Student student;
    StudentNode next;

    StudentNode(Student student) {
        this.student = student;
    }
}
