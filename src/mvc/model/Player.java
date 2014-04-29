package mvc.model;

import mvc.controller.Controller;

/**
 * Classe che rappresenta un giocatore
 * @author Solomon Marian & Luca Negrini
 */
public class Player {

    /**
     * Il nome del giocatore
     */
    protected String nome;
    /**
     * Il controller di riferimento
     */
    protected Controller controller;
    /**
     * Rappresenta il team
     * true = bianco
     * false = nero
     */
    protected boolean team;// true = bianco     false = nero
    /**
     * Distingue i giocatori dall'ia
     */
    protected boolean human;//serve solo per non far evindenziare le mosse all'ia

    /**
     * Costruttore di default
     * @param nome Il nome del giocatore
     * @param controller Il controller di riferimento
     * @param team Il team del giocatore
     */
    public Player(String nome, Controller controller, boolean team) {
        this.nome = nome;
        this.controller = controller;
        this.team = team;
        this.human = true;
    }

    /**
     * Determina se il giocatore è reale o artificiale
     * @return L'umanita' del giocatore
     */
    public boolean isHuman() {
        return human;
    }

    /**
     * Restituisce il nome del giocatore
     * @return Il nome del giocatore
     */
    public String getNome() {
        return nome;
    }

    /**
     * Segnala al giocatore quando inizia il suo turno.
     * Controlla anche se ci sono mosse disponibili, in caso contrario la partita termina
     */
    public void wakeUp() {
        if (!controller.getModel().canPlayerMove(team)) {
            controller.getModel().endGame(team, false);
        } else {
            if (human) {
                controller.getView().hilightPossibleMoves(controller.getModel().getPossibleMoves(team));
            }
        }
    }

}
