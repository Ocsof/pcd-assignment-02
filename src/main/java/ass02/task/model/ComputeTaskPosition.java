package ass02.task.model;

import java.util.List;
import java.util.concurrent.Callable;

public class ComputeTaskPosition implements Callable<Void>  {

    private final List<Body> bodies;
    private final int indexFrom;
    private final int indexTo;
    private final Boundary bounds;
    private final double DT;

    public ComputeTaskPosition(final List<Body> bodies, final int indexFrom, final int indexTo, final Boundary bounds,
                               final double DT) {
        this.bodies = bodies;
        this.indexFrom = indexFrom;
        this.indexTo = indexTo;
        this.bounds = bounds;
        this.DT = DT;
    }

    @Override
    public Void call() throws Exception {
        for (int i = this.indexFrom; i <= this.indexTo; i++) {
            this.bodies.get(i).updatePos(this.DT);
            this.bodies.get(i).checkAndSolveBoundaryCollision(this.bounds);
        }
        return null;
    }
}
