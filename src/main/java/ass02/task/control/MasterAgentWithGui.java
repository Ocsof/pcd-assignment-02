package ass02.task.control;


import ass02.task.model.Flag;
import ass02.task.view.SimulationView;

public class MasterAgentWithGui extends AbstractSimulatorService{

    private final Flag stopFlag;
    private double vt; /* virtual time */
    private final SimulationView viewer;

    public MasterAgentWithGui(final int nSteps, final int numBodies, final int nWorkers, final SimulationView viewer) {
        super(numBodies, nSteps, nWorkers);
        this.viewer = viewer;
        this.stopFlag = this.viewer.getStopFlag();
        this.vt = 0; /* init virtual time */
    }


    @Override
    protected void manageGUI(final int iter) {
        this.vt = this.vt + this.getDT(); /* update virtual time */
        this.stopFlag.waitWhile(true);
        this.viewer.display(this.getBodies(), this.vt, iter, this.getBounds()); /* display current stage */
    }
}
