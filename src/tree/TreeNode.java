package tree;

import student.Student;

final class TreeNode {
    Student student;
    TreeNode left;
    TreeNode right;
    int height = 1;

    TreeNode(Student student) {
        this.student = student;
    }
}
