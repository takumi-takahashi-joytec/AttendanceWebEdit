package com.example.attendance.servlet;

import com.example.attendance.AttendanceApplication;
import com.example.attendance.dao.SqlConstants;
import com.example.attendance.model.Employee;
import com.example.attendance.model.Record;
import com.example.attendance.service.AttendanceService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 勤怠管理のメインサーブレット
 * actionパラメータで処理を分岐
 */
@WebServlet("/attendance")
public class AttendanceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final AttendanceService service = new AttendanceService();
    private final Gson gson = new Gson();

    /**
     * DB接続を取得（AttendanceApplicationからproperties経由で取得）
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            AttendanceApplication.getDbUrl(),
            AttendanceApplication.getDbUsername(),
            AttendanceApplication.getDbPassword()
        );
    }
    
    //情報取得
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        JsonObject json = new JsonObject();

        try {
            if ("auth".equals(action)) {
                // 社員認証
                String employeeId = req.getParameter("employee_id");
                Employee employee = service.getEmployeeById(employeeId);
                if (employee != null) {
                    json.addProperty("success", true);
                    json.addProperty("name", employee.getEmployeeName());
                } else {
                    json.addProperty("success", false);
                }

            } else if ("check".equals(action)) {
                // 年月日の登録済みチェック
                String employeeId = req.getParameter("employee_id");
                String date = req.getParameter("date");
                boolean exists = service.recordExists(employeeId, date);
                json.addProperty("exists", exists);

            } else if ("fetch".equals(action)) {
                // 削除確認用：既存勤怠データの取得（SQLインジェクション対策済み）
                String employeeId = req.getParameter("employee_id");
                String date = req.getParameter("date");
                if (employeeId == null || date == null) {
                    json.addProperty("error", "employee_id と date は必須です");
                } else {
                    try (Connection conn = getConnection()) {
                        // SQLインジェクション対策：PreparedStatement + プレースホルダ使用
                        try (PreparedStatement pstmt = conn.prepareStatement(SqlConstants.RECORD_FIND_BY_ID_AND_DATE)) {
                            pstmt.setString(1, employeeId);
                            pstmt.setString(2, date);

                            try (ResultSet rs = pstmt.executeQuery()) {
                                if (rs.next()) {
                                    json.addProperty("start_time", rs.getString("start_time") != null ? rs.getString("start_time").substring(0, 5) : "");
                                    json.addProperty("end_time", rs.getString("end_time") != null ? rs.getString("end_time").substring(0, 5) : "");
                                    json.addProperty("status_name", rs.getString("status_name") != null ? rs.getString("status_name") : "");
                                    json.addProperty("note", rs.getString("note") != null ? rs.getString("note") : "");
                                } else {
                                    // 未登録の場合、空の値を返す
                                    json.addProperty("start_time", "");
                                    json.addProperty("end_time", "");
                                    json.addProperty("status_name", "");
                                    json.addProperty("note", "");
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        json.addProperty("error", "データベースエラー: " + e.getMessage());
                    }
                }
            } else {
                json.addProperty("success", false);
                json.addProperty("message", "無効なアクション");
            }

        } catch (Exception e) {
            json.addProperty("success", false);
            json.addProperty("message", "サーバーエラー: " + e.getMessage());
            e.printStackTrace();
        }

        // try-catchの外側でJSON出力（必ず返す）
        out.write(gson.toJson(json));
        out.flush();
    }
    
    //登録・編集
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        JsonObject json = new JsonObject();

        try {
            if ("register".equals(action) || "edit".equals(action)) {
                Record record = new Record();
                record.setEmployeeId(req.getParameter("employee_id"));
                record.setWorkDate(java.sql.Date.valueOf(req.getParameter("work_date")));
                
                String start = req.getParameter("start_time");
                String end = req.getParameter("end_time");
                
                record.setStartTime(start != null && !start.trim().isEmpty()
                        ? java.sql.Time.valueOf(start) : null); 
                    record.setEndTime(end != null && !end.trim().isEmpty()
                        ? java.sql.Time.valueOf(end) : null);
                record.setStatusId(Integer.parseInt(req.getParameter("status_id")));
                record.setNote(req.getParameter("note"));
                
                String userId = req.getParameter("employee_id");  
                record.setCreatedBy(userId);     // 新規登録時
                record.setUpdatedBy(userId);     //更新時

                if ("register".equals(action)) {
                    service.insertRecord(record);
                    json.addProperty("success", true);
                    json.addProperty("message", "登録に成功しました");
                } else if ("edit".equals(action)) {
                    service.updateRecord(record);
                    json.addProperty("success", true);
                    json.addProperty("message", "編集に成功しました");
                }
            } else {
                json.addProperty("success", false);
                json.addProperty("message", "無効なアクション");
            }
        } catch (Exception e) {
            json.addProperty("success", false);
            json.addProperty("message", e.getMessage());
        }

        out.write(gson.toJson(json));
        out.flush();
    }
    
    //削除
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        JsonObject json = new JsonObject();

        if ("delete".equals(action)) {
            String employeeId = req.getParameter("employee_id");
            String date = req.getParameter("date");
            try {
                service.deleteRecord(employeeId, date);
                json.addProperty("success", true);
                json.addProperty("message", "削除に成功しました");
            } catch (Exception e) {
                json.addProperty("success", false);
                json.addProperty("message", e.getMessage());
            }
        } else {
            json.addProperty("success", false);
            json.addProperty("message", "無効なアクション");
        }

        out.write(gson.toJson(json));
        out.flush();
    }
}