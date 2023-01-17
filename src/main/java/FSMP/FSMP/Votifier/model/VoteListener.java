package FSMP.FSMP.Votifier.model;

public interface VoteListener {

    /**
     * Called when a vote is made.
     *
     * @param vote
     *            The vote that was made
     */
    public void voteMade(Vote vote);

}