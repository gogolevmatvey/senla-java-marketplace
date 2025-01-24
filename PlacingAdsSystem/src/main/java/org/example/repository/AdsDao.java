package org.example.repository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.example.model.Ads;
import org.example.model.AdsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AdsDao extends GenericDao<Ads>{
    private static final Logger logger = LoggerFactory.getLogger(AdsDao.class);

    public AdsDao(EntityManagerFactory entityManagerFactory) {
        super(Ads.class, entityManagerFactory);
    }

    public List<Ads> searchAds(String keyword, String category, Double minPrice, Double maxPrice, AdsStatus status,
                               String priceSort, int page, int size) {
        StringBuilder hql = new StringBuilder("FROM Ads a WHERE 1=1");
        Map<String, Object> parameters = new HashMap<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append(" AND (lower(a.title) like :keyword OR lower(a.description) like :keyword)");
            parameters.put("keyword", "%" + keyword.toLowerCase() + "%");  //% для частичного совпадения
        }

        if (category != null && !category.trim().isEmpty()) {
            hql.append(" AND lower(a.category) = :category");
            parameters.put("category", category.toLowerCase());
        }

        if (minPrice != null) {
            hql.append(" AND a.price >= :minPrice");
            parameters.put("minPrice", minPrice);
        }

        if (maxPrice != null) {
            hql.append(" AND a.price <= :maxPrice");
            parameters.put("maxPrice", maxPrice);
        }

        if (status == AdsStatus.ACTIVE || status == AdsStatus.SOLD)
            parameters.put("status", status);

        hql.append(" AND a.status = :status");

        hql.append(" ORDER BY CASE WHEN a.promoted = true AND a.promotionEndDate >= CURRENT_DATE THEN 1 ELSE 0 END DESC");

        if (priceSort != null) {
            if ("asc".equalsIgnoreCase(priceSort)) {
                hql.append(", a.price ASC");
            } else if ("desc".equalsIgnoreCase(priceSort)) {
                hql.append(", a.price DESC");
            }
        }

        hql.append(", a.user.sellerRating DESC, a.creationDate DESC");

        TypedQuery<Ads> query = entityManager.createQuery(hql.toString(), Ads.class);
        parameters.forEach(query::setParameter);

        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    public Long getTotalCount(String keyword, String category, Double minPrice, Double maxPrice, AdsStatus status) {
        StringBuilder hql = new StringBuilder("SELECT COUNT(a) FROM Ads a WHERE 1=1");
        Map<String, Object> parameters = new HashMap<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append(" AND (lower(a.title) like :keyword OR lower(a.description) like :keyword)");
            parameters.put("keyword", "%" + keyword.toLowerCase() + "%");  //% для частичного совпадения
        }

        if (category != null && !category.trim().isEmpty()) {
            hql.append(" AND lower(a.category) = :category");
            parameters.put("category", category.toLowerCase());
        }

        if (minPrice != null) {
            hql.append(" AND a.price >= :minPrice");
            parameters.put("minPrice", minPrice);
        }

        if (maxPrice != null) {
            hql.append(" AND a.price <= :maxPrice");
            parameters.put("maxPrice", maxPrice);
        }

        if (status == AdsStatus.ACTIVE || status == AdsStatus.SOLD)
            parameters.put("status", status);

        hql.append(" AND a.status = :status");

        TypedQuery<Long> countQuery = entityManager.createQuery(hql.toString(), Long.class);
        parameters.forEach(countQuery::setParameter);

        return countQuery.getSingleResult();
    }
}
