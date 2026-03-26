package com.example.attendance.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * application.properties からDB接続情報を読み込むユーティリティクラス
 */
public class DatabaseConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("application.properties が見つかりません");
            }
            props.load(input);
            System.out.println("application.properties の読み込みに成功しました");
        } catch (IOException e) {
            System.err.println("ERROR: application.properties の読み込みに失敗しました");
            System.err.println("原因: " + e.getMessage());
            // 起動は継続するが接続情報が取得できない状態になる
        }
    }

    public static String getUrl() {
        return props.getProperty("db.url");
    }

    public static String getUsername() {
        return props.getProperty("db.username");
    }

    public static String getPassword() {
        return props.getProperty("db.password");
    }

    public static String getDriver() {
        return props.getProperty("db.driver");
    }
}