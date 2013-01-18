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
    private long block;
    private int threadNo;
    private RandomAccessFile out;
    private int blockSize;


    public SingleThreadDownloader(URL url, long block, int blockSize, int threadCount, RandomAccessFile out) {
        this.url = url;
        this.block = block;
        this.blockSize = blockSize;
        this.threadNo = threadCount;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Range", "bytes=" + block * (threadNo - 1));
            in = connection.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buffer = new byte[blockSize];
        int offset = 0;
        int receivedSize = 0;
        try {
            while ((offset = in.read(buffer)) != -1 && receivedSize <= block) {
                out.write(buffer, 0, offset);
                receivedSize += offset;

            }
            done = Boolean.TRUE;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
        }
    }

    public boolean isDone() {
        return done;
    }
}
