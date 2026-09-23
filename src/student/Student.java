package student;

import java.util.Locale;

/** Immutable record: updates replace the same record in all three indexes. */
public final class Student {
    private final String studentId;
    private final String name;
    private final int age;
    private final String gender;
    private final String degreeProgramme;
    private final String email;
    private final String contactNumber;
    private final String address;
    private final double gpa;

    public Student(String studentId, String name, int age, String gender,
                   String degreeProgramme, String email, String contactNumber,
                   String address, double gpa) {
        this.studentId = normalizeId(studentId);
        this.name = requireText(name, "Name");
        this.age = validateAge(age);
        this.gender = requireText(gender, "Gender");
        this.degreeProgramme = requireText(degreeProgramme, "Degree programme");
        this.email = validateEmail(email);
        this.contactNumber = validateContact(contactNumber);
        this.address = requireText(address, "Address");
        this.gpa = validateGpa(gpa);
    }

    public static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
        return value.trim();
    }

    public static String normalizeId(String id) {
        String normalized = requireText(id, "Student ID").toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z0-9][A-Z0-9_-]{0,39}")) {
            throw new IllegalArgumentException("Student ID must contain 1-40 letters, digits, hyphens or underscores.");
        }
        return normalized;
    }

    public static int validateAge(int age) {
        if (age < 1 || age > 120) {
            throw new IllegalArgumentException("Age must be between 1 and 120.");
        }
        return age;
    }

    public static double validateGpa(double gpa) {
        if (!Double.isFinite(gpa) || gpa < 0.0 || gpa > 4.0) {
            throw new IllegalArgumentException("GPA must be a finite number between 0.00 and 4.00.");
        }
        return gpa;
    }

    public static String validateEmail(String email) {
        String value = requireText(email, "Email");
        if (!value.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Enter a valid email, for example name@example.com.");
        }
        return value;
    }

    public static String validateContact(String contact) {
        String value = requireText(contact, "Contact number");
        if (!value.matches("\\+?[0-9 ()-]+")) {
            throw new IllegalArgumentException("Contact number may contain digits, spaces, (), hyphens and an initial +.");
        }
        int digits = 0;
        for (int i = 0; i < value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) {
                digits++;
            }
        }
        if (digits < 7 || digits > 15) {
            throw new IllegalArgumentException("Contact number must contain 7-15 digits.");
        }
        return value;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getDegreeProgramme() { return degreeProgramme; }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }
    public String getAddress() { return address; }
    public double getGpa() { return gpa; }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s | %s | %s | GPA %.2f",
                studentId, name, degreeProgramme, gpa);
    }
}
