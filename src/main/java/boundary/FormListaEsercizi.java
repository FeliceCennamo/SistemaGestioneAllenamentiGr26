package boundary;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import controller.Controller;

import notifier.Notifier;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.*;
import java.util.List;

public class FormListaEsercizi extends JFrame {
    private JPanel PanelBase;
    private JScrollPane Scorrimento;
    private JPanel Header;
    private JPanel Footer;
    private JButton backBtn;
    private JButton completaBtn;
    private JPanel panelCentrale;
    private JLabel emptyLbl;

    private JFrame previousFrame;  // finestra delle sessioni
    private JFrame currentFrame;

    private Long idCurrentSession;
    private FormListaSessioni parentForm;

    private ArrayList<SchedaSingolaEsercizio> schede = new ArrayList<>();

    /**
     * Costruttore del FormEsercizi
     * @param id_sessione id SessioneDiAllenamento
     * @param parentForm oggetto FormSessioniDiAllenamento
     * @param parentFrame Ancestor
     */
    public FormListaEsercizi(Long id_sessione, JFrame parentFrame, FormListaSessioni parentForm) {
        this.previousFrame = parentFrame;
        this.idCurrentSession = id_sessione;
        this.parentForm = parentForm;

        // Crea la finestra corrente
        currentFrame = new JFrame();
        currentFrame.setTitle("Visualizza allenamenti");
        currentFrame.setContentPane(this.PanelBase);  // usa il pannello dell'istanza corrente
        currentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // NON EXIT!
        currentFrame.setResizable(false);
        currentFrame.pack();
        currentFrame.setLocationRelativeTo(null);

        // Aggiungi gli esercizi al pannello centrale
        aggiungiEsercizi(id_sessione);
        // Imposta lo scrolling
        Scorrimento.getVerticalScrollBar().setUnitIncrement(20);

        // Nascondi la finestra padre (quella delle sessioni)
        parentFrame.setVisible(false);
        // Mostra la nuova finestra
        currentFrame.setVisible(true);

        Controller controller = Controller.getInstance();

        if (((String) controller.getDettaglioSessionePerId(id_sessione).get("stato")).equalsIgnoreCase("COMPLETATA")) {
            completaBtn.setVisible(false);
        }

        // ----- LISTENER PER IL BOTTONE BACK -----
        backBtn.addActionListener(e -> {
            returnToPreviousFrame();
        });

        // ----- LISTENER PER IL BOTTONE COMPLETA -----
        completaBtn.addActionListener(e -> {
            sendCompleta();
        });
    }

    /**
     * Rende di nuovo visibile la finestra delle sessioni e chiude la finestra corrente
     * */
    private void returnToPreviousFrame() {
        previousFrame.setVisible(true);
        currentFrame.dispose();
    }

    public void aggiungiEsercizi(Long id_sessione) {
        Controller controller = Controller.getInstance();

        panelCentrale.setLayout(new BoxLayout(panelCentrale, BoxLayout.Y_AXIS));
        List<Long> id_esercizi = controller.getIdEserciziPerSessione(id_sessione);
        if (!id_esercizi.isEmpty()) {
            for (Long id_esercizio : id_esercizi) {
                SchedaSingolaEsercizio scheda = new SchedaSingolaEsercizio(id_sessione, id_esercizio);
                panelCentrale.add(scheda.$$$getRootComponent$$$());
                panelCentrale.add(Box.createVerticalStrut(5));
                schede.add(scheda);
            }
            emptyLbl.setVisible(false);
        } else {
            emptyLbl.setVisible(true);
        }
    }

    /**
     * Inizia il completamento di una SessioneDiALlenamento
     *
     */
    private void sendCompleta() {
        Controller controller = Controller.getInstance();

        Map<Long, String[]> risultati_row = new HashMap<>();
        for (SchedaSingolaEsercizio s : this.schede) {
            if (s.getTestoRisultato().isEmpty() && s.getTestoNota().isEmpty()) {
                continue;
            }
            risultati_row.put(s.getIdEsercizio(), new String[]{s.getTestoNota(), s.getTestoRisultato()});
        }

        try {
            controller.completaSessione(this.idCurrentSession, risultati_row);

            if (controller.getDettaglioSessionePerId(idCurrentSession).get("stato").equals("COMPLETATA")) {
                notifica((String) controller.getDettaglioSessionePerId(idCurrentSession).get("allenatore"));
            }

            parentForm.refreshPanelCentrale();
            previousFrame.setVisible(true);
            currentFrame.dispose();
        } catch (ClassCastException  | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "I risultati devono essere necessariamente dei numeri interi maggiori di zero",
                    "Errore inserimento risultati", JOptionPane.ERROR_MESSAGE);
        }


    }

    public void notifica(String mail){
        Notifier.getInstance().sendMailComplete(mail);
    }

    public JPanel getPanelBase() {
        return this.PanelBase;
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        PanelBase = new JPanel();
        PanelBase.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        PanelBase.setPreferredSize(new Dimension(750, 500));
        Scorrimento = new JScrollPane();
        PanelBase.add(Scorrimento, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panelCentrale = new JPanel();
        panelCentrale.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        Scorrimento.setViewportView(panelCentrale);
        Header = new JPanel();
        Header.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        PanelBase.add(Header, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backBtn = new JButton();
        backBtn.setText("<");
        Header.add(backBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        Header.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setFocusable(false);
        Font label1Font = this.$$$getFont$$$(null, -1, 20, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setHorizontalAlignment(0);
        label1.setHorizontalTextPosition(0);
        label1.setText("I TUOI ESERCIZI");
        label1.setVerticalAlignment(1);
        Header.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 30), new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        Footer = new JPanel();
        Footer.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        PanelBase.add(Footer, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        completaBtn = new JButton();
        completaBtn.setText("Completa sessione");
        Footer.add(completaBtn, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        emptyLbl = new JLabel();
        Font emptyLblFont = this.$$$getFont$$$(null, -1, 16, emptyLbl.getFont());
        if (emptyLblFont != null) emptyLbl.setFont(emptyLblFont);
        emptyLbl.setText("Nessun esercizio nella sessione");
        Footer.add(emptyLbl, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return PanelBase;
    }


}
