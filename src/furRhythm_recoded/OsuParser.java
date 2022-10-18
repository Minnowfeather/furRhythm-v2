package furRhythm_recoded;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class OsuParser {
	static void parse(String filename, ArrayList<Note> noteList) {
		Scanner scan = null;
		try {
			scan = new Scanner(new File(filename));
		} catch(FileNotFoundException e){
			System.out.println("File not found.");
		}
		if(scan == null) {
			return;
		}
		String read = scan.nextLine().toLowerCase();
		while(!read.equals("[hitobjects]")) {
			read = scan.nextLine().toLowerCase();
		}
		read = scan.nextLine();
		String[] stringData = read.split(",");
		Math.floor(Double.parseDouble(stringData[0]) / 512.0);
		noteList.add(new TapNote())
		// https://osu.ppy.sh/wiki/en/Client/File_formats/Osu_%28file_format%29#holds-(osu!mania-only)
	}
}
