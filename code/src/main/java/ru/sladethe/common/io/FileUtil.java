package ru.sladethe.common.io;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Maxim Shipko (sladethe@gmail.com)
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class FileUtil {
    public static final long TB_PER_PB = 1024L;

    public static final long GB_PER_TB = 1024L;
    public static final long GB_PER_PB = GB_PER_TB * TB_PER_PB;

    public static final long MB_PER_GB = 1024L;
    public static final long MB_PER_TB = MB_PER_GB * GB_PER_TB;
    public static final long MB_PER_PB = MB_PER_TB * TB_PER_PB;

    public static final long KB_PER_MB = 1024L;
    public static final long KB_PER_GB = KB_PER_MB * MB_PER_GB;
    public static final long KB_PER_TB = KB_PER_GB * GB_PER_TB;
    public static final long KB_PER_PB = KB_PER_TB * TB_PER_PB;

    public static final long BYTES_PER_KB = 1024L;
    public static final long BYTES_PER_MB = BYTES_PER_KB * KB_PER_MB;
    public static final long BYTES_PER_GB = BYTES_PER_MB * MB_PER_GB;
    public static final long BYTES_PER_TB = BYTES_PER_GB * GB_PER_TB;
    public static final long BYTES_PER_PB = BYTES_PER_TB * TB_PER_PB;

    private FileUtil() {
        throw new UnsupportedOperationException();
    }

    @Contract("null -> fail")
    @Nonnull
    public static File getCriticalBackupFile(@Nonnull File file) {
        String path = file.getPath();
        return new File(FilenameUtils.getFullPath(path) + FilenameUtils.getBaseName(path) + ".bak");
    }

    @Contract("null -> fail")
    @Nullable
    private static byte[] getSubscribedFileBytes(@Nonnull File subscribedFile) throws IOException {
        if (subscribedFile.isFile()) {
            int digestLength = 32;
            byte[] subscribedBytes = FileUtils.readFileToByteArray(subscribedFile);
            int subscribedByteCount = subscribedBytes.length;

            if (subscribedByteCount >= digestLength) {
                byte[] bytes = new byte[subscribedByteCount - digestLength];
                byte[] digest = new byte[digestLength];

                System.arraycopy(subscribedBytes, 0, bytes, 0, subscribedByteCount - digestLength);
                System.arraycopy(subscribedBytes, subscribedByteCount - digestLength, digest, 0, digestLength);

                if (Arrays.equals(DigestUtils.sha256(bytes), digest)) {
                    return bytes;
                }
            }
        }

        return null;
    }

    @Contract("null, _ -> fail; _, null -> fail")
    @Nonnull
    public static byte[] getCriticalFileBytes(@Nonnull File file, @Nonnull File backupFile) throws IOException {
        byte[] subscribedFileBytes = getSubscribedFileBytes(file);
        if (subscribedFileBytes != null) {
            FileUtils.deleteQuietly(backupFile);
            return subscribedFileBytes;
        }

        subscribedFileBytes = getSubscribedFileBytes(backupFile);
        if (subscribedFileBytes != null) {
            FileUtils.copyFile(backupFile, file);
            FileUtils.deleteQuietly(backupFile);
            return subscribedFileBytes;
        }

        throw new IOException("Can't read neither critical file '" + file + "', nor backup file '" + backupFile + "'.");
    }

    @Contract("null -> fail")
    @Nonnull
    public static byte[] getCriticalFileBytes(@Nonnull File file) throws IOException {
        return getCriticalFileBytes(file, getCriticalBackupFile(file));
    }

    @Contract("null, _ -> fail; _, null -> fail")
    private static void writeSubscribedFile(@Nonnull File subscribedFile, @Nonnull byte[] bytes) throws IOException {
        byte[] digest = DigestUtils.sha256(bytes);
        byte[] subscribedBytes = new byte[bytes.length + digest.length];

        System.arraycopy(bytes, 0, subscribedBytes, 0, bytes.length);
        System.arraycopy(digest, 0, subscribedBytes, bytes.length, digest.length);

        FileUtils.writeByteArrayToFile(subscribedFile, subscribedBytes);
    }

    @Contract("null, _, _ -> fail; _, null, _ -> fail; _, _, null -> fail")
    public static void writeCriticalFile(
            @Nonnull File file, @Nonnull File backupFile, @Nonnull byte[] bytes) throws IOException {
        if (file.isFile()) {
            FileUtils.copyFile(file, backupFile);
        }

        writeSubscribedFile(file, bytes);
        FileUtils.forceDelete(backupFile);
    }

    @Contract("null, _ -> fail; _, null -> fail")
    public static void writeCriticalFile(@Nonnull File file, @Nonnull byte[] bytes) throws IOException {
        writeCriticalFile(file, getCriticalBackupFile(file), bytes);
    }
}
