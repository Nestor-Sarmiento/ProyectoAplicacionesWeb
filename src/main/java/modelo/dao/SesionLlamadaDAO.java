package modelo.dao;


import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.entities.SesionLlamada;
import util.HibernateUtil;

import java.util.Optional;

public class SesionLlamadaDAO {

    public void guardar(SesionLlamada sesion) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(sesion);
            session.flush();
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public Optional<SesionLlamada> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(SesionLlamada.class, id));
        }
    }

    public void actualizar(SesionLlamada sesion) {
        if (sesion == null || sesion.getId() == null) {
            throw new IllegalArgumentException("La sesión de llamada es obligatoria.");
        }
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(sesion);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}