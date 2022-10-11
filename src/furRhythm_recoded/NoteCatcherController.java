package furRhythm_recoded;

import java.awt.geom.Rectangle2D;

public class NoteCatcherController {
	FurInputHandler inputhandler;
	NoteCatcher[] catchers;
	final Rectangle2D.Double BINDING_BOX;
	public NoteCatcherController(FurInputHandler inputhandler) {
		BINDING_BOX = new Rectangle2D.Double(0, 750, 1000, 50);
		this.inputhandler = inputhandler;
		catchers = new NoteCatcher[inputhandler.getSize()];
		createCatchers();
	}
	private void createCatchers() {
		char[] tempChars = inputhandler.getKeys();
		
		for(int i = 0; i < tempChars.length; i++) {
			catchers[i] = new NoteCatcher(
					new Rectangle2D.Double(0 + i*(1000.0/catchers.length), 750, (double)(1000.0/catchers.length), 50)
					, tempChars[i]);
		}
	}
	public void update() {
		for(NoteCatcher i:catchers) {
			if(inputhandler.getValue(i.getLane())) {
				i.press();
			}
			else {
				i.release();
			}
		}
	}
	public Rectangle2D.Double[] getList(){
		Rectangle2D.Double[] temp = new Rectangle2D.Double[catchers.length];
		for(int i = 0; i < catchers.length; i++) {
			temp[i] = catchers[i].getRect();
		}
		return temp;
	}
	public Rectangle2D.Double[] getFill(){
		int c = 0;
		for(NoteCatcher i:catchers) {
			if(i.isPressed()) {
				c++;
			}
		}
		Rectangle2D.Double[] temp = new Rectangle2D.Double[c];
		int index = 0;
		for(int i = 0; i < catchers.length; i++) {
			if(catchers[i].isPressed()) {
				temp[index] = catchers[i].getRect();
				index++;
			}
		}
		return temp;
	}
	public Rectangle2D.Double[] getDraw(){
		int c = 0;
		for(NoteCatcher i:catchers) {
			if(!i.isPressed()) {
				c++;
			}
		}
		Rectangle2D.Double[] temp = new Rectangle2D.Double[c];
		int index = 0;
		for(int i = 0; i < catchers.length; i++) {
			if(!catchers[i].isPressed()) {
				temp[index] = catchers[i].getRect();
				index++;
			}
		}
		return temp;
	}
	public Rectangle2D.Double getBindingBox(){
		return BINDING_BOX;
	}
}
