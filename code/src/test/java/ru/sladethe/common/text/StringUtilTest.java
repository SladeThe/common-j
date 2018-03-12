package ru.sladethe.common.text;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Maxim Shipko (sladethe@gmail.com)
 */
@SuppressWarnings("MessageMissingOnJUnitAssertion")
public class StringUtilTest {
    private static final String BLANK_STRING = String.format(
            " \r\n\t%c%c%c", StringUtil.ZERO_WIDTH_SPACE, StringUtil.THIN_SPACE, StringUtil.NON_BREAKING_SPACE
    );

    @SuppressWarnings("ConstantConditions")
    @Test
    public void isEmpty() {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty("a"));
        assertFalse(StringUtil.isEmpty(" "));
        assertFalse(StringUtil.isEmpty("          "));
        assertFalse(StringUtil.isEmpty(" a"));
        assertFalse(StringUtil.isEmpty("z "));
        assertFalse(StringUtil.isEmpty("тест"));
    }

    @Test
    public void isBlank() {
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        assertFalse(StringUtil.isBlank("a"));
        assertTrue(StringUtil.isBlank(" "));
        assertTrue(StringUtil.isBlank("          "));
        assertFalse(StringUtil.isBlank(" a"));
        assertFalse(StringUtil.isBlank("z "));
        assertFalse(StringUtil.isBlank("тест"));
        assertTrue(StringUtil.isBlank(BLANK_STRING));
        assertFalse(StringUtil.isBlank(BLANK_STRING + '_'));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void isNotEmpty() {
        assertFalse(StringUtil.isNotEmpty(null));
        assertFalse(StringUtil.isNotEmpty(""));
        assertTrue(StringUtil.isNotEmpty("a"));
        assertTrue(StringUtil.isNotEmpty(" "));
        assertTrue(StringUtil.isNotEmpty("          "));
        assertTrue(StringUtil.isNotEmpty(" a"));
        assertTrue(StringUtil.isNotEmpty("z "));
        assertTrue(StringUtil.isNotEmpty("тест"));
    }

    @Test
    public void isNotBlank() {
        assertFalse(StringUtil.isNotBlank(null));
        assertFalse(StringUtil.isNotBlank(""));
        assertTrue(StringUtil.isNotBlank("a"));
        assertFalse(StringUtil.isNotBlank(" "));
        assertFalse(StringUtil.isNotBlank("          "));
        assertTrue(StringUtil.isNotBlank(" a"));
        assertTrue(StringUtil.isNotBlank("z "));
        assertTrue(StringUtil.isNotBlank("тест"));
        assertFalse(StringUtil.isNotBlank(BLANK_STRING));
        assertTrue(StringUtil.isNotBlank(BLANK_STRING + '_'));
    }

    @Test
    public void trimLeft() {
        assertNull(StringUtil.trimLeft(null));
        assertEquals("", StringUtil.trimLeft(""));
        assertEquals("a", StringUtil.trimLeft("a"));
        assertEquals("", StringUtil.trimLeft(" "));
        assertEquals("", StringUtil.trimLeft("          "));
        assertEquals("a", StringUtil.trimLeft(" a"));
        assertEquals("z ", StringUtil.trimLeft("z "));
        assertEquals("тест", StringUtil.trimLeft("тест"));
        assertEquals("", StringUtil.trimLeft(BLANK_STRING));
        assertEquals("_", StringUtil.trimLeft(BLANK_STRING + '_'));
        assertEquals('_' + BLANK_STRING, StringUtil.trimLeft('_' + BLANK_STRING));
        assertEquals('_' + BLANK_STRING + '_', StringUtil.trimLeft('_' + BLANK_STRING + '_'));
    }

    @Test
    public void trimRight() {
        assertNull(StringUtil.trimRight(null));
        assertEquals("", StringUtil.trimRight(""));
        assertEquals("a", StringUtil.trimRight("a"));
        assertEquals("", StringUtil.trimRight(" "));
        assertEquals("", StringUtil.trimRight("          "));
        assertEquals(" a", StringUtil.trimRight(" a"));
        assertEquals("z", StringUtil.trimRight("z "));
        assertEquals("тест", StringUtil.trimRight("тест"));
        assertEquals("", StringUtil.trimRight(BLANK_STRING));
        assertEquals(BLANK_STRING + '_', StringUtil.trimRight(BLANK_STRING + '_'));
        assertEquals("_", StringUtil.trimRight('_' + BLANK_STRING));
        assertEquals('_' + BLANK_STRING + '_', StringUtil.trimRight('_' + BLANK_STRING + '_'));
    }

    @Test
    public void trim() {
        assertNull(StringUtil.trim(null));
        assertEquals("", StringUtil.trim(""));
        assertEquals("a", StringUtil.trim("a"));
        assertEquals("", StringUtil.trim(" "));
        assertEquals("", StringUtil.trim("          "));
        assertEquals("a", StringUtil.trim(" a"));
        assertEquals("z", StringUtil.trim("z "));
        assertEquals("тест", StringUtil.trim("тест"));
        assertEquals("", StringUtil.trim(BLANK_STRING));
        assertEquals("_", StringUtil.trim(BLANK_STRING + '_'));
        assertEquals("_", StringUtil.trim('_' + BLANK_STRING));
        assertEquals('_' + BLANK_STRING + '_', StringUtil.trim('_' + BLANK_STRING + '_'));
    }

    @Test
    public void sortStringsSmart() {
        String[] strings = {"A", "BA", "AB", "", "1", "2", "21", "12", "01", "02"};
        String[] sortedStrings = {"", "01", "1", "02", "2", "12", "21", "A", "AB", "BA"};
        StringUtil.sortStringsSmart(strings);
        assertArrayEquals(sortedStrings, strings);

        strings = new String[] {"AA12", "AA1", "AA1.12", "AA1.11", "AA1.13", "AA12BC1", "AA12BA1", "AA12BC2"};
        sortedStrings = new String[] {"AA1", "AA1.11", "AA1.12", "AA1.13", "AA12", "AA12BA1", "AA12BC1", "AA12BC2"};
        StringUtil.sortStringsSmart(strings);
        assertArrayEquals(sortedStrings, strings);

        strings = new String[] {"F", "FF", "FA", "FAA", "FAA12", "F12", "FA100"};
        sortedStrings = new String[] {"F", "F12", "FA", "FA100", "FAA", "FAA12", "FF"};
        StringUtil.sortStringsSmart(strings);
        assertArrayEquals(sortedStrings, strings);

        strings = new String[] {"10", "1", "12", "13", "111", "1", "100"};
        sortedStrings = new String[] {"1", "1", "10", "12", "13", "100", "111"};
        StringUtil.sortStringsSmart(strings);
        assertArrayEquals(sortedStrings, strings);

        strings = new String[] {"10", "1a1a", "12", "13", "111", "1a1a1", "100"};
        sortedStrings = new String[] {"1a1a", "1a1a1", "10", "12", "13", "100", "111"};
        StringUtil.sortStringsSmart(strings);
        assertArrayEquals(sortedStrings, strings);

        strings = new String[] {
                "AA.9999999999999999999999.0100", "AA.9999999999999999999999.2", "AA.9999999999999999999999.0200",
                "AA.9999999999999999999998.0100", "AB.99999999999999999999.0100", "AB.0999999999999999999999.0100"
        };
        sortedStrings = new String[] {
                "AA.9999999999999999999998.0100", "AA.9999999999999999999999.2", "AA.9999999999999999999999.0100",
                "AA.9999999999999999999999.0200", "AB.0999999999999999999999.0100", "AB.99999999999999999999.0100"
        };
        StringUtil.sortStringsSmart(strings);
        assertArrayEquals(sortedStrings, strings);
    }

    @Test
    public void split() {
        internalTestSplit("size", '.', new String[] {"size"});
        internalTestSplit("size.dice", '.', new String[] {"size", "dice"});
        internalTestSplit("size.dice.nice.lays.mays", '.', new String[] {"size", "dice", "nice", "lays", "mays"});

        internalTestSplit("size" + StringUtils.repeat("_size", 1000), '.', new String[] {
                "size" + StringUtils.repeat("_size", 1000)
        });
        internalTestSplit("size.dice" + StringUtils.repeat("_dice", 1000), '.', new String[] {
                "size", "dice" + StringUtils.repeat("_dice", 1000)
        });
        internalTestSplit("size.dice.nice.lays.mays" + StringUtils.repeat("_mays", 1000), '.', new String[] {
                "size", "dice", "nice", "lays", "mays" + StringUtils.repeat("_mays", 1000)
        });

        internalTestSplit(".size.", '.', new String[] {"", "size", ""});
        internalTestSplit(" size  ", ' ', new String[] {"", "size", "", ""});
        internalTestSplit(",,,,", ',', new String[] {"", "", "", "", ""});
    }

    private static void internalTestSplit(String s, char c, String[] parts) {
        assertArrayEquals("Illegal split of '" + s + "' by '" + c + "'.", parts, StringUtil.split(s, c));
    }
}
