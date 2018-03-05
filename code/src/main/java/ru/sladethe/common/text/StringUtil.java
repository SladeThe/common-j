package ru.sladethe.common.text;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.translate.*;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.StrictMath.max;

/**
 * @author Maxim Shipko (sladethe@gmail.com)
 */
@SuppressWarnings("WeakerAccess")
public final class StringUtil {
    private static final CharSequenceTranslator ESCAPE_JAVA_RETAIN_CYRILLIC = new LookupTranslator(
            ((Supplier<Map<CharSequence, CharSequence>>) () -> {
                Map<CharSequence, CharSequence> lookupMap = new HashMap<>();
                lookupMap.put("\"", "\\\"");
                lookupMap.put("\\", "\\\\");
                return Collections.unmodifiableMap(lookupMap);
            }).get()
    ).with(
            new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE)
    ).with(
            JavaUnicodeEscaper.below(32)
    ).with(
            JavaUnicodeEscaper.between(128, (int) 'Ё' - 1)
    ).with(
            JavaUnicodeEscaper.between((int) 'Ё' + 1, (int) 'А' - 1)
    ).with(
            JavaUnicodeEscaper.between((int) 'я' + 1, (int) 'ё' - 1)
    ).with(
            JavaUnicodeEscaper.above((int) 'ё')
    );

    static final char NON_BREAKING_SPACE = (char) 160;
    static final char THIN_SPACE = '\u2009';
    static final char ZERO_WIDTH_SPACE = '\u200B';

    private StringUtil() {
        throw new UnsupportedOperationException();
    }

    public static boolean isWhitespace(char c) {
        return Character.isWhitespace(c) || c == NON_BREAKING_SPACE || c == ZERO_WIDTH_SPACE;
    }

    /**
     * @param s String.
     * @return {@code true} iff {@code s} is {@code null} or empty.
     */
    @Contract(value = "null -> true", pure = true)
    public static boolean isEmpty(@Nullable String s) {
        return s == null || s.isEmpty();
    }

    /**
     * @param s String.
     * @return {@code true} iff {@code s} is not {@code null} and not empty.
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean isNotEmpty(@Nullable String s) {
        return s != null && !s.isEmpty();
    }

    /**
     * @param s String.
     * @return {@code true} iff {@code s} is {@code null}, empty or contains only whitespaces.
     * @see #isWhitespace(char)
     */
    @SuppressWarnings("ForLoopWithMissingComponent")
    @Contract("null -> true")
    public static boolean isBlank(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }

        for (int charIndex = s.length(); --charIndex >= 0; ) {
            if (!isWhitespace(s.charAt(charIndex))) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param s String.
     * @return {@code true} iff {@code s} is not {@code null}, not empty
     * and contains at least one character that is not whitespace.
     * @see #isWhitespace(char)
     */
    @SuppressWarnings("ForLoopWithMissingComponent")
    @Contract("null -> false")
    public static boolean isNotBlank(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        for (int charIndex = s.length(); --charIndex >= 0; ) {
            if (!isWhitespace(s.charAt(charIndex))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Compares two strings case-sensitive.
     *
     * @param stringA first string
     * @param stringB second string
     * @return {@code true} iff both strings A and B are {@code null}
     * or the string B represents a {@code String} equivalent to the string A
     */
    @Contract(value = "null, null -> true; null, !null -> false; !null, null -> false", pure = true)
    public static boolean equals(@Nullable String stringA, @Nullable String stringB) {
        return stringA == null ? stringB == null : stringA.equals(stringB);
    }

    /**
     * Compares two strings case-sensitive. {@code null} and empty values considered equal.
     *
     * @param stringA first string
     * @param stringB second string
     * @return {@code true} iff both strings A and B are {@link #isEmpty(String) empty}
     * or the string B represents a {@code String} equal to the string A
     */
    public static boolean equalsOrEmpty(@Nullable String stringA, @Nullable String stringB) {
        return isEmpty(stringA) ? isEmpty(stringB) : stringA.equals(stringB);
    }

    /**
     * Compares two strings case-sensitive. {@code null}, empty and blank values considered equal.
     *
     * @param stringA first string
     * @param stringB second string
     * @return {@code true} iff both strings A and B are {@link #isBlank(String) blank}
     * or the string B represents a {@code String} equal to the string A
     */
    public static boolean equalsOrBlank(@Nullable String stringA, @Nullable String stringB) {
        return isBlank(stringA) ? isBlank(stringB) : stringA.equals(stringB);
    }

    /**
     * Compares two strings case-insensitive.
     *
     * @param stringA first string
     * @param stringB second string
     * @return {@code true} iff both strings A and B are {@code null}
     * or the string B represents a {@code String} equal to the string A
     */
    @Contract("null, null -> true; null, !null -> false; !null, null -> false")
    public static boolean equalsIgnoreCase(@Nullable String stringA, @Nullable String stringB) {
        return stringA == null ? stringB == null : stringA.equalsIgnoreCase(stringB);
    }

    /**
     * Compares two strings case-insensitive. {@code null} and empty values considered equal.
     *
     * @param stringA first string
     * @param stringB second string
     * @return {@code true} iff both strings A and B are {@link #isEmpty(String) empty}
     * or the string B represents a {@code String} equal to the string A
     */
    public static boolean equalsOrEmptyIgnoreCase(@Nullable String stringA, @Nullable String stringB) {
        return isEmpty(stringA) ? isEmpty(stringB) : stringA.equalsIgnoreCase(stringB);
    }

    /**
     * Compares two strings case-insensitive. {@code null}, empty and blank values considered equal.
     *
     * @param stringA first string
     * @param stringB second string
     * @return {@code true} iff both strings A and B are {@link #isBlank(String) blank}
     * or the string B represents a {@code String} equal to the string A
     */
    public static boolean equalsOrBlankIgnoreCase(@Nullable String stringA, @Nullable String stringB) {
        return isBlank(stringA) ? isBlank(stringB) : stringA.equalsIgnoreCase(stringB);
    }

    /**
     * Compares two strings case-sensitive and inverts result.
     *
     * @param stringA first string
     * @param stringB second string
     * @return {@code false} iff both strings A and B are {@code null}
     * or the string B represents a {@code String} equivalent to the string A
     */
    @Contract(value = "null, null -> false; null, !null -> true; !null, null -> true", pure = true)
    public static boolean notEquals(@Nullable String stringA, @Nullable String stringB) {
        return stringA == null ? stringB != null : !stringA.equals(stringB);
    }

    /**
     * Compares two strings case-sensitive and inverts result. {@code null} and empty values considered equal.
     *
     * @param stringA first string
     * @param stringB second string
     * @return {@code false} iff both strings A and B are {@link #isEmpty(String) empty}
     * or the string B represents a {@code String} equal to the string A
     */
    public static boolean notEqualsOrEmpty(@Nullable String stringA, @Nullable String stringB) {
        return isEmpty(stringA) ? isNotEmpty(stringB) : !stringA.equals(stringB);
    }

    /**
     * Compares two strings case-sensitive and inverts result.
     * {@code null}, empty and blank values considered equal.
     *
     * @param stringA first string
     * @param stringB second string
     * @return {@code false} iff both strings A and B are {@link #isBlank(String) blank}
     * or the string B represents a {@code String} equal to the string A
     */
    public static boolean notEqualsOrBlank(@Nullable String stringA, @Nullable String stringB) {
        return isBlank(stringA) ? isNotBlank(stringB) : !stringA.equals(stringB);
    }

    /**
     * Compares two strings case-insensitive and inverts result.
     *
     * @param stringA first string
     * @param stringB second string
     * @return {@code false} iff both strings A and B are {@code null}
     * or the string B represents a {@code String} equal to the string A
     */
    @Contract("null, null -> false; null, !null -> true; !null, null -> true")
    public static boolean notEqualsIgnoreCase(@Nullable String stringA, @Nullable String stringB) {
        return stringA == null ? stringB != null : !stringA.equalsIgnoreCase(stringB);
    }

    /**
     * Compares two strings case-insensitive and inverts result. {@code null} and empty values considered equal.
     *
     * @param stringA first string
     * @param stringB second string
     * @return {@code false} iff both strings A and B are {@link #isEmpty(String) empty}
     * or the string B represents a {@code String} equal to the string A
     */
    public static boolean notEqualsOrEmptyIgnoreCase(@Nullable String stringA, @Nullable String stringB) {
        return isEmpty(stringA) ? isNotEmpty(stringB) : !stringA.equalsIgnoreCase(stringB);
    }

    /**
     * Compares two strings case-insensitive and inverts result.
     * {@code null}, empty and blank values considered equal.
     *
     * @param stringA first string
     * @param stringB second string
     * @return {@code false} iff both strings A and B are {@link #isBlank(String) blank}
     * or the string B represents a {@code String} equal to the string A
     */
    public static boolean notEqualsOrBlankIgnoreCase(@Nullable String stringA, @Nullable String stringB) {
        return isBlank(stringA) ? isNotBlank(stringB) : !stringA.equalsIgnoreCase(stringB);
    }

    @Contract(pure = true)
    public static int length(@Nullable String s) {
        return s == null ? 0 : s.length();
    }

    @Contract(pure = true)
    @Nonnull
    public static String nullToEmpty(@Nullable String s) {
        return s == null ? "" : s;
    }

    @Contract(value = "null -> null", pure = true)
    @Nullable
    public static String emptyToNull(@Nullable String s) {
        return s == null || s.isEmpty() ? null : s;
    }

    @Contract(value = "!null, _ -> ! null; _, !null -> ! null", pure = true)
    @Nullable
    public static String nullToDefault(@Nullable String s, @Nullable String defaultValue) {
        return s == null ? defaultValue : s;
    }

    @Contract(value = "null -> null; !null -> !null", pure = true)
    @Nullable
    public static String trim(@Nullable String s) {
        if (s == null) {
            return null;
        }

        int lastIndex = s.length() - 1;
        int beginIndex = 0;
        int endIndex = lastIndex;

        while (beginIndex <= lastIndex && isWhitespace(s.charAt(beginIndex))) {
            ++beginIndex;
        }

        while (endIndex > beginIndex && isWhitespace(s.charAt(endIndex))) {
            --endIndex;
        }

        return beginIndex == 0 && endIndex == lastIndex ? s : s.substring(beginIndex, endIndex + 1);
    }

    @Contract(value = "null -> null", pure = true)
    @Nullable
    public static String trimToNull(@Nullable String s) {
        return s == null ? null : (s = trim(s)).isEmpty() ? null : s;
    }

    @Contract(pure = true)
    @Nonnull
    public static String trimToEmpty(@Nullable String s) {
        return s == null ? "" : trim(s);
    }

    @Contract(value = "null -> null; !null -> !null", pure = true)
    @Nullable
    public static String trimRight(@Nullable String s) {
        if (s == null) {
            return null;
        }

        int lastIndex = s.length() - 1;
        int endIndex = lastIndex;

        while (endIndex >= 0 && isWhitespace(s.charAt(endIndex))) {
            --endIndex;
        }

        return endIndex == lastIndex ? s : s.substring(0, endIndex + 1);
    }

    @Contract(value = "null -> null", pure = true)
    @Nullable
    public static String trimRightToNull(@Nullable String s) {
        return s == null ? null : (s = trimRight(s)).isEmpty() ? null : s;
    }

    @Contract(pure = true)
    @Nonnull
    public static String trimRightToEmpty(@Nullable String s) {
        return s == null ? "" : trimRight(s);
    }

    @Contract(value = "null -> null; !null -> !null", pure = true)
    @Nullable
    public static String trimLeft(@Nullable String s) {
        if (s == null) {
            return null;
        }

        int lastIndex = s.length() - 1;
        int beginIndex = 0;

        while (beginIndex <= lastIndex && isWhitespace(s.charAt(beginIndex))) {
            ++beginIndex;
        }

        return beginIndex == 0 ? s : s.substring(beginIndex, lastIndex + 1);
    }

    @Contract(value = "null -> null", pure = true)
    @Nullable
    public static String trimLeftToNull(@Nullable String s) {
        return s == null ? null : (s = trimLeft(s)).isEmpty() ? null : s;
    }

    @Contract(pure = true)
    @Nonnull
    public static String trimLeftToEmpty(@Nullable String s) {
        return s == null ? "" : trimLeft(s);
    }

    @Contract(value = "null, _ -> false", pure = true)
    public static boolean startsWith(@Nullable String s, @Nonnull String prefix) {
        return s != null && s.startsWith(prefix);
    }

    @Contract(value = "null, _ -> false", pure = true)
    public static boolean endsWith(@Nullable String s, @Nonnull String suffix) {
        return s != null && s.endsWith(suffix);
    }

    /**
     * Splits given string using separator char. All empty parts are included in the result.
     *
     * @param s         the string to be split
     * @param separator the delimiting character
     * @return the array of string parts
     */
    @Nonnull
    public static String[] split(@Nonnull String s, char separator) {
        int length = s.length();
        int start = 0;
        int i = 0;

        String[] parts = null;
        int count = 0;

        while (i < length) {
            if (s.charAt(i) == separator) {
                if (parts == null) {
                    parts = new String[8];
                } else if (count == parts.length) {
                    String[] tempParts = new String[count << 1];
                    System.arraycopy(parts, 0, tempParts, 0, count);
                    parts = tempParts;
                }
                parts[count++] = s.substring(start, i);
                start = ++i;
                continue;
            }
            ++i;
        }

        if (parts == null) {
            return new String[] {s};
        }

        if (count == parts.length) {
            String[] tempParts = new String[count + 1];
            System.arraycopy(parts, 0, tempParts, 0, count);
            parts = tempParts;
        }

        parts[count++] = s.substring(start, i);

        if (count == parts.length) {
            return parts;
        }

        String[] tempParts = new String[count];
        System.arraycopy(parts, 0, tempParts, 0, count);
        return tempParts;
    }

    @Contract(value = "null, _, _ -> null; !null, _, _ -> !null", pure = true)
    @Nullable
    public static String replace(@Nullable String s, @Nullable String target, @Nullable String replacement) {
        if (isEmpty(s) || isEmpty(target) || replacement == null) {
            return s;
        }

        int targetIndex = s.indexOf(target);
        if (targetIndex == -1) {
            return s;
        }

        int i = 0;
        int targetLength = target.length();
        StringBuilder result = new StringBuilder(s.length() + (max(replacement.length() - targetLength, 0) << 4));

        do {
            if (targetIndex > i) {
                result.append(s.substring(i, targetIndex));
            }

            result.append(replacement);
            i = targetIndex + targetLength;
            targetIndex = s.indexOf(target, i);
        } while (targetIndex != -1);

        return result.append(s.substring(i)).toString();
    }

    /**
     * Compares two strings by splitting them on character groups.
     *
     * @param stringA the first string to be compared
     * @param stringB the second string to be compared
     * @return a negative integer, zero, or a positive integer as the first argument
     * is less than, equal to, or greater than the second
     */
    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    public static int compareStringsSmart(@Nonnull String stringA, @Nonnull String stringB) {
        int lengthA = stringA.length();
        int lengthB = stringB.length();

        StringBuilder numberGroupA = new StringBuilder();
        StringBuilder numberGroupB = new StringBuilder();

        int offsetA = 0;
        int offsetB = 0;

        while (true) {
            char charA;
            char charB;

            while (offsetA < lengthA && !Character.isDigit(charA = stringA.charAt(offsetA))) {
                if (offsetB < lengthB && !Character.isDigit(charB = stringB.charAt(offsetB))) {
                    if (charA != charB) {
                        return (int) charA - (int) charB;
                    }
                } else {
                    return 1;
                }

                ++offsetA;
                ++offsetB;
            }

            if (offsetB < lengthB && !Character.isDigit(stringB.charAt(offsetB))) {
                return -1;
            }

            while (offsetA < lengthA && Character.isDigit(charA = stringA.charAt(offsetA))) {
                numberGroupA.append(charA);
                ++offsetA;
            }

            while (offsetB < lengthB && Character.isDigit(charB = stringB.charAt(offsetB))) {
                numberGroupB.append(charB);
                ++offsetB;
            }

            if (numberGroupA.length() == 0) {
                return numberGroupB.length() == 0 ? 0 : -1;
            }

            if (numberGroupB.length() == 0) {
                return 1;
            }

            String groupValueA = numberGroupA.toString();
            String groupValueB = numberGroupB.toString();

            numberGroupA.setLength(0);
            numberGroupB.setLength(0);

            long numberA;
            try {
                numberA = Long.parseLong(groupValueA);
            } catch (NumberFormatException ignored) {
                int numberAsStringComparisonResult = groupValueA.compareTo(groupValueB);
                if (numberAsStringComparisonResult == 0) {
                    continue;
                } else {
                    return numberAsStringComparisonResult;
                }
            }

            long numberB;
            try {
                numberB = Long.parseLong(groupValueB);
            } catch (NumberFormatException ignored) {
                return groupValueA.compareTo(groupValueB);
            }

            if (numberA > numberB) {
                return 1;
            }

            if (numberA < numberB) {
                return -1;
            }

            if (groupValueA.length() != groupValueB.length()) {
                return groupValueA.compareTo(groupValueB);
            }
        }
    }

    public static void sortStringsSmart(@Nonnull String[] strings) {
        Arrays.sort(strings, StringUtil::compareStringsSmart);
    }

    public static void sortStringsSmart(@Nonnull List<String> strings) {
        strings.sort(StringUtil::compareStringsSmart);
    }

    @Contract(value = "null -> null; !null -> !null", pure = true)
    @Nullable
    public static String escapeJavaRetainCyrillic(@Nullable String s) {
        return s == null ? null : ESCAPE_JAVA_RETAIN_CYRILLIC.translate(s);
    }

    @SuppressWarnings({"IfStatementWithIdenticalBranches", "OverlyComplexMethod", "AssignmentOrReturnOfFieldWithMutableType"})
    @Contract("null -> null; !null -> !null")
    @Nullable
    public static byte[] removeBoms(@Nullable byte[] bytes) {
        int byteCount;

        if (bytes == null || (byteCount = bytes.length) == 0) {
            return bytes;
        }

        int bomLength;

        if (bytes.length >= 3
                && (bytes[0] & 0xFF) == 239 && (bytes[1] & 0xFF) == 187 && (bytes[2] & 0xFF) == 191) { // UTF-8
            bomLength = 3;
        } else if (bytes.length >= 2
                && (bytes[0] & 0xFF) == 254 && (bytes[1] & 0xFF) == 255) { // UTF-16 (BE)
            bomLength = 2;
        } else if (bytes.length >= 2
                && (bytes[0] & 0xFF) == 255 && (bytes[1] & 0xFF) == 254) { // UTF-16 (LE)
            bomLength = 2;
        } else if (bytes.length >= 4
                && (bytes[0] & 0xFF) == 0 && (bytes[1] & 0xFF) == 0
                && (bytes[0] & 0xFF) == 254 && (bytes[1] & 0xFF) == 255) { // UTF-32 (BE)
            bomLength = 4;
        } else if (bytes.length >= 4
                && (bytes[0] & 0xFF) == 255 && (bytes[1] & 0xFF) == 254
                && (bytes[0] & 0xFF) == 0 && (bytes[1] & 0xFF) == 0) { // UTF-32 (LE)
            bomLength = 4;
        } else {
            bomLength = 0;
        }

        if (bomLength == 0) {
            return bytes;
        }

        if (bomLength == byteCount) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }

        byte[] processedBytes = new byte[byteCount - bomLength];
        System.arraycopy(bytes, bomLength, processedBytes, 0, byteCount - bomLength);
        return processedBytes;
    }

    public static void ifNotEmpty(@Nullable String s, @Nonnull Consumer<String> consumer) {
        if (isNotEmpty(s)) {
            consumer.accept(s);
        }
    }

    public static void ifNotBlank(@Nullable String s, @Nonnull Consumer<String> consumer) {
        if (isNotBlank(s)) {
            consumer.accept(s);
        }
    }
}
