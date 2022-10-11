package furRhythm_recoded;

import java.awt.geom.Rectangle2D;

public abstract class Note {
	
	// constants
	public static double WIDTH = 100;
	public static double HEIGHT = 20;
	public static int HITVALUE = 100;
	public double SPEED = 10;
	public double SCONST = 100;
	
	// move functions
	abstract void autoMove(double dt);
	abstract void move(double x, double y);
	abstract void moveTo(double x, double y);
	
	// getters
	abstract double getX();
	abstract double getY();
	abstract double getCenterX();
	abstract double getCenterY();
	abstract char getLane();
	abstract Rectangle2D getRect();
	abstract boolean getMoving();
	abstract double getStartTime();
	abstract double getEndTime();
	
	// setters
	abstract void setMoving(boolean m);
	abstract void setTarget(Rectangle2D.Double s);
	
	abstract int computeInput(double currentTiming);
	
	abstract void destroy();
}
