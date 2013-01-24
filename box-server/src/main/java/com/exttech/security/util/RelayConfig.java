package com.exttech.security.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RelayConfig {

    public static final String URL_RELAY_SERVER;
    public static final String FILE_PATH;
    public static final int FILE_SIZE;
    public static final int POOL_CORE_SIZE;
    public static final int SLEEP_TIME;


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
        URL_RELAY_SERVER = properties.getProperty("relay.server.url");
        FILE_PATH = properties.getProperty("file.path");
        FILE_SIZE = Integer.valueOf(properties.getProperty("file.size", "512"));
        POOL_CORE_SIZE = Integer.valueOf(properties.getProperty("pool.core.size"));
        SLEEP_TIME = Integer.valueOf(properties.getProperty("sleep.time"));
    }

    public static void main(String[] args) {
        System.out.println(RelayConfig.URL_RELAY_SERVER);
    }
}
