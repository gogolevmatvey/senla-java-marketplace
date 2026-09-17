package org.example.repository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

@Repository
public class UserDao extends GenericDao<User> {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public UserDao(EntityManagerFactory entityManagerFactory) {
        super(User.class, entityManagerFactory);
    }

    public User findUserByUsername(String username) {
        try {
            String hql = "FROM User WHERE username = :username";
            TypedQuery<User> query = entityManager.createQuery(hql, User.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.error("Пользователь {} не найден: {}", username, e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя {}: {}", username, e.getMessage());
            return null;
        }
    }

    public User findUserByEmail(String email) {
        try {
            String hql = "FROM User WHERE email = :email";
            TypedQuery<User> query = entityManager.createQuery(hql, User.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.error("User with email {} not found: {}", email, e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Error finding user with email {}: {}", email, e.getMessage());
            return null;
        }
    }

    public Double calculateAverageSellerRating(Long sellerId) {
        String hql = "SELECT AVG(c.rating) FROM Comment c JOIN c.ads a WHERE a.user.id = :sellerId";
        TypedQuery<Double> query = entityManager.createQuery(hql, Double.class);
        query.setParameter("sellerId", sellerId);
        Double result = query.getSingleResult();
        return result != null ? result : 0.0;
    }
}
