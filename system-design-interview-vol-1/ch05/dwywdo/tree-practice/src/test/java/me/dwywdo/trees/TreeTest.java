package me.dwywdo.trees;

import org.junit.jupiter.api.Test;

import me.dwywdo.trees.avl.AVLTree;
import me.dwywdo.trees.avl.rbt.RedBlackTree;
import me.dwywdo.trees.bst.BinarySearchTree;

public class TreeTest {

    @Test
    void testBinarySearchTree() {
        final BinarySearchTree bst = new BinarySearchTree();

        bst.insert(50);
        bst.insert(30);
        bst.insert(20);
        bst.insert(40);
        bst.insert(70);
        bst.insert(60);
        bst.insert(80);

        System.out.println("중위 순회: ");
        bst.inorderTraversal(); // 20 30 40 50 60 70 80

        System.out.println("40 삭제");
        bst.delete(30);
        bst.inorderTraversal(); // 20 30 50 60 70 80
    }


    @Test
    void testAVLTree() {
        final AVLTree tree = new AVLTree();

        // 삽입 테스트
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40);
        tree.insert(50);
        tree.insert(25);

        // 삭제 테스트
        tree.delete(40);
        tree.delete(50);

        tree.inorder();
    }

    @Test
    void testRedBlackTree() {
        final RedBlackTree tree = new RedBlackTree();
        tree.insert(8);
        tree.insert(18);
        tree.insert(5);
        tree.insert(15);
        tree.insert(17);
        tree.insert(25);
        // 최종 트리 구조:
        //       17(B)
        //     /     \
        //    8(R)   25(B)
        //   /  \      /
        // 5(B)15(B) 18(R)
    }

}
