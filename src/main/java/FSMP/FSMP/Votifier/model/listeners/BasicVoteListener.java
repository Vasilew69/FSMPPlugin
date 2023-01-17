package FSMP.FSMP.Votifier.model.listeners;

import java.util.logging.Logger;

import FSMP.FSMP.Votifier.model.Vote;
import FSMP.FSMP.Votifier.model.VoteListener;

/**
 * A basic vote listener for demonstration purposes.
 *
 * @author Blake Beaupain
 */
public class BasicVoteListener implements VoteListener {

    /** The logger instance. */
    private Logger log = Logger.getLogger("BasicVoteListener");

    public void voteMade(Vote vote) {
        log.info("Received: " + vote);
    }

}
