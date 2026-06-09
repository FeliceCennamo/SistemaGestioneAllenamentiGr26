package database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {

    /**
     * Oggetto JpaUtil instanziato seguendo il pattern Singleton
     *
     */
    private static JpaUtil instance;

    /**
     * EntityManagerFactory legge la persistence unit dal file
     * persistence.xml e prepara Hibernate per comunicare con il database.
     */
    private EntityManagerFactory emf;

    /**
     * Costruttore privato secondo il pattern Singleton (non Thread-safe)
     *
     */
    private JpaUtil() {
        emf = Persistence.createEntityManagerFactory("GestioneAllenamentiPU");
    }

    /**
     * Completa l'applicazione del pattern Singleton.
     *
     * @return Unico oggetto JpaUtil instanziabile
     */
    public static JpaUtil getInstance() {
        if (instance == null) {
            instance = new JpaUtil();
        }

        return instance;
    }

    /**
     * Restituisce un nuovo oggetto EntityManager, da utilizzare per una operazione di persistenza
     *
     * @return Oggetto EntityManager
     *
     */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Chiude l'istanza di EntityManagerFactory
     *
     */
    public void chiudi() {
        emf.close();
        instance = null;
    }
}
