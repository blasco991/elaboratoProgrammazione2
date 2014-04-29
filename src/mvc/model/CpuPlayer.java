package mvc.model;

import mvc.controller.Controller;

/**
 * Classe che rappresenta il giocatore artificiale
 * @author Solomon Marian & Luca Negrini
 */
public class CpuPlayer extends Player {

    private static int number = 0;

    /**
     * Costruttore predefinito 
     * @param controller il controller di riferimento
     * @param team il team di appartenenza
     */
    public CpuPlayer(Controller controller, boolean team) {
        super("CPU " + number, controller, team);
        number++;
        human = false;
    }

    /**
     * Sveglia il giocatore
     * Crea un thread separato in modo da non bloccare l'interfaccia durante l'elaborazione e lo fa partire
     */
    @Override
    public void wakeUp() {
        super.wakeUp();
        IA ia = new IA(controller, team);
        ia.start();
    }

}
