package queue;

/** Linked FIFO queue with constant-time enqueue/dequeue. */
public final class ServiceQueue {
    private QueueNode front;
    private QueueNode rear;
    private int size;

    public void enqueue(ServiceRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }
        QueueNode node = new QueueNode(request);
        if (rear == null) {
            front = node;
        } else {
            rear.next = node;
        }
        rear = node;
        size++;
    }

    public ServiceRequest dequeue() {
        if (front == null) {
            return null;
        }
        ServiceRequest result = front.request;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return result;
    }

    public ServiceRequest peek() { return front == null ? null : front.request; }
    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public boolean hasStudent(String studentId) {
        String id = student.Student.normalizeId(studentId);
        for (QueueNode node = front; node != null; node = node.next) {
            if (node.request.getStudentId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public ServiceRequest[] toArray() {
        ServiceRequest[] result = new ServiceRequest[size];
        int index = 0;
        for (QueueNode node = front; node != null; node = node.next) {
            result[index++] = node.request;
        }
        return result;
    }
}
