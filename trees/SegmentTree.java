package trees;

public class SegmentTree<T> {
    @FunctionalInterface
    public interface AddAble<T> {
        T add(T lhs, T rhs);
    }

    @FunctionalInterface
    public interface MulAble<T> {
        T mul(T value, int times);
    }

    private static final class Node<T> {
        private T tag;
        private T data;
        private Node<T> leftSon;
        private Node<T> rightSon;

        private Node(T zeroElement) {
            leftSon = rightSon = null;
            this.tag = this.data = zeroElement;
        }
    }

    private Node<T> root;
    private int leftRange;
    private int rightRange;
    private final int size;
    private final T zeroElement;
    private final AddAble<T> adder;
    private final MulAble<T> muler;

    private void updateNode(Node<T> current, int leftBound, int rightBound, T value) {
        current.data = adder.add(current.data, muler.mul(value, rightBound - leftBound + 1));
        current.tag = adder.add(current.tag, value);
    }

    private void spread(Node<T> current, int leftBound, int rightBound) {
        if (current.tag != zeroElement) {
            int mid = (leftBound + rightBound) >> 1;
            if (current.leftSon == null) {
                current.leftSon = new Node<>(zeroElement);
            }
            updateNode(current.leftSon, leftBound, mid, current.tag);
            if (current.rightSon == null) {
                current.rightSon = new Node<>(zeroElement);
            }
            updateNode(current.rightSon, mid + 1, rightBound, current.tag);
            current.tag = zeroElement;
        }
    }

    public SegmentTree(int size, T zeroElement, AddAble<T> adder, MulAble<T> muler) {
        this.root = null;
        this.size = size;
        this.adder = adder;
        this.muler = muler;
        this.zeroElement = zeroElement;
    }

    private Node<T> insert(Node<T> current, int leftBound, int rightBound, T value) {
        Node<T> node = current == null ? new Node<>(zeroElement) : current;
        node.data = adder.add(node.data, value);
        if (leftBound >= rightBound) {
            return node;
        }
        int mid = (leftBound + rightBound) >> 1;
        if (leftRange <= mid) {
            node.leftSon = insert(node.leftSon, leftBound, mid, value);
        }
        if (rightRange > mid) {
            node.rightSon = insert(node.rightSon, mid + 1, rightBound, value);
        }
        return node;
    }

    public void insert(int index, T value) {
        this.leftRange = this.rightRange = index;
        root = insert(root, 1, size, value);
    }

    private void update(Node<T> current, int leftBound, int rightBound, T value) {
        if (leftRange <= leftBound && rightBound <= rightRange) {
            updateNode(current, leftBound, rightBound, value);
            return;
        }
        spread(current, leftBound, rightBound);
        int mid = (leftBound + rightBound) >> 1;
        if (leftRange <= mid) {
            update(current.leftSon, leftBound, mid, value);
        }
        if (rightRange > mid) {
            update(current.rightSon, mid + 1, rightBound, value);
        }
        current.data = adder.add(current.leftSon.data, current.rightSon.data);
    }

    public void update(int leftRange, int rightRange, T value) {
        this.leftRange = leftRange;
        this.rightRange = rightRange;
        update(root, 1, size, value);
    }

    private T query(Node<T> current, int leftBound, int rightBound) {
        if (leftRange <= leftBound && rightBound <= rightRange) {
            return current.data;
        }
        spread(current, leftBound, rightBound);
        T result = zeroElement;
        int mid = (leftBound + rightBound) >> 1;
        if (leftRange <= mid) {
            result = adder.add(result, query(current.leftSon, leftBound, mid));
        }
        if (rightRange > mid) {
            result = adder.add(result, query(current.rightSon, mid + 1, rightBound));
        }
        return result;
    }

    public T query(int index) {
        this.leftRange = this.rightRange = index;
        return query(root, 1, size);
    }

    public T query(int leftRange, int rightRange) {
        this.leftRange = leftRange;
        this.rightRange = rightRange;
        return query(root, 1, size);
    }
}