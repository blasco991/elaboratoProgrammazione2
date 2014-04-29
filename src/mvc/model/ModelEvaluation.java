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
     * @param i coordinata x iniziale
     * @param i0 coordinata y iniziale
     * @param i1 coordinata x finale
     * @param i2 coordinata y finale
     * @return booleano che esprime il successo dell'operazione
     */
    @Override
    public boolean eat(int i, int i0, int i1, int i2) {
        if (canEat(i, i0)) {
            for (int[] hint : getEatingHints(i, i0)) {
                if ((i1 == hint[0]) && (i2 == hint[1])) {
                    //se arrivo in fondo, devo creare un damone
                    if (((i1 == 0) || (i1 == 8 - 1)) && ((tabellone[i][i0] == DAMA_BIANCA) || (tabellone[i][i0] == DAMA_NERA))) {
                        tabellone[i1][i2] = tabellone[i][i0] + 2;
                        justDamone = true;
                    } else {
                        tabellone[i1][i2] = tabellone[i][i0];
                    }
                    tabellone[(i + i1) / 2][(i0 + i2) / 2] = VUOTO;
                    if ((tabellone[i][i0] % 2) == 0) {
                        newNumNeri--;
                    } else {
                        newNumBianchi--;
                    }
                    tabellone[i][i0] = VUOTO;
                    while (canEat(i1, i2)) {
                        int[] c = eatAgain(i1, i2);
                        eat(i1, i2, c[0], c[1]);
                        i1 = c[0];
                        i2 = c[1];
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Simula la mossa
     * @param i coordinata x iniziale
     * @param i0 coordinata y iniziale
     * @param i1 coordinata x finale
     * @param i2 coordinata y finale
     * @return booleano che esprime il successo dell'operazione
     */
    @Override
    public boolean move(int i, int i0, int i1, int i2) {
        if (canMove(i, i0)) {
            for (int[] hint : getHints(i, i0)) {
                if ((i1 == hint[0]) && (i2 == hint[1])) {
                    //se arrivo in fondo, devo creare un damone
                    if (((i1 == 0) || (i1 == 8 - 1)) && (tabellone[i][i0] < 3)) {
                        tabellone[i1][i2] = tabellone[i][i0] + 2;
                        justDamone = true;
                    } else {
                        tabellone[i1][i2] = tabellone[i][i0];
                    }
                    tabellone[i][i0] = VUOTO;
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
        int k = evaluate(x, y);
        restoreData();
        return k;
    }

    private int evaluate(int x, int y) {
        valutation = 0;
        //valutazione mangiata
        valutation += 4 * ((numeroBianchi - newNumBianchi) + (numeroNeri - newNumNeri));
        //valutazione mossa errata
        if (canBeEaten(x, y)) {
            valutation -= 5;
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
        //valutazione movimento
        //dama : più ti avvicini a diventare damone, più il punteggio è alto. tengo anche conto della possibilità di mangiare una pedinna avversaria al prossimo round
        //damone : la direzione di movimento è ininfluente, se è possibile bisogna essere vicini a una pedina avversaria per mangiarla
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
            for (int j = 0; j < tabellone[i].length; j++) {
                tabellone[i][j] = tabelloneOriginale[i][j];
            }
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

        int[] a = new int[2];
        int[] b = new int[2];
        if (values.size() == 1) {

            a[0] = values.get(0)[0];
            a[1] = values.get(0)[1];
            b[0] = values.get(0)[2];
            b[1] = values.get(0)[3];
            return b;

        } else {
            Random r = new Random();
            int h = r.nextInt(values.size());

            a[0] = values.get(h)[0];
            a[1] = values.get(h)[1];
            b[0] = values.get(h)[2];
            b[1] = values.get(h)[3];
            return b;

        }
    }
}
