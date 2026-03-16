package com.example.attendance.dao;

public final class SqlConstants {
	  /**
	   * SQLを一元管理するためのクラス
	   */
    private SqlConstants() {} // インスタンス化防止

    public static final String EMPLOYEE_FIND_BY_ID =
        "SELECT * FROM employees WHERE employee_id = ? AND is_active = 1 ";

    public static final String RECORD_FIND_BY_EMPLOYEE_AND_DATE =
        "SELECT * FROM attendance_records WHERE employee_id = ? AND work_date = ?";

    public static final String RECORD_INSERT =
        "INSERT INTO attendance_records (employee_id, work_date, start_time, end_time, status_id, note, created_by, updated_by) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String RECORD_UPDATE =
    		"UPDATE attendance_records SET " +
                    "start_time = ?, " +
                    "end_time = ?, " +
                    "status_id = ?, " +
                    "note = ?, " +
                    "updated_by = ?, " +
                    "updated_at = NOW() " +
                    "WHERE employee_id = ? AND work_date = ?";

    public static final String RECORD_DELETE =
        "DELETE FROM attendance_records WHERE employee_id = ? AND work_date = ?";

    public static final String STATUS_FIND_ACTIVE =
        "SELECT * FROM attendance_statuses WHERE is_active = 1 ORDER BY display_order";
    
    public static final String RECORD_FIND_BY_ID_AND_DATE =
    "SELECT ar.start_time, ar.end_time, ar.note, " +
    " ast.name AS status_name " +
    "FROM attendance_records ar " +
    "LEFT JOIN attendance_statuses ast ON ar.status_id = ast.id " +
    "WHERE ar.employee_id = ? AND ar.work_date = ?";
}