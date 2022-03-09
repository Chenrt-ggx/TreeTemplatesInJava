package trees;

import utils.Pair;
import utils.BalancedTree;
import java.util.NoSuchElementException;

public class Splay<T extends Comparable<T>> implements BalancedTree<T> {
    private static final class Node<T> {
        private int size;
        private int repeat;
        private final T data;

        private Node<T> father;
        private Node<T> leftSon;
        private Node<T> rightSon;

        private Node(T data, Node<T> father) {
            this.data = data;
            this.father = father;
            this.size = this.repeat = 1;
            this.leftSon = this.rightSon = null;
        }

        private int countSmaller() {
            if (leftSon == null) {
                return 0;
            }
            return leftSon.size;
        }

        private void pushUp() {
            size = repeat;
            if (leftSon != null) {
                size += leftSon.size;
            }
            if (rightSon != null) {
                size += rightSon.size;
            }
        }
    }

    private Pair<Node<T>, Node<T>> getNode(Node<T> current, T data) {
        Node<T> father = null;
        Node<T> node = current;
        while (node != null) {
            father = node;
            if (data.compareTo(node.data) < 0) {
                node = node.leftSon;
            }
            else if (data.compareTo(node.data) > 0) {
                node = node.rightSon;
            }
            else {
                return new Pair<>(father, node);
            }
        }
        return new Pair<>(father, null);
    }

    private Node<T> getMinNode(Node<T> current) throws NoSuchElementException {
        Node<T> now = current;
        if (now == null) {
            throw new NoSuchElementException();
        }
        while (now.leftSon != null) {
            now = now.leftSon;
        }
        return now;
    }

    private Node<T> getMaxNode(Node<T> current) throws NoSuchElementException {
        Node<T> now = current;
        if (now == null) {
            throw new NoSuchElementException();
        }
        while (now.rightSon != null) {
            now = now.rightSon;
        }
        return now;
    }

    private void relink(Node<T> current, Node<T> relink) {
        if (relink != null) {
            relink.father = current.father;
        }
        if (current.father == null) {
            root = relink;
        }
        else if (current == current.father.leftSon) {
            current.father.leftSon = relink;
        }
        else {
            current.father.rightSon = relink;
        }
    }

    private void leftRotate(Node<T> current) {
        if (current != null) {
            Node<T> node = current.rightSon;
            current.rightSon = node.leftSon;
            if (node.leftSon != null) {
                node.leftSon.father = current;
            }
            relink(current, node);
            node.leftSon = current;
            current.father = node;
            current.pushUp();
            node.pushUp();
        }
    }

    private void rightRotate(Node<T> current) {
        if (current != null) {
            Node<T> node = current.leftSon;
            current.leftSon = node.rightSon;
            if (node.rightSon != null) {
                node.rightSon.father = current;
            }
            relink(current, node);
            node.rightSon = current;
            current.father = node;
            current.pushUp();
            node.pushUp();
        }
    }

    private void splay(Node<T> src, Node<T> dst) {
        while (src != null && src.father != null && src.father != dst) {
            if (src.father.father != dst) {
                if (src.father == src.father.father.leftSon && src == src.father.leftSon) {
                    rightRotate(src.father.father);
                }
                else if (src.father == src.father.father.leftSon && src == src.father.rightSon) {
                    leftRotate(src.father);
                }
                else if (src.father == src.father.father.rightSon && src == src.father.leftSon) {
                    rightRotate(src.father);
                }
                else if (src.father == src.father.father.rightSon && src == src.father.rightSon) {
                    leftRotate(src.father.father);
                }
            }
            if (src == src.father.leftSon) {
                rightRotate(src.father);
            }
            else {
                leftRotate(src.father);
            }
        }
        if (dst == null) {
            root = src;
        }
    }

    private Node<T> root;

    public Splay() {
        clear();
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public void insert(T data) {
        Node<T> father = null;
        Node<T> current = root;
        while (current != null) {
            father = current;
            father.size++;
            if (data.compareTo(current.data) < 0) {
                current = current.leftSon;
            }
            else if (data.compareTo(current.data) > 0) {
                current = current.rightSon;
            }
            else {
                splay(current, root.father);
                current.repeat++;
                return;
            }
        }
        current = new Node<>(data, father);
        if (father == null) {
            root = current;
        }
        else if (data.compareTo(father.data) < 0) {
            father.leftSon = current;
        }
        else {
            father.rightSon = current;
        }
        splay(current, root.father);
    }

    @Override
    public void delete(T data) {
        Pair<Node<T>, Node<T>> pair = getNode(root, data);
        if (pair.getValue() == null) {
            splay(pair.getKey(), root.father);
        }
        else {
            splay(pair.getValue(), root.father);
            if (root.repeat > 1) {
                root.repeat--;
                return;
            }
        }
        if (root.leftSon == null) {
            relink(root, root.rightSon);
        }
        else if (root.rightSon == null) {
            relink(root, root.leftSon);
        }
        else {
            Node<T> current = root;
            Node<T> node = getMinNode(current.rightSon);
            if (node.father != current) {
                for (Node<T> i = node; i != current; i = i.father) {
                    i.size -= node.repeat;
                }
                relink(node, node.rightSon);
                node.rightSon = current.rightSon;
                node.rightSon.father = node;
            }
            relink(current, node);
            node.leftSon = current.leftSon;
            node.leftSon.father = node;
            node.pushUp();
        }
    }

    @Override
    public int rank(T data) {
        Pair<Node<T>, Node<T>> pair = getNode(root, data);
        if (pair.getValue() != null) {
            splay(pair.getValue(), root.father);
            return root.countSmaller() + 1;
        }
        else if (pair.getKey() != null) {
            splay(pair.getKey(), root.father);
            if (data.compareTo(root.data) > 0) {
                return root.countSmaller() + root.repeat + 1;
            }
            return root.countSmaller() + 1;
        }
        else {
            return 1;
        }
    }

    @Override
    public T select(int rank) throws NoSuchElementException {
        int value = rank;
        Node<T> father = null;
        Node<T> current = root;
        while (current != null) {
            father = current;
            if (current.countSmaller() + 1 <= value &&
                    value <= current.countSmaller() + current.repeat) {
                splay(current, root.father);
                return current.data;
            }
            else if (value < current.countSmaller() + 1) {
                current = current.leftSon;
            }
            else {
                value -= current.countSmaller() + current.repeat;
                current = current.rightSon;
            }
        }
        if (father != null) {
            splay(father, root.father);
        }
        throw new NoSuchElementException();
    }

    @Override
    public T predecessor(T data) throws NoSuchElementException {
        Pair<Node<T>, Node<T>> pair = getNode(root, data);
        if (pair.getValue() != null) {
            splay(pair.getValue(), root.father);
            return getMaxNode(root.leftSon).data;
        }
        else if (pair.getKey() != null) {
            splay(pair.getKey(), root.father);
            if (data.compareTo(root.data) > 0) {
                return root.data;
            }
            return getMaxNode(root.leftSon).data;
        }
        else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public T successor(T data) throws NoSuchElementException {
        Pair<Node<T>, Node<T>> pair = getNode(root, data);
        if (pair.getValue() != null) {
            splay(pair.getValue(), root.father);
            return getMinNode(root.rightSon).data;
        }
        else if (pair.getKey() != null) {
            splay(pair.getKey(), root.father);
            if (data.compareTo(root.data) < 0) {
                return root.data;
            }
            return getMinNode(root.rightSon).data;
        }
        else {
            throw new NoSuchElementException();
        }
    }
}