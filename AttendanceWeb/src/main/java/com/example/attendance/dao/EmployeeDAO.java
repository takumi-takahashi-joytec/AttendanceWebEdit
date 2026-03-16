package com.example.attendance.dao;

import com.example.attendance.AttendanceApplication;
import com.example.attendance.model.Employee;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeDAO {
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
     * 社員IDで社員情報を取得（有効な社員のみ）
     * @param employeeId 社員ID (CHAR(5))
     * @return Employeeオブジェクト（存在しない or 無効の場合はnull）
     */
    public Employee findByEmployeeId(String employeeId) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlConstants.EMPLOYEE_FIND_BY_ID)) {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Employee emp = new Employee();
                    emp.setId(rs.getInt("id"));
                    emp.setEmployeeId(rs.getString("employee_id"));
                    emp.setEmployeeName(rs.getString("employee_name"));
                    emp.setActiveFlag(rs.getInt("is_active"));
                    emp.setCreatedBy(rs.getString("created_by"));
                    emp.setUpdatedBy(rs.getString("updated_by"));
                    emp.setCreatedAt(rs.getTimestamp("created_at"));
                    emp.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return emp;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}