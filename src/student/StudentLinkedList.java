package student;

/** Custom singly linked list; preserves student insertion order. */
public final class StudentLinkedList {
    private StudentNode head;
    private StudentNode tail;
    private int size;

    public boolean add(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null.");
        }
        if (search(student.getStudentId()) != null) {
            return false;
        }
        StudentNode node = new StudentNode(student);
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        size++;
        return true;
    }

    public Student search(String studentId) {
        String id = Student.normalizeId(studentId);
        for (StudentNode current = head; current != null; current = current.next) {
            if (current.student.getStudentId().equals(id)) {
                return current.student;
            }
        }
        return null;
    }

    public boolean update(Student replacement) {
        if (replacement == null) {
            throw new IllegalArgumentException("Student cannot be null.");
        }
        for (StudentNode current = head; current != null; current = current.next) {
            if (current.student.getStudentId().equals(replacement.getStudentId())) {
                current.student = replacement;
                return true;
            }
        }
        return false;
    }

    public Student delete(String studentId) {
        String id = Student.normalizeId(studentId);
        StudentNode previous = null;
        StudentNode current = head;
        while (current != null) {
            if (current.student.getStudentId().equals(id)) {
                if (previous == null) {
                    head = current.next;
                } else {
                    previous.next = current.next;
                }
                if (current == tail) {
                    tail = previous;
                }
                size--;
                return current.student;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    /** Traverses links into an independent array for display/search/sorting. */
    public Student[] toArray() {
        Student[] result = new Student[size];
        int index = 0;
        for (StudentNode current = head; current != null; current = current.next) {
            result[index++] = current.student;
        }
        return result;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}
