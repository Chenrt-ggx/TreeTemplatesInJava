package trees;

import utils.BalancedTree;
import java.util.NoSuchElementException;

public class RBTree<T extends Comparable<T>> implements BalancedTree<T> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private static final class Node<T> {
        private int size;
        private int repeat;
        private final T data;
        private boolean color;

        private Node<T> father;
        private Node<T> leftSon;
        private Node<T> rightSon;

        private Node(T data, Node<T> father) {
            this.data = data;
            this.color = BLACK;
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

    private Node<T> root;

    private Node<T> parentOf(Node<T> current) {
        if (current == null) {
            return null;
        }
        return current.father;
    }

    private Node<T> leftOf(Node<T> current) {
        if (current == null) {
            return null;
        }
        return current.leftSon;
    }

    private Node<T> rightOf(Node<T> current) {
        if (current == null) {
            return null;
        }
        return current.rightSon;
    }

    private boolean getColor(Node<T> current) {
        if (current == null) {
            return BLACK;
        }
        return current.color;
    }

    private void setColor(Node<T> current, boolean color) {
        if (current != null) {
            current.color = color;
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

    public RBTree() {
        clear();
    }

    @Override
    public void clear() {
        root = null;
    }

    private void fixInsert(Node<T> current) {
        Node<T> node = current;
        node.color = RED;
        while (node != null && node != root && node.father.color == RED) {
            if (parentOf(node) == leftOf(parentOf(parentOf(node)))) {
                Node<T> uncle = rightOf(parentOf(parentOf(node)));
                if (getColor(uncle) == RED) {
                    setColor(parentOf(node), BLACK);
                    setColor(uncle, BLACK);
                    setColor(parentOf(parentOf(node)), RED);
                    node = parentOf(parentOf(node));
                }
                else {
                    if (node == rightOf(parentOf(node))) {
                        node = parentOf(node);
                        leftRotate(node);
                    }
                    setColor(parentOf(node), BLACK);
                    setColor(parentOf(parentOf(node)), RED);
                    rightRotate(parentOf(parentOf(node)));
                }
            }
            else {
                Node<T> uncle = leftOf(parentOf(parentOf(node)));
                if (getColor(uncle) == RED) {
                    setColor(parentOf(node), BLACK);
                    setColor(uncle, BLACK);
                    setColor(parentOf(parentOf(node)), RED);
                    node = parentOf(parentOf(node));
                }
                else {
                    if (node == leftOf(parentOf(node))) {
                        node = parentOf(node);
                        rightRotate(node);
                    }
                    setColor(parentOf(node), BLACK);
                    setColor(parentOf(parentOf(node)), RED);
                    leftRotate(parentOf(parentOf(node)));
                }
            }
        }
        root.color = BLACK;
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
        fixInsert(current);
    }

    private void fixDelete(Node<T> current) {
        Node<T> node = current;
        while (node != null && node != root && getColor(node) == BLACK) {
            if (node == leftOf(parentOf(node))) {
                Node<T> cousin = rightOf(parentOf(node));
                if (getColor(cousin) == RED) {
                    setColor(parentOf(node), RED);
                    setColor(cousin, BLACK);
                    leftRotate(parentOf(node));
                    cousin = rightOf(parentOf(node));
                }
                if (getColor(leftOf(cousin))  == BLACK && getColor(rightOf(cousin)) == BLACK) {
                    setColor(cousin, RED);
                    node = parentOf(node);
                }
                else {
                    if (getColor(rightOf(cousin)) == BLACK) {
                        setColor(leftOf(cousin), BLACK);
                        setColor(cousin, RED);
                        rightRotate(cousin);
                        cousin = rightOf(parentOf(node));
                    }
                    setColor(cousin, getColor(parentOf(node)));
                    setColor(parentOf(node), BLACK);
                    setColor(rightOf(cousin), BLACK);
                    leftRotate(parentOf(node));
                    node = root;
                }
            }
            else {
                Node<T> cousin = leftOf(parentOf(node));
                if (getColor(cousin) == RED) {
                    setColor(parentOf(node), RED);
                    setColor(cousin, BLACK);
                    rightRotate(parentOf(node));
                    cousin = leftOf(parentOf(node));
                }
                if (getColor(rightOf(cousin)) == BLACK && getColor(leftOf(cousin)) == BLACK) {
                    setColor(cousin, RED);
                    node = parentOf(node);
                }
                else {
                    if (getColor(leftOf(cousin)) == BLACK) {
                        setColor(rightOf(cousin), BLACK);
                        setColor(cousin, RED);
                        leftRotate(cousin);
                        cousin = leftOf(parentOf(node));
                    }
                    setColor(cousin, getColor(parentOf(node)));
                    setColor(parentOf(node), BLACK);
                    setColor(leftOf(cousin), BLACK);
                    rightRotate(parentOf(node));
                    node = root;
                }
            }
        }
        setColor(node, BLACK);
    }

    @Override
    public void delete(T data) {
        Node<T> father = null;
        Node<T> current = root;
        while (current != null) {
            father = current;
            father.size--;
            if (data.compareTo(current.data) < 0) {
                current = current.leftSon;
            }
            else if (data.compareTo(current.data) > 0) {
                current = current.rightSon;
            }
            else {
                if (current.repeat > 1) {
                    current.repeat--;
                    return;
                }
                break;
            }
        }
        if (current == null) {
            while (father != null) {
                father.size++;
                father = father.father;
            }
        }
        else if (current.leftSon == null) {
            relink(current, current.rightSon);
            if (current.color == BLACK) {
                fixDelete(current.rightSon);
            }
        }
        else if (current.rightSon == null) {
            relink(current, current.leftSon);
            if (current.color == BLACK) {
                fixDelete(current.leftSon);
            }
        }
        else {
            Node<T> node = getMinNode(current.rightSon);
            Node<T> fixNode = node.rightSon;
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
            boolean color = node.color;
            node.color = current.color;
            node.pushUp();
            if (color == BLACK) {
                fixDelete(fixNode);
            }
        }
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