package com.kortide.relay.client;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxingyu
 * Date: 1/18/13
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class DownloadMain {

    public static void main(String[] args) {
        String url = "http://img0.itiexue.net/860/8602788.jpg";
        String
        for (int xuhao = 1; xuhao < 30; xuhao++) {
//            dlURL = "http://www.bz55.com/uploads1/allimg/120420/1_120420111352_" + Integer.toString(xuhao) + ".jpg";
            SaveDir = saveFileDirAs;
            DownloadMultiThread FileDN = new DownloadMultiThread(dlURL, SaveDir);
            ;
            FileDN.start();
            System.out.println("\n" + dlURL + " main thread is started.");

        }

    }
}
