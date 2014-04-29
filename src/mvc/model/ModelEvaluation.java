package mvc.model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Classe che simula l'esecuzione di una mossa e ne produce una valutazione 
 * @author Solomon Marian & Luca Negrini
 */
public class ModelEvaluation extends Model {

    private final int[][] tabelloneOriginale;
    private int valutation, newNumNeri, newNumBianchi;
    private boolean justDamone;

    /**
    * Costruttore del modello di valutazione
    * @param tabellone il tabellone di partenza dal quale generare la simulazione
    */
    public ModelEvaluation(int[][] tabellone) {
        super(tabellone);
        tabelloneOriginale = new int[tabellone.length][tabellone[0].length];
        newNumNeri = super.numeroNeri;
        newNumBianchi = super.numeroBianchi;
        justDamone = false;
        for (int i = 0; i < tabellone.length; i++) {
            for (int j = 0; j < tabellone[i].length; j++) {
                tabelloneOriginale[i][j] = tabellone[i][j];
            }
        }

    }

    /**
     * Simula la mangiata, anche multipla
     * @param x coordinata x iniziale
     * @param y coordinata y iniziale
     * @param xf coordinata x finale
     * @param yf coordinata y finale
     * @return booleano che esprime il successo dell'operazione
     */
    @Override
    public boolean eat(int x, int y, int xf, int yf) {
        if (canEat(x, y)) {
            for (int[] hint : getEatingHints(x, y)) {
                if ((xf == hint[0]) && (yf == hint[1])) {
                    //se arrivo in fondo, devo creare un damone
                    if (((xf == 0) || (xf == 8 - 1)) && ((tabellone[x][y] == DAMA_BIANCA) || (tabellone[x][y] == DAMA_NERA))) {
                        tabellone[xf][yf] = tabellone[x][y] + 2;
                        justDamone = true;
                    } else {
                        tabellone[xf][yf] = tabellone[x][y];
                    }
                    tabellone[(x + xf) / 2][(y + yf) / 2] = VUOTO;
                    if ((tabellone[x][y] % 2) == 0) {
                        newNumNeri--;
                    } else {
                        newNumBianchi--;
                    }
                    tabellone[x][y] = VUOTO;
                    while (canEat(xf, yf)) {
                        int[] c = eatAgain(xf, yf);
                        eat(xf, yf, c[0], c[1]);
                        xf = c[0];
                        yf = c[1];
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Simula la mossa
     * @param x coordinata x iniziale
     * @param y coordinata y iniziale
     * @param xf coordinata x finale
     * @param yf coordinata y finale
     * @return booleano che esprime il successo dell'operazione
     */
    @Override
    public boolean move(int x, int y, int xf, int yf) {
        if (canMove(x, y)) {
            for (int[] hint : getHints(x, y)) {
                if ((xf == hint[0]) && (yf == hint[1])) {
                    //se arrivo in fondo, devo creare un damone
                    if (((xf == 0) || (xf == 8 - 1)) && (tabellone[x][y] < 3)) {
                        tabellone[xf][yf] = tabellone[x][y] + 2;
                        justDamone = true;
                    } else {
                        tabellone[xf][yf] = tabellone[x][y];
                    }
                    tabellone[x][y] = VUOTO;
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Avvia la valutazione della mossa
     * @param x coordinata x iniziale
     * @param y coordinata y finale
     * @param xf coordinata x finale
     * @param yf coordinata y finale
     * @param type tipo di mossa da simulare     true = move, false = eat
     * @return la valutazione della mossa
     */
    public int getEvaluation(int x, int y, int xf, int yf, boolean type) {
        int x0 = x, y0 = y;
        //type = true -> move
        if (type) {
            this.move(x, y, xf, yf);
            x = xf;
            y = yf;
        } else {
            //mangiata multipla automatica
            this.eat(x, y, xf, yf);
            x = xf;
            y = yf;
        }
        int k = evaluate(x, y, x0, y0);
        restoreData();
        return k;
    }

    private int evaluate(int x, int y, int x0, int y0) {
        valutation = 0;
        //valutazione mangiata
        valutation += 4 * ((numeroBianchi - newNumBianchi) + (numeroNeri - newNumNeri));
        //valutazione mossa errata (sconveniente)
        if (canBeEaten(x, y)) {
            valutation -= 9;
        }
        //valutazione DOVREI scappare (sconveniente)
        if (canBeEaten(x0, y0)) { // equivale alla chiamate willBeEat()
            valutation += 9;
        }
        //calcolo move factor
        float moveFactor = 1;
        if(tabellone[x][y] % 2 == 0){
            //bianco
            moveFactor += (1.0f/newNumBianchi);
        } else {
            //nero
            moveFactor += (1.0f/newNumNeri);
        }
        /* valutazione movimento
        *  dama : più ti avvicini a diventare damone, più il punteggio è alto. 
                    tengo anche conto della possibilità di mangiare una pedinna avversaria al prossimo round
        *  damone : la direzione di movimento è ininfluente, se è possibile bisogna essere vicini a una pedina avversaria per mangiarla
        */
        switch (tabellone[x][y]) {
            case Model.DAMA_NERA:
                valutation += (int)Math.ceil(moveFactor*x);
                if (canEatNext(x, y)) {
                    valutation += 3;
                }
                break;
            case Model.DAMA_BIANCA:
                valutation += (int)Math.ceil(moveFactor*(7 - x));
                if (canEatNext(x, y)) {
                    valutation += 3;
                }
                break;
            case Model.DAMONE_NERO:
                if (justDamone) {
                    valutation += 5;
                } else {
                    valutation += 2;
                }
                if (canEatNext(x, y)) {
                    valutation += 3;
                }
                break;
            case Model.DAMONE_BIANCO:
                if (justDamone) {
                    valutation += 5;
                } else {
                    valutation += 2;
                }
                if (canEatNext(x, y)) {
                    valutation += 3;
                }
                break;
        }
        return valutation;
    }

    private void restoreData() {
        for (int i = 0; i < tabellone.length; i++) {
            System.arraycopy(tabelloneOriginale[i], 0, tabellone[i], 0, tabellone[i].length);
        }
    }

    private boolean canEatNext(int x, int y) {
        int enemy = tabellone[x][y] % 2 == 0 ? 1 : 2;
        //dama
        if (tabellone[x][y] < 3) {
            try {
                if ((tabellone[x - 2][y - 2] == VUOTO) && (tabellone[x - 1][y - 1] == enemy)) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            try {
                if ((tabellone[x + 2][y + 2] == VUOTO) && (tabellone[x + 1][y + 1] == enemy)) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            try {
                if ((tabellone[x + 2][y - 2] == VUOTO) && (tabellone[x + 1][y - 1] == enemy)) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            try {
                if ((tabellone[x - 2][y + 2] == VUOTO) && (tabellone[x - 1][y + 1] == enemy)) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
        } else {
            //damone
            try {
                if ((tabellone[x - 2][y - 2] == VUOTO) && ((tabellone[x - 1][y - 1] == enemy) || (tabellone[x - 1][y - 1] == enemy + 2))) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            try {
                if ((tabellone[x + 2][y + 2] == VUOTO) && ((tabellone[x + 1][y + 1] == enemy) || (tabellone[x + 1][y + 1] == enemy + 2))) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            try {
                if ((tabellone[x + 2][y - 2] == VUOTO) && ((tabellone[x + 1][y - 1] == enemy) || (tabellone[x + 1][y - 1] == enemy + 2))) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            try {
                if ((tabellone[x - 2][y + 2] == VUOTO) && ((tabellone[x - 1][y + 1] == enemy) || (tabellone[x - 1][y + 1] == enemy + 2))) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
        }
        return false;
    }

    private boolean canBeEaten(int x, int y) {
        int enemy = tabellone[x][y] % 2 == 0 ? 1 : 2;
        //dama
        if (tabellone[x][y] < 3) {
            try {
                if ((tabellone[x - 1][y - 1] == VUOTO) && ((tabellone[x + 1][y + 1] == enemy) || (tabellone[x + 1][y + 1] == enemy + 2))) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            try {
                if ((tabellone[x + 1][y + 1] == VUOTO) && ((tabellone[x - 1][y - 1] == enemy) || (tabellone[x - 1][y - 1] == enemy + 2))) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            try {
                if ((tabellone[x + 1][y - 1] == VUOTO) && ((tabellone[x - 1][y + 1] == enemy) || (tabellone[x - 1][y + 1] == enemy + 2))) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            try {
                if ((tabellone[x - 1][y + 1] == VUOTO) && ((tabellone[x + 1][y - 1] == enemy) || (tabellone[x + 1][y - 1] == enemy + 2))) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
        } else {
            //damone
            try {
                if ((tabellone[x - 1][y - 1] == VUOTO) && (tabellone[x + 1][y + 1] == enemy + 2)) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            try {
                if ((tabellone[x + 1][y + 1] == VUOTO) && (tabellone[x - 1][y - 1] == enemy + 2)) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            try {
                if ((tabellone[x + 1][y - 1] == VUOTO) && (tabellone[x - 1][y + 1] == enemy + 2)) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            try {
                if ((tabellone[x - 1][y + 1] == VUOTO) && (tabellone[x + 1][y - 1] == enemy + 2)) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
        }
        return false;
    }

    private int[] eatAgain(int i, int i0) {
        ArrayList<int[]> values = new ArrayList<>();//x y xf yf type valutation
        for (int[] hint : getEatingHints(i, i0)) {
            ModelEvaluation me = new ModelEvaluation(tabellone);
            int[] a = {i, i0, hint[0], hint[1], 0, me.getEvaluation(i, i0, hint[0], hint[1], false)};
            values.add(a);
        }

        //scelta mossa migliore
        int max = values.get(0)[5];
        for (int k = 0; k < values.size(); k++) {
            if (values.get(k)[5] > max) {
                max = values.get(k)[5];
            }
        }

        for (int k = 0; k < values.size(); k++) {
            if (values.get(k)[5] < max) {
                values.remove(k);
                k--;
            }
        }

        int[] b = new int[2];
        if (values.size() == 1) {
            b[0] = values.get(0)[2];
            b[1] = values.get(0)[3];
            return b;

        } else {
            Random r = new Random();
            int h = r.nextInt(values.size());
            b[0] = values.get(h)[2];
            b[1] = values.get(h)[3];
            return b;

        }
    }
}
