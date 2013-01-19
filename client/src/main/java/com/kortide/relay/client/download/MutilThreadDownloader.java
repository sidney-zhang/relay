package com.kortide.relay.client.download;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MutilThreadDownloader extends Thread {

    private static final int BLOCK_SIZE = 1024 * 5;

    private String urlStr;
    private String path;

    private URL url;
    private int fileSize;
    private int threadNum;
    private String fileName;
    private File fileOut;
    private FileWriter fileWriter;
    private PrintWriter fileUrlOut;


    public MutilThreadDownloader(String urlStr, String path) {
        this.urlStr = urlStr;
        this.path = path;
        HttpURLConnection connection = null;
        try {
            this.url = new URL(urlStr);
            connection = (HttpURLConnection) this.url.openConnection();
            fileSize = connection.getContentLength();
            int responseCode = connection.getResponseCode();
            if (responseCode > 400) {
                System.out.println("could not get file size.");
            } else {
                System.out.println("response code:" + responseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("file size: " + fileSize);

        if (fileSize % BLOCK_SIZE != 0) {
            threadNum = fileSize / BLOCK_SIZE + 1;
        } else {
            threadNum = fileSize / BLOCK_SIZE;
        }

        String u = connection.getURL().toString();

        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1);

        fileOut = new File(path, fileName);
    }

    @Override
    public void run() {
        SingleThreadDownloader[] downloaders = new SingleThreadDownloader[threadNum];
        try {
            long subLen = (long)Math.ceil((double)this.fileSize/(double)this.threadNum);
            long end = 0;
            long start = 0;
            for (int i = 0; i < threadNum; i++) {
                start = subLen * i;
                end = start + BLOCK_SIZE - 1;
                if (end > fileSize) {
                    end = fileSize;
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(fileOut, "rw");
                randomAccessFile.seek(start);
                downloaders[i] = new SingleThreadDownloader(url, start, end, i + 1, randomAccessFile);
                downloaders[i].setPriority(7);
                downloaders[i].start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //waiting for download
        boolean done = Boolean.FALSE;
        while (!done) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            done = Boolean.TRUE;

            for (int i = 0; i < threadNum; i++) {
                if (!downloaders[i].isDone()) {
                    done = Boolean.FALSE;
                    break;
                }
            }
        }
        System.out.print("finish save path to path:" + fileOut.getParent());
    }
}
