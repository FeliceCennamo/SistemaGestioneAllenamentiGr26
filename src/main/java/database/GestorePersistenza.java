package database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.metamodel.EntityType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Gestore centralizzato per le operazioni di persistenza JPA.
 * <p>
 * Fornisce metodi statici per le operazioni CRUD di base e per l'esecuzione
 * di query JPQL parametriche. Tutti i metodi si occupano di ottenere e
 * rilasciare correttamente l'{@link EntityManager} e di gestire le transazioni
 * quando necessario.
 * </p>
 *
 * <p><b>Utilizzo tipico:</b>
 * <pre>{@code
 *   // Salvataggio di una singola entità
 *   Utente utente = GestorePersistenza.salva(nuovoUtente);
 *
 *   // Recupero per id
 *   Utente trovato = GestorePersistenza.trovaPerId(Utente.class, 1L);
 *
 *   // Query parametrica
 *   List<Utente> utenti = GestorePersistenza.eseguiQuery(
 *       "SELECT u FROM Utente u WHERE u.cognome = :cognome",
 *       Utente.class,
 *       Map.of("cognome", "Rossi")
 *   );
 * }</pre>
 */
public class GestorePersistenza {

    /**
     * Persiste un oggetto nel database, restituendo l'istanza gestita.
     * <p>
     * Se l'oggetto passato è nuovo (privo di id o con id non presente) viene
     * effettuato un inserimento; se invece l'id corrisponde a una riga esistente
     * i dati vengono aggiornati. In entrambi i casi si utilizza
     * {@link EntityManager#merge(Object)} che restituisce l'entità managed.
     * </p>
     *
     * @param <T>    tipo dell'entità
     * @param oggetto l'entità da salvare (può essere in stato detached o new)
     * @return l'istanza managed dopo il salvataggio
     * @throws RuntimeException in caso di errore di persistenza (la transazione
     *                          viene rollbackata)
     */
    public static <T> T salva(T oggetto) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            T managed = em.merge(oggetto);
            em.getTransaction().commit();
            return managed;
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
     * Salva in blocco una lista eterogenea di oggetti in un'unica transazione.
     * <p>
     * Ogni oggetto viene passato a {@link EntityManager#merge(Object)}.
     * In caso di errore l'intera transazione viene annullata.
     * </p>
     *
     * @param oggetti uno o più oggetti entità da persistere/aggiornare
     * @throws RuntimeException se si verifica un errore durante il merge
     *                          o il commit
     */
    public static void salvaTutti(Object... oggetti) {
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
     * Recupera un'entità dal database usando il suo identificatore.
     *
     * @param <T>    tipo dell'entità
     * @param classe classe dell'entità da cercare
     * @param id     valore della chiave primaria
     * @return l'entità trovata, oppure {@code null} se non esiste
     */
    public static <T> T trovaPerId(Class<T> classe, Long id) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            return em.find(classe, id);
        } finally {
            em.close();
        }
    }

    /**
     * Esegue una query JPQL parametrica e restituisce la lista dei risultati.
     *
     * @param <T>       tipo degli elementi attesi nel risultato
     * @param jpql      stringa della query JPQL
     * @param classe    classe corrispondente al tipo di ritorno
     * @param parametri mappa che associa il nome di ogni parametro named ({@code :nome})
     *                  al valore da sostituire; può essere vuota ma non {@code null}
     * @return lista dei risultati (mai {@code null}, può essere vuota)
     * @throws IllegalArgumentException se un parametro dichiarato nella query
     *                                  non viene fornito o se la mappa è {@code null}
     */
    public static <T> List<T> eseguiQuery(String jpql,
                                          Class<T> classe,
                                          Map<String, Object> parametri) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            TypedQuery<T> query = em.createQuery(jpql, classe);
            for (Map.Entry<String, Object> entry : parametri.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Restituisce tutte le istanze di una data entità presenti nel database.
     * <p>
     * Genera una query JPQL del tipo {@code SELECT e FROM NomeEntita e}
     * e ne recupera tutti i risultati.
     * </p>
     *
     * @param <T>    tipo dell'entità
     * @param classe classe dell'entità
     * @return lista contenente tutte le righe della tabella corrispondente
     */
    public static <T> List<T> ottieniTutti(Class<T> classe) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            String nomeEntita = classe.getSimpleName();
            String jpql = "SELECT e FROM " + nomeEntita + " e";
            TypedQuery<T> query = em.createQuery(jpql, classe);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Restituisce l'insieme dei tipi di entità che estendono (o implementano)
     * la classe specificata, escludendo la classe stessa.
     * <p>
     * Utile per scoprire a runtime tutte le sottoclassi concrete mappate
     * come entità JPA, ad esempio in strategie di ereditarietà.
     * </p>
     *
     * @param <T>    tipo della classe base (superclasse astratta o interfaccia)
     * @param classe classe base di cui si vogliono conoscere le figlie
     * @return insieme di {@link EntityType} che rappresentano le entità
     *         che sono assegnabili alla classe data ma non coincidono con essa
     */
    public static <T> Set<EntityType<?>> getFiglie(Class<T> classe) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();

        // Partiamo da tutte le entità note al metamodel
        Set<EntityType<?>> figlie = new HashSet<>(em.getMetamodel().getEntities());

        // Rimuoviamo i tipi che non sono sottoclassi di `classe`
        // e la classe stessa se compare come entità concreta
        figlie.removeIf(e ->
                !classe.isAssignableFrom(e.getJavaType())
                        || e.getJavaType().equals(classe)
        );

        return figlie;
    }
}