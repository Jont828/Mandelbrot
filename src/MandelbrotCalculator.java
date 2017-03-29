import java.awt.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MandelbrotCalculator {

 	private int imgWidth;
	private int imgHeight;
	
    private double zoomFactor;
    private double centerX;
    private double centerY;
    
    private double oldCenterX;
    private double oldCenterY;
    
    private double XMIN, YMIN;
    private double XMAX, YMAX;
    
    private boolean done;
    
    private MandelbrotGraphics graphics;
    private MandelbrotRunnable threads[];
    
    private ExecutorService exec;
    
    private static final int NUM_THREADS = 8;

	public MandelbrotCalculator(MandelbrotGraphics g) {
		
		graphics = g;
	    zoomFactor = 0.35;
	    centerX = oldCenterX = -0.75;
	    centerY = oldCenterY = 0;

		imgWidth = graphics.imageWidth();
		imgHeight = graphics.imageHeight();
		
		MandelbrotRunnable.setMandelbrotGraphics(graphics);
		threads = new MandelbrotRunnable[NUM_THREADS];
		
		for(int i=0; i<threads.length; i++) {
			threads[i] = new MandelbrotRunnable();
		}
		
		exec = Executors.newFixedThreadPool(NUM_THREADS);
		
	}

	
//	private boolean outcircle(double center_x, double center_y, double r, double x, double y)
//	{ // checks if (x,y) is outside the circle around (center_x,center_y) with radius r
//	        x -= center_x;
//	        y -= center_y;
//	        if (x * x + y * y > r * r)
//	                return(true);
//	        return(false);
//
//	 // skip values we know they are inside
//	        if ((outcircle(-0.11, 0.0, 0.63, x, y) || x > 0.1) && outcircle(-1.0, 0.0, 0.25, x, y) && outcircle(-0.125, 0.744, 0.092, x, y) && outcircle(-1.308, 0.0, 0.058, x, y) && outcircle(0.0, 0.25, 0.35, x, y)) {
//	                          // code for iteration
//        	}
//	}
	
	
	private void recalculateCenter(int x, int y) {
		oldCenterX = centerX;
		oldCenterY = centerY;
		
		int yAdjusted = imgHeight - y - 1; // Since MouseEvent uses the upper left as the origin, we must change it for our coordinate system
		
		centerX = centerX + ( 2.0*x - imgWidth) / imgWidth * ( XMAX - XMIN ) / 2.0; 
		centerY = centerY + ( 2.0*yAdjusted - imgHeight) / imgHeight * ( YMAX - YMIN ) / 2.0; 
	}
	
	private void printData() {
		System.out.println("Center:\t\t(" + centerX + ", " + centerY + ")");
		System.out.println("Zoom:\t\t" + zoomFactor);
		System.out.println("XMIN:\t" + XMIN + "\tYMIN:\t" + YMIN);
		System.out.println("XMAX:\t" + XMAX + "\tYMAX:\t" + YMAX);
	}
	
	public void zoomIn(int x, int y) {
		zoomFactor *= 2;
		recalculateCenter(x, y);
		
		printData();
	}
	
	public void zoomOut() {
		zoomFactor /= 2;
		
		centerX = oldCenterX;
		centerY = oldCenterY;
		
		printData();
	}
	
	public void move(int x, int y) {
		recalculateCenter(x, y);

		printData();
	}
	
	public void start() {
		done = false;
		run();
	}
	
	public void run() {

//		draw();
		drawParallel();
	}
	
	public void drawParallelExec() {
		
		done = false;
		long start = System.nanoTime();
		
		double graphWidth = 1.0 / zoomFactor;
		double graphHeight = 1.0 / zoomFactor;
		
		if(imgWidth > imgHeight) {
			graphWidth *= imgWidth / (double) imgHeight;
		} else {
			graphHeight *= imgHeight / (double) imgWidth;
		}
		
        XMIN = centerX - graphWidth / 2;
        XMAX = centerX + graphWidth / 2;
        YMIN = centerY - graphHeight / 2;
        YMAX = centerY + graphHeight / 2;
        
        MandelbrotRunnable.initConstants(XMIN, XMAX, YMIN, YMAX);
        		
		int total = (int) imgHeight * imgWidth;
		int segmentLen = (int) Math.ceil(total / NUM_THREADS);
		
		int startRow = 0;
		int startCol = 0;
		int endRow = 0;
		int endCol = 0;
        
		for(int i=0; i<NUM_THREADS; i++) {

			startRow = endRow;
			startCol = endCol;
			
			endCol += segmentLen;
			
			while(endCol > imgWidth ) {
				endCol -= imgWidth;
				endRow++;
				
			}
			threads[i].setBounds(startRow, startCol, endRow, endCol);
		}
        
		for(MandelbrotRunnable l : threads)
			l.run();
		
        graphics.update();
        done = true;
        long end = System.nanoTime();
        
        System.out.println("Draw time = " + (end - start) / 1000000000d + " seconds");
	}
	
	
	public void drawParallel() {
		
		done = false;
		long start = System.nanoTime();
		
		double graphWidth = 1.0 / zoomFactor;
		double graphHeight = 1.0 / zoomFactor;
		
		if(imgWidth > imgHeight) {
			graphWidth *= imgWidth / (double) imgHeight;
		} else {
			graphHeight *= imgHeight / (double) imgWidth;
		}
		
        XMIN = centerX - graphWidth / 2;
        XMAX = centerX + graphWidth / 2;
        YMIN = centerY - graphHeight / 2;
        YMAX = centerY + graphHeight / 2;
        
        MandelbrotRunnable.initConstants(XMIN, XMAX, YMIN, YMAX);
        		
		int total = (int) imgHeight * imgWidth;
		int segmentLen = (int) Math.ceil(total / NUM_THREADS);
		
		int startRow = 0;
		int startCol = 0;
		int endRow = 0;
		int endCol = 0;
        
		for(int i=0; i<threads.length; i++) {

			startRow = endRow;
			startCol = endCol;
			
			endCol += segmentLen;
			
			while(endCol > imgWidth ) {
				endCol -= imgWidth;
				endRow++;
				
//				if(endRow == graphics.getImageHeight() - 1) {
//					endCol = graphics.getImageWidth();
//				}
			}
			threads[i].setBounds(startRow, startCol, endRow, endCol);
		}
        
        
		for(MandelbrotRunnable l : threads)
			l.run();
		
		graphics.setText("<html>Zoom = " + zoomFactor + 
				"<br>x = [" + XMIN + ", " + XMAX + "]" +
				"<br>y = [" + YMIN + ", " + YMAX + "]" + 
				"<br>Center = (" + centerX + ", " + centerY + ")</html>");
		
        graphics.update();
        done = true;
        long end = System.nanoTime();
        
        System.out.println("Draw time = " + (end - start) / 1000000000d + " seconds");
	}
	
	public boolean isDone() {
		return done;
	}
	
}