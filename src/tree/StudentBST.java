package tree;

import student.Student;

/** Binary search tree ordered lexicographically by normalized student ID. */
public class StudentBST {
    protected TreeNode root;
    private int size;

    public boolean insert(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null.");
        }
        if (search(student.getStudentId()) != null) {
            return false;
        }
        root = insertNode(root, student);
        size++;
        return true;
    }

    private TreeNode insertNode(TreeNode node, Student student) {
        if (node == null) {
            return new TreeNode(student);
        }
        if (student.getStudentId().compareTo(node.student.getStudentId()) < 0) {
            node.left = insertNode(node.left, student);
        } else {
            node.right = insertNode(node.right, student);
        }
        return restore(node);
    }

    public Student search(String studentId) {
        TreeNode node = findNode(Student.normalizeId(studentId));
        return node == null ? null : node.student;
    }

    private TreeNode findNode(String id) {
        TreeNode node = root;
        while (node != null) {
            int comparison = id.compareTo(node.student.getStudentId());
            if (comparison == 0) {
                return node;
            }
            node = comparison < 0 ? node.left : node.right;
        }
        return null;
    }

    public boolean update(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null.");
        }
        TreeNode node = findNode(student.getStudentId());
        if (node == null) {
            return false;
        }
        node.student = student;
        return true;
    }

    public Student delete(String studentId) {
        String id = Student.normalizeId(studentId);
        Student removed = search(id);
        if (removed != null) {
            root = deleteNode(root, id);
            size--;
        }
        return removed;
    }

    private TreeNode deleteNode(TreeNode node, String id) {
        if (node == null) {
            return null;
        }
        int comparison = id.compareTo(node.student.getStudentId());
        if (comparison < 0) {
            node.left = deleteNode(node.left, id);
        } else if (comparison > 0) {
            node.right = deleteNode(node.right, id);
        } else {
            if (node.left == null) { return node.right; }
            if (node.right == null) { return node.left; }
            TreeNode successor = node.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            node.student = successor.student;
            node.right = deleteNode(node.right, successor.student.getStudentId());
        }
        return restore(node);
    }

    /** AVL overrides this hook; the ordinary BST only refreshes height. */
    protected TreeNode restore(TreeNode node) {
        refreshHeight(node);
        return node;
    }

    protected static int height(TreeNode node) { return node == null ? 0 : node.height; }

    protected static void refreshHeight(TreeNode node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    public Student[] inorder() {
        Student[] result = new Student[size];
        fillInorder(root, result, 0);
        return result;
    }

    private int fillInorder(TreeNode node, Student[] result, int index) {
        if (node == null) {
            return index;
        }
        index = fillInorder(node.left, result, index);
        result[index++] = node.student;
        return fillInorder(node.right, result, index);
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
    public int height() { return height(root); }
}
