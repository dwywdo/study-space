package me.dwywdo.trees.avl.rbt;

public class RedBlackNode {
    int key;
    RedBlackNode left, right, parent;
    boolean color; // RED: false, BLACK: true

    public RedBlackNode(int key) {
        this.key = key;
        this.color = false;
    }
}
