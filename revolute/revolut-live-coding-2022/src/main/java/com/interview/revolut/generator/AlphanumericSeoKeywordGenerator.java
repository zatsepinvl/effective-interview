package com.interview.revolut.generator;

import java.util.Random;

public class AlphanumericSeoKeywordGenerator implements SeoKeywordGenerator {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwABCDEFGHIJKLMNOPQRSTUVW1234567890";
    private static final int KEYWORD_LENGTH = 4;

    private final Random random = new Random();

    @Override
    public String generate() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < KEYWORD_LENGTH; i++) {
            int charIndex = random.nextInt(ALPHABET.length());
            char letter = ALPHABET.charAt(charIndex);
            builder.append(letter);
        }
        return builder.toString();
    }
}
