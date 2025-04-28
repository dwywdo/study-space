package me.dwywdo.trees.avl;

public class AVLTree {
    private AVLNode root;

    // 트리 높이 계산
    private int height(AVLNode AVLNode) {
        return AVLNode != null ? AVLNode.height : -1;
    }

    // 균형 계수 계산
    private int getBalance(AVLNode AVLNode) {
        return height(AVLNode.left) - height(AVLNode.right);
    }

    // 오른쪽 회전 (LL Case)
    private AVLNode rightRotate(AVLNode y) {
        final AVLNode x = y.left;
        final AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    // 왼쪽 회전 (RR Case)
    private AVLNode leftRotate(AVLNode x) {
        final AVLNode y = x.right;
        final AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    public void insert(int key) {
        root = recursiveInsert(root, key);
    }

    private AVLNode recursiveInsert(AVLNode AVLNode, int key) {
        if (AVLNode == null) {return new AVLNode(key);}

        // 1. BST 삽입
        if (key < AVLNode.key) {
            AVLNode.left = recursiveInsert(AVLNode.left, key);
        } else if (key > AVLNode.key) {
            AVLNode.right = recursiveInsert(AVLNode.right, key);
        } else {
            return AVLNode; // 중복 허용 안 함
        }

        // 2. 높이 갱신
        AVLNode.height = 1 + Math.max(height(AVLNode.left), height(AVLNode.right));

        // 3. 균형 조정
        final int balance = getBalance(AVLNode);

        // LL Case
        if (balance > 1 && key < AVLNode.left.key) {return rightRotate(AVLNode);}

        // RR Case
        if (balance < -1 && key > AVLNode.right.key) {return leftRotate(AVLNode);}

        // LR Case
        if (balance > 1 && key > AVLNode.left.key) {
            AVLNode.left = leftRotate(AVLNode.left);
            return rightRotate(AVLNode);
        }

        // RL Case
        if (balance < -1 && key < AVLNode.right.key) {
            AVLNode.right = rightRotate(AVLNode.right);
            return leftRotate(AVLNode);
        }

        return AVLNode;
    }

    public void delete(int key) {
        root = deleteNode(root, key);
    }

    private AVLNode deleteNode(AVLNode root, int key) {
        if (root == null) {return null;}

        // 1. BST 삭제
        if (key < root.key) {root.left = deleteNode(root.left, key);} else if (key > root.key) {
            root.right = deleteNode(root.right, key);
        } else {
            // 단일/0개 자식 처리
            if (root.left == null || root.right == null) {
                AVLNode temp = (root.left != null) ? root.left : root.right;
                if (temp == null) {
                    temp = root;
                    root = null;
                } else {root = temp;}
            } else {
                // 두 자식: 후속자 찾기
                final AVLNode temp = minValueNode(root.right);
                root.key = temp.key;
                root.right = deleteNode(root.right, temp.key);
            }
        }

        if (root == null) {return null;}

        // 2. 높이 갱신
        root.height = 1 + Math.max(height(root.left), height(root.right));

        // 3. 균형 조정
        final int balance = getBalance(root);

        // LL/LR Case
        if (balance > 1) {
            if (getBalance(root.left) >= 0) {return rightRotate(root);} else {
                root.left = leftRotate(root.left);
                return rightRotate(root);
            }
        }

        // RR/RL Case
        if (balance < -1) {
            if (getBalance(root.right) <= 0) {return leftRotate(root);} else {
                root.right = rightRotate(root.right);
                return leftRotate(root);
            }
        }

        return root;
    }

    // 최소값 노드 찾기
    private AVLNode minValueNode(AVLNode AVLNode) {
        AVLNode current = AVLNode;
        while (current.left != null) {current = current.left;}
        return current;
    }

    // 중위 순회 (검증용)
    public void inorder() {
        inorder(root);
        System.out.println();
    }

    private void inorder(AVLNode AVLNode) {
        if (AVLNode != null) {
            inorder(AVLNode.left);
            System.out.print(AVLNode.key + " ");
            inorder(AVLNode.right);
        }
    }
}
