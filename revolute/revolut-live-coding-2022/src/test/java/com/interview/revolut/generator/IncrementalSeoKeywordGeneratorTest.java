package com.interview.revolut.generator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IncrementalSeoKeywordGeneratorTest {

    @Test
    void test_first_generate() {
        // Given
        IncrementalSeoKeywordGenerator generator = new IncrementalSeoKeywordGenerator(10);

        // When
        String keyword = generator.generate();

        // Then
        assertThat(keyword).isEqualTo("1");
    }

    @Test
    void test_sequential_generate() {
        // Given
        IncrementalSeoKeywordGenerator generator = new IncrementalSeoKeywordGenerator(10);

        // When
        String keyword1 = generator.generate();
        String keyword2 = generator.generate();
        String keyword3 = generator.generate();

        // Then
        assertThat(keyword1).isEqualTo("1");
        assertThat(keyword2).isEqualTo("2");
        assertThat(keyword3).isEqualTo("3");
    }

    @Test
    void test_reach_the_limit_1() {
        // Given
        IncrementalSeoKeywordGenerator generator = new IncrementalSeoKeywordGenerator(1);

        // When
        generator.generate();

        // Then
        assertThatThrownBy(generator::generate)
                .hasMessageContaining("Incremental limit")
                .hasMessageContaining("reached");
    }

    @Test
    void test_reach_the_limit_max_long() {
        // Given
        IncrementalSeoKeywordGenerator generator = new IncrementalSeoKeywordGenerator(Long.MAX_VALUE);

        // When
        for (long i = 0; i < Long.MAX_VALUE; i++) {
            generator.generate();
        }

        // Then
        assertThatThrownBy(generator::generate)
                .hasMessageContaining("Incremental limit")
                .hasMessageContaining("reached");
    }
}
