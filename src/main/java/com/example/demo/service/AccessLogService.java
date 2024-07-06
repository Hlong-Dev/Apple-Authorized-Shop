package com.example.demo.service;

import com.example.demo.model.AccessLog;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AccessLogService {

    @PersistenceContext
    private EntityManager entityManager;

    public void logAccess(String action) {
        AccessLog accessLog = new AccessLog();
        accessLog.setTimestamp(LocalDateTime.now());
        accessLog.setAction(action);

        entityManager.persist(accessLog);
    }
    public long getAccessLogCount() {
        Query query = entityManager.createQuery("SELECT COUNT(a) FROM AccessLog a");
        return (long) query.getSingleResult();
    }
    public List<Object[]> getAccessLogStatsByMonthYear() {
        Query query = entityManager.createQuery(
                "SELECT DAY(a.timestamp), COUNT(a) " +
                        "FROM AccessLog a " +
                        "WHERE YEAR(a.timestamp) = :year AND MONTH(a.timestamp) = :month " +
                        "GROUP BY DAY(a.timestamp) " +
                        "ORDER BY DAY(a.timestamp)");

        // Thiết lập các tham số year và month
        int year = LocalDate.now().getYear(); // Năm hiện tại
        int month = LocalDate.now().getMonthValue(); // Tháng hiện tại
        query.setParameter("year", year);
        query.setParameter("month", month);

        return query.getResultList();
    }

}
