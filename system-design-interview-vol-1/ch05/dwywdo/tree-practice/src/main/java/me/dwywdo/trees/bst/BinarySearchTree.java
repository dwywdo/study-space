package me.dwywdo.trees.bst;

import javax.annotation.Nullable;

public class BinarySearchTree {
    @Nullable
    BSTNode root;

    public BinarySearchTree() {
        root = null;
    }

    public void insert(int key) {
        root = recursiveInsert(root, key);
    }

    public void delete(int key) {
        root = recursiveDelete(root, key);
    }

    public boolean search(int key) {
        return recursiveSearch(root, key);
    }

    private static BSTNode recursiveInsert(@Nullable BSTNode root, int key) {
        if (root == null) {
            root = new BSTNode(key);
            return root;
        }

        if (key < root.key) {
            root.left = recursiveInsert(root.left, key);
        } else if (key > root.key) {
            root.right = recursiveInsert(root.right, key);
        }

        return root;
    }

    @Nullable
    private static BSTNode recursiveDelete(@Nullable BSTNode root, int key) {
        if (root == null) {
            return null;
        }

        if (key < root.key) {
            root.left = recursiveDelete(root.left, key);
        } else if (key > root.key) {
            root.right = recursiveDelete(root.right, key);
        } else {
            // IF there's no child OR only one child exists
            if (root.left == null) {
                return root.right;
            }

            if (root.right == null) {
                return root.left;
            }

            // IF there are two children nodes: Replace it with maximum nodes from left subtree.
            root.key = maxValue(root.left);

            // Remove duplicate maximum nodes from left subtree.
            root.left = recursiveDelete(root.left, root.key);
        }
        return root;
    }

    private static int minValue(BSTNode root) {
        int minV = root.key;
        while (root.left != null) {
            minV = root.left.key;
            root = root.left;
        }
        return minV;
    }

    private static int maxValue(BSTNode root) {
        int maxV = root.key;
        while (root.right != null) {
            maxV = root.right.key;
            root = root.right;
        }
        return maxV;
    }

    private static boolean recursiveSearch(@Nullable BSTNode root, int key) {
        if (root == null) {
            return false;
        }
        if (root.key == key) {
            return true;
        }
        if (key < root.key) {
            return recursiveSearch(root.left, key);
        } else {
            return recursiveSearch(root.right, key);
        }
    }

    public void inorderTraversal() {
        recursiveInorderTraversal(root);
        System.out.println();
    }

    private void recursiveInorderTraversal(@Nullable BSTNode root) {
        if (root != null) {
            recursiveInorderTraversal(root.left);
            System.out.println("root.key = " + root.key);
            recursiveInorderTraversal(root.right);
        }
    }
}
