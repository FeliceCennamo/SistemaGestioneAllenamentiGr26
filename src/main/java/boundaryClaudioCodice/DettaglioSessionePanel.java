package boundaryClaudioCodice;

import boundaryClaudioCodice.SessioniListaPanel;
import controller.AtletaSessioniController;
import controller.AtletaSessioniController.EsercizioDTO;
import controller.AtletaSessioniController.SessioneDTO;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * BOUNDARY – Pannello di dettaglio di una sessione.
 *
 * Mostra le informazioni della sessione nell'header e la lista
 * degli esercizi con i rispettivi risultati attesi/effettivi.
 *
 * Il bottone "← Indietro" notifica il MainFrame per tornare alla lista.
 */
public class DettaglioSessionePanel extends JPanel {

    // Palette condivisa con SessioniListaPanel
    private static final Color BG_MAIN      = SessioniListaPanel.BG_MAIN;
    private static final Color BG_CARD      = Color.WHITE;
    private static final Color ACCENT       = SessioniListaPanel.ACCENT;
    private static final Color TEXT_PRIMARY = SessioniListaPanel.TEXT_PRIMARY;
    private static final Color TEXT_SEC     = SessioniListaPanel.TEXT_SEC;
    private static final Color BORDER_CARD  = SessioniListaPanel.BORDER_CARD;

    // Colori specifici del dettaglio
    private static final Color BG_HEADER = new Color(0x1A1A1A);
    private static final Color BG_RIPS   = new Color(0xEFF6FF);  // azzurrino per rip.
    private static final Color BG_TEMPO  = new Color(0xFFF7ED);  // caldo per tempo

    private final AtletaSessioniController controller;
    private final SessioneDTO sessione;
    private IndietroListener indietroListener;

    // ── Costruttore ─────────────────────────────────────────────────────────

    public DettaglioSessionePanel(AtletaSessioniController controller, SessioneDTO sessione) {
        this.controller = controller;
        this.sessione   = sessione;

        setBackground(BG_MAIN);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCorpo(),  BorderLayout.CENTER);
    }

    // ── Builder ─────────────────────────────────────────────────────────────

    /** Header scuro con titolo sessione e bottone indietro. */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_HEADER);
        header.setBorder(new EmptyBorder(20, 32, 20, 32));

        // Bottone indietro
        JButton btnBack = new JButton("← Torna alle sessioni");
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnBack.setForeground(new Color(0xAAAAAA));
        btnBack.setBackground(new Color(0x2A2A2A));
        btnBack.setBorder(new CompoundBorder(
                new LineBorder(new Color(0x444444), 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> { if (indietroListener != null) indietroListener.onIndietro(); });
        btnBack.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnBack.setForeground(Color.WHITE); }
            @Override public void mouseExited(MouseEvent e)  { btnBack.setForeground(new Color(0xAAAAAA)); }
        });

        // Titolo + meta
        JLabel titolo = new JLabel(sessione.titolo());
        titolo.setFont(new Font("Georgia", Font.BOLD, 22));
        titolo.setForeground(Color.WHITE);

        String metaTesto = "📅 " + sessione.dataSvolgimento()
                + "   ⏱ " + sessione.durata()
                + "   " + statoIcon(sessione.stato()) + " " + sessione.stato().replace("_", " ");
        JLabel meta = new JLabel(metaTesto);
        meta.setFont(new Font("SansSerif", Font.PLAIN, 12));
        meta.setForeground(new Color(0x888888));

        JLabel desc = new JLabel("<html><body style='width:480px'>" + sessione.descrizione() + "</body></html>");
        desc.setFont(new Font("SansSerif", Font.ITALIC, 12));
        desc.setForeground(new Color(0xAAAAAA));

        JPanel testo = new JPanel();
        testo.setOpaque(false);
        testo.setLayout(new BoxLayout(testo, BoxLayout.Y_AXIS));
        testo.add(titolo);
        testo.add(Box.createVerticalStrut(4));
        testo.add(meta);
        testo.add(Box.createVerticalStrut(6));
        testo.add(desc);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(btnBack, BorderLayout.WEST);

        header.add(top,   BorderLayout.NORTH);
        header.add(testo, BorderLayout.SOUTH);
        // piccolo padding tra bottone e titolo
        header.add(Box.createVerticalStrut(12) instanceof Component c ? c : new JLabel(), BorderLayout.CENTER);

        return header;
    }

    /** Corpo con intestazione colonne e lista esercizi. */
    private JScrollPane buildCorpo() {
        List<EsercizioDTO> esercizi = controller.getEserciziSessione(sessione.id());

        JPanel corpo = new JPanel();
        corpo.setBackground(BG_MAIN);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBorder(new EmptyBorder(24, 32, 32, 32));

        // Sezione titolo
        JLabel titSection = new JLabel("Esercizi (" + esercizi.size() + ")");
        titSection.setFont(new Font("Georgia", Font.BOLD, 18));
        titSection.setForeground(TEXT_PRIMARY);
        titSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        corpo.add(titSection);
        corpo.add(Box.createVerticalStrut(16));

        if (esercizi.isEmpty()) {
            JLabel empty = new JLabel("Nessun esercizio presente in questa sessione.");
            empty.setFont(new Font("SansSerif", Font.ITALIC, 13));
            empty.setForeground(TEXT_SEC);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            corpo.add(empty);
        } else {
            int index = 1;
            for (EsercizioDTO e : esercizi) {
                corpo.add(buildEsercizioCard(e, index++));
                corpo.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scroll = new JScrollPane(corpo);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(BG_MAIN);
        scroll.getViewport().setBackground(BG_MAIN);
        return scroll;
    }

    /** Card singolo esercizio. */
    private JPanel buildEsercizioCard(EsercizioDTO e, int numero) {
        boolean isRip    = e.tipo().equals("RIPETIZIONI");
        Color   bgAccent = isRip ? BG_RIPS : BG_TEMPO;

        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_CARD, 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Numero esercizio (cerchio)
        JLabel numLabel = new JLabel(String.valueOf(numero)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isRip ? new Color(0x3B82F6) : new Color(0xF59E0B));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        numLabel.setPreferredSize(new Dimension(32, 32));
        numLabel.setHorizontalAlignment(SwingConstants.CENTER);
        numLabel.setVerticalAlignment(SwingConstants.CENTER);
        numLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        numLabel.setForeground(Color.WHITE);
        numLabel.setOpaque(false);
        card.add(numLabel, BorderLayout.WEST);

        // Corpo centrale
        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));

        JLabel nome = new JLabel(e.nome());
        nome.setFont(new Font("Georgia", Font.BOLD, 15));
        nome.setForeground(TEXT_PRIMARY);

        JLabel descLabel = new JLabel("<html><body style='width:400px'>" + (e.descrizione() != null ? e.descrizione() : "") + "</body></html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_SEC);

        corpo.add(nome);
        corpo.add(Box.createVerticalStrut(4));
        corpo.add(descLabel);
        corpo.add(Box.createVerticalStrut(10));

        // Chip tipo + risultato atteso / effettivo
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        chips.setOpaque(false);
        chips.add(buildChip(isRip ? "🔁 RIPETIZIONI" : "⏱ TEMPO", bgAccent,
                isRip ? new Color(0x1D4ED8) : new Color(0x92400E)));
        chips.add(Box.createHorizontalStrut(8));
        chips.add(buildChip("Atteso: " + e.risultatoAtteso(), new Color(0xF3F4F6), TEXT_SEC));

        if (e.risultatoEffettivo() != null) {
            chips.add(Box.createHorizontalStrut(8));
            chips.add(buildChip("✓ " + e.risultatoEffettivo(), new Color(0xDCFCE7), new Color(0x166534)));
        }
        corpo.add(chips);

        // Nota (se presente)
        if (e.nota() != null && !e.nota().isBlank()) {
            corpo.add(Box.createVerticalStrut(8));
            JLabel notaLabel = new JLabel("📝  " + e.nota());
            notaLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
            notaLabel.setForeground(new Color(0x888888));
            corpo.add(notaLabel);
        }

        card.add(corpo, BorderLayout.CENTER);
        return card;
    }

    private JLabel buildChip(String testo, Color bg, Color fg) {
        JLabel chip = new JLabel(testo) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(new Font("SansSerif", Font.BOLD, 10));
        chip.setForeground(fg);
        chip.setOpaque(false);
        chip.setBorder(new EmptyBorder(3, 9, 3, 9));
        return chip;
    }

    private String statoIcon(String stato) {
        return switch (stato) {
            case "COMPLETATA" -> "✅";
            case "IN_CORSO"   -> "🔄";
            default           -> "📋";
        };
    }

    // ── Listener ────────────────────────────────────────────────────────────

    public interface IndietroListener {
        void onIndietro();
    }

    public void setIndietroListener(IndietroListener l) {
        this.indietroListener = l;
    }
}
