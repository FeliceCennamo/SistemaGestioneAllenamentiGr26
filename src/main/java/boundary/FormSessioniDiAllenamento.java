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
 * Boundary BCED – lista di tutte le sessioni dell'atleta autenticato.
 *
 * Responsabilità:
 *  - richiedere al controller la lista di SessioneSummaryDTO
 *  - costruire dinamicamente una SchedaSingola per ogni sessione
 *  - non contenere logica di business
 */
public class FormSessioniDiAllenamento {

    // -------------------------------------------------------------------------
    // Stato locale
    // -------------------------------------------------------------------------

    /** Id dell'atleta attualmente autenticato, passato dall'esterno al lancio. */
    private final Long idAtletaAutenticato;

    // -------------------------------------------------------------------------
    // Componenti Swing (generati da IntelliJ GUI Designer – NON modificare)
    // -------------------------------------------------------------------------

    private JPanel PanelBase;
    private JPanel PanelCentrale;
    private JScrollPane Scorrimento;
    private JPanel Header;
    private JPanel Footer;

    // -------------------------------------------------------------------------
    // Costruttore
    // -------------------------------------------------------------------------

    /**
     * Crea il form per l'atleta indicato.
     *
     * @param idAtletaAutenticato id dell'atleta loggato
     */
    public FormSessioniDiAllenamento(Long idAtletaAutenticato) {
        this.idAtletaAutenticato = idAtletaAutenticato;
        $$$setupUI$$$();
        popolaLista();
    }

    // -------------------------------------------------------------------------
    // Logica boundary (popola la lista via controller)
    // -------------------------------------------------------------------------

    /**
     * Chiede al controller la lista delle sessioni e crea una SchedaSingola
     * per ciascuna, collegando i listener ai pulsanti.
     */
    private void popolaLista() {
        Control_Session ctrl = Control_Session.getInstance();
        List<Control_Session.SessioneSummaryDTO> sessioni =
                ctrl.getSessioniAtleta(idAtletaAutenticato);

        PanelCentrale.removeAll();
        PanelCentrale.setLayout(new GridLayout(0, 1, 0, 8));

        for (Control_Session.SessioneSummaryDTO dto : sessioni) {
            SchedaSingola scheda = new SchedaSingola(dto);

            // --- Pulsante VISUALIZZA DETTAGLIO ---
            scheda.setOnDettaglio(() -> apriDettaglio(dto.id()));

            // --- Pulsante COMPLETA SESSIONE ---
            scheda.setOnCompleta(() -> {
                int confirm = JOptionPane.showConfirmDialog(
                        PanelBase,
                        "Sei sicuro di voler completare la sessione\n\"" + dto.titolo() + "\"?\n"
                                + "Tutti i risultati già inseriti verranno salvati.",
                        "Conferma completamento",
                        JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION) return;

                try {
                    // Nessun risultato extra qui: l'utente li inserisce nel dettaglio.
                    ctrl.completaSessione(idAtletaAutenticato, dto.id(),
                            new java.util.HashMap<>(), new java.util.HashMap<>());
                    JOptionPane.showMessageDialog(PanelBase,
                            "Sessione completata con successo!", "Fatto",
                            JOptionPane.INFORMATION_MESSAGE);
                    popolaLista(); // ricarica per aggiornare lo stato
                } catch (IllegalAccessException ex) {
                    JOptionPane.showMessageDialog(PanelBase,
                            "Non hai i permessi per completare questa sessione.",
                            "Errore", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PanelBase,
                            "Errore: " + ex.getMessage(),
                            "Errore", JOptionPane.ERROR_MESSAGE);
                }
            });

            PanelCentrale.add(scheda.$$$getRootComponent$$$());
        }

        PanelCentrale.revalidate();
        PanelCentrale.repaint();
    }

    /**
     * Apre la finestra di dettaglio per la sessione selezionata.
     */
    private void apriDettaglio(Long idSessione) {
        JFrame dettaglio = new JFrame("Dettaglio sessione");
        FormDettaglioSessione formDett =
                new FormDettaglioSessione(idSessione, idAtletaAutenticato, dettaglio);
        dettaglio.setContentPane(formDett.$$$getRootComponent$$$());
        dettaglio.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dettaglio.pack();
        dettaglio.setLocationRelativeTo(PanelBase);
        dettaglio.setVisible(true);

        // Al ritorno dal dettaglio, ricarica la lista (potrebbero esserci stati completamenti)
        dettaglio.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                popolaLista();
            }
        });
    }

    // -------------------------------------------------------------------------
    // Getter root component (usato da chi instanzia questo form)
    // -------------------------------------------------------------------------

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return PanelBase;
    }

    // -------------------------------------------------------------------------
    // Entry point di test (da rimuovere o sostituire con il vero login)
    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        // ID di test – in produzione arriva dal flusso di autenticazione
        final Long idAtletaTest = 1L;

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("I Tuoi Allenamenti");
            FormSessioniDiAllenamento form = new FormSessioniDiAllenamento(idAtletaTest);
            form.Scorrimento.getVerticalScrollBar().setUnitIncrement(20);
            frame.setContentPane(form.PanelBase);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // =========================================================================
    // Codice generato da IntelliJ GUI Designer – NON MODIFICARE
    // =========================================================================

    {
        $$$setupUI$$$();
    }

    /** @noinspection ALL */
    private void $$$setupUI$$$() {
        PanelBase = new JPanel();
        PanelBase.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        PanelBase.setBackground(new Color(-1));
        PanelBase.setMaximumSize(new Dimension(2147483647, 500));
        PanelBase.setMinimumSize(new Dimension(28, 500));
        PanelBase.setPreferredSize(new Dimension(750, 500));
        Scorrimento = new JScrollPane();
        Scorrimento.setHorizontalScrollBarPolicy(30);
        Scorrimento.setInheritsPopupMenu(true);
        Scorrimento.setVerticalScrollBarPolicy(22);
        PanelBase.add(Scorrimento, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                null, null, null, 0, false));
        PanelCentrale = new JPanel();
        PanelCentrale.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        PanelCentrale.setBackground(new Color(-657936));
        Font pcFont = $$$getFont$$$("Segoe UI Semibold", Font.BOLD, 0, PanelCentrale.getFont());
        if (pcFont != null) PanelCentrale.setFont(pcFont);
        PanelCentrale.setMaximumSize(new Dimension(32767, 1200));
        PanelCentrale.setMinimumSize(new Dimension(10, 1200));
        PanelCentrale.setOpaque(true);
        PanelCentrale.setPreferredSize(new Dimension(10, 1200));
        Scorrimento.setViewportView(PanelCentrale);
        final JLabel labelTitolo = new JLabel();
        Font lblFont = $$$getFont$$$("Segoe UI Semibold", Font.BOLD, 28, labelTitolo.getFont());
        if (lblFont != null) labelTitolo.setFont(lblFont);
        labelTitolo.setHorizontalAlignment(0);
        labelTitolo.setHorizontalTextPosition(0);
        labelTitolo.setOpaque(false);
        labelTitolo.setPreferredSize(new Dimension(100, 100));
        labelTitolo.setText("I TUOI ALLENAMENTI");
        PanelCentrale.add(labelTitolo);
        Header = new JPanel();
        Header.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        PanelBase.add(Header, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null, 0, false));
        Footer = new JPanel();
        Footer.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        PanelBase.add(Footer, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null, 0, false));
        labelTitolo.setLabelFor(Scorrimento);
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
