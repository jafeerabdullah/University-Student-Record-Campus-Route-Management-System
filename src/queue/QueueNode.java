package queue;

final class QueueNode {
    final ServiceRequest request;
    QueueNode next;

    QueueNode(ServiceRequest request) {
        this.request = request;
    }
}
