package trees;

import utils.BalancedTree;
import java.util.NoSuchElementException;

public class Treap<T extends Comparable<T>> implements BalancedTree<T> {
    private static final class Node<T> {
        private int size;
        private int repeat;
        private final T data;
        private final double index;

        private Node<T> leftSon;
        private Node<T> rightSon;

        private Node(T data) {
            this.data = data;
            this.index = Math.random();
            this.size = this.repeat = 1;
            this.leftSon = this.rightSon = null;
        }

        private void enlarge() {
            size++;
            repeat++;
        }

        private void reduce() {
            size--;
            repeat--;
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

    private Node<T> root;

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

    private Node<T> leftRotate(Node<T> current) {
        if (current == null) {
            return null;
        }
        Node<T> node = current.rightSon;
        current.rightSon = node.leftSon;
        node.leftSon = current;
        current.pushUp();
        node.pushUp();
        return node;
    }

    private Node<T> rightRotate(Node<T> current) {
        if (current == null) {
            return null;
        }
        Node<T> node = current.leftSon;
        current.leftSon = node.rightSon;
        node.rightSon = current;
        current.pushUp();
        node.pushUp();
        return node;
    }

    private Node<T> maintain(Node<T> current) {
        if (current.leftSon != null && current.leftSon.index > current.index) {
            return rightRotate(current);
        }
        if (current.rightSon != null && current.rightSon.index > current.index) {
            return leftRotate(current);
        }
        return current;
    }

    public Treap() {
        clear();
    }

    @Override
    public void clear() {
        root = null;
    }

    private Node<T> insert(Node<T> current, T data) {
        if (current == null) {
            return new Node<>(data);
        }
        else if (data.compareTo(current.data) < 0) {
            current.leftSon = insert(current.leftSon, data);
            current.pushUp();
            return maintain(current);
        }
        else if (data.compareTo(current.data) > 0) {
            current.rightSon = insert(current.rightSon, data);
            current.pushUp();
            return maintain(current);
        }
        else {
            current.enlarge();
            return current;
        }
    }

    @Override
    public void insert(T data) {
        root = insert(root, data);
    }

    private Node<T> delete(Node<T> current, T data) {
        if (current == null) {
            return null;
        }
        else if (data.compareTo(current.data) < 0) {
            current.leftSon = delete(current.leftSon, data);
            current.pushUp();
            return current;
        }
        else if (data.compareTo(current.data) > 0) {
            current.rightSon = delete(current.rightSon, data);
            current.pushUp();
            return current;
        }
        else if (current.repeat > 1) {
            current.reduce();
            return current;
        }
        else if (current.leftSon == null) {
            return current.rightSon;
        }
        else if (current.rightSon == null) {
            return current.leftSon;
        }
        else if (current.leftSon.index > current.rightSon.index) {
            Node<T> node = rightRotate(current);
            node.rightSon = delete(node.rightSon, data);
            node.pushUp();
            return node;
        }
        else {
            Node<T> node = leftRotate(current);
            node.leftSon = delete(node.leftSon, data);
            node.pushUp();
            return node;
        }
    }

    @Override
    public void delete(T data) {
        root = delete(root, data);
    }

    @Override
    public int rank(T data) {
        int rank = 1;
        Node<T> current = root;
        while (current != null) {
            if (data.compareTo(current.data) == 0) {
                rank += current.countSmaller();
                break;
            }
            else if (data.compareTo(current.data) < 0) {
                current = current.leftSon;
            }
            else {
                rank += current.countSmaller() + current.repeat;
                current = current.rightSon;
            }
        }
        return rank;
    }

    @Override
    public T select(int rank) throws NoSuchElementException {
        int value = rank;
        Node<T> current = root;
        while (current != null) {
            if (current.countSmaller() + 1 <= value &&
                    value <= current.countSmaller() + current.repeat) {
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
        throw new NoSuchElementException();
    }

    @Override
    public T predecessor(T data) throws NoSuchElementException {
        Node<T> current = root;
        Node<T> node = getMinNode(root);
        if (data.compareTo(node.data) <= 0) {
            throw new NoSuchElementException();
        }
        while (current != null) {
            if (data.compareTo(current.data) > 0) {
                if (node.data.compareTo(current.data) < 0) {
                    node = current;
                }
                current = current.rightSon;
            }
            else {
                current = current.leftSon;
            }
        }
        return node.data;
    }

    @Override
    public T successor(T data) throws NoSuchElementException {
        Node<T> current = root;
        Node<T> node = getMaxNode(root);
        if (data.compareTo(node.data) >= 0) {
            throw new NoSuchElementException();
        }
        while (current != null) {
            if (data.compareTo(current.data) < 0) {
                if (node.data.compareTo(current.data) > 0) {
                    node = current;
                }
                current = current.leftSon;
            }
            else {
                current = current.rightSon;
            }
        }
        return node.data;
    }
}