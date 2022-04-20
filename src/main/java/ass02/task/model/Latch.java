package ass02.task.model;

public interface Latch {
    void notifyCompletion();

    void waitCompletion() throws InterruptedException;
}


