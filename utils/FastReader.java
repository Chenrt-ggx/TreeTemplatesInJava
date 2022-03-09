package utils;

import java.io.IOException;
import java.io.InputStream;

public class FastReader {
    private int next;
    private int length;
    private int offset;
    private final byte[] buffer;
    private final InputStream inputStream;

    public FastReader(InputStream inputStream) {
        this.inputStream = inputStream;
        this.buffer = new byte[1 << 16];
    }

    public int getChar() {
        while (length == offset) {
            offset = 0;
            try {
                length = inputStream.read(buffer);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer[offset++];
    }

    public int readInt() {
        boolean positive = true;
        while (0 <= next && next <= 32) {
            next = getChar();
        }
        if (next == '+' || next == '-') {
            positive = next == '+';
            next = getChar();
        }
        int val = 0;
        while (next >= '0' && next <= '9') {
            val = val * 10 + next - '0';
            next = getChar();
        }
        return positive ? val : -val;
    }

    public String asString() {
        StringBuilder builder = new StringBuilder();
        while (true) {
            while (length == offset) {
                offset = 0;
                try {
                    length = inputStream.read(buffer);
                    if (length == -1) {
                        return builder.toString();
                    }
                }
                catch (IOException e) {
                    return builder.toString();
                }
            }
            builder.append((char)buffer[offset++]);
        }
    }
}