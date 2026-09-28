package queue;

import student.Student;

public final class ServiceRequest {
    private final String requestId;
    private final String studentId;
    private final String requestType;
    private final String description;

    public ServiceRequest(String requestId, String studentId, String requestType, String description) {
        this.requestId = Student.requireText(requestId, "Request ID");
        this.studentId = Student.normalizeId(studentId);
        this.requestType = Student.requireText(requestType, "Request type");
        this.description = Student.requireText(description, "Description");
    }

    public String getRequestId() { return requestId; }
    public String getStudentId() { return studentId; }
    public String getRequestType() { return requestType; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return requestId + " | Student " + studentId + " | " + requestType + " | " + description;
    }
}
