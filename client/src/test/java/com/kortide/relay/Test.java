package com.kortide.relay;


import java.io.IOException;
import java.io.RandomAccessFile;

public class Test {
    public static void main(String[] args) throws IOException {
        RandomAccessFile out = new RandomAccessFile("/tmp/relay-debug/a.txt", "rw");
        out.write("123456789".getBytes());
        out.seek(3);
        out.write("test".getBytes());
        out.close();
    }
}
