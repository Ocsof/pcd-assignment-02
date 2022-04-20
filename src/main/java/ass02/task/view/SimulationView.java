package ass02.task.view;

import ass02.task.model.Body;
import ass02.task.model.Boundary;
import ass02.task.model.Flag;

import java.util.List;

/**
 * Simulation view
 *
 * @author Francesco Foschini & Davide Alpi
 *
 */
public class SimulationView {
        
	private final SimulationFrame frame;
    private final Flag stopFlag;
	
    /**
     * Creates a view of the specified size (in pixels)
     * 
     * @param width
     * @param height
     */
    public SimulationView(final int width, final int height){
        this.stopFlag = new Flag();
        this.frame = new SimulationFrame(width,height,this.stopFlag);
    }
        
    public void display(final List<Body> bodies, final double vt, final long iter, final Boundary bounds){
 	   this.frame.display(bodies, vt, iter, bounds);
    }

    public Flag getStopFlag(){
        return this.stopFlag;
    }
    
    
}
