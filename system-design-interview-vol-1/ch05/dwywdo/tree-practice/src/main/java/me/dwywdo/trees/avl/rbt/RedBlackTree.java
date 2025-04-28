package me.dwywdo.trees.avl.rbt;

public class RedBlackTree {
    private RedBlackNode root;
    private static final boolean RED = false;
    private static final boolean BLACK = true;

    public void insert(int key) {
        final RedBlackNode newNode = new RedBlackNode(key);
        if (root == null) {
            root = newNode;
            root.color = BLACK;
            return;
        }

        RedBlackNode parent = null;
        RedBlackNode current = root;

        while (current != null) {
            parent = current;
            if (key < current.key) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        newNode.parent = parent;
        if (key < parent.key) {parent.left = newNode;} else {parent.right = newNode;}

        fixInsert(newNode);
    }

    private void fixInsert(RedBlackNode node) {
        while (node.parent != null && node.parent.color == RED) {
            RedBlackNode parent = node.parent;
            RedBlackNode grandparent = parent.parent;
            RedBlackNode uncle = (parent == grandparent.left) ? grandparent.right : grandparent.left;

            if (uncle != null && uncle.color == RED) {
                parent.color = BLACK;
                uncle.color = BLACK;
                grandparent.color = RED;
                node = grandparent;
            } else {
                if (parent == grandparent.left) {
                    if (node == parent.right) {
                        rotateLeft(parent);
                        parent = node;
                    }
                    rotateRight(grandparent);
                } else {
                    if (node == parent.left) {
                        rotateRight(parent);
                        parent = node;
                    }
                    rotateLeft(grandparent);
                }
                parent.color = BLACK;
                grandparent.color = RED;
            }
        }
        root.color = BLACK;
    }

    // 좌회전 (RR 케이스)
    private void rotateLeft(RedBlackNode node) {
        final RedBlackNode rightChild = node.right;
        node.right = rightChild.left;
        if (rightChild.left != null) {rightChild.left.parent = node;}
        rightChild.parent = node.parent;
        if (node.parent == null) {
            root = rightChild;
        } else if (node == node.parent.left) {
            node.parent.left = rightChild;
        } else {
            node.parent.right = rightChild;
        }
        rightChild.left = node;
        node.parent = rightChild;
    }

    // 우회전 (LL 케이스)
    private void rotateRight(RedBlackNode node) {
        final RedBlackNode leftChild = node.left;
        node.left = leftChild.right;
        if (leftChild.right != null) {leftChild.right.parent = node;}
        leftChild.parent = node.parent;
        if (node.parent == null) {
            root = leftChild;
        } else if (node == node.parent.right) {
            node.parent.right = leftChild;
        } else {
            node.parent.left = leftChild;
        }
        leftChild.right = node;
        node.parent = leftChild;
    }
}
