package com.chessgrinder.chessgrinder.chessengine.trf.parser;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.Player001TrfLine;
import org.junit.jupiter.api.Test;

import static com.chessgrinder.chessgrinder.chessengine.trf.parser.PlayerTrfLineParser.NAME_LENGTH;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTrfLineParserTest {

    private static String sanitize(String name) {
        return PlayerTrfLineParser.sanitizeName(name, NAME_LENGTH);
    }

    private static String writeName(String name, int startingRank) {
        var parser = new PlayerTrfLineParser();
        StringBuilder trf = new StringBuilder();
        parser.tryWrite(trf::append, Player001TrfLine.builder().startingRank(startingRank).name(name).build());
        return parser.tryParse(trf.toString().stripTrailing() + " ".repeat(100)).getName();
    }

    @Test
    void testReadAndWrite() {
        var parser = new PlayerTrfLineParser();
        String trfLine = "001    1      Test0001 Player0001               2573                             7.5    3  0000 - H    73 b 1    55 w 1    37 w =    26 b =    13 w =    38 b 1    19 w 1    17 b =    11 w 1  ";
        var result = parser.tryParse(trfLine);
        assertEquals(1, result.getStartingRank());
        assertEquals("Test0001 Player0001", result.getName());
        assertEquals(2573, result.getRating());
        assertEquals(7.5, result.getPoints(), 0.00001);
        assertEquals(3, result.getRank());
        assertEquals(10, result.getMatches().size());
        assertEquals(0, result.getMatches().get(0).getOpponentPlayerId());
        assertEquals('-', result.getMatches().get(0).getColor());
        assertEquals('H', result.getMatches().get(0).getResult());
        StringBuilder trf = new StringBuilder();
        parser.tryWrite(trf::append, result);
        assertEquals(trfLine, trf.toString());
    }

    /**
     * Tests the case if the name length is very long, then it should be trimmed
     */
    @Test
    void testOverflow() {
        var parser = new PlayerTrfLineParser();
        String trfLine = "001    0      abcdefghijklmnopqrttuwwxyzabcdefg                                            ";
        var result = Player001TrfLine.builder()
                .name("abcdefghijklmnopqrttuwwxyzabcdefghijklmnopqrttuwwxyz")
                .build();
        StringBuilder trf = new StringBuilder();
        parser.tryWrite(trf::append, result);
        assertEquals(trfLine, trf.toString());
    }

    @Test
    void sanitizeName_shouldNeutralizeLineBreaksAndInvisibleChars() {
        assertEquals("Bob 001 9999 FAKE", sanitize("Bob\n001 9999\r\nFAKE"));
        assertEquals("Bob", sanitize("\u202EB\u200Bo\u2066b"));
    }

    @Test
    void sanitizeName_shouldKeepNonAsciiNames() {
        assertEquals("Владимир Шефер", sanitize("Владимир Шефер"));
        assertEquals("გიორგი", sanitize("გიორგი"));
        assertEquals("李小龙", sanitize("李小龙"));
    }

    @Test
    void sanitizeName_shouldNormalizeAndCollapseWhitespace() {
        assertEquals("\u00E9 a b", sanitize("  e\u0301   a \t b "));
    }

    @Test
    void sanitizeName_shouldCutLongNameWithoutSplittingCharacters() {
        assertEquals(NAME_LENGTH, sanitize("Ж".repeat(50)).length());
        assertEquals("abcde", PlayerTrfLineParser.sanitizeName("abcdefghij", 5));
        // 32 letters + emoji (2 chars) would exceed 33, so the emoji is dropped whole
        String name = "a".repeat(32) + "\uD83D\uDE00";
        assertEquals("a".repeat(32), sanitize(name));
    }

    @Test
    void sanitizeName_shouldReturnEmptyStringIfNothingLeft() {
        assertEquals("", sanitize(null));
        assertEquals("", sanitize(""));
        assertEquals("", sanitize(" \n\u200B "));
    }

    @Test
    void tryWrite_shouldFallBackToPlayerNumberIfNameIsEmpty() {
        assertEquals("Player 7", writeName(null, 7));
        assertEquals("Player 7", writeName(" \n\u200B ", 7));
        assertEquals("Bob", writeName("Bob", 7));
    }

    @Test
    void sanitizeName_shouldKeepUrlsAsInertText() {
        assertEquals("http://evil.com/?a=1&b=2", sanitize("http://evil.com/?a=1&b=2"));
        assertEquals("javascript:alert(1)", sanitize("javascript:alert(1)"));
        // a long URL is just cut to the column width
        assertEquals("https://example.com/" + "a".repeat(13),
                sanitize("https://example.com/" + "a".repeat(40)));
    }

    @Test
    void sanitizeName_shouldKeepSqlInjectionAndHtmlAsInertText() {
        // Protection against SQL injection and XSS is parameterized queries and output escaping elsewhere;
        // here these strings must only be harmless in the fixed-width TRF line.
        assertEquals("Robert'); DROP TABLE students;--", sanitize("Robert'); DROP TABLE students;--"));
        assertEquals("' OR '1'='1", sanitize("' OR '1'='1"));
        assertEquals("<script>alert('xss')</script>", sanitize("<script>alert('xss')</script>"));
        // a multi-line payload cannot start a new TRF line
        assertEquals("x'; -- DROP TABLE users", sanitize("x'; --\nDROP TABLE users"));
    }

    @Test
    void sanitizeName_shouldKeepEmojis() {
        assertEquals("Knight \u265E \uD83D\uDE00\uD83D\uDD25", sanitize("Knight \u265E \uD83D\uDE00\uD83D\uDD25"));
        // family emoji: 4 people joined by zero-width joiners, must survive as one character
        String family = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66";
        assertEquals(family, sanitize(family));
        // an emoji that does not fit is dropped whole, not split
        assertEquals("a".repeat(30), sanitize("a".repeat(30) + family));
    }

    @Test
    void sanitizeName_shouldHandleCrazyUmlauts() {
        // decomposed umlauts (letter + combining diaeresis) are composed
        assertEquals("M\u00F6tley Cr\u00FCe", sanitize("Mo\u0308tley Cru\u0308e"));
        assertEquals("\u00C4\u00D6\u00DC\u00E4\u00F6\u00FC\u00DF", sanitize("\u00C4\u00D6\u00DC\u00E4\u00F6\u00FC\u00DF"));
        // zalgo text: a letter with a stack of 40 combining marks is too wide for the column and is dropped
        String zalgo = "Z" + "\u0308".repeat(40);
        assertEquals("", sanitize(zalgo));
        // a modest zalgo letter (8 combining marks) fits into the column and is kept
        String shortZalgo = "a" + "\u0300\u0301\u0302\u0303".repeat(2);
        String expected = java.text.Normalizer.normalize(shortZalgo + "bc", java.text.Normalizer.Form.NFC);
        assertEquals(expected, sanitize(shortZalgo + "bc"));
    }

}
