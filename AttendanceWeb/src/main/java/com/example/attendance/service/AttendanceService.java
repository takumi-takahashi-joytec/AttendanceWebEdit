package com.example.attendance.service;

import com.example.attendance.dao.EmployeeDAO;
import com.example.attendance.dao.RecordDAO;
import com.example.attendance.dao.StatusDAO;
import com.example.attendance.model.Employee;
import com.example.attendance.model.Record;
import com.example.attendance.model.Status;

import java.util.List;

public class AttendanceService {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final RecordDAO recordDAO = new RecordDAO();
    private final StatusDAO statusDAO = new StatusDAO();

    public Employee getEmployeeById(String employeeId) {
        return employeeDAO.findByEmployeeId(employeeId);
    }

    public List<Status> getActiveStatuses() {
        return statusDAO.findActiveStatuses();
    }

    public boolean recordExists(String employeeId, String date) {
        return recordDAO.findByEmployeeIdAndDate(employeeId, date) != null;
    }

    public Record getRecord(String employeeId, String date) {
        return recordDAO.findByEmployeeIdAndDate(employeeId, date);
    }
    public void insertRecord(Record record) {
        recordDAO.insert(record);
    }

    public void updateRecord(Record record) {
        recordDAO.update(record);
    }

    public void deleteRecord(String employeeId, String date) {
        recordDAO.deleteByEmployeeIdAndDate(employeeId, date);
    }
}