package furRhythm_recoded;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import java.awt.event.*;

import java.io.File;
import java.util.Scanner;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class Main extends JPanel{

	static NoteController noteList;
	static NoteCatcherController catcher;
	static FurInputHandler k;
	static ScoreCounter scorecounter;
	static Font scoreFont, comboFont;
	static Rectangle2D.Double blocc;
	static boolean quit;
	static final boolean hidden = false;
	public static void main(String[] args) {		
		quit = false;
		
		k = new FurInputHandler(4);
		scorecounter = new ScoreCounter();
		catcher = new NoteCatcherController(k);
		
		scoreFont = new Font("Arial", Font.PLAIN, 50);
		comboFont = new Font("Arial", Font.PLAIN, 64);
		
		JFrame j = new JFrame();
		j.setTitle("hehe");
		j.setSize(new Dimension(400,900));
		j.setPreferredSize(new Dimension(400,900));
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		j.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				k.press(e.getKeyChar());
			}
			public void keyReleased(KeyEvent e) {
				k.release(e.getKeyChar());
			}
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == 'q') {
					quit = true;
				}
			}
		});
		j.add(new Main());
		j.setBackground(new Color(0,0,0));
		j.setResizable(false);
		//j.pack();
		j.setVisible(true);
		
		
		catcher.setWindowSize(j.getWidth(), j.getHeight());
		
		noteList = new NoteController(k, 4);
		noteList.setTarget(catcher.getBindingBox());
		noteList.attachScoreCounter(scorecounter);
		noteList.setWindowSize(j.getWidth(), j.getHeight());
		
		blocc = new Rectangle2D.Double(
				0, 
				catcher.getBindingBox().getY()+catcher.getBindingBox().getHeight(), 
				j.getWidth(), 
				j.getHeight() - catcher.getBindingBox().getMaxY()
				);
		
		//String folderPath = "C:\\Users\\minno\\AppData\\Local\\osu!\\Songs\\530756 Kuroneko Dungeon - Lilieze to Enryuu Laevateinn\\";
		//String folderPath = "/home/furry/Desktop/Files/Code/furRhythm-v2/testMaps/CaitSith/";
		//String folderPath = "/home/furry/Desktop/Files/Code/furRhythm-v2/testMaps/Pallet/";
		String folderPath = "/home/furry/Desktop/Files/Code/furRhythm-v2/testMaps/Distance/";


		OsuParser osuparse = new OsuParser(folderPath);
		String[] possibleDiffs = osuparse.getDifficulties();
		for(int i = 0; i < possibleDiffs.length; i++) {
			System.out.println((i+1) + ": " + possibleDiffs[i]);
		}
		Scanner scanner = new Scanner(System.in);
		osuparse.setDifficulty(possibleDiffs[scanner.nextInt()-1]);
		osuparse.parse(noteList);
		JFXPanel fxPanel = new JFXPanel();
		j.add(fxPanel);
		Media media = new Media(new File(osuparse.getPathToAudio()).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setVolume(0.02);
		//System.out.println(noteList);
		/*
		Scanner s = null;
		try{
			s = new Scanner(new File("myMap.txt"));
		} catch(Exception e){
			System.out.println("nope");
		}
		while(s.hasNext()) {
			String[] lineData = s.nextLine().split(",");
			noteList.createTapNote(lineData[0].charAt(0), Double.parseDouble(lineData[1]));
		}
		*/
		noteList.sort();
		//System.out.println(noteList);
		double startOffset = Math.min(noteList.getEarliest(), 0) - 5000;
		double timeElapsed = 0 + startOffset;
		double startTime = System.currentTimeMillis();
		long timeStamp_before = System.nanoTime();
		long timeStamp_after = timeStamp_before;
		double dT = 0;
		while(timeElapsed < noteList.getLatest() + 5000 && !quit) {
			if(timeElapsed == 0) {
				mediaPlayer.play();
			}
			// time upkeep
			timeStamp_before = System.nanoTime();
			dT += timeStamp_before - timeStamp_after;
			
			// main update loop
			catcher.update();
			noteList.update(timeElapsed, dT);
			//System.out.println(timeElapsed);
			
			// re-draw all
			j.repaint();
			
			//System.out.println(dT);
			
			// time upkeep
			timeStamp_after = System.nanoTime();
			dT = timeStamp_after - timeStamp_before;
			timeElapsed = System.currentTimeMillis() - startTime + startOffset;
		} // end update loop
		j.repaint();
		
		mediaPlayer.stop();
		
		
		noteList.destroy();
		catcher.destroy();
		k.destroy();
		
		noteList = null;
		catcher = null;
		k = null;
		scoreFont = null;
		comboFont = null;
		blocc = null;
		media = null;
		mediaPlayer = null;
		j.dispose();
		System.out.println("done");
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setColor(new Color(255,255,255));
		
		/* outline deactivated noteCatchers
		for(Rectangle2D.Double i:catcher.getDraw()) {
			g2.draw(i);
		}
		*/
		if(catcher != null) {
			g2.draw(catcher.getBindingBox());
		}
		// draw notes
		for(Note i:noteList.getVisibleNotes()) {
			if(i == null)
				continue;
			try {
				if(i.getLane() == 'f' || i.getLane() == 'j') {
					g2.setColor(new Color(194, 61, 83));
					if(i.isLocked()) {
						g2.setColor(new Color(116, 37, 50));
					}
				}
				else {
					g2.setColor(new Color(194, 61, 130));
					if(i.isLocked()) {
						g2.setColor(new Color(143, 44, 95));
					}
				}
				g2.fill(i.getRect());
			} catch(NullPointerException e) {
				System.out.println("Bad!");
			}
			
		}
		
		if(hidden) {
			GradientPaint gradient = new GradientPaint(
					0, 
					(int)catcher.getBindingBox().getY(), 
					new Color(0xFF000000, true), 
					0,
					(int)catcher.getBindingBox().getY()-500,
					new Color(0x00FFFFFF, true));
			g2.setPaint(gradient);
			g2.fill(new Area(new Rectangle2D.Double(0,catcher.getBindingBox().getY()-500,400,500)));
		}
		// fill the activated catchers
		g2.setColor(new Color(255,0,0));
		for(Rectangle2D.Double i:catcher.getFill()) {
			g2.fill(i);
		}
		g2.setFont(scoreFont);
		g2.drawString(""+(int)scorecounter.getScore(), 0, 40);
		g2.setFont(comboFont);
		g2.drawString(""+(int)scorecounter.getCombo(), 100, 500);
		g2.setColor(new Color(50,50,50));
		g2.fill(blocc);
	}
	
	

}
