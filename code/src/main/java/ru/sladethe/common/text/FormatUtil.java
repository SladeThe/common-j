package ru.sladethe.common.text;

import ru.sladethe.common.math.NumberUtil;

import javax.annotation.*;
import java.util.Locale;
import java.util.regex.Pattern;

import static ru.sladethe.common.io.FileUtil.*;

/**
 * @author Maxim Shipko (sladethe@gmail.com)
 */
public final class FormatUtil {
    private static final Pattern DATA_SIZE_PATTERN = Pattern.compile(
            "(0|[1-9][01-9]{0,5})(\\.[01-9]{1,5})? ?[KMGTP]?B?"
    );

    private FormatUtil() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    public static String formatDataSize(@Nonnegative long size) {
        if (size < 0L) {
            throw new IllegalArgumentException("Argument 'size' must be a positive integer or zero.");
        }

        if (size >= BYTES_PER_PB) {
            return formatDataSize(size, BYTES_PER_PB, "PB");
        }

        if (size >= BYTES_PER_TB) {
            return formatDataSize(size, BYTES_PER_TB, "TB");
        }

        if (size >= BYTES_PER_GB) {
            return formatDataSize(size, BYTES_PER_GB, "GB");
        }

        if (size >= BYTES_PER_MB) {
            return formatDataSize(size, BYTES_PER_MB, "MB");
        }

        if (size >= BYTES_PER_KB) {
            return formatDataSize(size, BYTES_PER_KB, "kB");
        }

        return size + " B";
    }

    @Nonnull
    private static String formatDataSize(@Nonnegative long size, @Nonnegative long unit, @Nonnull String unitName) {
        if (size % unit == 0) {
            return size / unit + " " + unitName;
        }

        return String.format(Locale.US, "%.1f %s", (double) size / (double) unit, unitName);
    }

    public static long parseDataSize(@Nullable String size) {
        size = StringUtil.trimToNull(size);
        if (size == null) {
            return 0L;
        }

        size = size.toUpperCase();

        if (!DATA_SIZE_PATTERN.matcher(size).matches()) {
            throw new IllegalArgumentException(String.format(
                    "'%s' does not match the pattern '%s'.", size, DATA_SIZE_PATTERN
            ));
        }

        int lastCharIndex = size.length() - 1;
        char lastChar = size.charAt(lastCharIndex);

        if (lastChar == 'B') {
            size = size.substring(0, lastCharIndex);

            lastCharIndex = size.length() - 1;
            lastChar = size.charAt(lastCharIndex);
        }

        switch (lastChar) {
            case 'K':
                return parseDataSize(size, lastCharIndex, BYTES_PER_KB);
            case 'M':
                return parseDataSize(size, lastCharIndex, BYTES_PER_MB);
            case 'G':
                return parseDataSize(size, lastCharIndex, BYTES_PER_GB);
            case 'T':
                return parseDataSize(size, lastCharIndex, BYTES_PER_TB);
            case 'P':
                return parseDataSize(size, lastCharIndex, BYTES_PER_PB);
            default:
                return NumberUtil.toLong(Double.parseDouble(size.trim()));
        }
    }

    private static long parseDataSize(@Nonnull String size, @Nonnegative int lastCharIndex, @Nonnegative long unit) {
        return NumberUtil.toLong(Double.parseDouble(size.substring(0, lastCharIndex).trim()) * unit);
    }
}
