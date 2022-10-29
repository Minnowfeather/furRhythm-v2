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
	private double latestEnd;
	private int amtOfLanes;
	public int WINDOW_WIDTH = 1000;
	public int WINDOW_HEIGHT = 1000;
	private double lastKnownTime;
	
	public NoteController(FurInputHandler iH, int amtOfLanes) {
		this.lastKnownTime = 0;
		this.amtOfLanes = amtOfLanes;
		this.iH = iH;
		Note.WIDTH = (WINDOW_WIDTH / amtOfLanes);
		noteList = new ArrayList<Note>();
		removeList = new ArrayList<Note>();
	}
	public NoteController(Rectangle2D.Double target, int amtOfLanes) {
		this.lastKnownTime = 0;
		this.amtOfLanes = amtOfLanes;
		Note.WIDTH = (WINDOW_WIDTH / amtOfLanes);
		noteList = new ArrayList<Note>();
		this.target = target;
		removeList = new ArrayList<Note>();
	}
	public void attachScoreCounter(ScoreCounter c) {
		this.sC = c;
	}

	public void addNote(Note n) {
		earliestStart = Math.min(n.getStartTime(), earliestStart);
		latestEnd = Math.max(n.getEndTime(), latestEnd);
		addNote(n, this.target);
	}
	public void addNote(Note n, Rectangle2D.Double t) {
		noteList.add(n);
		noteList.get(noteList.size()-1).setTarget(t);
		earliestStart = Math.min(n.getStartTime(), earliestStart);
		latestEnd = Math.max(n.getEndTime(), latestEnd);
	}
	public void createTapNote(char lane, double timing) {
		addNote(new TapNote(lane, timing));
	}
	public void createTapNote(int lane, double timing) {
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
	public void setWindowSize(int width, int height) {
		WINDOW_WIDTH = width;
		WINDOW_HEIGHT = height;
		Note.WIDTH = WINDOW_WIDTH / amtOfLanes;
	}
	
	public void sort() {
		noteList.sort(null);
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
		lastKnownTime = time;
		for(char c: iH.getKeys()) {
			if(!iH.getValue(c) && iH.getLock(c)) {
				iH.unlock(c);
			}
		}
		for(Note n:noteList) {
			if(n.getStartTime() > time)
				continue;
			if(n.getEndTime() + 150 < time) {
				sC.addScore(1);
				removeList.add(n);
			}
			if(time >= n.getStartTime() && !n.getMoving()) {
				n.setMoving(true);
			}
			n.autoMove(dt);
			if(iH.getValue(n.getLane()) && !iH.getLock(n.getLane())) {
				boolean removeN = computeInput(n, time);
				if(removeN) {
					removeList.add(n);
				}
				iH.lock(n.getLane());
			}
			
		}
		while(!removeList.isEmpty()) {
			Note n = removeList.remove(0);
			noteList.remove(n);
			n.destroy();
			n = null;			
		}		
	}

	public ArrayList<Note> getList(){
		return this.noteList;
	}
	
	public double getEarliest() {
		return earliestStart;
	}
	public double getLatest() {
		return latestEnd;
	}
	
	public int getLaneCount() {
		return amtOfLanes;
	}
	public Note[] getVisibleNotes() {
		ArrayList<Note> n = new ArrayList<>();
		int index = 0;
		while(index < noteList.size() && noteList.get(index).getStartTime() <= lastKnownTime) {
			if(noteList.get(index) == null) {
				index++;
				continue;
			}
			n.add(noteList.get(index));
			index++;
		}
		Note[] out = new Note[n.size()];
		for(int i = 0; i < out.length; i++) {
			out[i] = n.remove(0);
		}
		
		return out;
	}
	
	public String toString() {
		StringBuilder out = new StringBuilder();
		for(Note n:noteList) {
			out.append(n + "\n");
		}
		return out.toString();
	}
	public void destroy() {
		while(!noteList.isEmpty()) {
			Note n = noteList.remove(0);
			n.destroy();
			n = null;
		}
		while(!removeList.isEmpty()) {
			Note n = removeList.remove(0);
			n.destroy();
			n = null;
		}
		noteList = null;
		removeList = null;
		target = null;
		iH = null;
		sC = null;
	}
}
