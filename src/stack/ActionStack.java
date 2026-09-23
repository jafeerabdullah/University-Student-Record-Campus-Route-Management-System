package stack;

/** Linked LIFO stack. Popping history does not undo the original operation. */
public final class ActionStack {
    private StackNode top;
    private int size;

    public void push(Action action) {
        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null.");
        }
        top = new StackNode(action, top);
        size++;
    }

    public Action pop() {
        if (top == null) {
            return null;
        }
        Action result = top.action;
        top = top.next;
        size--;
        return result;
    }

    public Action peek() { return top == null ? null : top.action; }
    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public Action[] toArray() {
        Action[] result = new Action[size];
        int index = 0;
        for (StackNode node = top; node != null; node = node.next) {
            result[index++] = node.action;
        }
        return result;
    }
}
