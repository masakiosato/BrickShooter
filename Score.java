public class Score {
	int score;
	String name;
	
	public Score() {
		score = 0;
		name = null;
	}
	
	public Score(int s, String n) {
		score = s;
		name = n;
	}
	
	public String print() {
		return name + " " + score + " points";
	}
}
