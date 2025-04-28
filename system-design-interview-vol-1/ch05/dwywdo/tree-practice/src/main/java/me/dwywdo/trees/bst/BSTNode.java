package me.dwywdo.trees.bst;

import javax.annotation.Nullable;

public class BSTNode {
    int key;

    @Nullable
    BSTNode left;

    @Nullable
    BSTNode right;

    public BSTNode(int item) {
        key = item;
        left = null;
        right = null;
    }
}
