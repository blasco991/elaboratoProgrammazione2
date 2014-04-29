package mvc.controller;

/**
 * Classe che gestisce i turni
 * @author Solomon Marian & Luca Negrini
 */
public class Round {

    private boolean team; // true --> WHITE      false ---> BLACK
    private int count;
    private final Controller controller;

    /**
     * Costruttore di default della classe
     * @param controller il controller di riferimento
     */
    public Round(Controller controller) {
        team = true;
        count = -1;
        this.controller = controller;
    }

    /**
     * Avvia il conteggio dei turni e sveglia il giocatore bianco
     * @return il successo o meno dell'operazione
     */
    public boolean init() {
        if (count > 0) {
            return false;
        }
        count = 0;
        controller.getView().getWhite().hilightPlayer();
        controller.getWhite().wakeUp();
        return true;

    }

    /**
     * Restituisce la squadra che deve muovere
     * @return la squadra
     */
    public boolean getTurn() {
        return team;
    }

    /**
     * Arresta il conteggio dei turni
     */
    public void stop() {
        count = -1;
    }

    /**
     * Opera il passaggio del turno
     */
    public void next() {
        if (count >= 0) {
            team = !team;
            count++;
            if (team) {
                controller.getView().getBlack().lowlightPlayer();
                controller.getView().getWhite().hilightPlayer();
                controller.getWhite().wakeUp();
            } else {
                controller.getView().getWhite().lowlightPlayer();
                controller.getView().getBlack().hilightPlayer();
                controller.getBlack().wakeUp();
            }
        }
    }
}
