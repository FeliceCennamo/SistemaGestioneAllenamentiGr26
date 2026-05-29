package boundary;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import controller.Control_Session;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

/**
 * Boundary BCED – card per un singolo esercizio nel dettaglio della sessione.
 *
 * Mostra:
 *  - nome e descrizione dell'esercizio
 *  - risultato atteso (fornito dall'allenatore)
 *  - risultato già registrato (se presente)
 *  - campo testuale per la nota dell'atleta
 *  - campo testuale per il risultato effettivo
 *  - pulsante "SALVA RISULTATO"
 *
 * Espone un callback {@link BiConsumer}{@code <String risultatoRaw, String nota>}
 * iniettato da {@link FormDettaglioSessione} – nessuna logica di business interna.
 */
public class SchedaSingolaEsercizio {

    // -------------------------------------------------------------------------
    // ID esercizio (necessario al padre per costruire le mappe)
    // -------------------------------------------------------------------------

    private final Long idEsercizio;

    // -------------------------------------------------------------------------
    // Callback iniettato dall'esterno
    // -------------------------------------------------------------------------

    /** Chiamato al click di SALVA con (risultatoRaw, nota). */
    private BiConsumer<String, String> onSalva;

    // -------------------------------------------------------------------------
    // Componenti Swing
    // -------------------------------------------------------------------------

    private JPanel     PanelBase;
    private JLabel     LabelNome;
    private JLabel     LabelDescrizione;
    private JLabel     LabelTipoAtteso;       // "TIPO – RISULTATO ATTESO"
    private JLabel     LabelRisultatoOttenuto; // risultato già registrato (o "—")
    private JTextField FieldNota;             // input nota
    private JTextField FieldRisultato;        // input risultato
    private JButton    SalvaButton;

    // -------------------------------------------------------------------------
    // Costruttore
    // -------------------------------------------------------------------------

    /**
     * Crea la scheda e popola i componenti con i dati del DTO.
     *
     * @param dto dati dell'esercizio forniti dal controller
     */
    public SchedaSingolaEsercizio(Control_Session.EsercizioDettDTO dto) {
        this.idEsercizio = dto.id();
        buildUI();
        bind(dto);
        collegaPulsante();
    }

    // -------------------------------------------------------------------------
    // Costruzione dinamica dell'UI  (equivalente al $$$setupUI$$$ generato)
    // -------------------------------------------------------------------------

    private void buildUI() {
        PanelBase = new JPanel(new GridLayoutManager(6, 2,
                new Insets(10, 12, 10, 12), -1, -1));
        PanelBase.setBackground(Color.WHITE);
        PanelBase.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDDDDDD), 1, true),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        PanelBase.setPreferredSize(new Dimension(720, 170));
        PanelBase.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        // Riga 0 – Nome esercizio (colonna intera)
        LabelNome = new JLabel("NOME ESERCIZIO");
        LabelNome.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        PanelBase.add(LabelNome, new GridConstraints(0, 0, 1, 2,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));

        // Riga 1 – Descrizione (colonna intera)
        LabelDescrizione = new JLabel("Descrizione esercizio");
        LabelDescrizione.setForeground(new Color(0x555555));
        PanelBase.add(LabelDescrizione, new GridConstraints(1, 0, 1, 2,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));

        // Riga 2 – Tipo/atteso (sx) | Risultato già registrato (dx)
        LabelTipoAtteso = new JLabel("TIPO – RISULTATO ATTESO");
        LabelTipoAtteso.setForeground(new Color(0x3F51B5));
        PanelBase.add(LabelTipoAtteso, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));

        LabelRisultatoOttenuto = new JLabel("Risultato ottenuto: —");
        LabelRisultatoOttenuto.setForeground(new Color(0x4CAF50));
        PanelBase.add(LabelRisultatoOttenuto, new GridConstraints(2, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));

        // Riga 3 – Etichette input
        JLabel lblNota = new JLabel("REGISTRA NOTA");
        PanelBase.add(lblNota, new GridConstraints(3, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        JLabel lblRis = new JLabel("REGISTRA RISULTATO");
        PanelBase.add(lblRis, new GridConstraints(3, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));

        // Riga 4 – Campi di input
        FieldNota = new JTextField();
        FieldNota.setToolTipText("Es: ho sentito affaticamento alla spalla sinistra");
        PanelBase.add(FieldNota, new GridConstraints(4, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, new Dimension(150, -1), null, 0, false));
        FieldRisultato = new JTextField();
        FieldRisultato.setToolTipText("Intero per ripetizioni, MM:SS per tempo");
        PanelBase.add(FieldRisultato, new GridConstraints(4, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, new Dimension(150, -1), null, 0, false));

        // Riga 5 – Pulsante SALVA (larghezza intera)
        SalvaButton = new JButton("SALVA RISULTATO");
        SalvaButton.setBackground(new Color(0x3F51B5));
        SalvaButton.setForeground(Color.WHITE);
        SalvaButton.setFocusPainted(false);
        PanelBase.add(SalvaButton, new GridConstraints(5, 0, 1, 2,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    // -------------------------------------------------------------------------
    // Binding dati → componenti
    // -------------------------------------------------------------------------

    private void bind(Control_Session.EsercizioDettDTO dto) {
        LabelNome.setText(dto.nome());
        LabelDescrizione.setText(dto.descrizione() != null ? dto.descrizione() : "");

        String tipoLabel = "RIPETIZIONI".equals(dto.tipo()) ? "Ripetizioni" : "Tempo";
        LabelTipoAtteso.setText(tipoLabel + " – atteso: " + dto.risultatoAttesoStr());

        if (dto.risultatoOttenutoStr() != null) {
            LabelRisultatoOttenuto.setText(
                    "Risultato: " + dto.risultatoOttenutoStr()
                    + (dto.notaStr() != null ? "  |  Nota: " + dto.notaStr() : ""));
            LabelRisultatoOttenuto.setForeground(new Color(0x4CAF50));
            // Pre-popola i campi con i valori già registrati
            FieldRisultato.setText(dto.risultatoOttenutoStr());
            if (dto.notaStr() != null) FieldNota.setText(dto.notaStr());
        } else {
            LabelRisultatoOttenuto.setText("Non ancora registrato");
            LabelRisultatoOttenuto.setForeground(Color.GRAY);
        }
    }

    // -------------------------------------------------------------------------
    // Collegamento pulsante SALVA
    // -------------------------------------------------------------------------

    private void collegaPulsante() {
        SalvaButton.addActionListener(e -> {
            if (onSalva != null) {
                onSalva.accept(
                        FieldRisultato.getText().trim(),
                        FieldNota.getText().trim());
            }
        });
    }

    // -------------------------------------------------------------------------
    // API pubblica
    // -------------------------------------------------------------------------

    /**
     * Inietta il callback chiamato al salvataggio.
     * Firma: {@code (risultatoRaw, nota) -> void}
     */
    public void setOnSalva(BiConsumer<String, String> callback) {
        this.onSalva = callback;
    }

    /** @return id dell'esercizio (usato dal padre per costruire le mappe) */
    public Long getIdEsercizio() { return idEsercizio; }

    /** @return testo grezzo del campo risultato (può essere vuoto) */
    public String getRisultatoRaw() { return FieldRisultato.getText().trim(); }

    /** @return testo del campo nota (può essere vuoto) */
    public String getNota() { return FieldNota.getText().trim(); }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return PanelBase;
    }
}
