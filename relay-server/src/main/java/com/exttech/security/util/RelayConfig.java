package com.exttech.security.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RelayConfig {

    public static final String URL_BOX;
    public static final String TMP_DIR;

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
        TMP_DIR = properties.getProperty("tmp.dir");
    }
}
