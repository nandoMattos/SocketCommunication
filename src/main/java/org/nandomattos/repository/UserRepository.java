package org.nandomattos.repository;

import org.hibernate.Transaction;
import org.nandomattos.entity.User;
import org.nandomattos.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class UserRepository {
    private static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public static void save(User user) {
        Transaction transaction = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.persist(user);
            transaction.commit();
        }catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
        }
    }

    public static void update(User user) {
        Transaction transaction = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.merge(user);
            transaction.commit();
        }catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
        }
    }

    public static List<User> findAll() {
        Transaction transaction = null;
        List<User> users = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            users = session.createQuery("from User").list();
            transaction.commit();

        }catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
        }
        return users;
    }

    public static User findByRa(String ra) {
        Transaction transaction = null;
        User user = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            user = session.createQuery("FROM User WHERE ra = :ra", User.class).setParameter("ra", ra).uniqueResult();
            transaction.commit();

        }catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
        }
        return user;
    }
}
