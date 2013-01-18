package com.kortide.relay.client.download;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MutiThreadDownloader extends Thread {

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


    public MutiThreadDownloader(String urlStr, String path) throws Exception {
        this.urlStr = urlStr;
        this.path = path;
        this.url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
        fileSize = connection.getContentLength();
        if (connection.getResponseCode() > 400) {
            System.out.println("response code:" + connection.getResponseCode());
        } else {
            System.out.println("could not get file size.");
        }

        System.out.println("file size: " + fileSize);

        threadNum = fileSize / BLOCK_SIZE + 1;

        String u = connection.getURL().toString();

        new File(path).mkdirs();


        fileOut = new File(path, fileName);
    }

    @Override
    public void run() {
        SingleThreadDownloader[] downloaders = new SingleThreadDownloader[threadNum];
        try {
            fileWriter = new FileWriter("/home/zhangxingyu/relayserver-test/", true);
            fileUrlOut = new PrintWriter(new BufferedWriter(fileWriter));
            for (int i = 0; i < threadNum; i++) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(fileOut, "rw");
                long block = fileSize / threadNum + 1;
                randomAccessFile.seek(block * i);
                downloaders[i] = new SingleThreadDownloader(url, block, BLOCK_SIZE, i, randomAccessFile);
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
