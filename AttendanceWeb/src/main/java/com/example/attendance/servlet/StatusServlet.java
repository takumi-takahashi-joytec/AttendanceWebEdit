package com.example.attendance.servlet;

import com.example.attendance.model.Status;
import com.example.attendance.service.AttendanceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

/**
 * 勤怠ステータス一覧を取得する専用Servlet。
 * 
 * <p>主な用途：
 * <ul>
 *   <li>勤怠登録画面のステータス選択ドロップダウン用データ提供</li>
 * </ul>
 * 
 * <p>特徴：
 * <ul>
 *   <li>認証不要の公開API（機密情報は含まない）</li>
 *   <li>レスポンスはJSON形式でキャッシュ可能</li>
 * </ul>
 * 
 * @author takahashi
 * @version 1.0
 * @since 2025-12
 */
@WebServlet("/status")
public class StatusServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AttendanceService attendanceService = new AttendanceService();
    private final Gson gson = new Gson();

    /**
     * GETリクエストを受け取り、有効なステータス一覧をJSON形式で返却します。
     * 
     * <p>例：
     * <pre>
     * [
     *   {"id":1,"name":"通常出勤"},
     *   {"id":2,"name":"遅刻"},
     *   ...
     * ]
     * </pre>
     * 
     * @param req  HTTPリクエスト
     * @param resp HTTPレスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException      IO例外
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 有効なステータスのみ取得（is_active = 1 のレコード）
        List<Status> statusList = attendanceService.getActiveStatuses();

        // レスポンスヘッダ設定
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // キャッシュ許可（ステータスマスタは変更頻度が低いため）
        // 本番では適切なmax-ageを設定（例: 3600秒 = 1時間）
        resp.setHeader("Cache-Control", "max-age=3600, public");

        // JSON出力
        resp.getWriter().write(gson.toJson(statusList));
    }
}