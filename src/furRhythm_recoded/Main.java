package furRhythm_recoded;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import java.awt.event.*;

import java.io.File;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class Main extends JPanel{

	static NoteController noteList;
	static NoteCatcherController catcher;
	static FurInputHandler k;
	static ScoreCounter scorecounter;
	static Font scoreFont, comboFont;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		k = new FurInputHandler(4);
		scorecounter = new ScoreCounter();
		catcher = new NoteCatcherController(k);
		
		scoreFont = new Font("Arial", Font.PLAIN, 50);
		comboFont = new Font("Arial", Font.PLAIN, 64);
		
		JFrame j = new JFrame();
		j.setTitle("hehe");
		j.setSize(new Dimension(1000,1000));
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		j.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				k.press(e.getKeyChar());
			}
			public void keyReleased(KeyEvent e) {
				k.release(e.getKeyChar());
			}
			public void keyTyped(KeyEvent e) {}
		});
		j.add(new Main());
		j.setVisible(true);
		
		
		catcher.setWindowSize(j.getWidth(), j.getHeight());
		
		noteList = new NoteController(k, 4);
		noteList.setTarget(catcher.getBindingBox());
		noteList.attachScoreCounter(scorecounter);
		noteList.setWindowSize(j.getWidth(), j.getHeight());
		OsuParser.parse("Kurokotei - Wtf/Kurokotei - wtf (FAMoss) [insane].osu", noteList);
		JFXPanel fxPanel = new JFXPanel();
		j.add(fxPanel);
		Media media = new Media(new File("Kurokotei - Wtf/wtf.mp3").toURI().toString());
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
		while(timeElapsed < noteList.getLatest() + 1000) {
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
		System.out.println("done");
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setColor(new Color(0,0,0));
		
		/* outline deactivated noteCatchers
		for(Rectangle2D.Double i:catcher.getDraw()) {
			g2.draw(i);
		}
		*/
		g2.draw(catcher.getBindingBox());
		// draw notes
		g2.setColor(new Color(0,255,0));
		for(Note i:noteList.getVisibleNotes()) {
			if(i == null)
				continue;
			try {
				g2.fill(i.getRect());
			} catch(NullPointerException e) {
				System.out.println("Bad!");
			}
			
		}
		// fill the activated catchers
		g2.setColor(new Color(255,0,0));
		for(Rectangle2D.Double i:catcher.getFill()) {
			g2.fill(i);
		}
		g2.setFont(scoreFont);
		g2.drawString(""+(int)scorecounter.getScore(), 0, 40);
		g2.setFont(comboFont);
		g2.drawString(""+(int)scorecounter.getCombo(), 500, 500);
	}
	
	

}
