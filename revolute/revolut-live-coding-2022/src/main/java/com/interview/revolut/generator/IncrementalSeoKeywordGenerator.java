package com.interview.revolut.generator;

import java.util.concurrent.atomic.AtomicLong;

public class IncrementalSeoKeywordGenerator implements SeoKeywordGenerator {
    private final AtomicLong counter = new AtomicLong();

    private final long incrementLimit;

    public IncrementalSeoKeywordGenerator(long incrementLimit) {
        this.incrementLimit = incrementLimit;
    }

    @Override
    public String generate() {
        if (counter.get() == incrementLimit || counter.get() < 0) {
            throw new IllegalStateException("Incremental limit has been reached.");
        }
        long nextValue = counter.incrementAndGet();
        return String.valueOf(nextValue);
    }
}
