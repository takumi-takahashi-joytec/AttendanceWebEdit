-- 1. 社員マスタ
CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id CHAR(5) NOT NULL UNIQUE,
    employee_name VARCHAR(50) NOT NULL,
    is_active INTEGER NOT NULL DEFAULT 0,  -- 0=無効、1=有効
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by CHAR(5) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by CHAR(5) NOT NULL,
    INDEX idx_employee_id (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 勤怠ステータスマスタ
CREATE TABLE attendance_statuses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active INTEGER NOT NULL DEFAULT 0,  -- 0=無効、1=有効
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by CHAR(5) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by CHAR(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 勤怠実績
CREATE TABLE attendance_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id CHAR(5) NOT NULL,
    work_date DATE NOT NULL,
    start_time TIME NULL,
    end_time TIME NULL,
    status_id INTEGER NOT NULL DEFAULT 0,  -- 0=通常出勤
    note VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by CHAR(5) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by CHAR(5) NOT NULL,
    UNIQUE KEY uk_employee_date (employee_id, work_date),
    INDEX idx_employee_id (employee_id),
    INDEX idx_work_date (work_date),
    CONSTRAINT fk_records_employee 
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_records_status 
        FOREIGN KEY (status_id) REFERENCES attendance_statuses(id) 
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;