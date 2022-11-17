package furRhythm_recoded;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.ActionMap;

public class NoteController {
	private ArrayList<Note> noteList;
	private ArrayList<Note> activeHolds;
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
		activeHolds = new ArrayList<Note>();
	}
	public NoteController(Rectangle2D.Double target, int amtOfLanes) {
		this.lastKnownTime = 0;
		this.amtOfLanes = amtOfLanes;
		Note.WIDTH = (WINDOW_WIDTH / amtOfLanes);
		noteList = new ArrayList<Note>();
		this.target = target;
		removeList = new ArrayList<Note>();
		activeHolds = new ArrayList<Note>();
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
		n.setTarget(t);
		noteList.add(n);
		earliestStart = Math.min(n.getStartTime(), earliestStart);
		latestEnd = Math.max(n.getEndTime(), latestEnd);
	}
	
	public void addNote(Note n, Rectangle2D.Double t, double releaseTime) {
		earliestStart = Math.min(n.getStartTime(), earliestStart);
		latestEnd = Math.max(n.getReleaseTime(), latestEnd);
		n.setTarget(t,releaseTime);
		noteList.add(n);
	}
	public void addNote(Note n, double releaseTime) {
		addNote(n, this.target, releaseTime);
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
	
	
	public void createHoldNote(char lane, double timing, double releaseTime) {
		addNote(new HoldNote(lane, timing), releaseTime);
	}
	public void createHoldNote(int lane, double timing, double releaseTime) {
		addNote(new HoldNote(lane, timing), releaseTime);
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
		// compute TapNote 
		if(n instanceof TapNote) {
			int val = n.computeInput(time);
			if(val > 0) {
				sC.addScore(val);
				return true;
			}
		} else if(n instanceof HoldNote) {
			// ignore note if its already being tracked or if it's not within the timing range
			if(n.computeInput(time) > 1 && !n.isTracked()) {
				// add the untracked note to the activeHolds list
				activeHolds.add(n);
				n.setTracked(true);
			}
			if(n.isTracked() && (time - n.getReleaseTime() > 0)) {
				return true; // return true to signify its deletion time
			}
		}
		
		return false;
	}
	
	public void update(double time, double dt) {
		lastKnownTime = time;
		// unlock notes
		for(char c: iH.getKeys()) {
			if(!iH.getValue(c) && iH.getLock(c)) {
				iH.unlock(c);
			}
		}
		// compute score increases from held notes
		for(Note i:activeHolds) {
			if(i.isLocked()) {
				continue;
			}
			sC.addScoreRaw(0.005,0.005);
		}
		for(Note n:noteList) {
			// skip notes that aren't onscreen
			if(n.getStartTime() > time)
				continue;
			// timeout TapNote
			if(n.getEndTime() + Note.MISS < time && n instanceof TapNote) {
				sC.addScore(1);
				removeList.add(n);
			}
			// timeout HoldNotes that have been released
			if(n instanceof HoldNote && time >= n.getReleaseTime() && n.isLocked()) {
				// break combo
				sC.addScore(1);
				// KILL
				removeList.add(n);
				// remove it from activeholds if possible
				if(n.isTracked()) {
					activeHolds.remove(n);
					n.setTracked(false);
				}
			}
			// move notes that should be on screen
			if(time >= n.getStartTime() && !n.getMoving()) {
				n.setMoving(true);
			}
			// translate the note
			n.autoMove(dt);
			// TAPNOTES: update locks and compute inputs 
			if(n instanceof TapNote) {
				if(iH.getValue(n.getLane()) && !iH.getLock(n.getLane())) {
					boolean removeN = computeInput(n, time);
					if(removeN) {
						removeList.add(n);
					}
					iH.lock(n.getLane());
				}
			}
			// TODO: make this logic more robust and not bad
			// HOLDNOTES: this is way too complicated to summarize im so sorry
			if(n instanceof HoldNote) {
				// lock the key if its pressed and not already locked
				if(iH.getValue(n.getLane())) {
					iH.lock(n.getLane());
				}
				if(!n.isLocked()) {
					// determine whether to yeet the note or not
					boolean removeN = computeInput(n, time);
					// if the player lets go, lock the note and break combo
					if(!iH.getValue(n.getLane()) && n.isTracked() && n.getEndTime() + Note.MISS < time) {
						n.lock();
						sC.addScore(1);
					}
					// remove notes that should be removed
					if(removeN) {
						sC.round();
						activeHolds.remove(n);
						n.setTracked(false);
						removeList.add(n);
					}
				}
			}
		}
		
		// remove notes that should be removed
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
