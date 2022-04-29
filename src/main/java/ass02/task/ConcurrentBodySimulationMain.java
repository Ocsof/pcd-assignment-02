package ass02.task;


import ass02.task.control.MasterAgentWithGui;
import ass02.task.model.Chronometer;
import ass02.task.model.ChronometerImpl;
import ass02.task.view.SimulationView;

/**
 * Bodies simulation - legacy code: sequential, unstructured
 * 
 * @author aricci
 */
public class ConcurrentBodySimulationMain {

    public static void main(String[] args){

    	SimulationView viewer = new SimulationView(620,620);

        int nSteps = 10000;
        int nBody = 1000;
        int nWorkers = Runtime.getRuntime().availableProcessors() + 1;
        //int nWorkers = 9;
        Chronometer chrono = new ChronometerImpl();
        MasterAgentWithGui master = new MasterAgentWithGui(nSteps, nBody, nWorkers, viewer); //versione con view
        //MasterAgent master = new MasterAgent(nSteps, nBody, nWorkers);  //versione senza view
        chrono.start();
        master.start();
        try {
            master.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        chrono.stop();
        System.out.println("Time elapsed: " + chrono.getTime() + "ms");
    }
}
