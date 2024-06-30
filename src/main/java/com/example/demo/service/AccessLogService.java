package com.example.demo.service;

import com.example.demo.model.AccessLog;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    public List<Object[]> getAccessLogStats() {
        Query query = entityManager.createQuery(
                "SELECT DATE(a.timestamp), COUNT(a) " +
                        "FROM AccessLog a " +
                        "GROUP BY DATE(a.timestamp) " +
                        "ORDER BY DATE(a.timestamp)");
        return query.getResultList();
    }
}
