package stack;

final class StackNode {
    final Action action;
    final StackNode next;

    StackNode(Action action, StackNode next) {
        this.action = action;
        this.next = next;
    }
}
