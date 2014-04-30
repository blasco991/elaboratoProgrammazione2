package mvc.model;

import java.util.ArrayList;
import java.util.Observable;
import javax.swing.JOptionPane;
import mvc.controller.Controller;
import mvc.view.View;

/**
 * Classe che rappresenta il modello di gioco
 * Fornisce una rappresentazione del campo di gioco e tutti i le funzioni che interagiscono con esso
 * @see Observable
 * @author Solomon Marian & Luca Negrini
 */
public class Model extends Observable {

    protected final int[][] tabellone;
    /**
     * Il valore di default per la cella vuota
     */
    public final static int VUOTO = 0;
    /**
     * Il valore di default per la dama nera
     */
    public final static int DAMA_NERA = 1;
    /**
     * Il valore di default per la dama bianca
     */
    public final static int DAMA_BIANCA = 2;
    /**
     * Il valore di default per il damone nero
     */
    public final static int DAMONE_NERO = 3;
    /**
     * Il valore di default per il damone bianco
     */
    public final static int DAMONE_BIANCO = 4;
    protected int numeroNeri, numeroBianchi;
    private Controller controller;

    public Model() {
        super();
        tabellone = new int[8][8];
        numeroNeri = 3 * (8 / 2);// le pedine occupano metà di una riga, e ci sono tre righe di pedine
        numeroBianchi = numeroNeri;
        inizializzaTabellone();
    }

    /**
     * Crea il collegamento tra il modello e la finestra che lo rappresenta
     * @param view la finestra di gioco che deve rappresentare il modello
     */
    public void addNewObserver(View view) {
        addObserver(view);
    }

    /**
     * Costruttore che crea il modello in base a un tabellone già inizializzato
     * @param tabellone il tabellone su cui lavorare
     */
    public Model(int[][] tabellone) {
        this.tabellone = tabellone;
        numeroBianchi = 0;
        numeroNeri = 0;
        for (int i = 0; i < tabellone.length; i++) {
            for (int j = 0; j < tabellone.length; j++) {
                switch (tabellone[i][j] % 2) {
                    case 0:
                        if (tabellone[i][j] != VUOTO) {
                            numeroBianchi++;
                        }
                        break;
                    case 1:
                        numeroNeri++;
                        break;
                }
            }
        }
    }

    /**
     * Restituisce il tabellone di gioco
     * @return il tabellone di gioco
     */
    public int[][] getTabellone() {
        return tabellone;
    }

    /**
     * Restituisce tutte le possibili mosse di un giocatore
     * @param team il team di cui cercare le mosse
     * @return le mosse possibili
     */
    public ArrayList<int[]> getPossibleMoves(boolean team) {
        // true = bianco     false = nero
        int x = team ? 0 : 1;
        ArrayList<int[]> moves = new ArrayList();
        for (int i = 0; i < tabellone.length; i++) {
            for (int j = 0; j < tabellone[i].length; j++) {
                if (tabellone[j][i] != VUOTO && ((tabellone[j][i] % 2) == x) && canEat(j, i)) {
                    int[] a = {j, i};
                    moves.add(a);
                }
            }
        }
        if (moves.isEmpty()) {
            for (int i = 0; i < tabellone.length; i++) {
                for (int j = 0; j < tabellone[i].length; j++) {
                    if (tabellone[j][i] != VUOTO && ((tabellone[j][i] % 2) == x) && canMove(j, i)) {
                        int[] a = {j, i};
                        moves.add(a);
                    }
                }
            }
        }
        return moves;

    }

    private void inizializzaTabellone() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //le prime tre righe sono dei neri
                if (i < 3) {
                    if ((j + i) % 2 == 0) {
                        tabellone[i][j] = DAMA_NERA;
                        continue;
                    }
                }
                //le ultime tre sono dei bianchi
                if (i > 8 - 4) {
                    if ((j + i) % 2 == 0) {
                        tabellone[i][j] = DAMA_BIANCA;
                        continue;
                    }
                }
                tabellone[i][j] = VUOTO;
            }
        }
    }

    /**
     * Stabilisce se una pedina si puo' muovere
     * @param x la coordinata x della pedina
     * @param y la coordinata y della pedina
     * @return la possibilita' di una pedina di muoversi
     */
    public boolean canMove(int x, int y) {
        boolean canMove = false;
        //se c'è eccezione perché esce dall'array, non faccio niente, così è come se la cella non esistesse e non facesse nessun controllo
        switch (tabellone[x][y]) {
            // casella vuota, niente da muovere
            case VUOTO:
                return false;
            // dama nera, deve muovere in diagonale verso il basso
            case DAMA_NERA:
                try {
                    if (tabellone[x + 1][y + 1] == VUOTO) {
                        canMove = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x + 1][y - 1] == VUOTO) {
                        canMove = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
            case DAMA_BIANCA:
                // dama bianca, deve muovere in diagonale verso l'alto
                try {
                    if (tabellone[x - 1][y - 1] == VUOTO) {
                        canMove = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x - 1][y + 1] == VUOTO) {
                        canMove = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
            //damoni, possono muovere ovunque
            case DAMONE_NERO:
                try {
                    if (tabellone[x - 1][y + 1] == VUOTO) {
                        canMove = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x + 1][y + 1] == VUOTO) {
                        canMove = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x - 1][y - 1] == VUOTO) {
                        canMove = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x + 1][y - 1] == VUOTO) {
                        canMove = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
            case DAMONE_BIANCO:
                try {
                    if (tabellone[x - 1][y + 1] == VUOTO) {
                        canMove = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x + 1][y + 1] == VUOTO) {
                        canMove = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x - 1][y - 1] == VUOTO) {
                        canMove = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x + 1][y - 1] == VUOTO) {
                        canMove = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
        }
        return canMove;
    }

    /**
     * stabilisce se un giocatore può muovere o meno
     * @param team la squadra del giocatore
     * @return la possibilita' di un giocatore di muoversi
     */
    public boolean canPlayerMove(boolean team) {
        //player: true -> white    false -> black
        int x = team ? 0 : 1;
        for (int i = 0; i < tabellone.length; i++) {
            for (int j = 0; j < tabellone[i].length; j++) {
                if (tabellone[j][i] != VUOTO && ((tabellone[j][i] % 2) == x) && (canMove(j, i) || canEat(j, i))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Restituisce una lista di tutte i possibili movimenti che una pedina può effettuare
     * @param i la riga della pedina
     * @param j la colonna della pedina
     * @return tutti i possibili movimenti della pedina
     */
    public ArrayList<int[]> getHints(int i, int j) {
        //i = colonna, j = riga
        ArrayList<int[]> ret = new ArrayList<>();
        //ogni possibile cella nella quale la pedina si può muovere viene inserita nella lista
        switch (tabellone[i][j]) {
            case DAMA_NERA:
                try {
                    if (tabellone[i + 1][j + 1] == VUOTO) {
                        int[] temp = new int[2];
                        temp[0] = (i + 1);
                        temp[1] = (j + 1);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i + 1][j - 1] == VUOTO) {
                        int[] temp = new int[2];
                        temp[0] = (i + 1);
                        temp[1] = (j - 1);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
            case DAMA_BIANCA:
                try {
                    if (tabellone[i - 1][j - 1] == VUOTO) {
                        int[] temp = new int[2];
                        temp[0] = (i - 1);
                        temp[1] = (j - 1);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i - 1][j + 1] == VUOTO) {
                        int[] temp = new int[2];
                        temp[0] = (i - 1);
                        temp[1] = (j + 1);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
            case DAMONE_NERO:
                try {
                    if (tabellone[i - 1][j + 1] == VUOTO) {
                        int[] temp = new int[2];
                        temp[0] = (i - 1);
                        temp[1] = (j + 1);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i + 1][j + 1] == VUOTO) {
                        int[] temp = new int[2];
                        temp[0] = (i + 1);
                        temp[1] = (j + 1);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i - 1][j - 1] == VUOTO) {
                        int[] temp = new int[2];
                        temp[0] = (i - 1);
                        temp[1] = (j - 1);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i + 1][j - 1] == VUOTO) {
                        int[] temp = new int[2];
                        temp[0] = (i + 1);
                        temp[1] = (j - 1);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
            case DAMONE_BIANCO:
                try {
                    if (tabellone[i - 1][j + 1] == VUOTO) {
                        int[] temp = new int[2];
                        temp[0] = (i - 1);
                        temp[1] = (j + 1);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i + 1][j + 1] == VUOTO) {
                        int[] temp = new int[2];
                        temp[0] = (i + 1);
                        temp[1] = (j + 1);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i - 1][j - 1] == VUOTO) {
                        int[] temp = new int[2];
                        temp[0] = (i - 1);
                        temp[1] = (j - 1);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i + 1][j - 1] == VUOTO) {
                        int[] temp = new int[2];
                        temp[0] = (i + 1);
                        temp[1] = (j - 1);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
        }
        return ret;
    }

    /**
     * Se possibile, muove la pedina indicata nella posizione destinazione
     * @param i la riga della pedina
     * @param i0 la colonna della pedina
     * @param i1 la riga destinazione
     * @param i2 la colonna destinazione
     * @return risultato dell'operazione di movimento
     */
    public boolean move(int i, int i0, int i1, int i2) {
        if (canMove(i, i0)) {
            for (int[] hint : getHints(i, i0)) {
                if ((i1 == hint[0]) && (i2 == hint[1])) {
                    //se arrivo in fondo, devo creare un damone
                    if (((i1 == 0) || (i1 == 8 - 1)) && (tabellone[i][i0] < 3)) {
                        tabellone[i1][i2] = tabellone[i][i0] + 2;
                    } else {
                        tabellone[i1][i2] = tabellone[i][i0];
                    }
                    tabellone[i][i0] = VUOTO;
                    setChanged();
                    notifyObservers();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Stabilisce se una pedina può mangiare
     * @param x la riga della pedina
     * @param y la colonna della pedina
     * @return la possibilita' della pedina di muoversi
     */
    public boolean canEat(int x, int y) {
        boolean canEat = false;
        //se c'è eccezione perché esce dall'array, non faccio niente, così è come se la cella on esistesse e non facesse nessun controllo
        switch (tabellone[x][y]) {
            // casella vuota, niente da muovere
            case VUOTO:
                return false;
            // dama nera, deve mangiare in diagonale verso il basso
            case DAMA_NERA:
                try {
                    if (tabellone[x + 2][y + 2] == VUOTO && tabellone[x + 1][y + 1] == DAMA_BIANCA) {
                        canEat = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x + 2][y - 2] == VUOTO && tabellone[x + 1][y - 1] == DAMA_BIANCA) {
                        canEat = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
            //dama bianca, deve mangiare in diagonale verso l'alto
            case DAMA_BIANCA:
                try {
                    if (tabellone[x - 2][y - 2] == VUOTO && tabellone[x - 1][y - 1] == DAMA_NERA) {
                        canEat = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x - 2][y + 2] == VUOTO && tabellone[x - 1][y + 1] == DAMA_NERA) {
                        canEat = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
            //i damoni possono mangiare in qualsiasi posizione
            case DAMONE_NERO:
                try {
                    if (tabellone[x - 2][y + 2] == VUOTO && (tabellone[x - 1][y + 1] == DAMA_BIANCA || tabellone[x - 1][y + 1] == DAMONE_BIANCO)) {
                        canEat = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x + 2][y + 2] == VUOTO && (tabellone[x + 1][y + 1] == DAMA_BIANCA || tabellone[x + 1][y + 1] == DAMONE_BIANCO)) {
                        canEat = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x - 2][y - 2] == VUOTO && (tabellone[x - 1][y - 1] == DAMA_BIANCA || tabellone[x - 1][y - 1] == DAMONE_BIANCO)) {
                        canEat = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x + 2][y - 2] == VUOTO && (tabellone[x + 1][y - 1] == DAMA_BIANCA || tabellone[x + 1][y - 1] == DAMONE_BIANCO)) {
                        canEat = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
            case DAMONE_BIANCO:
                try {
                    if (tabellone[x - 2][y + 2] == VUOTO && (tabellone[x - 1][y + 1] == DAMA_NERA || tabellone[x - 1][y + 1] == DAMONE_NERO)) {
                        canEat = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x + 2][y + 2] == VUOTO && (tabellone[x + 1][y + 1] == DAMA_NERA || tabellone[x + 1][y + 1] == DAMONE_NERO)) {
                        canEat = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x - 2][y - 2] == VUOTO && (tabellone[x - 1][y - 1] == DAMA_NERA || tabellone[x - 1][y - 1] == DAMONE_NERO)) {
                        canEat = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[x + 2][y - 2] == VUOTO && (tabellone[x + 1][y - 1] == DAMA_NERA || tabellone[x + 1][y - 1] == DAMONE_NERO)) {
                        canEat = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
        }
        return canEat;
    }

    /**
     * Restituisce una lista di tutte le possibili mangiate che una pedina può effettuare
     * @param i la riga della pedina
     * @param j la colonna della pedina
     * @return tutte le possibili mangiate della pedina
     */
    public ArrayList<int[]> getEatingHints(int i, int j) {
        //i = colonna, j = riga
        ArrayList<int[]> ret = new ArrayList<>();
        //ogni possibile cella nella quale la pedina si può muovere viene inserita nella lista
        switch (tabellone[i][j]) {
            case 1:
                try {
                    if (tabellone[i + 2][j + 2] == VUOTO && tabellone[i + 1][j + 1] == DAMA_BIANCA) {
                        int[] temp = new int[2];
                        temp[0] = (i + 2);
                        temp[1] = (j + 2);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i + 2][j - 2] == VUOTO && tabellone[i + 1][j - 1] == DAMA_BIANCA) {
                        int[] temp = new int[2];
                        temp[0] = (i + 2);
                        temp[1] = (j - 2);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
            case 2:
                try {
                    if (tabellone[i - 2][j - 2] == VUOTO && tabellone[i - 1][j - 1] == DAMA_NERA) {
                        int[] temp = new int[2];
                        temp[0] = (i - 2);
                        temp[1] = (j - 2);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i - 2][j + 2] == VUOTO && tabellone[i - 1][j + 1] == DAMA_NERA) {
                        int[] temp = new int[2];
                        temp[0] = (i - 2);
                        temp[1] = (j + 2);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
            case 3:
                try {
                    if (tabellone[i - 2][j + 2] == VUOTO && (tabellone[i - 1][j + 1] == DAMA_BIANCA || tabellone[i - 1][j + 1] == DAMONE_BIANCO)) {
                        int[] temp = new int[2];
                        temp[0] = (i - 2);
                        temp[1] = (j + 2);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i + 2][j + 2] == VUOTO && (tabellone[i + 1][j + 1] == DAMA_BIANCA || tabellone[i + 1][j + 1] == DAMONE_BIANCO)) {
                        int[] temp = new int[2];
                        temp[0] = (i + 2);
                        temp[1] = (j + 2);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i - 2][j - 2] == VUOTO && (tabellone[i - 1][j - 1] == DAMA_BIANCA || tabellone[i - 1][j - 1] == DAMONE_BIANCO)) {
                        int[] temp = new int[2];
                        temp[0] = (i - 2);
                        temp[1] = (j - 2);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i + 2][j - 2] == VUOTO && (tabellone[i + 1][j - 1] == DAMA_BIANCA || tabellone[i + 1][j - 1] == DAMONE_BIANCO)) {
                        int[] temp = new int[2];
                        temp[0] = (i + 2);
                        temp[1] = (j - 2);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
            case 4:
                try {
                    if (tabellone[i - 2][j + 2] == VUOTO && (tabellone[i - 1][j + 1] == DAMA_NERA || tabellone[i - 1][j + 1] == DAMONE_NERO)) {
                        int[] temp = new int[2];
                        temp[0] = (i - 2);
                        temp[1] = (j + 2);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i + 2][j + 2] == VUOTO && (tabellone[i + 1][j + 1] == DAMA_NERA || tabellone[i + 1][j + 1] == DAMONE_NERO)) {
                        int[] temp = new int[2];
                        temp[0] = (i + 2);
                        temp[1] = (j + 2);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i - 2][j - 2] == VUOTO && (tabellone[i - 1][j - 1] == DAMA_NERA || tabellone[i - 1][j - 1] == DAMONE_NERO)) {
                        int[] temp = new int[2];
                        temp[0] = (i - 2);
                        temp[1] = (j - 2);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                try {
                    if (tabellone[i + 2][j - 2] == VUOTO && (tabellone[i + 1][j - 1] == DAMA_NERA || tabellone[i + 1][j - 1] == DAMONE_NERO)) {
                        int[] temp = new int[2];
                        temp[0] = (i + 2);
                        temp[1] = (j - 2);
                        ret.add(temp);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                break;
        }
        return ret;
    }

    /**
     * Se possibile, fa mangiare una pedina
     * @param i la riga della pedina
     * @param i0 la colonna della pedina
     * @param i1 la riga destinazione
     * @param i2 la colonna destinazione
     * @return 
     */
    public boolean eat(int i, int i0, int i1, int i2) {
        if (canEat(i, i0)) {
            for (int[] hint : getEatingHints(i, i0)) {
                if ((i1 == hint[0]) && (i2 == hint[1])) {
                    //se arrivo in fondo, devo creare un damone
                    if (((i1 == 0) || (i1 == 8 - 1))
                            && ((tabellone[i][i0] == DAMA_BIANCA) || (tabellone[i][i0] == DAMA_NERA))) {
                        tabellone[i1][i2] = tabellone[i][i0] + 2;
                    } else {
                        tabellone[i1][i2] = tabellone[i][i0];
                    }
                    tabellone[(i + i1) / 2][(i0 + i2) / 2] = VUOTO;
                    tabellone[i][i0] = VUOTO;
                    setChanged();
                    notifyObservers();
                    if ((tabellone[i1][i2] % 2) == 0) {
                        numeroNeri--;
                        if (numeroNeri == 0) {
                            endGame(false, true);
                        }
                    } else {
                        numeroBianchi--;
                        if (numeroBianchi == 0) {
                            endGame(true, true);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Stampa a video il messaggio di fine partita
     * @param winner la squadra vincitrice
     * @param reason il motivo della sconfitta
     */
    public void endGame(boolean winner, boolean reason) {
        //winner: true -> neri    false -> bianchi
        //reason: true -> fine pedine     false -> fine mosse

        String message = "Gioco terminato. Vittoria dei ";
        message += winner ? "neri" : "bianchi";
        message += " perché l'avversario ";
        message += reason ? "ha finito le pedine." : "non può più muovere.";
        JOptionPane.showMessageDialog(null, message, "Fine partita", JOptionPane.PLAIN_MESSAGE);
        Controller.getController().getRound().stop();

    }

}
