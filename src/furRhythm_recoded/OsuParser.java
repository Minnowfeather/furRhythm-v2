package furRhythm_recoded;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;

public class OsuParser{
	private String path;
	private String diff;
	private int numObjects;
	public OsuParser() {
		this("");
	}
	public OsuParser(String path) {
		this(path, "");
	}
	public OsuParser(String path, String diff) {
		this.path = path;
		this.diff = diff;
		numObjects = 0;
	}
	
	public void setDirectory(String path) {
		this.path = path;
	}
	
	public void setDifficulty(String diff) {
		this.diff = diff;
	}
	
	void parse(NoteController noteList) {
		parse(path + diff, noteList);
	}
	
	void parse(String filename, NoteController noteList) {
		Scanner scan = null;
		try {
			scan = new Scanner(new File(filename), "UTF-8");
		} catch(FileNotFoundException e){
			System.out.println("File not found.");
		}
		if(scan == null) {
			return;
		}
		String read = scan.nextLine();
		while(!read.equals("[hitobjects]")) {
			read = scan.nextLine().toLowerCase();
		}
		String[] stringData;
		while(scan.hasNextLine()) {
			read = scan.nextLine();
			stringData = read.split(",");
			double hold = Double.parseDouble(stringData[5].split(":")[0]);
			if(hold == 0) {
				noteList.createTapNote(
						(int)Math.floor(Double.parseDouble(stringData[0]) * noteList.getLaneCount() / 512.0),
						Double.parseDouble(stringData[2]));
			} else {
				noteList.createHoldNote(
						(int)Math.floor(Double.parseDouble(stringData[0]) * noteList.getLaneCount() / 512.0),
						Double.parseDouble(stringData[2]),
						hold);
			}
			numObjects++;
		}
		
		// https://osu.ppy.sh/wiki/en/Client/File_formats/Osu_%28file_format%29#holds-(osu!mania-only)
	}
	String getPathToAudio() {
		return getPathToAudio(this.path, this.diff);
	}
	
	String getPathToAudio(String folder, String map) {
		Scanner s = null;
		try {
			s = new Scanner(new File(folder + map), "UTF-8");
		} catch(FileNotFoundException e) {
			System.out.println("File not found.");
		}
		String r = s.nextLine().toLowerCase();
		while(s.hasNextLine() && !r.equals("[general]")) {
			r = s.nextLine().toLowerCase();
		}
		while(s.hasNextLine() && !r.split(":")[0].equals("audiofilename")) {
			r = s.nextLine().toLowerCase();
		}
		String out = r.split(":")[1];
		if(out.substring(0,1).equals(" ")) {
			out = out.substring(1);
		}
		return folder + out;
	}
	String[] getDifficulties() {
		return getDifficulties(this.path);
	}
	String[] getDifficulties(String p) {
		LinkedList<String> s = new LinkedList<>();
		File[] dir = new File(p).listFiles();
		for(File i:dir) {
			if(i.isFile() && i.getName().endsWith(".osu")) {
				s.add(i.getName());
			}
		}
		String[] output = new String[s.size()];
		for(int i = 0; i < output.length; i++) {
			output[i] = s.remove();
		}
		return output;
	}
}
