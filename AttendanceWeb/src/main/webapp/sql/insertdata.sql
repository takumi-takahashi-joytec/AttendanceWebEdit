-- 管理者データ作成
INSERT INTO employees (employee_id, employee_name, is_active, created_by, updated_by) VALUES 
('99999', '管理者', 1, '99999', '99999'),

-- テスト社員データ挿入(実装時挿入したID)
INSERT INTO employees (employee_id, employee_name, is_active, created_by, updated_by) VALUES 
('00001', 'テスト社員', 1, '99999', '99999'),

-- テスト社員データ挿入(試験用追加ID）
INSERT INTO employees (employee_id, employee_name, is_active, created_by, updated_by) VALUES 
('00011', 'テスト次郎', 1, '99999', '99999'),
('00121', 'テスト三郎', 1, '99999', '99999');

-- テスト勤怠データ挿入(試験用）
INSERT INTO attendance_records (employee_id, work_date, start_time, end_time, status_id, note, created_by, updated_by) VALUES
('00011', '2025-11-30', '10:00:00', '18:00:00', '1', 'test_past_date_01', '00011','00011'),
('00011', '2026-02-01', '09:00:00', '18:00:00', '1', 'test_future_date_01', '00011','00011');

-- 勤怠ステータスデータ挿入
INSERT INTO attendance_statuses (code, name, display_order, is_active, created_by, updated_by) VALUES
('01', '通常出勤', 1, 1, 99999, 99999),
('02', '遅刻', 2, 1, 99999, 99999),
('03', '早退', 3, 1, 99999, 99999),
('04', '遅刻・早退', 4, 1, 99999, 99999),
('05', '明け休', 5, 0, 99999, 99999),  -- 無効
('06', '代休', 6, 1, 99999, 99999),
('07', '有給', 7, 1, 99999, 99999),
('08', '社休日', 8, 1, 99999, 99999),
('09', '特別休', 9, 1, 99999, 99999),
('10', '欠勤', 10, 1, 99999, 99999),
('11', '休職', 11, 1, 99999, 99999),
('99', 'その他', 12, 1, 99999, 99999);