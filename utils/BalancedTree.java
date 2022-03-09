package utils;

import java.util.NoSuchElementException;

public interface BalancedTree<T extends Comparable<T>> {
    void clear();

    void insert(T data);

    void delete(T data);

    int rank(T data);

    T select(int rank) throws NoSuchElementException;

    T predecessor(T data) throws NoSuchElementException;

    T successor(T data) throws NoSuchElementException;
}