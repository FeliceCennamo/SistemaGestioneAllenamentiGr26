package entity;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una sessione di allenamento assegnata a un atleta da un allenatore.
 * <p>
 * Ogni sessione contiene una lista ordinata di esercizi e tiene traccia
 * del proprio stato (assegnata, in corso, completata), della data di
 * svolgimento e di un'eventuale durata prevista.
 * </p>
 * <p>
 * L'ordinamento naturale (implementato tramite {@link Comparable})
 * ordina le sessioni per data di svolgimento e, a parità di data, per titolo.
 * </p>
 */
@Entity
@Table(name = "sessioni")
public class SessioneDiAllenamento implements Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titolo;
    private String descrizione;
    private LocalDate dataSvolgimento;

    @Enumerated(EnumType.STRING)
    private StatoSessione stato;

    private Duration durata = null;

    /**
     * Esercizi che compongono la sessione.
     * Relazione uno-a-molti con tabella di join {@code sessione_esercizio}.
     * Cascade e orphan removal assicurano che le modifiche alla lista
     * vengano propagate e che gli esercizi orfani siano rimossi.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinTable(name = "sessione_esercizio",
            joinColumns = @JoinColumn(name = "sessione_id"),
            inverseJoinColumns = @JoinColumn(name = "esercizio_id"))
    private List<Esercizio> esercizi = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "atleta_id")
    private Atleta atleta;

    @ManyToOne
    @JoinColumn(name = "allenatore_id")
    private Allenatore allenatore;

    /**
     * Costruttore di default obbligatorio per JPA.
     */
    public SessioneDiAllenamento() {
    }

    /**
     * Crea una sessione con i dati essenziali e stato iniziale
     * impostato ad {@link StatoSessione#ASSEGNATA}.
     *
     * @param titolo          titolo della sessione
     * @param descrizione     descrizione testuale
     * @param dataSvolgimento giorno in cui si svolge la sessione
     * @param atleta          atleta a cui è destinata
     * @param allenatore      allenatore che l'ha creata
     */
    public SessioneDiAllenamento(String titolo, String descrizione,
                                 LocalDate dataSvolgimento,
                                 Atleta atleta, Allenatore allenatore) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.dataSvolgimento = dataSvolgimento;
        this.stato = StatoSessione.ASSEGNATA;
        this.setAtleta(atleta);
        this.setAllenatore(allenatore);
    }

    /**
     * Crea una sessione con durata prevista esplicitamente indicata.
     *
     * @param titolo          titolo della sessione
     * @param descrizione     descrizione testuale
     * @param dataSvolgimento giorno di svolgimento
     * @param durata          durata totale prevista
     * @param atleta          atleta destinatario
     * @param allenatore      allenatore creatore
     */
    public SessioneDiAllenamento(String titolo, String descrizione,
                                 LocalDate dataSvolgimento,
                                 Duration durata,
                                 Atleta atleta, Allenatore allenatore) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.dataSvolgimento = dataSvolgimento;
        this.stato = StatoSessione.ASSEGNATA;
        this.durata = durata;
        this.setAtleta(atleta);
        this.setAllenatore(allenatore);
    }

    // ---------- Getters ----------

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public LocalDate getDataSvolgimento() {
        return dataSvolgimento;
    }

    public Long getId() {
        return id;
    }

    public Atleta getAtleta() {
        return atleta;
    }

    public Allenatore getAllenatore() {
        return allenatore;
    }

    public StatoSessione getStato() {
        return stato;
    }

    public Duration getDurata() {
        return durata;
    }

    /**
     * Restituisce la lista degli esercizi che compongono la sessione.
     * L'ordine è quello di inserimento.
     *
     * @return lista di esercizi (non {@code null})
     */
    protected List<Esercizio> getEsercizi() {
        return esercizi;
    }

    // ---------- Setters protetti ----------

    protected void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    protected void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    protected void setDataSvolgimento(LocalDate dataSvolgimento) {
        this.dataSvolgimento = dataSvolgimento;
    }

    protected void setDurata(Duration durata) {
        this.durata = durata;
    }

    /**
     * Imposta lo stato della sessione a partire dalla stringa
     * corrispondente al nome dell'enum.
     *
     * @param stato stringa che rappresenta il nuovo stato
     *              ("COMPLETATA", "IN CORSO", "ASSEGNATA")
     * @throws IllegalArgumentException se la stringa non corrisponde a
     *                                  uno stato valido
     */
    protected void setStato(String stato) {
        this.stato = StatoSessione.valueOf(stato.toUpperCase().replace(" ", "_"));
    }

    /**
     * Associa un atleta a questa sessione e aggiorna il lato inverso
     * della relazione aggiungendo la sessione all'atleta.
     *
     * @param atleta l'atleta da associare
     */
    protected void setAtleta(Atleta atleta) {
        this.atleta = atleta;
        atleta.getSessioni().add(this);
    }

    /**
     * Associa un allenatore a questa sessione e aggiorna il lato inverso
     * della relazione.
     *
     * @param allenatore l'allenatore da associare
     */
    protected void setAllenatore(Allenatore allenatore) {
        this.allenatore = allenatore;
        allenatore.getSessioni().add(this);
    }

    /**
     * Sostituisce completamente la lista degli esercizi della sessione.
     *
     * @param esercizi nuova lista di esercizi
     */
    public void setEsercizi(List<Esercizio> esercizi) {
        this.esercizi = esercizi;
    }

    // ---------- Logica di business ----------

    /**
     * Registra il risultato di un esercizio all'interno di questa sessione.
     * <p>
     * Se il valore del risultato è {@code null}, viene registrata solo
     * una nota; altrimenti viene registrato il valore insieme alla nota.
     * </p>
     *
     * @param risultato   valore del risultato: {@link Integer} per ripetizioni,
     *                    {@link Duration} per tempo, oppure {@code null}
     * @param nota        annotazione testuale (può essere {@code null})
     * @param idEsercizio identificativo dell'esercizio da aggiornare
     * @throws IllegalArgumentException se non esiste un esercizio con l'id fornito
     */
    protected void registraRisultato(Object risultato, String nota, Long idEsercizio) {
        for (Esercizio e : esercizi) {
            if (e.getId().equals(idEsercizio)) {
                if (nota == null) {
                    e.setRisultato(risultato, "");
                }

                else {
                    e.setRisultato(risultato, nota);
                }
                return;
            }
        }
        throw new IllegalArgumentException("Esercizio non trovato nella sessione");
    }

    /**
     * Cerca un esercizio all'interno della sessione dato il suo id.
     *
     * @param idEsercizio identificativo dell'esercizio
     * @return l'esercizio corrispondente, oppure {@code null} se non presente
     */
    public Esercizio getEsercizioPerId(Long idEsercizio) {
        for (Esercizio e : this.esercizi) {
            if (e.getId().equals(idEsercizio)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Confronta due sessioni per data di svolgimento e, a parità di data,
     * per titolo (ordine alfabetico).
     *
     * @param o la sessione da confrontare
     * @return un valore negativo, zero o positivo in base all'ordinamento
     */
    @Override
    public int compareTo(Object o) {
        SessioneDiAllenamento other = (SessioneDiAllenamento) o;
        if (dataSvolgimento.isEqual(other.getDataSvolgimento())) {
            return titolo.compareTo(other.getTitolo());
        }
        return dataSvolgimento.compareTo(other.getDataSvolgimento());
    }
}