package mvc.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import mvc.controller.Controller;

/**
 * Classe che rappresenta l'intelligienza del giocatore artificiale
 *
 * @see Thread
 * @author Solomon Marian & Luca Negrini
 */
public class IA extends Thread {

    private Controller controller;
    private boolean team;

    /**
     * Costruttore predefinito
     *
     * @param controller il controller di riferimento della partita
     * @param team la squadra di riferimento
     */
    public IA(Controller controller, boolean team) {
        this.controller = controller;
        this.team = team;
    }

    @Override
    public void run() {
        super.run();
        try {
            sleep((int) (Math.random() * 100 * 2));
        } catch (InterruptedException ex) {
        }

        controller.disableInterface();
        ArrayList<int[]> values = new ArrayList<>();//x y xf yf type valutation

        int x = this.team ? 0 : 1;
        for (int i = 0; i < this.controller.getModel().getTabellone().length; i++) {
            for (int j = 0; j < this.controller.getModel().getTabellone()[i].length; j++) {
                if (this.controller.getModel().getTabellone()[j][i] != Model.VUOTO && ((this.controller.getModel().getTabellone()[j][i] % 2) == x) && this.controller.getModel().canEat(j, i)) {
                    for (Object hint : controller.getEatingHints(j, i)) {
                        ModelEvaluation me = new ModelEvaluation(controller.getModel().getTabellone());
                        int[] a = {j, i, ((int[]) hint)[0], ((int[]) hint)[1], 0, me.getEvaluation(j, i, ((int[]) hint)[0], ((int[]) hint)[1], false)};
                        values.add(a);
                    }
                }

                if (this.controller.getModel().getTabellone()[j][i] != Model.VUOTO && ((this.controller.getModel().getTabellone()[j][i] % 2) == x) && this.controller.getModel().canMove(j, i)) {
                    for (Object hint : controller.getHints(j, i)) {
                        ModelEvaluation me = new ModelEvaluation(controller.getModel().getTabellone());
                        int[] a = {j, i, ((int[]) hint)[0], ((int[]) hint)[1], 1, me.getEvaluation(j, i, ((int[]) hint)[0], ((int[]) hint)[1], true)};
                        values.add(a);
                    }
                }
            }
        }

        //controllo se nella lista c'è qualche pedina che può mangiare. se c'è rimuovo tutte quelle che non lo possono fare perché devo mangiare per forza
        boolean v = false;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i)[4] == 0) {
                v = true;
            }
        }
        if (v) {
            Iterator i = values.iterator();
            while (i.hasNext()) {
                if (((int[]) i.next())[4] != 0) {
                    i.remove();
                }
            }
        }

        //scelta mossa migliore
        int max = values.get(0)[5];
        for (int[] value : values) {
            if (value[5] > max) {
                max = value[5];
            }
        }

        for (int i = 0; i < values.size(); i++) {
            if (values.get(i)[5] < max) {
                values.remove(i);
                i--;
            }
        }

        int[] a = new int[2];
        int[] b = new int[2];
        if (values.size() == 1) {
            if (values.get(0)[4] == 1) {
                a[0] = values.get(0)[0];
                a[1] = values.get(0)[1];
                b[0] = values.get(0)[2];
                b[1] = values.get(0)[3];
                controller.move(a, b);
            } else {
                a[0] = values.get(0)[0];
                a[1] = values.get(0)[1];
                b[0] = values.get(0)[2];
                b[1] = values.get(0)[3];
                controller.eat(a, b);
                while (controller.canEat(b[0], b[1])) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                    }
                    int[] c = eatAgain(b[0], b[1]);
                    controller.eat(b, c);
                    b = c;
                }
            }
        } else {
            Random r = new Random();
            int h = r.nextInt(values.size());
            if (values.get(h)[4] == 1) {
                a[0] = values.get(h)[0];
                a[1] = values.get(h)[1];
                b[0] = values.get(h)[2];
                b[1] = values.get(h)[3];
                controller.move(a, b);
            } else {
                a[0] = values.get(h)[0];
                a[1] = values.get(h)[1];
                b[0] = values.get(h)[2];
                b[1] = values.get(h)[3];
                controller.eat(a, b);
                while (controller.canEat(b[0], b[1])) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                    }
                    int[] c = eatAgain(b[0], b[1]);
                    controller.eat(b, c);
                    b = c;
                }
            }

        }

        controller.enableInterface();
    }

    private int[] eatAgain(int i, int i0) {
        ArrayList<int[]> values = new ArrayList<>();//x y xf yf type valutation
        for (int[] hint : controller.getEatingHints(i, i0)) {
            ModelEvaluation me = new ModelEvaluation(controller.getModel().getTabellone());
            int[] a = {i, i0, hint[0], hint[1], 0, me.getEvaluation(i, i0, hint[0], hint[1], false)};
            values.add(a);
        }
        //scelta mossa migliore
        int max = values.get(0)[5];
        for (int[] value : values) {
            if (value[5] > max) {
                max = value[5];
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
