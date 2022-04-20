package ass02.task.model;

public class Point2D {

    private double x, y;

    public Point2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Point2D sum(Vector2D v) {
    	x += v.x;
    	y += v.y;
    	return this;
    }
     
    public void change(double x, double y){
    	this.x = x;
    	this.y = y;
    }

    public double getX() {
    	return x;
    }

    public double getY() {
    	return y;
    }
}
