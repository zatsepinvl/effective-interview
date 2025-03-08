package com.interview.revolut;

import com.interview.revolut.generator.SeoKeywordGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UrlShortenerTest {

    public static final URI TEST_INPUT_URI = URI.create("http://looooong.com/somepath");
    public static final String EXPECTED_BASE_PATH = "http://short.com/";

    @Mock
    private SeoKeywordGenerator generator;
    private UrlShortener shortener;

    @BeforeEach
    void setUp() {
        shortener = new UrlShortener(generator);
    }

    @Test
    void test_positive_case() {
        // Given
        String seoWord = "test";

        // When
        URI output = shortener.shorten(TEST_INPUT_URI, seoWord);

        // Then
        assertThat(output).isEqualTo(URI.create(EXPECTED_BASE_PATH + seoWord));
    }

    @Test
    void test_negative_case_seo_word_length() {
        // Given
        String seoWord = "123456789123456789012";

        // Then
        assertThatThrownBy(() -> shortener.shorten(TEST_INPUT_URI, seoWord))
                .hasMessageContaining("Seo word")
                .hasMessageContaining("length")
                .hasMessageContaining("20");
    }

    @Test
    void test_random_url() {
        // Given
        String randomKeyword = "test";
        given(generator.generate()).willReturn(randomKeyword);

        // Then
        URI output = shortener.shortenWithRandomSeoKeyword(TEST_INPUT_URI);

        // Then
        assertThat(output).isEqualTo(URI.create(EXPECTED_BASE_PATH + randomKeyword));
    }
}
