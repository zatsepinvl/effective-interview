package com.interview.revolut.generator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SeoKeywordGeneratorTest {

    private final static String ALPHABET = "abcdefghijklmnopqrstuvwABCDEFGHIJKLMNOPQRSTUVW1234567890";

    // ToDo: make test parametrized
    @Test
    void test_generate_keyword_4_length() {
        // Given
        int length = 4;
        SeoKeywordGenerator generator = new AlphanumericSeoKeywordGenerator();

        // When
        String keyword = generator.generate();

        // Then
        assertThat(keyword).hasSize(length)
                .containsAnyOf(ALPHABET.split(""));
    }

}
