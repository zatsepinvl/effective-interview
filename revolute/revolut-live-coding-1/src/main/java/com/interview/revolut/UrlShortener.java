package com.interview.revolut;

import com.interview.revolut.generator.SeoKeywordGenerator;

import java.net.URI;

public class UrlShortener {

    public static final String BASE_PATH = "http://short.com/";
    public static final int MAX_SEO_WORD_LENGTH = 20;

    private final SeoKeywordGenerator keywordGenerator;

    public UrlShortener(SeoKeywordGenerator keywordGenerator) {
        this.keywordGenerator = keywordGenerator;
    }

    public URI shorten(URI inputUrl, String seoWord) {
        if (seoWord.length() > MAX_SEO_WORD_LENGTH) {
            throw new IllegalArgumentException("Seo word length should be not more than " + MAX_SEO_WORD_LENGTH);
        }
        return getFinalUrl(seoWord);
    }

    public URI shortenWithRandomSeoKeyword(URI inputUrl) {
        //  ToDO
        String randomKeyword = keywordGenerator.generate();
        return getFinalUrl(randomKeyword);
    }

    private URI getFinalUrl(String seoKeyword) {
        return URI.create(BASE_PATH + seoKeyword);
    }
}
