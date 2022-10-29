package furRhythm_recoded;
import java.awt.*;
import java.awt.geom.Rectangle2D;
public class TapNote extends Note{
	
	public TapNote(char l, double time) {
		MOVING = false;
		x = laneCharToInt(l)*WIDTH;
		y = -50;
		LANE = l;
		endTime = time;
		startTime = endTime - (super.SPEED*super.SCONST);
		rect = new Rectangle2D.Double(this.x, this.y, WIDTH, HEIGHT);
	}
	public TapNote(int l, double time) {
		MOVING = false;
		x = l*WIDTH;
		y = -50;
		LANE = laneIntToChar(l);
		endTime = time;
		startTime = endTime - (super.SPEED*super.SCONST);
		rect = new Rectangle2D.Double(this.x, this.y, WIDTH, HEIGHT);
	}
	public TapNote(double x, double y, char l, double time) {
		MOVING = false;
		this.x = x;
		this.y = y;
		LANE = l;
		endTime = time;
		startTime = endTime - (super.SPEED*super.SCONST);
		rect = new Rectangle2D.Double(this.x, this.y, WIDTH, HEIGHT);
	}
	
	public int computeInput(double currentTiming) {
		double distance = Math.abs(endTime - currentTiming);
		if(distance < 40) {
			return 3;
		}
		if(distance < 60) {
			return 2;
		}
		/*if(distance < 90) {
			return 1;
		}*/
		return 0;
	}
	
	@Override
	public String toString() {
		return "{" + LANE + "," + endTime + "," + MOVING + "}";
	}
	
}
