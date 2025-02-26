package com.ravingarinc.api;

import java.util.Objects;

public class Pair<L, R> {

    private final L left;
    private final R right;

    public Pair(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return this.left;
    }

    public R getRight() {
        return this.right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Pair<?, ?>) {
            final var pair = (Pair<?, ?>) obj;
            final var left = pair.getLeft();
            final var right = pair.getRight();
            if (this.left.getClass().isInstance(left) && this.right.getClass().isInstance(right)) {
                return left.equals(this.left) && right.equals(this.right);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
