package furRhythm_recoded;
public class ScoreCounter {
	private double totalScore;
	private int combo;

	public ScoreCounter() {
		this(0, 0);
	}
	public ScoreCounter(double score, int combo) {
		totalScore = score;
		this.combo = combo;
	}
	
	public void addScore(int i) {
		switch(i) {
			case 1:
				totalScore += 0;
				combo = 0;
				break;
			case 2:
				totalScore += 100;
				combo++;
				break;
			case 3:
				totalScore += 200;
				combo++;
				break;
			default:
				break;
		}
	}
	
	public double getScore() {
		return totalScore;
	}
	public int getCombo() {
		return combo;
	}
}
