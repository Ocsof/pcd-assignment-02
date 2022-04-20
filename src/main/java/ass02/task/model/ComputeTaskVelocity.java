package ass02.task.model;

import java.util.List;
import java.util.concurrent.Callable;

public class ComputeTaskVelocity implements Callable<Void> {

    private final List<Body> bodies;
    private final int indexFrom;
    private final int indexTo;
    private final double DT;

    public ComputeTaskVelocity(final List<Body> bodies, final int indexFrom, final int indexTo, final double DT) {
        this.bodies = bodies;
        this.indexFrom = indexFrom;
        this.indexTo = indexTo;
        this.DT = DT;
    }

    @Override
    public Void call() throws Exception {
        for (int i = this.indexFrom; i <= this.indexTo; i++) { /* update bodies velocity */
            Body body = this.bodies.get(i);
            Vector2D totalForce = computeTotalForceOnBody(body);
            /* compute instant acceleration */
            Vector2D acc = new Vector2D(totalForce).scalarMul(1.0 / body.getMass());
            body.updateVelocity(acc, this.DT); /* update velocity */
        }
        return null;
    }

    private Vector2D computeTotalForceOnBody(Body b) {
        Vector2D totalForce = new Vector2D(0, 0);
        /* compute total repulsive force */
        for (int j = 0; j < this.bodies.size(); j++) {
            Body otherBody = this.bodies.get(j);
            if (!b.equals(otherBody)) {
                try {
                    Vector2D forceByOtherBody = b.computeRepulsiveForceBy(otherBody);
                    totalForce.sum(forceByOtherBody);
                } catch (Exception ex) {
                }
            }
        }
        /* add friction force */
        totalForce.sum(b.getCurrentFrictionForce());
        return totalForce;
    }
}
