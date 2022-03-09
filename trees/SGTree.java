package trees;

import utils.BalancedTree;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class SGTree<T extends Comparable<T>> implements BalancedTree<T> {
    private static final double ALPHA = 0.8;
    private static final double BETA = 0.6;

    private static final class Node<T> {
        private int size;
        private int real;
        private int unique;
        private int repeat;
        private final T data;

        private Node<T> leftSon;
        private Node<T> rightSon;

        private Node(T data) {
            this.data = data;
            this.leftSon = this.rightSon = null;
            this.size = this.real = this.unique = this.repeat = 1;
        }

        private void enlarge() {
            size++;
            unique++;
            repeat++;
        }

        private void reduce() {
            if (repeat > 0) {
                size--;
                unique--;
                repeat--;
            }
        }

        private int countSmaller() {
            if (leftSon == null) {
                return 0;
            }
            return leftSon.size;
        }

        private boolean unbalance() {
            return (leftSon != null && leftSon.real > (int)(real * ALPHA) + 10)
                    || (rightSon != null && rightSon.real > (int)(real * ALPHA) + 10)
                    || (unique < (int)(real * BETA) - 10);
        }

        private void pushUp() {
            real = 1;
            size = repeat;
            unique = repeat == 0 ? 0 : 1;
            if (leftSon != null) {
                size += leftSon.size;
                real += leftSon.real;
                unique += leftSon.unique;
            }
            if (rightSon != null) {
                size += rightSon.size;
                real += rightSon.real;
                unique += rightSon.unique;
            }
        }
    }

    private Node<T> root;
    private Node<T> trace;
    private Node<T> rebuild;

    private void serialize(Node<T> current, ArrayList<Node<T>> buffer) {
        if (current == null) {
            return;
        }
        serialize(current.leftSon, buffer);
        if (current.repeat > 0) {
            buffer.add(current);
        }
        serialize(current.rightSon, buffer);
    }

    private Node<T> unSerialize(int left, int right, ArrayList<Node<T>> buffer) {
        if (left >= right) {
            return null;
        }
        int mid = (left + right) >> 1;
        Node<T> result = buffer.get(mid);
        result.leftSon = unSerialize(left, mid, buffer);
        result.rightSon = unSerialize(mid + 1, right, buffer);
        result.pushUp();
        return result;
    }

    private void rebuild() {
        ArrayList<Node<T>> buffer = new ArrayList<>(rebuild.size);
        serialize(rebuild, buffer);
        if (trace == null) {
            root = unSerialize(0, buffer.size(), buffer);
        }
        else if (rebuild == trace.leftSon) {
            trace.leftSon = unSerialize(0, buffer.size(), buffer);
        }
        else {
            trace.rightSon = unSerialize(0, buffer.size(), buffer);
        }
        trace = rebuild = null;
        buffer.clear();
    }

    private int count(T data, boolean close) {
        int rank = 0;
        Node<T> current = root;
        while (current != null) {
            if (data.compareTo(current.data) == 0) {
                rank += current.countSmaller() + (close ? current.repeat : 0);
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

    private Node<T> modify(Node<T> current, Node<T> father, T data, boolean insert) {
        if (current == null) {
            return insert ? new Node<>(data) : null;
        }
        else if (data.compareTo(current.data) < 0) {
            current.leftSon = modify(current.leftSon, current, data, insert);
            current.pushUp();
        }
        else if (data.compareTo(current.data) > 0) {
            current.rightSon = modify(current.rightSon, current, data, insert);
            current.pushUp();
        }
        else if (insert) {
            current.enlarge();
        }
        else {
            current.reduce();
        }
        if (current.unbalance()) {
            trace = father;
            rebuild = current;
        }
        return current;
    }

    public SGTree() {
        clear();
    }

    @Override
    public void clear() {
        root = trace = rebuild = null;
    }

    @Override
    public void insert(T data) {
        root = modify(root, null, data, true);
        if (rebuild != null) {
            rebuild();
        }
    }

    @Override
    public void delete(T data) {
        root = modify(root, null, data, false);
        if (rebuild != null) {
            rebuild();
        }
    }

    @Override
    public int rank(T data) {
        return count(data, false) + 1;
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
        return select(count(data, false));
    }

    @Override
    public T successor(T data) throws NoSuchElementException {
        return select(count(data, true) + 1);
    }
}