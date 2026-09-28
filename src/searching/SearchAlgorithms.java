package searching;

import sorting.SortingAlgorithms;
import student.Student;

/** Exact ID/name searches; name searches return every case-insensitive match. */
public final class SearchAlgorithms {
    private SearchAlgorithms() { }

    public static Student linearSearchById(Student[] students, String studentId) {
        validate(students);
        String id = Student.normalizeId(studentId);
        for (Student student : students) {
            if (student.getStudentId().equals(id)) { return student; }
        }
        return null;
    }

    public static Student[] linearSearchByName(Student[] students, String name) {
        validate(students);
        String query = Student.requireText(name, "Name");
        Student[] matches = new Student[students.length];
        int count = 0;
        for (Student student : students) {
            if (student.getName().equalsIgnoreCase(query)) { matches[count++] = student; }
        }
        Student[] result = new Student[count];
        System.arraycopy(matches, 0, result, 0, count);
        return result;
    }

    /** Sorts a copy first so callers cannot accidentally search unsorted data. */
    public static Student binarySearchById(Student[] students, String studentId) {
        String id = Student.normalizeId(studentId);
        Student[] sorted = SortingAlgorithms.sortById(students);
        int low = 0;
        int high = sorted.length - 1;
        while (low <= high) {
            int middle = low + (high - low) / 2;
            int comparison = sorted[middle].getStudentId().compareTo(id);
            if (comparison == 0) { return sorted[middle]; }
            if (comparison < 0) { low = middle + 1; }
            else { high = middle - 1; }
        }
        return null;
    }

    public static Student[] binarySearchByName(Student[] students, String name) {
        String query = Student.requireText(name, "Name");
        Student[] sorted = SortingAlgorithms.sortByName(students);
        int low = 0;
        int high = sorted.length;
        // Lower bound finds the first match, including repeated student names.
        while (low < high) {
            int middle = low + (high - low) / 2;
            if (sorted[middle].getName().compareToIgnoreCase(query) < 0) { low = middle + 1; }
            else { high = middle; }
        }
        int end = low;
        while (end < sorted.length && sorted[end].getName().equalsIgnoreCase(query)) { end++; }
        Student[] result = new Student[end - low];
        System.arraycopy(sorted, low, result, 0, result.length);
        return result;
    }

    private static void validate(Student[] students) {
        if (students == null) { throw new IllegalArgumentException("Students cannot be null."); }
        for (Student student : students) {
            if (student == null) { throw new IllegalArgumentException("Student entries cannot be null."); }
        }
    }
}
