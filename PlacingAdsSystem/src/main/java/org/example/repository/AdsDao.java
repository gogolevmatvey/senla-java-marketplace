package org.example.repository;

import jakarta.persistence.EntityManagerFactory;
import org.example.model.Ads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AdsDao extends GenericDao<Ads>{
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public AdsDao(EntityManagerFactory entityManagerFactory) {
        super(Ads.class, entityManagerFactory);
    }
}
