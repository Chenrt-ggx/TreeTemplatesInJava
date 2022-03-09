package trees;

import utils.Pair;
import utils.BalancedTree;
import java.util.NoSuchElementException;

public class FHQTreap<T extends Comparable<T>> implements BalancedTree<T> {
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

    private Pair<Node<T>, Node<T>> splitByKey(Node<T> current, T data, boolean close) {
        if (current == null) {
            return new Pair<>(null, null);
        }
        else if ((!close && data.compareTo(current.data) <= 0)
                || (close && data.compareTo(current.data) < 0)) {
            Pair<Node<T>, Node<T>> pair = splitByKey(current.leftSon, data, close);
            current.leftSon = pair.getValue();
            current.pushUp();
            return new Pair<>(pair.getKey(), current);
        }
        else {
            Pair<Node<T>, Node<T>> pair = splitByKey(current.rightSon, data, close);
            current.rightSon = pair.getKey();
            current.pushUp();
            return new Pair<>(current, pair.getValue());
        }
    }

    private Pair<Node<T>, Node<T>> splitBySize(Node<T> current, int size) {
        if (current == null) {
            return new Pair<>(null, null);
        }
        else if (size <= 0) {
            return new Pair<>(null, current);
        }
        else if (size >= current.size) {
            return new Pair<>(current, null);
        }
        else if (size <= current.countSmaller()) {
            Pair<Node<T>, Node<T>> pair = splitBySize(current.leftSon, size);
            current.leftSon = pair.getValue();
            current.pushUp();
            return new Pair<>(pair.getKey(), current);
        }
        else {
            Pair<Node<T>, Node<T>> pair = splitBySize(current.rightSon
                    , size - current.countSmaller() - current.repeat);
            current.rightSon = pair.getKey();
            current.pushUp();
            return new Pair<>(current, pair.getValue());
        }
    }

    private Node<T> merge(Node<T> leftRoot, Node<T> rightRoot) {
        if (leftRoot == null) {
            return rightRoot;
        }
        if (rightRoot == null) {
            return leftRoot;
        }
        if (leftRoot.index < rightRoot.index) {
            leftRoot.rightSon = merge(leftRoot.rightSon, rightRoot);
            leftRoot.pushUp();
            return leftRoot;
        }
        else {
            rightRoot.leftSon = merge(leftRoot, rightRoot.leftSon);
            rightRoot.pushUp();
            return rightRoot;
        }
    }

    private Node<T> root;

    public FHQTreap() {
        clear();
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public void insert(T data) {
        Pair<Node<T>, Node<T>> left = splitByKey(root, data, false);
        Pair<Node<T>, Node<T>> right = splitByKey(left.getValue(), data, true);
        if (right.getKey() != null) {
            right.getKey().enlarge();
            root = merge(left.getKey(), merge(right.getKey(), right.getValue()));
        }
        else {
            root = merge(left.getKey(), merge(new Node<>(data), right.getValue()));
        }
    }

    @Override
    public void delete(T data) {
        Pair<Node<T>, Node<T>> left = splitByKey(root, data, false);
        Pair<Node<T>, Node<T>> right = splitByKey(left.getValue(), data, true);
        if (right.getKey() != null && right.getKey().repeat > 1) {
            right.getKey().reduce();
            root = merge(left.getKey(), merge(right.getKey(), right.getValue()));
        }
        else {
            root = merge(left.getKey(), right.getValue());
        }
    }

    @Override
    public int rank(T data) {
        Pair<Node<T>, Node<T>> pair = splitByKey(root, data, false);
        int rank = pair.getKey() == null ? 1 : pair.getKey().size + 1;
        root = merge(pair.getKey(), pair.getValue());
        return rank;
    }

    @Override
    public T select(int rank) throws NoSuchElementException {
        Pair<Node<T>, Node<T>> pair = splitBySize(root, rank);
        if (pair.getKey() == null) {
            root = merge(pair.getKey(), pair.getValue());
            throw new NoSuchElementException();
        }
        else {
            T data = getMaxNode(pair.getKey()).data;
            root = merge(pair.getKey(), pair.getValue());
            return data;
        }
    }

    @Override
    public T predecessor(T data) throws NoSuchElementException {
        Pair<Node<T>, Node<T>> pair = splitByKey(root, data, false);
        if (pair.getKey() == null) {
            root = merge(pair.getKey(), pair.getValue());
            throw new NoSuchElementException();
        }
        else {
            T predecessor = getMaxNode(pair.getKey()).data;
            root = merge(pair.getKey(), pair.getValue());
            return predecessor;
        }
    }

    @Override
    public T successor(T data) throws NoSuchElementException {
        Pair<Node<T>, Node<T>> pair = splitByKey(root, data, true);
        if (pair.getValue() == null) {
            root = merge(pair.getKey(), pair.getValue());
            throw new NoSuchElementException();
        }
        else {
            T successor = getMinNode(pair.getValue()).data;
            root = merge(pair.getKey(), pair.getValue());
            return successor;
        }
    }
}