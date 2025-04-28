package me.dwywdo.trees.avl;

public class AVLNode {
    public int key;
    public int height;
    public AVLNode left, right;

    AVLNode(int key) {
        this.key = key;
        height = 1;  // Default height of a new node: 1
    }
}
