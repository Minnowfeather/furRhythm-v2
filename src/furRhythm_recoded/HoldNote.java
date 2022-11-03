package furRhythm_recoded;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

public class HoldNote extends Note{
	
	private double releaseTime;
	private double localHeight;
	
	public HoldNote(char l, double time){
		MOVING = false;
		x = laneCharToInt(l)*WIDTH;
		y = -50;
		LANE = l;
		endTime = time;
		startTime = endTime - (super.SPEED*super.SCONST);
		rect = new Rectangle2D.Double(this.x, this.y, super.WIDTH, super.HEIGHT);
	}
	
	public HoldNote(int l, double time) {
		MOVING = false;
		x = l*WIDTH;
		y = -50;
		LANE = laneIntToChar(l);
		endTime = time;
		startTime = endTime - (super.SPEED*super.SCONST);
		rect = new Rectangle2D.Double(this.x, this.y, super.WIDTH, super.HEIGHT);
	}
	
	@Override
	public void setTarget(Rectangle2D.Double s, double releaseTime) {
		dist = Math.abs(s.getCenterY() - getCenterY());
		spd = dist/(endTime - startTime);
		
		this.releaseTime = releaseTime;
		localHeight = spd*(releaseTime - getEndTime());
		rect.setRect(rect.getX(), rect.getY() - this.localHeight, rect.getWidth(), this.localHeight);
		this.y = rect.getY();
		this.x = rect.getX();
	}
	
	public Rectangle2D.Double getRect() {
		return rect;
	}

	public double getReleaseTime() {
		return this.releaseTime;
	}
	public int computeInput(double currentTiming) {
		
		return 0;
	}

}
