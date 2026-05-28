package boundaryClaudioCodice;

import boundaryClaudioCodice.SessioniListaPanel;
import controller.AtletaSessioniController;
import controller.AtletaSessioniController.SessioneDTO;

import javax.swing.*;
import java.awt.*;

/**
 * BOUNDARY – Finestra principale dell'applicazione.
 *
 * Usa un CardLayout per alternare tra:
 *   - "lista"    → SessioniListaPanel
 *   - "dettaglio"→ DettaglioSessionePanel
 *
 * La navigazione è guidata dai listener definiti nei sotto-pannelli.
 *
 * Utilizzo:
 *   SwingUtilities.invokeLater(() -> new MainFrame(idAtleta).setVisible(true));
 */
public class MainFrame extends JFrame {

    private static final String CARD_LISTA     = "lista";
    private static final String CARD_DETTAGLIO = "dettaglio";

    private final CardLayout cardLayout;
    private final JPanel     cardPanel;

    private final AtletaSessioniController controller;
    private final Long idAtleta;

    // ── Costruttore ─────────────────────────────────────────────────────────

    public MainFrame(Long idAtleta) {
        this.idAtleta   = idAtleta;
        this.controller = new AtletaSessioniController();

        setTitle("Training Manager – Sessioni");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 560));
        setPreferredSize(new Dimension(900, 660));

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(SessioniListaPanel.BG_MAIN);

        // Carica subito il pannello lista
        cardPanel.add(buildListaPanel(), CARD_LISTA);

        add(cardPanel);
        pack();
        setLocationRelativeTo(null); // centra sullo schermo
    }

    // ── Navigation ──────────────────────────────────────────────────────────

    /** Naviga verso il dettaglio della sessione selezionata. */
    private void apriDettaglio(SessioneDTO sessione) {
        // Rimuovi eventuale vecchio pannello di dettaglio (per garbage collection)
        Component old = findCard(CARD_DETTAGLIO);
        if (old != null) cardPanel.remove(old);

        DettaglioSessionePanel dettaglio = new DettaglioSessionePanel(controller, sessione);
        dettaglio.setIndietroListener(this::tornaALista);

        cardPanel.add(dettaglio, CARD_DETTAGLIO);
        cardLayout.show(cardPanel, CARD_DETTAGLIO);
        setTitle("Training Manager – " + sessione.titolo());
    }

    /** Torna alla lista delle sessioni. */
    private void tornaALista() {
        cardLayout.show(cardPanel, CARD_LISTA);
        setTitle("Training Manager – Sessioni");
    }

    // ── Helper ──────────────────────────────────────────────────────────────

    private SessioniListaPanel buildListaPanel() {
        SessioniListaPanel lista = new SessioniListaPanel(controller, idAtleta);
        lista.setSessioneSelezionataListener(this::apriDettaglio);
        return lista;
    }

    /** Cerca un componente nel CardPanel per nome (chiave). */
    private Component findCard(String name) {
        for (Component c : cardPanel.getComponents()) {
            if (name.equals(cardPanel.getLayout() instanceof CardLayout
                    ? ((CardLayout) cardPanel.getLayout()).toString()
                    : null)) {
                return c;
            }
        }
        return null; // CardLayout non espone getComponentForKey direttamente
    }

    // ── Main di test ────────────────────────────────────────────────────────

    /**
     * Entry point di test. In produzione, l'id atleta viene passato
     * dopo il login dall'use case di autenticazione.
     */
    public static void main(String[] args) {
        // Look & Feel di sistema per sembrare nativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Applica font anti-aliasing globale
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        Long idAtletaDiTest = 1L; // sostituire con l'id reale proveniente dal login

        SwingUtilities.invokeLater(() -> new MainFrame(idAtletaDiTest).setVisible(true));
    }
}
