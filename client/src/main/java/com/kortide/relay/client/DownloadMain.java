package com.kortide.relay.client;

import com.kortide.relay.client.download.MutilThreadDownloader;

public class DownloadMain {

    public static void main(String[] args) {
        String url = "http://img0.itiexue.net/860/8602788.jpg";
        String savePath = "/tmp/relay-debug/";
//        for (int i = 1; i < 30; i++) {
        MutilThreadDownloader downloader = new MutilThreadDownloader(url, savePath);
        downloader.start();

//        }
    }
}
