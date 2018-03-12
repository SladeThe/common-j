package ru.sladethe.common.math;

import gnu.trove.list.*;
import gnu.trove.list.array.*;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author Maxim Shipko (sladethe@gmail.com)
 */
public class NumberUtilTest {
    @Test
    public void toLong() {
        assertEquals("Can't convert double to long.", 100L, NumberUtil.toLong(100.01D));
        assertEquals("Can't convert double to long.", -1000000000L, NumberUtil.toLong(-1000000000.0D));
        assertEquals("Can't convert double to long.", -1000000000L, NumberUtil.toLong(-1000000000.9D));
        assertEquals("Can't convert double to long.", -1000000001L, NumberUtil.toLong(-1000000000.999999999999999999D));
        assertEquals("Can't convert string to long.", -100L, (long) NumberUtil.toLong("-100.0"));
        assertEquals("Can't convert string to long.", -101L, (long) NumberUtil.toLong(new BigDecimal("-101.0")));
    }

    @Test
    public void packIntsToLong() {
        @SuppressWarnings("UnsecureRandomNumberGeneration") Random random = new Random();
        TIntList ints = new TIntArrayList();

        ints.add(0);
        ints.add(1);
        ints.add(-1);
        ints.add(1_000);
        ints.add(-1_000);
        ints.add(1_000_000);
        ints.add(-1_000_000);
        ints.add(1_000_000_000);
        ints.add(-1_000_000_000);
        ints.add(Integer.MIN_VALUE);
        ints.add(Integer.MAX_VALUE);

        for (int i = 0; i < 10000; ++i) {
            ints.add(random.nextInt());
        }

        for (int i = 0; i < ints.size(); ++i) {
            int left0 = ints.get(i);

            for (int j = 0; j < ints.size(); ++j) {
                int right0 = ints.get(j);

                long packedValue = NumberUtil.packInts(left0, right0);

                Assert.assertEquals("Can't unpack left integer.", left0, NumberUtil.unpackLeftInt(packedValue));
                Assert.assertEquals("Can't unpack right integer.", right0, NumberUtil.unpackRightInt(packedValue));
            }
        }
    }

    @Test
    public void packShortsToInt() {
        @SuppressWarnings("UnsecureRandomNumberGeneration") Random random = new Random();
        TShortList shorts = new TShortArrayList();

        shorts.add((short) 0);
        shorts.add((short) 1);
        shorts.add((short) -1);
        shorts.add((short) 1_000);
        shorts.add((short) -1_000);
        shorts.add(Short.MIN_VALUE);
        shorts.add(Short.MAX_VALUE);

        byte[] bytes = new byte[Short.BYTES];

        for (int i = 0; i < 10000; ++i) {
            random.nextBytes(bytes);
            shorts.add(ByteBuffer.allocate(Short.BYTES).put(bytes).getShort(0));
        }

        for (int i = 0; i < shorts.size(); ++i) {
            short left0 = shorts.get(i);

            for (int j = 0; j < shorts.size(); ++j) {
                short right0 = shorts.get(j);

                int packedValue = NumberUtil.packShorts(left0, right0);

                Assert.assertEquals("Can't unpack left short.", left0, NumberUtil.unpackLeftShort(packedValue));
                Assert.assertEquals("Can't unpack right short.", right0, NumberUtil.unpackRightShort(packedValue));
            }
        }
    }

    @Test
    public void packBytesToShort() {
        TByteList bytes = new TByteArrayList();

        for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; ++i) {
            @SuppressWarnings("NumericCastThatLosesPrecision") byte b = (byte) i;
            bytes.add(b);
        }

        for (int i = 0; i < bytes.size(); ++i) {
            byte left0 = bytes.get(i);

            for (int j = 0; j < bytes.size(); ++j) {
                byte right0 = bytes.get(j);

                short packedValue = NumberUtil.packBytes(left0, right0);

                Assert.assertEquals("Can't unpack left byte.", left0, NumberUtil.unpackLeftByte(packedValue));
                Assert.assertEquals("Can't unpack right byte.", right0, NumberUtil.unpackRightByte(packedValue));
            }
        }
    }

    @Test
    public void packFloatsToLong() {
        @SuppressWarnings("UnsecureRandomNumberGeneration") Random random = new Random();
        TFloatList floats = new TFloatArrayList();

        floats.add(0.0f);
        floats.add(0.001f);
        floats.add(-0.001f);
        floats.add(1.0f);
        floats.add(-1.0f);
        floats.add(1_000.0f);
        floats.add(-1_000.0f);
        floats.add(Float.MIN_VALUE);
        floats.add(Float.MAX_VALUE);

        for (int i = 0; i < 10000; ++i) {
            floats.add(Float.intBitsToFloat(random.nextInt()));
        }

        for (int i = 0; i < floats.size(); ++i) {
            float left0 = floats.get(i);

            for (int j = 0; j < floats.size(); ++j) {
                float right0 = floats.get(j);

                long packedValue = NumberUtil.packFloats(left0, right0);

                Assert.assertEquals("Can't unpack left float.", left0, NumberUtil.unpackLeftFloat(packedValue), 0.0f);
                Assert.assertEquals("Can't unpack right float.", right0, NumberUtil.unpackRightFloat(packedValue), 0.0f);
            }
        }
    }
}
