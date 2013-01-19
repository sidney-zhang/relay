package com.kortide.relay.client.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class SingleThreadDownloader extends Thread {

    private boolean done = Boolean.FALSE;

    private InputStream in;

    private URL url;
    private int threadNo;
    private RandomAccessFile out;
    private final long start;
    private final long end;


    public SingleThreadDownloader(URL url, long start, long end, int threadCount, RandomAccessFile out) {
        this.url = url;
        this.threadNo = threadCount;
        this.out = out;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Range", "bytes=" + start + "-" + end);
            in = connection.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
        int total = (int) (end - start + 1);
        byte[] buffer = new byte[total];
        int offset = 0;
        long curPos = start;
        try {
            while (curPos <= end) {
                offset = in.read(buffer);
                if (offset == -1) {
                    break;
                }
                out.write(buffer, 0, offset);
                curPos += offset;
//                System.out.println("total:" + total + ",start:" + start + ",end:" + end + ",offset==" + offset + ", curPos:" + curPos);
            }
            done = Boolean.TRUE;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out = null;
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        System.out.println(threadNo + " finished.");
    }

    public boolean isDone() {
        return done;
    }
}
