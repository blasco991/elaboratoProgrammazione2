package mvc.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import mvc.controller.Controller;
import mvc.model.Model;

/**
 * Rappresenta la finestra principale del programma
 * Fornisce una rappresentazione del modello di gioco
 * @see JFrame
 * @see ActionListener
 * @see Observer
 * @author Solomon Marian & Luca Negrini
 */
public class View extends JFrame implements ActionListener, Observer {

    private final Model model;
    private JPanel pannello;
    private final static Border hintBorder = BorderFactory.createLineBorder(new Color(25, 227, 72, 150), 3, true);
    private final static Border selectionBorder = BorderFactory.createLineBorder(new Color(227, 224, 25, 150), 3, true);
    private final static Border eatingHintBorder = BorderFactory.createLineBorder(new Color(227, 25, 25, 150), 3, true);
    private final static Border canDoSomethingBorder = BorderFactory.createLineBorder(new Color(8, 93, 212, 100), 3, true);
    private boolean selected;
    private int[] selectionLocation;
    private boolean movementEnabled;// disattiva l'interfaccia se tocca all'ia
    private PlayerLabel black, white;
    private final ImageIcon whiteCell, blackCell, border, whiteDama, blackDama, whiteDamone, blackDamone;

    /**
     * Costruttore di default
     * @param bThis il modello da rappresentare
     */
    public View(Model bThis) {
        movementEnabled = true;
        model = bThis;
        selected = false;
        whiteCell = new ImageIcon(getClass().getResource("/resources/images.jpg"));
        blackCell = new ImageIcon(getClass().getResource("/resources/images1.jpg"));
        border = new ImageIcon(getClass().getResource("/resources/images3.jpg"));
        whiteDama = new ImageIcon(getClass().getResource("/resources/pedina bianca.png"));
        blackDama = new ImageIcon(getClass().getResource("/resources/pedina rossa.png"));
        whiteDamone = new ImageIcon(getClass().getResource("/resources/damone bianco.png"));
        blackDamone = new ImageIcon(getClass().getResource("/resources/damone rosso.png"));
        initInterface();
        setVisible(true);
    }

    private void buildMenu() {
        //creazione barra del menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Opzioni");
        JMenuItem restart = new JMenuItem("Ricomincia");
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int x = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler cominciare una nuova partita?");
                if (x == JOptionPane.YES_OPTION) {
                    Controller.getController().restart();
                }
            }
        });
        menu.add(restart);
        JMenuItem exit = new JMenuItem("Esci");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });
        menu.add(exit);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void initInterface() {
        setBounds(300, 30, 0, 0);
        setSize(694, 711);
        setResizable(false);
        setTitle("Dama");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        buildMenu();
        black = new PlayerLabel(Controller.getController().getBlack().getNome(), false);
        add(black, BorderLayout.NORTH);
        white = new PlayerLabel(Controller.getController().getWhite().getNome(), true);
        add(white, BorderLayout.SOUTH);
        pannello = new JPanel(new GridLayout(8, 8, 0, 0));
        pannello.setDoubleBuffered(true);
        pannello.setBorder(new MatteBorder(2, 4, 2, 4, border));
        createInterface();
        add(pannello, BorderLayout.CENTER);
    }

    /**
     * Restituisce l'etichetta che contiene il nome del giocatore nero
     * @return l'etichetta
     */
    public PlayerLabel getBlack() {
        return black;
    }

    /**
     * Restituisce l'etichetta che contiene il nome del giocatore bianco
     * @return l'etichetta
     */
    public PlayerLabel getWhite() {
        return white;
    }

    private void hilight(int i, int j, int type) {
        //evidenzia la cella indicata del colore specificato in type
        Border b = null;
        switch (type) {
            case 0:
                //cella che contiene la pedina su cui ho cliccato
                b = selectionBorder;
                break;
            case 1:
                //cella in cui la pedina selezionata si può muovere
                b = hintBorder;
                break;
            case 2:
                //cella in cui la pedina selezionata può mangiare
                b = eatingHintBorder;
                break;
            case 3:
                //cella che contiene una pedina che si può muovere o può mangiare. Serve per i suggerimenti di inizio turno
                b = canDoSomethingBorder;
                break;
        }
        ((JButton) pannello.getComponent(i * 8 + j)).setBorder(b);
    }

    private void reset() {
        //toglie tutti i bordi dalle celle
        for (Component c : pannello.getComponents()) {
            ((JButton) c).setBorder(null);
        }
    }

    /**
     * Abilita l'interfaccia per le interazioni dell'utente
     */
    public void enableInterface() {
        movementEnabled = true;
    }

    /**
     * Disabilita l'interfaccia
     */
    public void disableInterface() {
        movementEnabled = false;
    }

    private void createInterface() {
        boolean b = true;
        for (int i = 0; i < model.getTabellone().length; i++) {
            for (int j = 0; j < model.getTabellone()[i].length; j++) {
                Cell cell = new Cell(i, j);
                if (b) {
                    cell.setImage(whiteCell.getImage());
                } else {
                    cell.setImage(blackCell.getImage());
                }
                b = !b;
                cell.setBorder(null);
                ImageIcon icon = null;
                if (model.getTabellone()[i][j] != 0) {
                    if (model.getTabellone()[i][j] == 1) {
                        icon = blackDama;
                    }
                    if (model.getTabellone()[i][j] == 2) {
                        icon = whiteDama;
                    }
                    if (model.getTabellone()[i][j] == 3) {
                        icon = blackDamone;
                    }
                    if (model.getTabellone()[i][j] == 4) {
                        icon = whiteDamone;
                    }
                    Pedina button = new Pedina(icon, this);
                    cell.add(button);
                }
                cell.addActionListener(this);
                pannello.add(cell);
            }
            b = !b;
        }
    }

    /**
     * Gestische gli eventi del mouse
     * @param ae l'evento
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (movementEnabled) {
            reset();
            if (ae.getSource() instanceof Pedina) {
                int[] position = {((Cell) ((Pedina) ae.getSource()).getParent()).x, ((Cell) ((Pedina) ae.getSource()).getParent()).y};
                //movimento
                if (Controller.getController().canMove(position[0], position[1])) {
                    selected = true;
                    selectionLocation = position;
                    hilight(position[0], position[1], 0);
                    for (int[] hint : Controller.getController().getHints(position[0], position[1])) {
                        hilight(hint[0], hint[1], 1);
                    }
                } else {
                    hilightPossibleMoves(model.getPossibleMoves(Controller.getController().getRound().getTurn()));
                }
                //mangiata
                if (Controller.getController().canEat(position[0], position[1])) {
                    selected = true;
                    selectionLocation = position;
                    hilight(position[0], position[1], 0);
                    for (int[] hint : Controller.getController().getEatingHints(position[0], position[1])) {
                        hilight(hint[0], hint[1], 2);
                    }
                }
            } else if (ae.getSource() instanceof Cell) {
                if (selected) {
                    int[] position = {((Cell) ae.getSource()).x, ((Cell) ae.getSource()).y};
                    if (Controller.getController().move(selectionLocation, position)) {
                    } else {
                        if (!Controller.getController().eat(selectionLocation, position)) {
                            hilightPossibleMoves(model.getPossibleMoves(Controller.getController().getRound().getTurn()));
                        }
                    }
                    selected = false;
                    selectionLocation = null;
                }
            }
        }
    }

    /**
     * Aggiorna l'interfaccia in funzione ad un cambiamento del modello
     * @param o il modello
     * @param o1 non serve
     */
    @Override
    public void update(Observable o, Object o1) {
        for (Component c : pannello.getComponents()) {
            ((JButton) c).setBorder(null);
            if (((JButton) c).getComponentCount() > 0) {
                ((JButton) c).remove(0);
            }
        }
        for (int i = 0; i < model.getTabellone().length; i++) {
            for (int j = 0; j < model.getTabellone()[i].length; j++) {
                ImageIcon icon = null;
                if (model.getTabellone()[i][j] != 0) {
                    if (model.getTabellone()[i][j] == 1) {
                        icon = blackDama;
                    }
                    if (model.getTabellone()[i][j] == 2) {
                        icon = whiteDama;
                    }
                    if (model.getTabellone()[i][j] == 3) {
                        icon = blackDamone;
                    }
                    if (model.getTabellone()[i][j] == 4) {
                        icon = whiteDamone;
                    }
                    Pedina button = new Pedina(icon, this);
                    button.setName(i + " " + j);
                    ((Cell) pannello.getComponent(i * 8 + j)).add(button);
                }
            }
        }
        pannello.revalidate();
        pannello.repaint();
    }

    /**
     * Evidenzia le celle contenute nella lista passata come parametro
     * @param possibleMoves le celle da evidenziare
     */
    public void hilightPossibleMoves(ArrayList<int[]> possibleMoves) {
        for (int[] is : possibleMoves) {
            hilight(is[0], is[1], 3);
        }
    }

}
