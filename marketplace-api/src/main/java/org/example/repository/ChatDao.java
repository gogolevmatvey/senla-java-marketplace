package org.example.repository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.example.model.Chat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ChatDao extends GenericDao<Chat> {
    private static final Logger logger = LoggerFactory.getLogger(ChatDao.class);

    public ChatDao(EntityManagerFactory entityManagerFactory) {
        super(Chat.class, entityManagerFactory);
    }

    public Chat findByAdsAndBuyer(Long adsId, Long buyerId) {
        String hql = "FROM Chat c WHERE c.ads.id = :adsId AND c.buyer.id = :buyerId";

        try {
            TypedQuery<Chat> query = entityManager.createQuery(hql, Chat.class);

            query.setParameter("adsId", adsId);
            query.setParameter("buyerId", buyerId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.error("No chat found for ads {} and buyer {}", adsId, buyerId);
            return null;
        }
    }
}
