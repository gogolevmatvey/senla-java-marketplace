package org.example.repository;

import jakarta.persistence.EntityManagerFactory;
import org.example.model.Message;
import org.springframework.stereotype.Repository;

@Repository
public class MessageDao extends GenericDao<Message> {
    public MessageDao(EntityManagerFactory entityManagerFactory) {
        super(Message.class, entityManagerFactory);
    }
}
