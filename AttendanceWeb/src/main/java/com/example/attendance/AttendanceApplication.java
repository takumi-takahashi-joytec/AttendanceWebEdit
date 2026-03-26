package com.example.attendance;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * アプリケーションのコンテキストリスナー。
 * 起動時に application.properties からDB接続情報を読み込み、
 * DB接続テストを行い、停止時にログを出力します。
 */
@WebListener
public class AttendanceApplication implements ServletContextListener {

    private static final Properties props = new Properties();

    // 静的初期化ブロックで application.properties を読み込み + ドライバ登録
    static {
        System.out.println("[DB-INIT] application.properties 読み込み開始");

        InputStream input = null;
        try {
            input = AttendanceApplication.class.getClassLoader()
                    .getResourceAsStream("application.properties");
            if (input == null) {
                System.err.println("[DB-INIT] ERROR: application.properties が見つかりません");
                System.err.println("[DB-INIT] 期待される場所: src/main/resources/application.properties");
                throw new RuntimeException("application.properties not found");
            }
            props.load(input);
            System.out.println("[DB-INIT] application.properties の読み込みに成功しました");
        } catch (IOException e) {
            System.err.println("[DB-INIT] ERROR: application.properties の読み込みに失敗しました");
            System.err.println("[DB-INIT] 原因: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load application.properties", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {}
            }
        }

        // MySQL JDBCドライバを明示的にロード & DriverManagerに登録
        String driverClassName = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        try {
            Class<?> driverClass = Class.forName(driverClassName);
            System.out.println("[DB-INIT] JDBCドライバクラスロード成功: " + driverClassName);

            // Driverインスタンスを作成してDriverManagerに登録（Tomcat 10のクラスローダー問題対策）
            java.sql.Driver driverInstance = (java.sql.Driver) driverClass.getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(driverInstance);
            System.out.println("[DB-INIT] MySQL JDBC Driver を DriverManager に明示的に登録しました");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB-INIT] ERROR: JDBCドライバクラスが見つかりません: " + driverClassName);
            System.err.println("[DB-INIT] 原因: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("JDBC driver class not found", e);
        } catch (Exception e) {
            System.err.println("[DB-INIT] ERROR: Driverインスタンス作成または登録に失敗: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Driver registration failed", e);
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== 勤怠管理システム 起動開始 ===");
        System.out.println("Tomcatコンテキスト初期化開始");

        String dbUrl = props.getProperty("db.url");
        String dbUsername = props.getProperty("db.username");
        String dbPassword = props.getProperty("db.password");

        if (dbUrl == null || dbUsername == null || dbPassword == null) {
            System.err.println("[DB-INIT] ERROR: application.properties からDB接続情報が取得できませんでした");
            System.err.println("[DB-INIT] 必要なキー: db.url, db.username, db.password");
            return;
        }

        System.out.println("[DB-TEST] 接続テスト開始...");
        System.out.println("[DB-TEST] URL: " + dbUrl);
        System.out.println("[DB-TEST] Username: " + dbUsername);
        // パスワードはログに出さない

        testDatabaseConnection(dbUrl, dbUsername, dbPassword);

        System.out.println("Tomcatコンテキスト初期化完了");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== 勤怠管理システム 停止 ===");
    }

    /**
     * MySQL接続テストを実行
     */
    private void testDatabaseConnection(String url, String username, String password) {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            boolean isValid = conn.isValid(5);
            if (isValid) {
                System.out.println("[DB-TEST] MySQL接続テスト：成功");
                System.out.println("[DB-TEST] 接続URL: " + url);
                System.out.println("アプリケーションURL: http://localhost:8080/AttendanceSystem/");
                System.out.println("勤怠管理システムが正常に起動しました。");
            } else {
                System.err.println("[DB-TEST] MySQL接続テスト：失敗 (isValid=false)");
            }
        } catch (SQLException e) {
            System.err.println("[DB-TEST] DB接続エラー: " + e.getMessage());
            System.err.println("[DB-TEST] SQLState: " + e.getSQLState());
            System.err.println("[DB-TEST] VendorErrorCode: " + e.getErrorCode());
            e.printStackTrace(); // 詳細スタックトレースを出力
        } catch (Exception e) {
            System.err.println("[DB-TEST] 予期せぬ例外: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 他のクラスからDB接続情報を取得するためのgetter
    public static String getDbUrl() {
        return props.getProperty("db.url");
    }

    public static String getDbUsername() {
        return props.getProperty("db.username");
    }

    public static String getDbPassword() {
        return props.getProperty("db.password");
    }

    public static String getDbDriver() {
        return props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }
}