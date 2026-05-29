package boundary;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import controller.Control_Session;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.Locale;

/**
 * Boundary BCED – card di riepilogo per una singola SessioneDiAllenamento.
 *
 * Riceve i dati già elaborati dal controller tramite {@link Control_Session.SessioneSummaryDTO}
 * e non contiene alcuna logica di business.
 *
 * I pulsanti espongono callback ({@link Runnable}) che vengono iniettati
 * da chi istanzia questa classe (es. {@link FormSessioniDiAllenamento}).
 */
public class SchedaSingola {

    // -------------------------------------------------------------------------
    // Componenti Swing (binding da GUI Designer)
    // -------------------------------------------------------------------------

    private JButton VISUALIZZADETTAGLIOButton;
    private JButton COMPLETASESSIONEButton;
    private JPanel  PanelBase;
    private JLabel  Titolo;
    private JLabel  Esercizi_placeholder;
    private JLabel  Esercizi_real;
    private JLabel  Allenatore_placeholder;
    private JLabel  Allenatore_real;

    // -------------------------------------------------------------------------
    // Costruttore
    // -------------------------------------------------------------------------

    /**
     * Crea la scheda e popola le label con i dati del DTO.
     *
     * @param dto snapshot dei dati della sessione fornito dal controller
     */
    public SchedaSingola(Control_Session.SessioneSummaryDTO dto) {
        $$$setupUI$$$();
        bind(dto);
    }

    // -------------------------------------------------------------------------
    // Binding dati → componenti
    // -------------------------------------------------------------------------

    /**
     * Popola tutti i componenti visivi con i valori del DTO.
     * Se la sessione è già COMPLETATA, disabilita il pulsante COMPLETA SESSIONE.
     */
    private void bind(Control_Session.SessioneSummaryDTO dto) {
        Titolo.setText(dto.titolo() + "  –  " + dto.dataStr());
        Esercizi_real.setText(String.valueOf(dto.numeroEsercizi()));
        Allenatore_real.setText(dto.nomeAllenatore());

        // Feedback visivo dello stato
        boolean completata = "COMPLETATA".equals(dto.stato());
        COMPLETASESSIONEButton.setEnabled(!completata);
        if (completata) {
            Titolo.setForeground(new Color(0x4CAF50)); // verde
            COMPLETASESSIONEButton.setText("GIÀ COMPLETATA");
        }
    }

    // -------------------------------------------------------------------------
    // API per iniettare i callback (pattern Command / lambda)
    // -------------------------------------------------------------------------

    /**
     * Imposta l'azione da eseguire al click di "VISUALIZZA DETTAGLIO".
     * Esempio: {@code scheda.setOnDettaglio(() -> apriDettaglio(dto.id()));}
     */
    public void setOnDettaglio(Runnable action) {
        // Rimuove eventuali listener precedenti
        for (var l : VISUALIZZADETTAGLIOButton.getActionListeners()) {
            VISUALIZZADETTAGLIOButton.removeActionListener(l);
        }
        VISUALIZZADETTAGLIOButton.addActionListener(e -> action.run());
    }

    /**
     * Imposta l'azione da eseguire al click di "COMPLETA SESSIONE".
     */
    public void setOnCompleta(Runnable action) {
        for (var l : COMPLETASESSIONEButton.getActionListeners()) {
            COMPLETASESSIONEButton.removeActionListener(l);
        }
        COMPLETASESSIONEButton.addActionListener(e -> action.run());
    }

    // -------------------------------------------------------------------------
    // Root component
    // -------------------------------------------------------------------------

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return PanelBase;
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
        PanelBase.setLayout(new GridLayoutManager(6, 5, new Insets(10, 10, 10, 10), -1, -1));
        PanelBase.setBackground(new Color(-657936));
        PanelBase.setForeground(new Color(-13884898));
        PanelBase.setMaximumSize(new Dimension(1000, 150));
        PanelBase.setMinimumSize(new Dimension(1000, 150));
        PanelBase.setPreferredSize(new Dimension(720, 130));
        final Spacer spacer1 = new Spacer();
        PanelBase.add(spacer1, new GridConstraints(4, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        COMPLETASESSIONEButton = new JButton();
        COMPLETASESSIONEButton.setText("COMPLETA SESSIONE");
        PanelBase.add(COMPLETASESSIONEButton, new GridConstraints(4, 3, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Titolo = new JLabel();
        Titolo.setText("TITOLO DELLA SESSIONE");
        PanelBase.add(Titolo, new GridConstraints(0, 0, 1, 5,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        Esercizi_placeholder = new JLabel();
        Esercizi_placeholder.setText("NUMERO DI ESERCIZI:");
        PanelBase.add(Esercizi_placeholder, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        Allenatore_placeholder = new JLabel();
        Allenatore_placeholder.setText("ALLENATORE:");
        PanelBase.add(Allenatore_placeholder, new GridConstraints(3, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        Esercizi_real = new JLabel();
        Esercizi_real.setText("—");
        PanelBase.add(Esercizi_real, new GridConstraints(2, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        Allenatore_real = new JLabel();
        Allenatore_real.setBackground(new Color(-460294));
        Font arFont = $$$getFont$$$("Segoe UI Semibold", Font.PLAIN, -1, Allenatore_real.getFont());
        if (arFont != null) Allenatore_real.setFont(arFont);
        Allenatore_real.setForeground(new Color(-14605015));
        Allenatore_real.setText("—");
        PanelBase.add(Allenatore_real, new GridConstraints(3, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        VISUALIZZADETTAGLIOButton = new JButton();
        VISUALIZZADETTAGLIOButton.setText("VISUALIZZA DETTAGLIO");
        PanelBase.add(VISUALIZZADETTAGLIOButton, new GridConstraints(4, 2, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        PanelBase.add(spacer2, new GridConstraints(5, 3, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        PanelBase.add(spacer3, new GridConstraints(4, 4, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        PanelBase.add(spacer4, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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
