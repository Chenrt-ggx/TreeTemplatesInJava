package trees;

import java.util.Arrays;

public class BITree<T> {
    @FunctionalInterface
    public interface AddAble<T> {
        T add(T lhs, T rhs);
    }

    private final T zeroElement;
    private final Object[] tree;
    private final AddAble<T> adder;

    public BITree(int size, T zeroElement, AddAble<T> adder) {
        this.adder = adder;
        tree = new Object[size];
        this.zeroElement = zeroElement;
        Arrays.fill(tree, zeroElement);
    }

    public void update(int index, T value) {
        assert index > 0;
        for (int i = index; i <= tree.length; i += i & -i) {
            @SuppressWarnings("unchecked")
            T cast = (T)tree[i - 1];
            tree[i - 1] = adder.add(cast, value);
        }
    }

    public T query(int index) {
        assert index <= tree.length;
        T result = zeroElement;
        for (int i = index; i > 0; i -= i & -i) {
            @SuppressWarnings("unchecked")
            T cast = (T)tree[i - 1];
            result = adder.add(result, cast);
        }
        return result;
    }
}