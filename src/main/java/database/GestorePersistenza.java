package database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.metamodel.EntityType;

import java.util.*;

public class GestorePersistenza {

    /**
     * Inserisce un oggetto nel database
     *
     * @param oggetto oggetto da voler inserire
     *
     */
    /*
    public void salva(Object oggetto) {

        EntityManager em = JpaUtil.getInstance().getEntityManager();


        try {
            em.getTransaction().begin();

            em.merge(oggetto);

            em.getTransaction().commit();

        } catch (RuntimeException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {

            em.close();
        }
    }*/
    public <T> T salva(T oggetto) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            T managed = em.merge(oggetto);
            em.getTransaction().commit();
            return managed;
        } finally {
            em.close();
        }
    }

    /**
     * Inserisce una lista di oggetti nel database
     *
     * @param oggetti Lista eterogenea di oggetti da voler inserire
     *
     */
    public void salvaTutti(Object... oggetti) {

        EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {
            em.getTransaction().begin();

            for (Object oggetto : oggetti) {
                em.merge(oggetto);
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

    /**
     * Restituisce l'oggetto cercato nel database
     *
     * @param classe classe dell'oggetto cercato nel database
     * @param id     id dell'oggetto cercato nel database
     * @return oggetto cercato nel database
     *
     */
    public <T> T trovaPerId(Class<T> classe, Long id) {

        EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {

            return em.find(classe, id);

        } finally {
            em.close();
        }
    }

    /**
     * Restituisce una lista di oggetti corrispondente al risultato di una query jpql
     *
     * @param jpql      String rappresentante la query tipizzata da eseguire
     * @param classe    Classe letterale del tipo di dato aspettato
     * @param parametri mappa che contiene i valori dinamici da inserire nella query
     *
     */
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

    //ottieniTutti2 dovrebbe essere una forma più compatta di questo
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

    public <T> Set<EntityType<?>> getFiglie(Class<T> classe) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();

        Set<EntityType<?>> figlie = new HashSet<>(em.getMetamodel().getEntities());

        figlie.removeIf(e -> !classe.isAssignableFrom(e.getJavaType())
                ||
                e.getJavaType().equals(classe));

        return figlie;
    }

}




