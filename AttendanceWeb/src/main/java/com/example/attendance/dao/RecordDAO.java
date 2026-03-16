package com.example.attendance.dao;

import com.example.attendance.AttendanceApplication;
import com.example.attendance.model.Record;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecordDAO {
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
     * 指定社員・指定日の勤怠データを取得
     */
    public Record findByEmployeeIdAndDate(String employeeId, String date) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlConstants.RECORD_FIND_BY_EMPLOYEE_AND_DATE)) {
            pstmt.setString(1, employeeId);
            pstmt.setString(2, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Record record = new Record();
                    record.setId(rs.getInt("id"));
                    record.setEmployeeId(rs.getString("employee_id"));
                    record.setWorkDate(rs.getDate("work_date"));
                    record.setStartTime(rs.getTime("start_time"));
                    record.setEndTime(rs.getTime("end_time"));
                    record.setStatusId(rs.getInt("status_id"));
                    record.setNote(rs.getString("note"));
                    record.setCreatedBy(rs.getString("created_by"));
                    record.setUpdatedBy(rs.getString("updated_by"));
                    record.setCreatedAt(rs.getTimestamp("created_at"));
                    record.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return record;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 勤怠データ登録
     */
    public void insert(Record record) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlConstants.RECORD_INSERT)) {
            setRecordParameters(pstmt, record);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 勤怠データ更新
     */
    public void update(Record record) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlConstants.RECORD_UPDATE)) {
            pstmt.setTime(1, record.getStartTime());
            pstmt.setTime(2, record.getEndTime());
            pstmt.setInt(3, record.getStatusId());
            pstmt.setString(4, record.getNote());
            pstmt.setString(5, record.getUpdatedBy());
            pstmt.setString(6, record.getEmployeeId());
            pstmt.setDate(7, record.getWorkDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 勤怠データ削除
     */
    public void deleteByEmployeeIdAndDate(String employeeId, String date) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlConstants.RECORD_DELETE)) {
            pstmt.setString(1, employeeId);
            pstmt.setString(2, date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setRecordParameters(PreparedStatement pstmt, Record record) throws SQLException {
        pstmt.setString(1, record.getEmployeeId());
        pstmt.setDate(2, record.getWorkDate());
        pstmt.setTime(3, record.getStartTime());
        pstmt.setTime(4, record.getEndTime());
        pstmt.setInt(5, record.getStatusId());
        pstmt.setString(6, record.getNote());
        pstmt.setString(7, record.getCreatedBy());
        pstmt.setString(8, record.getUpdatedBy());
    }
}