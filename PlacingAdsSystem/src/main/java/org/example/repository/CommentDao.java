package org.example.repository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.example.model.Comment;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDao extends GenericDao<Comment>{
    public CommentDao(EntityManagerFactory entityManagerFactory) {
        super(Comment.class, entityManagerFactory);
    }

    public boolean hasUserCommented(Long adsId, Long userId) {
        String hql = "SELECT COUNT(c) > 0 FROM Comment c WHERE c.ads.id = :adsId AND c.user.id = :userId";
        TypedQuery<Boolean> query = entityManager.createQuery(hql, Boolean.class);
        query.setParameter("adsId", adsId);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }
}
