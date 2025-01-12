package org.example.repository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.example.dto.SaleHistoryDto;
import org.example.model.SaleHistory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SaleHistoryDao extends GenericDao<SaleHistory> {
    public SaleHistoryDao(EntityManagerFactory entityManagerFactory) {
        super(SaleHistory.class, entityManagerFactory);
    }

    public List<SaleHistory> findSalesByUser(Long userId) {
        String hql = "FROM SaleHistory WHERE seller.id = :userId";
        TypedQuery<SaleHistory> query = entityManager.createQuery(hql, SaleHistory.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}
