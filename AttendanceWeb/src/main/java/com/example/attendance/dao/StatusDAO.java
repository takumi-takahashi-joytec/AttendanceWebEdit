package com.example.attendance.dao;

import com.example.attendance.AttendanceApplication;
import com.example.attendance.model.Status;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatusDAO {
	   /**
     * DB接続を取得（application.propertiesから読み込んだ情報を利用）
     */
    private Connection getConnection() throws SQLException {
        String url      = AttendanceApplication.getDbUrl();
        String username = AttendanceApplication.getDbUsername();
        String password = AttendanceApplication.getDbPassword();
        String driver   = AttendanceApplication.getDbDriver();

        // ドライバロード（安全策）
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBCドライバが見つかりません: " + driver, e);
        }

        return DriverManager.getConnection(url, username, password);
    }

    /**
     * 有効な勤怠ステータス一覧を取得
     * @return 有効なステータスリスト（is_active = 1）
     */
    public List<Status> findActiveStatuses() {
        List<Status> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlConstants.STATUS_FIND_ACTIVE);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Status status = new Status();
                status.setId(rs.getInt("id"));
                status.setCode(rs.getString("code"));
                status.setName(rs.getString("name"));
                status.setDisplayOrder(rs.getInt("display_order"));
                status.setActiveFlag(rs.getInt("is_active"));
                status.setCreatedAt(rs.getTimestamp("created_at"));
                status.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}