package ass02.task.control;

public class MasterAgent extends AbstractSimulatorService{


    public MasterAgent(int nSteps, int numBodies, int nWorkers) {
        super(numBodies, nSteps, nWorkers);
    }

    @Override
    protected void manageGUI(int iter) { //doNothing

    }


}
