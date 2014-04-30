package mvc.controller;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import mvc.view.View;
import mvc.model.*;

/**
 * Classe che controlla l'andamento della partita
 * @author Solomon Marian & Luca Negrini
 */
public class Controller {

    private static final int size = 8;

    private Model model;
    private View view;
    private Player white;
    private Player black;
    private Round round;
    private static Controller currentController;

    /**
     * Crea il controllore della partita
     */
    public Controller() {
        currentController = this;
        model = new Model();
        init();
        view = new View(model);
        model.addNewObserver(view);
        round = new Round(this);
        round.init();
    }

    /**
     * Restituisce il controllore attuale
     * @return il controllore
     */
    public static Controller getController() {
        return currentController;
    }

    /**
     * Avvia una nuova partita
     */
    public void restart() {
        view.dispose();
        model = new Model();
        init();
        view = new View(model);
        model.addNewObserver(view);
        round = new Round(this);
        round.init();
    }

    /**
     * Restituisce la finestra di gioco
     * @return la finestra di gioco
     */
    public View getView() {
        return view;
    }

    /**
     * Restituisce la rappresentazione della partita corrente
     * @return il modello corrente
     */
    public Model getModel() {
        return model;
    }

    /**
     * Restituisce l'oggetto che gestisce i turni
     * @return il gestore dei turni
     */
    public Round getRound() {
        return round;
    }

    /**
     * Restituisce il giocatore bianco
     * @return il giocatore bianco
     */
    public Player getWhite() {
        return white;
    }

    /**
     * Restituisce il giocatore nero
     * @return il giocatore nero
     */
    public Player getBlack() {
        return black;
    }

    public static void main(String args[]) {
        //impostazione look and feel nimbus
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString());
        }

        Controller controller = new Controller();
    }

    private void init() {
        //scelta del tipo di partita e richiesta dei nomi dei giocatori
        String[] scelte = {"Player vs Player", "Player vs CPU", "CPU vs CPU"};
        Object scelta = JOptionPane.showInputDialog(null, "Selezionare il tipo di gioco", "Seleziona giocatori", JOptionPane.INFORMATION_MESSAGE, null, scelte, scelte[1]);
        if (scelta instanceof Integer) {
            System.exit(0);
        }
        String s = (String) scelta;
        switch (s) {
            case "Player vs Player":
                do {
                    s = (String) JOptionPane.showInputDialog(null, "Nome giocatore bianco");
                } while (s.equals(""));
                white = new Player(s, this, true);
                do {
                    s = (String) JOptionPane.showInputDialog(null, "Nome giocatore nero");
                } while (s.equals(""));
                black = new Player(s, this, false);
                break;
            case "Player vs CPU":
                String[] teams = {"Bianco", "Nero"};
                String team = (String) JOptionPane.showInputDialog(null, "Selezione squadra", "Seleziona colore", JOptionPane.INFORMATION_MESSAGE, null, teams, teams[0]);
                String nome;
                do {
                    nome = (String) JOptionPane.showInputDialog(null, "Nome giocatore");
                } while (nome.equals(""));
                if (team.equals("Bianco")) {
                    white = new Player(nome, this, true);
                    black = new CpuPlayer(this, false);
                } else {
                    black = new Player(nome, this, false);
                    white = new CpuPlayer(this, true);
                }
                break;
            case "CPU vs CPU":
                black = new CpuPlayer(this, false);
                white = new CpuPlayer(this, true);
                break;
        }
    }

    /**
     * Interroga il modello per sapere se la pedina può muovere
     * @param i la riga della pedina
     * @param j la colonna della pedina
     * @return la possibilità di muoversi della pedina
     */
    public boolean canMove(int i, int j) {
        int x = round.getTurn() ? 0 : 1;
        if ((model.getTabellone()[i][j] % 2) == x) {
            for (int k = 0; k < model.getTabellone().length; k++) {
                for (int l = 0; l < model.getTabellone()[k].length; l++) {
                    if (((model.getTabellone()[k][l] % 2) == x) && (model.canEat(k, l))) {
                        return false;
                    }
                }
            }
            return model.canMove(i, j);
        } else {
            return false;
        }

    }

    /**
     * Comunica al modello di muovere una pedina
     * @param sourceLocation contiene le coordinate della pedina
     * @param destLocation contiene le coordinate della destinazione
     * @return il risultato dell'operazione
     */
    public boolean move(int[] sourceLocation, int[] destLocation) {
        int x = round.getTurn() ? 0 : 1;
        if ((model.getTabellone()[sourceLocation[0]][sourceLocation[1]] % 2) == x) {
            for (int k = 0; k < model.getTabellone().length; k++) {
                for (int l = 0; l < model.getTabellone()[k].length; l++) {
                    if (((model.getTabellone()[k][l] % 2) == x) && (model.canEat(k, l))) {
                        return false;
                    }
                }
            }
            if (model.move(sourceLocation[0], sourceLocation[1], destLocation[0], destLocation[1])) {
                round.next();
                return true;
            }
        }
        return false;

    }

    /**
     * Interroga il modello per ricevere la lista delle celle in cui la pedina si può muovere
     * @param i la riga della cella
     * @param j la colonna della cella
     * @return la lista dei suggerimenti
     */
    public ArrayList<int[]> getHints(int i, int j) {
        return model.getHints(i, j);
    }

    /**
     * Interroga il modello per sapere se la pedina può mangiare
     * @param i la riga della pedina
     * @param i0 la colonna della pedina
     * @return la possibilità di mangiare della pedina
     */
    public boolean canEat(int i, int i0) {
        int x = round.getTurn() ? 0 : 1;
        if ((model.getTabellone()[i][i0] % 2) == x) {
            return model.canEat(i, i0);
        }
        return false;
    }

    /**
     * Interroga il modello per ricevere la lista delle celle in cui la pedina può mangiare
     * @param i la riga della cella
     * @param i0 la colonna della cella
     * @return la lista dei suggerimenti
     */
    public ArrayList<int[]> getEatingHints(int i, int i0) {
        return model.getEatingHints(i, i0);
    }

    /**
     * Comunica al modello che una pedina deve mangiare
     * @param sourceLocation contiene le coordinate della pedina
     * @param destLocation contiene le coordinate della destinazione
     * @return il risultato dell'operazione
     */
    public boolean eat(int[] sourceLocation, int[] destLocation) {
        int x = round.getTurn() ? 0 : 1;
        if ((model.getTabellone()[sourceLocation[0]][sourceLocation[1]] % 2) == x) {
            if (model.eat(sourceLocation[0], sourceLocation[1], destLocation[0], destLocation[1])) {
                if (!model.canEat(destLocation[0], destLocation[1])) {
                    round.next();
                } else {
                    if (x == 0) {
                        if (white.isHuman()) {
                            view.hilightPossibleMoves(model.getPossibleMoves(round.getTurn()));
                        }
                    } else {
                        if (black.isHuman()) {
                            view.hilightPossibleMoves(model.getPossibleMoves(round.getTurn()));
                        }
                    }
                }
                return true;
            }
        }
        return false;

    }

    /**
     * Abilita l'interfaccia per l'uso dell'utente
     */
    public void enableInterface() {
        view.enableInterface();
        view.setVisible(true);
    }

    /**
     * Disabilita l'interfaccia
     */
    public void disableInterface() {
        view.disableInterface();
    }

}
