package me.erikhennig.worktracks.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

class AccumulatingIterator<T> implements Iterator<T> {

    private T accumulator;
    private final BinaryOperator<T> accumulatorFunction;
    private Iterator<T> sourceStreamIterator;

    public AccumulatingIterator(Collection<T> collection, T seed, BinaryOperator<T> accumulatorFunction)
    {
        this(collection.stream(), seed, accumulatorFunction);
    }

    public AccumulatingIterator(Stream<T> stream, T seed, BinaryOperator<T> accumulatorFunction) {
       this.sourceStreamIterator = stream.iterator();
       this.accumulator = seed;
       this.accumulatorFunction = accumulatorFunction;
    }

    @Override
    public boolean hasNext() {
        return this.sourceStreamIterator.hasNext();
    }

    @Override
    public T next() {
        T nextElement = this.sourceStreamIterator.next();
        this.accumulator = this.accumulatorFunction.apply(this.accumulator, nextElement);
        return this.accumulator;
    }
}
