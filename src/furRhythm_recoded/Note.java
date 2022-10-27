package furRhythm_recoded;

import java.awt.geom.Rectangle2D;

public abstract class Note implements Comparable<Note>{
	
	// constants
	public static double WIDTH = 100;
	public static double HEIGHT = 20;
	public static int HITVALUE = 100;
	public double SPEED = 6.3;
	public double SCONST = 100;
	
	// stuff for lower classes
	protected double x,y;
	protected char LANE;
	protected boolean MOVING;
	protected double endTime;
	protected double startTime;
	protected double dist;
	protected double spd;
	protected Rectangle2D.Double rect;
	
	// move functions
	public void autoMove(double dt) {
		if(MOVING) {
			double dtMili = dt/1000000.0;
			move(0,dtMili*spd);
		}
	}
	public void move(double x, double y) {
		this.x += x;
		this.y += y;
		rect.setRect(this.x, this.y, WIDTH, HEIGHT);
	}
	public void moveTo(double x, double y) {
		this.x = x;
		this.y = y;
		rect.setRect(this.x, this.y, WIDTH, HEIGHT);
	}
	
	
	// getters
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getCenterX() {
		return rect.getCenterX();
	}
	public double getCenterY() {
		return rect.getCenterY();
	}
	public char getLane() {
		return LANE;
	}
	public int getLaneInt() {
		return laneCharToInt(getLane());
	}
	
	public boolean getMoving() {
		return MOVING;
	}
	public double getStartTime() {
		return startTime;
	}
	public double getEndTime() {
		return endTime;
	}
	abstract Rectangle2D.Double getRect();
	
	// setters
	public void setMoving(boolean m) {
		MOVING = m;
	}
	public void setTarget(Rectangle2D.Double s) {
		dist = Math.abs(s.getCenterY() - getCenterY());
		spd = dist/(endTime - startTime);
	}
	
	// char to lane
	protected int laneCharToInt(char lane) {
		switch(lane) {
			case 'd':
				return 0;
			case 'f':
				return 1;
			case 'j':
				return 2;
			case 'k':
				return 3;
			case ' ':
				return 4;
			case 's':
				return 5;
			case 'l':
				return 6;
		}
		return -1;
	}
	
	protected char laneIntToChar(int lane){
		switch(lane) {
			case 0:
				return 'd';
			case 1:
				return 'f';
			case 2:
				return 'j';
			case 3:
				return 'k';
			case 4:
				return ' ';
			case 5:
				return 's';
			case 6:
				return 'l';
		}
		return '|';
	}
	
	abstract int computeInput(double currentTiming);
	
	public String toString() {
		return "";
	}
	
	public int compareTo(Note n) {
		double diff = getEndTime() - n.getStartTime();
		if(diff == 0) {
			return 0;
		}
		return diff > 0 ? 1: -1;
	}
	
	public void destroy() {
		moveTo(-100,-100);
		rect = null;
		MOVING = false;
	}
}
