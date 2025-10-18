package org.openlyrics.jlyrics.song.properties;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorTest {

    @ParameterizedTest
    @MethodSource("getAuthorTypesAndLanguages")
    void testLangAccordingToType(AuthorType type, String lang) {
        Author author = new Author().setType(type).setLang("en_US");
        assertEquals(lang, author.getLang());
    }

    static Stream<Arguments> getAuthorTypesAndLanguages() {
        return Stream.of(
                Arguments.of(AuthorType.WORDS, null),
                Arguments.of(AuthorType.MUSIC, null),
                Arguments.of(AuthorType.TRANSLATION, "en_US"),
                Arguments.of(AuthorType.ARRANGEMENT, null)
        );
    }
}
