package cc.luojia.nbt_speed_test;

import com.mojang.nbt.NbtIo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;

@DisplayName("Test NBT IO Speed")
class SpeedTest {

    private byte[] big = new byte[]{
            (byte) 0xa, // compound #1
            (byte) 0x0, (byte) 0xb, (byte) 0x68, (byte) 0x65, (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0x20, (byte) 0x77, (byte) 0x6f, (byte) 0x72, (byte) 0x6c, (byte) 0x64, // "hello world"
            (byte) 0x1, // byte
            (byte) 0x0, (byte) 0x5, (byte) 0x31, (byte) 0x62, (byte) 0x79, (byte) 0x74, (byte) 0x65, // "1byte"
            (byte) 0x80, // -128
            (byte) 0x8, // string
            (byte) 0x0, (byte) 0x7, (byte) 0x38, (byte) 0x73, (byte) 0x74, (byte) 0x72, (byte) 0x69, (byte) 0x6e, (byte) 0x67, // "8string"
            (byte) 0x0, (byte) 0x5, (byte) 0x68, (byte) 0x65, (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, // hello
            (byte) 0x7, // byte array
            (byte) 0x0, (byte) 0xb, (byte) 0x37, (byte) 0x62, (byte) 0x79, (byte) 0x74, (byte) 0x65, (byte) 0x5f, (byte) 0x61, (byte) 0x72, (byte) 0x72, (byte) 0x61, (byte) 0x79, // "7byte_array"
            (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x4, //len = 4
            (byte) 0xc, (byte) 0xde, (byte) 0x38, (byte) 0xb2, // [12, -34, 56, -78]
            (byte) 0x9, // list tag
            (byte) 0x0, (byte) 0x9, (byte) 0x39, (byte) 0x6c, (byte) 0x69, (byte) 0x73, (byte) 0x74, (byte) 0x5f, (byte) 0x69, (byte) 0x6e, (byte) 0x74, // "9list_int"
            (byte) 0x3, // inner type: int
            (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x3, // len: 3
            (byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x6e, (byte) 0xee, (byte) 0xee, (byte) 0xee,
            (byte) 0x5d, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd,
            (byte) 0x5, // float
            (byte) 0x0, (byte) 0x6, (byte) 0x35, (byte) 0x66, (byte) 0x6c, (byte) 0x6f, (byte) 0x61, (byte) 0x74,  // "5float"
            (byte) 0x40, (byte) 0x49, (byte) 0xf, (byte) 0xdb, // float value of math constant PI
            (byte) 0x4, // long
            (byte) 0x0, (byte) 0x5, (byte) 0x34, (byte) 0x6c, (byte) 0x6f, (byte) 0x6e, (byte) 0x67, // "4long"
            (byte) 0x80, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, // i64::min_value()
            (byte) 0x6, // double
            (byte) 0x0, (byte) 0x7, (byte) 0x36, (byte) 0x64, (byte) 0x6f, (byte) 0x75, (byte) 0x62, (byte) 0x6c, (byte) 0x65, // "6double"
            (byte) 0x40, (byte) 0x5, (byte) 0xbf, (byte) 0xa, (byte) 0x8b, (byte) 0x14, (byte) 0x57, (byte) 0x69, // float value of math constant E
            (byte) 0x2, // short
            (byte) 0x0, (byte) 0x6, (byte) 0x32, (byte) 0x73, (byte) 0x68, (byte) 0x6f, (byte) 0x72, (byte) 0x74, // "2short"
            (byte) 0x7f, (byte) 0xff, // i16::max_value()
            (byte) 0x3, // int
            (byte) 0x0, (byte) 0x4, (byte) 0x33, (byte) 0x69, (byte) 0x6e, (byte) 0x74, // "3int"
            (byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff, // i16::max_value()
            (byte) 0xa, // compound #2
            (byte) 0x0, (byte) 0x9, (byte) 0x31, (byte) 0x63, (byte) 0x6f, (byte) 0x6d, (byte) 0x70, (byte) 0x6f, (byte) 0x75, (byte) 0x6e, (byte) 0x64, // "1compound"
            (byte) 0x5, // float
            (byte) 0x0, (byte) 0xb, (byte) 0x31, (byte) 0x31, (byte) 0x66, (byte) 0x6c, (byte) 0x6f, (byte) 0x61, (byte) 0x74, (byte) 0x5f, (byte) 0x31, (byte) 0x2e, (byte) 0x30, // "11float_1.0"
            (byte) 0x3f, (byte) 0x80, (byte) 0x0, (byte) 0x0, // 1.0
            (byte) 0x6, // double
            (byte) 0x0, (byte) 0xd, (byte) 0x31, (byte) 0x32, (byte) 0x64, (byte) 0x6f, (byte) 0x75, (byte) 0x62, (byte) 0x6c, (byte) 0x65, (byte) 0x5f, (byte) 0x2d, (byte) 0x31, (byte) 0x2e, (byte) 0x30, // "12double_-1.0"
            (byte) 0xbf, (byte) 0xf0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, // -1.0
            (byte) 0x0, // end of #2
            (byte) 0xb, // int array
            (byte) 0x0, (byte) 0xa, (byte) 0x32, (byte) 0x69, (byte) 0x6e, (byte) 0x74, (byte) 0x5f, (byte) 0x61, (byte) 0x72, (byte) 0x72, (byte) 0x61, (byte) 0x79, // "2int_array"
            (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x4, // len: 4
            (byte) 0x1a, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
            (byte) 0x2b, (byte) 0xbb, (byte) 0xbb, (byte) 0xbb,
            (byte) 0x2c, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc,
            (byte) 0x1d, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd,
            (byte) 0x0, // end of #1
    };

    private byte[] small = new byte[] {
            0xa, // compound #1
            0x0, 0x5, 0x68, 0x65, 0x6c, 0x6c, 0x6f, // "hello"
            0x1, // byte
            0x0, 0x4, 0x62, 0x79, 0x74, 0x65, // "byte"
            (byte)0x80, // -128
            0x0, // end of #1
    };

    @DisplayName("read_nbt_big")
    @Test
    void testReadBig() throws Exception {
        testOne(big);
    }

    @DisplayName("read_nbt_small")
    @Test
    void testReadSmall() throws Exception {
        testOne(small);
    }

    private void testOne(byte[] input) throws Exception {
        List<Long> time = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ByteArrayInputStream bis = new ByteArrayInputStream(input);
            DataInputStream is = new DataInputStream(bis);
            long start_time = System.nanoTime();
            NbtIo.read(is);
            long end_time = System.nanoTime();
            time.add(end_time - start_time);
        }
        LongSummaryStatistics stats = time.stream().mapToLong((a) -> a).summaryStatistics();
        double avg = stats.getAverage();
        double d = Math.min(stats.getMax() - avg, avg - stats.getMin());
        System.out.println(String.format("%f ns (+/- %f)", avg, d));
        System.out.println("stats: "+stats);
    }

}
