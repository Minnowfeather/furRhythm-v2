package furRhythm_recoded;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class OsuParser {
	static void parse(String filename, NoteController noteList) {
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
			//hold = stringData[5];
			noteList.createTapNote((int)Math.floor(Double.parseDouble(stringData[0]) * noteList.getLaneCount() / 512.0),
					Double.parseDouble(stringData[2]));
			
		}
		
		// https://osu.ppy.sh/wiki/en/Client/File_formats/Osu_%28file_format%29#holds-(osu!mania-only)
	}
	
	static String getPathToAudio(String folder, String map) {
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
}
