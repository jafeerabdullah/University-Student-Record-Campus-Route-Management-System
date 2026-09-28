package tree;

/** AVL tree using single/double rotations after both insertion and deletion. */
public final class AVLTree extends StudentBST {
    private static int balance(TreeNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    @Override
    protected TreeNode restore(TreeNode node) {
        refreshHeight(node);
        if (balance(node) > 1) {
            if (balance(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }
        if (balance(node) < -1) {
            if (balance(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }
        return node;
    }

    private TreeNode rotateRight(TreeNode oldRoot) {
        TreeNode newRoot = oldRoot.left;
        oldRoot.left = newRoot.right;
        newRoot.right = oldRoot;
        refreshHeight(oldRoot);
        refreshHeight(newRoot);
        return newRoot;
    }

    private TreeNode rotateLeft(TreeNode oldRoot) {
        TreeNode newRoot = oldRoot.right;
        oldRoot.right = newRoot.left;
        newRoot.left = oldRoot;
        refreshHeight(oldRoot);
        refreshHeight(newRoot);
        return newRoot;
    }

    /** Checks actual subtree heights, ordering and balance for demonstrations/tests. */
    public boolean isValidAvl() {
        return validate(root, null, null) >= 0;
    }

    private int validate(TreeNode node, String lower, String upper) {
        if (node == null) { return 0; }
        String id = node.student.getStudentId();
        if ((lower != null && id.compareTo(lower) <= 0)
                || (upper != null && id.compareTo(upper) >= 0)) { return -1; }
        int left = validate(node.left, lower, id);
        int right = validate(node.right, id, upper);
        if (left < 0 || right < 0 || Math.abs(left - right) > 1
                || node.height != 1 + Math.max(left, right)) { return -1; }
        return node.height;
    }
}
