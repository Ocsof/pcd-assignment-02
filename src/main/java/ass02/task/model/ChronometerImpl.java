package ass02.task.model;

/**
 * a Chronometer implementation
 *
 */
public class ChronometerImpl implements Chronometer {

	private boolean running;
	private long startTime;

	public ChronometerImpl(){
		running = false;
	}
	
	@Override
	public void start(){
		this.running = true;
		this.startTime = System.currentTimeMillis();
	}
	
	@Override
	public void stop(){
		this.startTime = getTime();
		this.running = false;
	}
	
	@Override
	public long getTime(){
		if (this.running){
			return 	System.currentTimeMillis() - this.startTime;
		} else {
			return this.startTime;
		}
	}
}
