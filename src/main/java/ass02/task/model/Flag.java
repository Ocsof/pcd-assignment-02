package ass02.task.model;

// TODO check and act?
public class Flag {
    boolean flag = false;

    public synchronized void set(boolean v){
        this.flag = v;
        notifyAll();
    }

    public synchronized void waitWhile(boolean v) {
        while (this.flag == v){
            try {
                wait();
            } catch (InterruptedException ex){}
        }
    }
}
