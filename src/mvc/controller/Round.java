package mvc.controller;

/**
 *
 * @author Solomon Marian & Luca Negrini
 */
public class Round {

    private boolean team; // true --> WHITE      false ---> BLACK
    private int count;
    private final Controller controller;

    public Round(Controller controller) {
        team = true;
        count = -1;
        this.controller = controller;
    }

    public boolean init() {
        if (count > 0) {
            return false;
        }
        count = 0;
        controller.getView().getWhite().hilightPlayer();
        controller.getWhite().wakeUp();
        return true;

    }

    public boolean getTurn() {
        return team;
    }

    public void stop() {
        count = -1;
    }

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
