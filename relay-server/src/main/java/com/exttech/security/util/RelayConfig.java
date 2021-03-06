package com.exttech.security.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RelayConfig {

    public static final String URL_BOX;
    public static final String URL_DOWNLOAD_BOX;
    public static final String TMP_DIR;
    public static final int POOL_CORE_SIZE;
    public static final int FILE_SIZE;

    public static final int TIMEOUT = 20 * 1000;


    private static final String CONFIG_FILE_PATH = "/config.properties";

    static {
        InputStream in = RelayConfig.class.getResourceAsStream(CONFIG_FILE_PATH);

        Properties properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        URL_BOX = properties.getProperty("box.url");
        URL_DOWNLOAD_BOX = properties.getProperty("box.download.url");
        TMP_DIR = properties.getProperty("tmp.dir");
        POOL_CORE_SIZE = Integer.valueOf(properties.getProperty("pool.core.size"));
        FILE_SIZE = Integer.valueOf(properties.getProperty("file.size"));
    }
}
