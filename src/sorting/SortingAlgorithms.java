package sorting;

import student.Student;


public final class SortingAlgorithms {
    private enum Key { ID, NAME, GPA }

    private SortingAlgorithms() { }

    public static Student[] sortById(Student[] students) { return sortedCopy(students, Key.ID); }
    public static Student[] sortByName(Student[] students) { return sortedCopy(students, Key.NAME); }
    public static Student[] sortByGpa(Student[] students) { return sortedCopy(students, Key.GPA); }

    private static Student[] sortedCopy(Student[] students, Key key) {
        if (students == null) { throw new IllegalArgumentException("Students cannot be null."); }
        Student[] result = students.clone();
        for (Student student : result) {
            if (student == null) { throw new IllegalArgumentException("Student entries cannot be null."); }
        }
        mergeSort(result, new Student[result.length], 0, result.length, key);
        return result;
    }

    private static void mergeSort(Student[] values, Student[] buffer, int start, int end, Key key) {
        if (end - start < 2) { return; }
        int middle = start + (end - start) / 2;
        mergeSort(values, buffer, start, middle, key);
        mergeSort(values, buffer, middle, end, key);
        int left = start;
        int right = middle;
        int index = start;
        while (left < middle && right < end) {
            buffer[index++] = compare(values[left], values[right], key) <= 0
                    ? values[left++] : values[right++];
        }
        while (left < middle) { buffer[index++] = values[left++]; }
        while (right < end) { buffer[index++] = values[right++]; }
        System.arraycopy(buffer, start, values, start, end - start);
    }

    private static int compare(Student left, Student right, Key key) {
        int comparison = switch (key) {
            case ID -> left.getStudentId().compareTo(right.getStudentId());
            case NAME -> left.getName().compareToIgnoreCase(right.getName());
            case GPA -> Double.compare(right.getGpa(), left.getGpa());
        };
        return comparison != 0 ? comparison : left.getStudentId().compareTo(right.getStudentId());
    }
}
