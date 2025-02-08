package org.nandomattos.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.nandomattos.entity.Categoria;
import org.nandomattos.util.HibernateUtil;

import java.util.List;

public class CategoriaRepository {
    private static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public static void save(Categoria categoria) {
        Transaction transaction = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.persist(categoria);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public static void update(Categoria categoria) {
        Transaction transaction = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.merge(categoria);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public static List<Categoria> findAll() {
        Transaction transaction = null;
        List<Categoria> categorias = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            categorias = session.createQuery("from Categoria ORDER BY id", Categoria.class).list();
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return categorias;
    }

    public static Categoria findById(Integer id) {
        Transaction transaction = null;
        Categoria categoria = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            categoria = session.createQuery("FROM Categoria WHERE id = :id", Categoria.class)
                    .setParameter("id", id)
                    .uniqueResult();
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return categoria;
    }

    public static void deleteById(Integer id) {
        Transaction transaction = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Categoria categoria = session.createQuery("FROM Categoria WHERE id = :id", Categoria.class)
                    .setParameter("id", id)
                    .uniqueResult();
            if (categoria != null) {
                session.remove(categoria);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
