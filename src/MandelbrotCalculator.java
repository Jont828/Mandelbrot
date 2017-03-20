import java.awt.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MandelbrotCalculator {
	
	int colors[][];
	Color color;
	
	private static final int MAX_ITERATIONS = 200;//199;
	
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
    
    MandelbrotGraphics graphics;

	public MandelbrotCalculator(MandelbrotGraphics g) {
		
		graphics = g;
	    zoomFactor = 0.35;
	    centerX = oldCenterX = -0.7;// 0.3485;
	    centerY = oldCenterY = 0;//-0.5015;
		
	    initColorArray();

		imgWidth = graphics.getImageWidth();
		imgHeight = graphics.getImageHeight();
		
	}

	private static double compMultReal(double a, double b, double c, double d) {
		return (a * c) - (b * d);
	}
	
	private static double compMultImag(double a, double b, double c, double d) {
		return (a * d) + (b * c);
	}
	
	private int mandelbrotTest(double a, double bi) {
		
		double atmp, btmp;
		int number = 0;
		double z = 0, zi = 0;
		
		while ( (number != MAX_ITERATIONS) && (compMagnitude(z,zi) < 2.0)) { // formerly number != 200
			number++;
			atmp = compMultReal(z,zi,z,zi);
			btmp = compMultImag(z,zi,z,zi);
			
			z = atmp;
			zi = btmp;
			
			z += a;
			zi += bi; 			
			
			// if (number < 10)
				//System.out.println(number + "("+ z + "," + zi + ")");
		}
		
		if (number == MAX_ITERATIONS) { // formerly number != 200
			// System.out.println("Part of the Mandelbrot set!");			
			return -1;
		} else {
			// System.out.print(" " + number);
			return number;
		}
	}
	
	private int mandelbrotTestParallel(double a, double bi) {
		
		double atmp, btmp;
		int number = 0;
		double z = 0,zi = 0;
		
		while ( (number != MAX_ITERATIONS) && (compMagnitude(z,zi) < 2.0)) { // formerly number != 200
			number++;
			atmp = compMultReal(z,zi,z,zi);
			btmp = compMultImag(z,zi,z,zi);
			
			z = atmp;
			zi = btmp;
			
			z += a;
			zi += bi; 			
			
			// if (number < 10)
				//System.out.println(number + "("+ z + "," + zi + ")");
		}
		
		if (number == MAX_ITERATIONS) { // formerly number != 200
			// System.out.println("Part of the Mandelbrot set!");			
			return -1;
		} else {
			// System.out.print(" " + number);
			return number;
		}
	}
	
	private static double compMagnitude(double a, double b) {
		return Math.sqrt( a * a + b * b );
	}
	
	private void recalculateCenter(int x, int y) {
		oldCenterX = centerX;
		oldCenterY = centerY;
		
		centerX = centerX + ( 2.0*x - imgWidth) / imgWidth * ( XMAX - XMIN ) / 2.0; 
		centerY = centerY + ( 2.0*y - imgHeight) / imgHeight * ( YMAX - YMIN ) / 2.0; 
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
		draw();
//		drawParallel();
	}
	
	
	public void drawParallel() {
		
		
	}
	
	public void drawParallelBroken() {
		final int NTHREADS = 10;
		
		final int segmentLen = imgHeight / NTHREADS;
		
		ExecutorService exec = Executors.newFixedThreadPool(NTHREADS-1);
		
		int offset = 0;
		
		for(int i=0; i<imgWidth; i++) {
			for(int j=0; j<NTHREADS-1; j++) {
				
				int iInner = i;
				int from = offset;
				int to = offset + segmentLen-1;
				
				exec.execute(new Runnable() {
				
					@Override
					public void run(){
						drawParallelHelper(iInner, from, to);
					}
				});
				
				offset += segmentLen;
				
			}
		}
		
		exec.shutdown();
		
		try {
			exec.awaitTermination(10, TimeUnit.SECONDS);
		} catch(InterruptedException ignore) {
			
		}
		
		graphics.update();
		done = true;
		
	}
	
	public void drawParallelHelper(int i, int fromj, int toj) {
		
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
		
	
    	for(int j=fromj; j<toj; j++) {
    		
    		double cx = XMIN + i * (XMAX - XMIN) / imgWidth;
    		double cy = YMIN + j * (YMAX - YMIN) / imgHeight;
    		int numIterations = mandelbrotTest(cx, cy);	    		
    		
    		if(numIterations == -1) {
    			graphics.setPixel(i, j, new int[]{0, 0, 0});
    		} else {
	    		graphics.setPixel(i, j, getColor(numIterations));
    		}
    	
        }
		
	}
	
	private void draw() {
		
		done = false;
		
//		System.out.println("Drawing Mandelbrot");
//		System.out.println(center_x + "\t" + center_y);
		
//        double squareWidth = 1.0 / zoomFactor;
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

        for(int i=0; i<imgWidth; i++) {
        	
        	for(int j=0; j<imgHeight; j++) {
	    		
	    		double cx = XMIN + i * (XMAX - XMIN) / imgWidth;
	    		double cy = YMIN + j * (YMAX - YMIN) / imgHeight;
	    		int numIterations = mandelbrotTest(cx, cy);	    		
	    		
	    		if(numIterations == -1) {
	    			graphics.setPixel(i, j, new int[]{0, 0, 0});
	    		} else {
		    		graphics.setPixel(i, j, getColor(numIterations));
	    		}

	    	}
        	
        }
        
        
        graphics.update();
        done = true;
	}
	
	private int[] getColor(int numIterations) {
		
		double offset = 0;
		
		int offsetInt = (int) ( offset * MAX_ITERATIONS );
//		System.out.println(offsetInt);
		int colorInt = ( numIterations + offsetInt ) % MAX_ITERATIONS;
				
		float hsbColor = colorInt / (float) MAX_ITERATIONS;
		
		color = Color.getHSBColor(hsbColor, 0.8f, 1);
		
		int rgb[] = new int[3];
		
		rgb[0] = color.getRed();
		rgb[1] = color.getGreen();
		rgb[2] = color.getBlue();
		
		return rgb;
	}
	
	public boolean isDone() {
		return done;
	}
	
	private void initColorArray() {
		
		colors = new int[200][3];
		int c = 255;
		for (int i = 0 ; i < 50 ; i++) {
			int[] color = new int[3];
			color[0] = 0; color[1] = 255 - c; color[2] = c;
			colors[i] = color;
			c -= 5;
		}


		c = 255;
		for (int i = 50 ; i < 100 ; i++) {
			int[] color = new int[3];
			color[0] = 255 - c; color[1] = 255; color[2] = 0;
			colors[i] = color;
			c -= 5;
		}

		c = 255;
		for (int i = 100 ; i < 150 ; i++) {
			int[] color = new int[3];
			color[0] = c; color[1] = c; color[2] = 255 - c;
			colors[i] = color;
			c -= 5;
		}
		
		c = 255;
		for (int i = 150 ; i < 200 ; i++) {
			int[] color = new int[3];
			color[0] = 255 - c; color[1] = 0; color[2] = 255;
			colors[i] = color;
			c -= 5;
		}
	}
	
}