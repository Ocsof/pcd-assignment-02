package ass02.task.control;


import ass02.task.model.Body;
import ass02.task.model.Boundary;
import ass02.task.model.Point2D;
import ass02.task.model.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractSimulator extends Thread{
    private Boundary bounds;
    private List<Body> bodies;
    private final int nSteps;

    public AbstractSimulator(final int numBodies, final int nSteps) {
        this.nSteps = nSteps;
        this.generateGameObject(numBodies);
    }

    protected Boundary getBounds() {
        return this.bounds;
    }

    protected List<Body> getBodies() {
        return this.bodies;
    }

    protected void log(String msg){  //logger per eventuali stampe
        synchronized(System.out){
            System.out.println("[" + this.getName() + "] " + msg);
        }
    }

    protected int getnSteps(){
        return this.nSteps;
    }

    private void generateGameObject(final int numBodies) {
        this.bounds = new Boundary(-6.0, -6.0, 6.0, 6.0); //perimetro di gioco
        Random rand = new Random(System.currentTimeMillis());
        this.bodies = new ArrayList<>();
        for (int i = 0; i < numBodies; i++) {
            double x = this.bounds.getX0() * 0.25 + rand.nextDouble() * (this.bounds.getX1() - this.bounds.getX0()) * 0.25;
            double y = this.bounds.getY0() * 0.25 + rand.nextDouble() * (this.bounds.getY1() - this.bounds.getY0()) * 0.25;
            Body b = new Body(i, new Point2D(x, y), new Vector2D(0, 0), 10);
            this.bodies.add(b);
        }
    }

}
