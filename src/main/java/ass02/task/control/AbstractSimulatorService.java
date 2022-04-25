package ass02.task.control;


import ass02.task.model.ComputeTaskPosition;
import ass02.task.model.ComputeTaskVelocity;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractSimulatorService extends AbstractSimulator{

    private final int nWorkers;
    private final ExecutorService executor;
    private Set<ComputeTaskVelocity> velocityTasks;
    private Set<ComputeTaskPosition> positionTasks;
    private Set<Future<Void>> futureList;
    private final double DT = 0.001; /* virtual time step */

    public AbstractSimulatorService(final int numBodies, final int nSteps, final int poolSize) {
        super(numBodies, nSteps);
        this.futureList = new HashSet<>();
        this.nWorkers = poolSize;
        this.executor = Executors.newFixedThreadPool(poolSize);
        this.createVelocityTasks();
        this.createPositionTasks();
    }

    @Override
    public void run() {
        int iter = 0;
        /* simulation loop */
        while (iter < this.getnSteps()) {
            //this.createVelocityTasks();
            for (ComputeTaskVelocity taskVelocity : this.velocityTasks) {
                this.futureList.add(this.executor.submit(taskVelocity));
            }
            this.getFuture();
            //this.createPositionTasks();
            for (ComputeTaskPosition taskPosition : this.positionTasks) {
                this.futureList.add(this.executor.submit(taskPosition));
            }
            this.getFuture();
            iter++;
            this.manageGUI(iter);
        }
    }

    private void getFuture() {
        for (Future<Void> future : futureList) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        this.futureList = new HashSet<>();
    }

    private void createVelocityTasks() {
        int indexFrom = 0;
        int indexTo;
        int numberOfBodies = this.getBodies().size();
        int indexIncrement = numberOfBodies / this.nWorkers;
        this.velocityTasks = new HashSet<>();

        for (int i = 0; i < this.nWorkers-1; i++){
            indexTo = (indexFrom + indexIncrement - 1);
            ComputeTaskVelocity taskvelocity = new ComputeTaskVelocity(this.getBodies(), indexFrom, indexTo, this.DT);
            this.velocityTasks.add(taskvelocity);
            indexFrom = indexFrom + indexIncrement;
        }
        ComputeTaskVelocity taskvelocity = new ComputeTaskVelocity(this.getBodies(), indexFrom, (numberOfBodies - 1), this.DT);
        this.velocityTasks.add(taskvelocity);
    }

    private void createPositionTasks() {
        int indexFrom = 0;
        int indexTo;
        int numberOfBodies = this.getBodies().size();
        int indexIncrement = numberOfBodies / this.nWorkers;
        this.positionTasks = new HashSet<>();

        for (int i = 0; i < this.nWorkers-1; i++){
            indexTo = (indexFrom + indexIncrement - 1);
            ComputeTaskPosition taskPosition = new ComputeTaskPosition(this.getBodies(), indexFrom, indexTo,
                    this.getBounds(), this.DT);
            this.positionTasks.add(taskPosition);
            indexFrom = indexFrom + indexIncrement;
        }
        ComputeTaskPosition taskPosition = new ComputeTaskPosition(this.getBodies(), indexFrom, (numberOfBodies - 1),
                this.getBounds(), this.DT);
        this.positionTasks.add(taskPosition);
    }



    protected abstract void manageGUI(final int iter);  //templateMethod

    protected double getDT(){
        return this.DT;
    }

}
