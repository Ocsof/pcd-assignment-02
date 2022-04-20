package ass02.task.model;


/**
 * Chronometer
 *
 */
public interface Chronometer {
    /**
     * Start the cronometer
     */
    void start();

    /**
     * Stop the cronometer
     */
    void stop();

    /**
     * Get the time elapsed from the start of the chronometer
     * @return the time elapsed from the start of the chronometer
     */
    long getTime();
}
