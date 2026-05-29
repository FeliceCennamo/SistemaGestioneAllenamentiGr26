package boundary;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import controller.Control_Session;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.List;
import java.util.Locale;

/**
 * Boundary BCED – dettaglio di una sessione di allenamento.
 *
 * Mostra la lista degli esercizi della sessione tramite una
 * {@link SchedaSingolaEsercizio} per ciascuno.
 * Contiene un pulsante "COMPLETA SESSIONE" che raccoglie tutti i risultati
 * inseriti e li invia al controller in un'unica chiamata.
 *
 * NON conosce le classi entity: lavora esclusivamente con i DTO del controller.
 */
public class FormDettaglioSessione {

    // -------------------------------------------------------------------------
    // Stato locale
    // -------------------------------------------------------------------------

    private final Long idSessione;
    private final Long idAtleta;

    /** Finestra padre: usata per chiuderla dopo il completamento. */
    private final JFrame frameParent;

    /** Riferimenti alle schede esercizio correnti (per raccogliere i dati). */
    private List<SchedaSingolaEsercizio> schedeEsercizi;

    // -------------------------------------------------------------------------
    // Componenti Swing
    // -------------------------------------------------------------------------

    private JPanel     PanelBase;
    private JPanel     PanelCentrale;
    private JScrollPane Scorrimento;
    private JPanel     Header;
    private JPanel     Footer;
    private JLabel     LabelTitoloSessione;
    private JButton    CompletaSessioneButton;

    // -------------------------------------------------------------------------
    // Costruttore
    // -------------------------------------------------------------------------

    /**
     * @param idSessione  id della sessione da visualizzare
     * @param idAtleta    id dell'atleta autenticato (per i controlli di accesso)
     * @param frameParent finestra padre da chiudere al completamento
     */
    public FormDettaglioSessione(Long idSessione, Long idAtleta, JFrame frameParent) {
        this.idSessione  = idSessione;
        this.idAtleta    = idAtleta;
        this.frameParent = frameParent;
        buildUI();
        popolaEsercizi();
        collegaPulsanti();
    }

    // -------------------------------------------------------------------------
    // Costruzione UI
    // -------------------------------------------------------------------------

    private void buildUI() {
        PanelBase = new JPanel(new GridLayoutManager(4, 1,
                new Insets(10, 10, 10, 10), -1, -1));
        PanelBase.setBackground(Color.WHITE);
        PanelBase.setPreferredSize(new Dimension(760, 600));

        // --- Header con titolo ---
        Header = new JPanel(new BorderLayout());
        Header.setBackground(new Color(0x3F51B5)); // indigo
        LabelTitoloSessione = new JLabel("DETTAGLIO SESSIONE");
        LabelTitoloSessione.setForeground(Color.WHITE);
        LabelTitoloSessione.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        LabelTitoloSessione.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        Header.add(LabelTitoloSessione, BorderLayout.WEST);
        PanelBase.add(Header, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        // --- Area scorrevole con esercizi ---
        Scorrimento = new JScrollPane();
        Scorrimento.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        Scorrimento.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        Scorrimento.getVerticalScrollBar().setUnitIncrement(20);
        PanelBase.add(Scorrimento, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                null, null, null, 0, false));

        PanelCentrale = new JPanel();
        PanelCentrale.setLayout(new BoxLayout(PanelCentrale, BoxLayout.Y_AXIS));
        PanelCentrale.setBackground(new Color(0xF5F5F5));
        Scorrimento.setViewportView(PanelCentrale);

        // --- Footer con pulsante COMPLETA ---
        Footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        Footer.setBackground(Color.WHITE);
        CompletaSessioneButton = new JButton("COMPLETA SESSIONE");
        CompletaSessioneButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        CompletaSessioneButton.setBackground(new Color(0x4CAF50));
        CompletaSessioneButton.setForeground(Color.WHITE);
        CompletaSessioneButton.setFocusPainted(false);
        Footer.add(CompletaSessioneButton);
        PanelBase.add(Footer, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    // -------------------------------------------------------------------------
    // Popola lista esercizi
    // -------------------------------------------------------------------------

    private void popolaEsercizi() {
        Control_Session ctrl = Control_Session.getInstance();
        List<Control_Session.EsercizioDettDTO> esercizi =
                ctrl.getDettaglioSessione(idSessione);

        PanelCentrale.removeAll();
        schedeEsercizi = new java.util.ArrayList<>();

        if (esercizi.isEmpty()) {
            JLabel vuoto = new JLabel("Nessun esercizio in questa sessione.");
            vuoto.setHorizontalAlignment(SwingConstants.CENTER);
            vuoto.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            PanelCentrale.add(vuoto);
        } else {
            for (Control_Session.EsercizioDettDTO dto : esercizi) {
                SchedaSingolaEsercizio scheda = new SchedaSingolaEsercizio(dto);

                // Callback "SALVA RISULTATO" singolo esercizio
                scheda.setOnSalva((risultatoRaw, nota) -> {
                    try {
                        ctrl.registraRisultatoEsercizio(idSessione, dto.id(), risultatoRaw, nota);
                        JOptionPane.showMessageDialog(PanelBase,
                                "Risultato salvato per: " + dto.nome(),
                                "Salvato", JOptionPane.INFORMATION_MESSAGE);
                        // Ricarica per mostrare il risultato appena registrato
                        popolaEsercizi();
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(PanelBase,
                                ex.getMessage(), "Formato non valido",
                                JOptionPane.WARNING_MESSAGE);
                    }
                });

                schedeEsercizi.add(scheda);
                PanelCentrale.add(scheda.$$$getRootComponent$$$());
                PanelCentrale.add(Box.createVerticalStrut(8));
            }
        }

        PanelCentrale.revalidate();
        PanelCentrale.repaint();
    }

    // -------------------------------------------------------------------------
    // Collega pulsante COMPLETA
    // -------------------------------------------------------------------------

    private void collegaPulsanti() {
        CompletaSessioneButton.addActionListener(e -> {
            // Raccoglie tutti i valori già inseriti nelle schede
            java.util.HashMap<Long, String> risultati = new java.util.HashMap<>();
            java.util.HashMap<Long, String> note      = new java.util.HashMap<>();
            for (SchedaSingolaEsercizio scheda : schedeEsercizi) {
                risultati.put(scheda.getIdEsercizio(), scheda.getRisultatoRaw());
                note.put     (scheda.getIdEsercizio(), scheda.getNota());
            }

            int confirm = JOptionPane.showConfirmDialog(PanelBase,
                    "Vuoi completare la sessione?\n"
                            + "I risultati non ancora salvati verranno registrati ora.",
                    "Conferma completamento", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                Control_Session.getInstance()
                        .completaSessione(idAtleta, idSessione, risultati, note);
                JOptionPane.showMessageDialog(PanelBase,
                        "Sessione completata con successo!",
                        "Completata", JOptionPane.INFORMATION_MESSAGE);
                if (frameParent != null) frameParent.dispose();
            } catch (IllegalAccessException ex) {
                JOptionPane.showMessageDialog(PanelBase,
                        "Non hai i permessi per completare questa sessione.",
                        "Errore", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(PanelBase,
                        "Errore nei dati inseriti: " + ex.getMessage(),
                        "Errore", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    // -------------------------------------------------------------------------
    // Root component
    // -------------------------------------------------------------------------

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return PanelBase;
    }

    /** @noinspection ALL */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            resultName = (testFont.canDisplay('a') && testFont.canDisplay('1'))
                    ? fontName : currentFont.getName();
        }
        Font font = new Font(resultName,
                style >= 0 ? style : currentFont.getStyle(),
                size  >= 0 ? size  : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font ff = isMac
                ? new Font(font.getFamily(), font.getStyle(), font.getSize())
                : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return ff instanceof FontUIResource ? ff : new FontUIResource(ff);
    }
}
