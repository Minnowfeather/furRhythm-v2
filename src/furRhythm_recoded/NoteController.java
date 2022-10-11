package furRhythm_recoded;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class NoteController {
	private ArrayList<Note> noteList;
	private Rectangle2D.Double target;
	private FurInputHandler iH;
	private ScoreCounter sC;
	private ArrayList<Note> removeList;
	private double earliestStart;
	public NoteController(FurInputHandler iH, int amtOfLanes) {
		this.iH = iH;
		Note.WIDTH = (1000.0 / amtOfLanes);
		noteList = new ArrayList<Note>();
		removeList = new ArrayList<Note>();
	}
	public NoteController(Rectangle2D.Double target, int amtOfLanes) {
		Note.WIDTH = (1000.0 / amtOfLanes);
		noteList = new ArrayList<Note>();
		this.target = target;
		removeList = new ArrayList<Note>();
	}
	public void attachScoreCounter(ScoreCounter c) {
		this.sC = c;
	}

	public void addNote(Note n) {
		addNote(n, this.target);
	}
	public void addNote(Note n, Rectangle2D.Double t) {
		noteList.add(n);
		noteList.get(noteList.size()-1).setTarget(t);
		earliestStart = Math.min(n.getStartTime(), earliestStart);
	}
	public void createTapNote(char lane, double timing) {
		addNote(new TapNote(lane, timing));
	}
	public void createTapNote(char lane, double timing, Rectangle2D.Double t) {
		addNote(new TapNote(lane, timing), t);
	}
	public void setTarget(Rectangle2D.Double t) {
		setTarget(t, false);
	}
	public void setTarget(Rectangle2D.Double t, boolean update) {
		this.target = t;
		if(update) {
			for(Note n:noteList) {
				n.setTarget(this.target);
			}
		}
	}
	public ArrayList<Note> getList(){
		return this.noteList;
	}
	public boolean computeInput(Note n, double time) {
		// ToDO: compute input for notes
		int val = n.computeInput(time);
		if(val > 0) {
			sC.addScore(val);
			return true;
		}
		return false;
	}
	
	public void update(double time, double dt) {
		for(Note n:noteList) {
			if(time >= n.getStartTime() && !n.getMoving()) {
				n.setMoving(true);
			}
			n.autoMove(dt);
			if(iH.getValue(n.getLane())) {
				boolean removeN = computeInput(n, time);
				if(removeN) {
					removeList.add(n);
				}
			}
		}
		while(!removeList.isEmpty()) {
			noteList.remove(removeList.remove(0));
		}
		
		//System.out.println();
	}
	
	public double getEarliest() {
		return earliestStart;
	}
	
	public void destroy() {
		while(!noteList.isEmpty()) {
			Note n = noteList.remove(0);
			n = null;
		}
		while(!removeList.isEmpty()) {
			Note n = removeList.remove(0);
			n = null;
		}
		noteList = null;
		removeList = null;
		target = null;
		iH = null;
		sC = null;
	}
}
