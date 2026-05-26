package database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Map;

public class GestorePersistenza {


    public void salva(Object oggetto) {

        EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(oggetto);

            em.getTransaction().commit();

        } catch (RuntimeException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {

            em.close();
        }
    }

    public void salvaTutti(Object... oggetti) {

        EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {
            em.getTransaction().begin();

            for (Object oggetto : oggetti) {
                em.persist(oggetto);
            }

            em.getTransaction().commit();

        } catch (RuntimeException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }


    public <T> T trovaPerId(Class<T> classe, Long id) {

        EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {

            return em.find(classe, id);

        } finally {
            em.close();
        }
    }


    public <T> List<T> eseguiQuery(String jpql,
                                   Class<T> classe,
                                   Map<String, Object> parametri) {

        EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {


            TypedQuery<T> query = em.createQuery(jpql, classe);


            for (String nomeParametro : parametri.keySet()) {
                query.setParameter(nomeParametro, parametri.get(nomeParametro));
            }


            return query.getResultList();

        } finally {
            em.close();
        }


    }


    public <T> List<T> ottieniTutti(Class<T> classe) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {
            // Ricaviamo il nome dell'entità (di default è il nome della classe)
            String nomeEntita = classe.getSimpleName();
            String jpql = "SELECT e FROM " + nomeEntita + " e";

            // Creiamo la query tipizzata senza bisogno di parametri esterni
            TypedQuery<T> query = em.createQuery(jpql, classe);

            /*
             * getResultList esegue la query e restituisce la lista
             * di tutte le righe presenti nel database per questa entità.
             */
            return query.getResultList();

        } finally {
            em.close();
        }
    }


    }




