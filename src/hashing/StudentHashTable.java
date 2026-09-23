package hashing;

import student.Student;

/** Custom polynomial hash and separate chaining; resizes above 0.75 load. */
public final class StudentHashTable {
    private static final class Entry {
        Student student;
        Entry next;

        Entry(Student student, Entry next) {
            this.student = student;
            this.next = next;
        }
    }

    private Entry[] buckets;
    private int size;

    public StudentHashTable() { this(17); }

    public StudentHashTable(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Hash table capacity must be positive.");
        }
        buckets = new Entry[capacity];
    }

    private int hash(String id) {
        int result = 0;
        for (int i = 0; i < id.length(); i++) {
            result = (int) (((long) result * 31 + id.charAt(i)) % buckets.length);
        }
        return result;
    }

    public boolean insert(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null.");
        }
        if (search(student.getStudentId()) != null) {
            return false;
        }
        if ((size + 1.0) / buckets.length > 0.75) {
            resize();
        }
        int index = hash(student.getStudentId());
        buckets[index] = new Entry(student, buckets[index]);
        size++;
        return true;
    }

    private void resize() {
        Entry[] oldBuckets = buckets;
        buckets = new Entry[oldBuckets.length * 2 + 1];
        for (Entry bucket : oldBuckets) {
            Entry entry = bucket;
            while (entry != null) {
                Entry next = entry.next;
                int index = hash(entry.student.getStudentId());
                entry.next = buckets[index];
                buckets[index] = entry;
                entry = next;
            }
        }
    }

    public Student search(String studentId) {
        String id = Student.normalizeId(studentId);
        for (Entry entry = buckets[hash(id)]; entry != null; entry = entry.next) {
            if (entry.student.getStudentId().equals(id)) {
                return entry.student;
            }
        }
        return null;
    }

    public boolean update(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null.");
        }
        String id = student.getStudentId();
        for (Entry entry = buckets[hash(id)]; entry != null; entry = entry.next) {
            if (entry.student.getStudentId().equals(id)) {
                entry.student = student;
                return true;
            }
        }
        return false;
    }

    public Student delete(String studentId) {
        String id = Student.normalizeId(studentId);
        int index = hash(id);
        Entry previous = null;
        Entry entry = buckets[index];
        while (entry != null) {
            if (entry.student.getStudentId().equals(id)) {
                if (previous == null) {
                    buckets[index] = entry.next;
                } else {
                    previous.next = entry.next;
                }
                size--;
                return entry.student;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    public String describeBuckets() {
        StringBuilder text = new StringBuilder("Hash table: " + size + " students, " + buckets.length + " buckets\n");
        for (int i = 0; i < buckets.length; i++) {
            text.append('[').append(i).append("] ");
            for (Entry entry = buckets[i]; entry != null; entry = entry.next) {
                text.append(entry.student.getStudentId()).append(" -> ");
            }
            text.append("null\n");
        }
        return text.toString();
    }

    public int size() { return size; }
    public int capacity() { return buckets.length; }
}
