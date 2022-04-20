package ass02.task.model;


/*
 * This class represents a body
 * 
 */
public class Body {
    
	private static final double REPULSIVE_CONST = 0.01;
	private static final double FRICTION_CONST = 1;
	
    private Point2D pos;
    private Vector2D vel;
    private double mass;
    private int id;
    
    public Body(int id, Point2D pos, Vector2D vel, double mass){
    	this.id = id;
        this.pos = pos;
        this.vel = vel;
        this.mass = mass;
    }
    
    public double getMass() {
    	return mass;
    }
    
    public Point2D getPos(){
        return pos;
    }

    public Vector2D getVel(){
        return vel;
    }
    
    public int getId() {
    	return id;
    }
    
    public boolean equals(Object b) {
    	return ((Body)b).id == id;
    }
    
    
    /**
     * Update the position, according to current velocity
     * 
     * @param dt time elapsed 
     */
    public void updatePos(double dt){    	
    	pos.sum(new Vector2D(vel).scalarMul(dt));
    }

    /**
     * Update the velocity, given the instant acceleration
     * @param acc instant acceleration
     * @param dt time elapsed
     */
    public void updateVelocity(Vector2D acc, double dt){
    	vel.sum(new Vector2D(acc).scalarMul(dt));
    }
    
    /**
     * Change the velocity
     * 
     * @param vx
     * @param vy
     */
    public void changeVel(double vx, double vy){
    	vel.change(vx, vy);
    }
  	
    /**
     * Computes the distance from the specified body
     * 
     * @param b
     * @return
     */
    public double getDistanceFrom(Body b) {
    	double dx = pos.getX() - b.getPos().getX();
    	double dy = pos.getY() - b.getPos().getY();
    	return Math.sqrt(dx*dx + dy*dy);
    }
    
    /**
     * 
     * Compute the repulsive force exerted by another body
     * 
     * @param b
     * @return
     * @throws InfiniteForceException
     */
    public Vector2D computeRepulsiveForceBy(Body b) throws InfiniteForceException {
		double dist = getDistanceFrom(b);
		if (dist > 0) {
			try {
				return new Vector2D(b.getPos(), pos)
					.normalize()
					.scalarMul(b.getMass()*REPULSIVE_CONST/(dist*dist));
			} catch (Exception ex) {
				throw new InfiniteForceException();
			}
		} else {
			throw new InfiniteForceException();
		}
    }
    
    /**
     * 
     * Compute current friction force, given the current velocity
     */
    public Vector2D getCurrentFrictionForce() {
        return new Vector2D(vel).scalarMul(-FRICTION_CONST);
    }
    
    /**
     * Check if there collisions with the boundaty and update the
     * position and velocity accordingly
     * 
     * @param bounds
     */
    public void checkAndSolveBoundaryCollision(Boundary bounds){
    	double x = pos.getX();
    	double y = pos.getY();    	
        
    	if (x > bounds.getX1()){
            pos.change(bounds.getX1(), pos.getY());
            vel.change(-vel.getX(), vel.getY());
        } else if (x < bounds.getX0()){
            pos.change(bounds.getX0(), pos.getY());
            vel.change(-vel.getX(), vel.getY());
        } 
        
        if (y > bounds.getY1()){
            pos.change(pos.getX(), bounds.getY1());
            vel.change(vel.getX(), -vel.getY());
        } else if (y < bounds.getY0()){
            pos.change(pos.getX(), bounds.getY0());
            vel.change(vel.getX(), -vel.getY());
        }
    }        
    

}
