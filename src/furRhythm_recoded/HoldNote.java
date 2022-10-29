package furRhythm_recoded;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

public class HoldNote extends Note{
	
	private double holdLength;
	
	public HoldNote(char l, double start, double holdLength){
		MOVING = false;
		x = laneCharToInt(l)*WIDTH;
		y = -50;
		LANE = l;
		endTime = start;
		startTime = endTime - (super.SPEED*super.SCONST);
		this.holdLength = holdLength;
		rect = new Rectangle2D.Double(this.x, this.y, WIDTH, HEIGHT);
	}
	
	
	public Rectangle2D.Double getRect() {
		
		return null;
	}

	public int computeInput(double currentTiming) {
		
		return 0;
	}

}
