package io.github.a5h73y.parkour.other;

import java.util.Objects;

@FunctionalInterface
public interface TriConsumer<A, B, C> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param a the first input argument
     * @param b the second input argument
     * @param c the second input argument
     */
    void accept(A a, B b, C c);

    /**
     * And then, and then, and then.
     * @param after after
     * @return triconsumer
     */
    default TriConsumer<A, B, C> andThen(TriConsumer<? super A, ? super B, ? super C> after) {
        Objects.requireNonNull(after);

        return (a, b, c) -> {
            accept(a, b, c);
            after.accept(a, b, c);
        };
    }
}
