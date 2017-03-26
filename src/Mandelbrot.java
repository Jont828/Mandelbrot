import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public class Mandelbrot implements MouseListener {

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		System.out.println("Mouse clicked:\t(" + x + ", " + y + ")");
		
		if(SwingUtilities.isRightMouseButton(e)) {
			calc.zoomOut();
		} else if(SwingUtilities.isMiddleMouseButton(e)) {
			calc.move(x, y);
		} else {
			calc.zoomIn(x, y);
		}
		
//		System.out.println("Mouse clicked");
		
		while (!calc.isDone()) {
			try { Thread.sleep(1000); } catch (InterruptedException ex) {}
		}
		
		draw();
		
		System.out.println();
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
	
	private MandelbrotGraphics graphics;
	private MandelbrotCalculator calc;
	
	public Mandelbrot(int width, int height) {
		
		graphics = new MandelbrotGraphics(width, height);
		
		graphics.addMouseListener(this);
		
        calc = new MandelbrotCalculator(graphics);
	}
	
	public void draw() {
		
		graphics.setOriginToBottomLeft();
        calc.start();
        graphics.setVisible(true);
	}
}


