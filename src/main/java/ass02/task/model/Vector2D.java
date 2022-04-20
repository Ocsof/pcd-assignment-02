/*
 *   V2d.java
 *
 * Copyright 2000-2001-2002  aliCE team at deis.unibo.it
 *
 * This software is the proprietary information of deis.unibo.it
 * Use is subject to license terms.
 *
 */
package ass02.task.model;

/**
 *
 * 2-dimensional vector
 * objects are completely state-less
 *
 */
public class Vector2D {

    public double x,y;

    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D v){
        this.x = v.x;
        this.y = v.y;
    }

    public Vector2D(Point2D from, Point2D to){
        this.x = to.getX() - from.getX();
        this.y = to.getY() - from.getY();
    }

    public Vector2D scalarMul(double k) {
    	x *= k;
    	y *= k;
    	return this;
    }
    
    public Vector2D sum(Vector2D v) {
    	x += v.x;
    	y += v.y;
    	return this;
    }
    
    public Vector2D normalize() throws NullVectorException {
    	double mod =  Math.sqrt(x*x + y*y);
    	if (mod > 0) {
    		x /= mod;
    		y /= mod;
    		return this;
    	} else {
    		throw new NullVectorException();
    	}

    }
    public Vector2D change(double x, double y) {
    	this.x = x;
    	this.y = y;
    	return this;
    }
    
    public double getX() {
    	return x;
    }

    public double getY() {
    	return y;
    }
    
    
}
