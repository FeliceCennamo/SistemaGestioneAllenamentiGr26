package boundaryClaudioCodice;

import controller.AtletaSessioniController;
import controller.AtletaSessioniController.SessioneDTO;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * BOUNDARY – Pannello che mostra la lista delle sessioni di un atleta.
 *
 * Struttura visiva:
 *   ┌─────────────────────────────────┐
 *   │  Header: "Le mie sessioni"      │
 *   ├─────────────────────────────────┤
 *   │  Card sessione 1                │
 *   │  Card sessione 2                │
 *   │  ...                            │
 *   └─────────────────────────────────┘
 *
 * Alla selezione di una card, notifica il MainFrame tramite il listener.
 */
public class SessioniListaPanel extends JPanel {

    // ── Palette colori ──────────────────────────────────────────────────────
    static final Color BG_MAIN     = new Color(0xF5F4F0);
    static final Color BG_CARD     = Color.WHITE;
    static final Color BG_CARD_HOV = new Color(0xFFF8EE);
    static final Color ACCENT      = new Color(0xD4580A);   // arancione bruciato
    static final Color TEXT_PRIMARY = new Color(0x1A1A1A);
    static final Color TEXT_SEC    = new Color(0x666666);
    static final Color BORDER_CARD = new Color(0xE0DDD8);

    // Colori badge stato
    private static final Color COL_ASSEGNATA  = new Color(0x3B82F6);
    private static final Color COL_IN_CORSO   = new Color(0xF59E0B);
    private static final Color COL_COMPLETATA = new Color(0x22C55E);

    // ── Campi ───────────────────────────────────────────────────────────────
    private final AtletaSessioniController controller;
    private final Long idAtleta;
    private SessioneSelezionataListener listener;

    // ── Costruttore ─────────────────────────────────────────────────────────

    public SessioniListaPanel(AtletaSessioniController controller, Long idAtleta) {
        this.controller = controller;
        this.idAtleta   = idAtleta;

        setBackground(BG_MAIN);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildLista(),  BorderLayout.CENTER);
    }

    // ── Builder componenti ──────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_MAIN);
        header.setBorder(new EmptyBorder(28, 32, 8, 32));

        JLabel titolo = new JLabel("Le mie sessioni");
        titolo.setFont(new Font("Georgia", Font.BOLD, 26));
        titolo.setForeground(TEXT_PRIMARY);

        JLabel sottotitolo = new JLabel("Seleziona una sessione per visualizzarne i dettagli");
        sottotitolo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sottotitolo.setForeground(TEXT_SEC);

        JPanel testo = new JPanel();
        testo.setLayout(new BoxLayout(testo, BoxLayout.Y_AXIS));
        testo.setOpaque(false);
        testo.add(titolo);
        testo.add(Box.createVerticalStrut(4));
        testo.add(sottotitolo);

        header.add(testo, BorderLayout.CENTER);

        // Riga separatrice
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_CARD);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(12, 0, 0, 0));
        wrapper.add(sep);
        header.add(wrapper, BorderLayout.SOUTH);

        return header;
    }

    private JScrollPane buildLista() {
        List<SessioneDTO> sessioni = controller.getSessioniAtleta(idAtleta);

        JPanel lista = new JPanel();
        lista.setBackground(BG_MAIN);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBorder(new EmptyBorder(16, 32, 32, 32));

        if (sessioni.isEmpty()) {
            JLabel empty = new JLabel("Nessuna sessione trovata.");
            empty.setFont(new Font("SansSerif", Font.ITALIC, 14));
            empty.setForeground(TEXT_SEC);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            lista.add(empty);
        } else {
            for (SessioneDTO s : sessioni) {
                lista.add(buildCard(s));
                lista.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(BG_MAIN);
        scroll.getViewport().setBackground(BG_MAIN);
        return scroll;
    }

    private JPanel buildCard(SessioneDTO s) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_CARD, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Striscia colorata a sinistra in base allo stato
        JPanel stripe = new JPanel();
        stripe.setPreferredSize(new Dimension(5, 0));
        stripe.setBackground(coloreStato(s.stato()));
        stripe.setOpaque(true);
        card.add(stripe, BorderLayout.WEST);

        // Corpo testo
        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));

        JLabel titolo = new JLabel(s.titolo());
        titolo.setFont(new Font("Georgia", Font.BOLD, 16));
        titolo.setForeground(TEXT_PRIMARY);

        JLabel desc = new JLabel(troncaTesto(s.descrizione(), 80));
        desc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        desc.setForeground(TEXT_SEC);

        JPanel meta = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        meta.setOpaque(false);
        meta.add(buildPillStato(s.stato()));
        meta.add(Box.createHorizontalStrut(10));
        meta.add(buildMetaLabel("📅 " + s.dataSvolgimento()));
        meta.add(Box.createHorizontalStrut(10));
        if (!s.durata().equals("—"))
            meta.add(buildMetaLabel("⏱ " + s.durata()));

        corpo.add(titolo);
        corpo.add(Box.createVerticalStrut(4));
        corpo.add(desc);
        corpo.add(Box.createVerticalStrut(8));
        corpo.add(meta);

        card.add(corpo, BorderLayout.CENTER);

        // Freccia →
        JLabel arrow = new JLabel("›");
        arrow.setFont(new Font("SansSerif", Font.PLAIN, 24));
        arrow.setForeground(new Color(0xCCCCCC));
        card.add(arrow, BorderLayout.EAST);

        // Hover + click
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(BG_CARD_HOV);
                stripe.setBackground(ACCENT);
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(BG_CARD);
                stripe.setBackground(coloreStato(s.stato()));
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (listener != null) listener.onSessioneSelezionata(s);
            }
        });

        return card;
    }

    private JLabel buildPillStato(String stato) {
        JLabel pill = new JLabel(stato.replace("_", " ")) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(coloreStato(stato).darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setFont(new Font("SansSerif", Font.BOLD, 10));
        pill.setForeground(Color.WHITE);
        pill.setOpaque(false);
        pill.setBorder(new EmptyBorder(3, 10, 3, 10));
        return pill;
    }

    private JLabel buildMetaLabel(String testo) {
        JLabel l = new JLabel(testo);
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setForeground(TEXT_SEC);
        return l;
    }

    private Color coloreStato(String stato) {
        return switch (stato) {
            case "COMPLETATA" -> COL_COMPLETATA;
            case "IN_CORSO"   -> COL_IN_CORSO;
            default           -> COL_ASSEGNATA;
        };
    }

    private String troncaTesto(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }

    // ── Listener ────────────────────────────────────────────────────────────

    public interface SessioneSelezionataListener {
        void onSessioneSelezionata(SessioneDTO sessione);
    }

    public void setSessioneSelezionataListener(SessioneSelezionataListener l) {
        this.listener = l;
    }
}
