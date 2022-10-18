package furRhythm_recoded;
import java.awt.*;
import java.awt.geom.Rectangle2D;
public class TapNote extends Note{
	private double x,y;
	private char LANE;
	private boolean MOVING = false;
	private double endTime;
	private double startTime;
	private double dist;
	private double spd;
	private Rectangle2D.Double rect;
	
	public TapNote(char l, double time) {
		this.x = laneCharToInt(l)*WIDTH;
		this.y = -10;
		LANE = l;
		endTime = time;
		startTime = endTime - (super.SPEED*super.SCONST);
		rect = new Rectangle2D.Double(this.x, this.y, WIDTH, HEIGHT);
	}
	public TapNote(int l, double time) {
		this.x = l*WIDTH;
		this.y = -10;
		LANE = laneIntToChar(l);
		endTime = time;
		startTime = endTime - (super.SPEED*super.SCONST);
		rect = new Rectangle2D.Double(this.x, this.y, WIDTH, HEIGHT);
	}
	public TapNote(double x, double y, char l, double time) {
		this.x = x;
		this.y = y;
		LANE = l;
		endTime = time;
		startTime = endTime - (super.SPEED*super.SCONST);
		rect = new Rectangle2D.Double(this.x, this.y, WIDTH, HEIGHT);
	}

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
	public Rectangle2D getRect() {
		return rect;
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
	
	public void setMoving(boolean m) {
		MOVING = m;
	}
	public void setTarget(Rectangle2D.Double s) {
		dist = Math.abs(s.getCenterY() - getCenterY());
		spd = dist/(endTime - startTime);
	}
	public int computeInput(double currentTiming) {
		double distance = Math.abs(endTime - currentTiming);
		if(distance < 50) {
			destroy();
			return 3;
		}
		if(distance < 100) {
			destroy();
			return 2;
		}
		if(distance < 120) {
			destroy();
			return 1;
		}
		return 0;
	}
	private int laneCharToInt(char lane) {
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
	private char laneIntToChar(int lane){
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
	
	
	public void destroy() {
		moveTo(-100,-100);
		rect = null;
	}
}
