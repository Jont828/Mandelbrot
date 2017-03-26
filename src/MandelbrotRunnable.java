import java.awt.Color;

public class MandelbrotRunnable implements Runnable {

	private static  MandelbrotGraphics graphics;
	
	private int startRow, startCol;
	private int endRow, endCol;
	
	private static double XMIN, XMAX, YMIN, YMAX;
	private static int imgWidth, imgHeight;
	
	private static final int MAX_ITERATIONS = 200; // Don't forget to change MAX_ITERATIONS in MandelbrotCalculator
	
	private static final double offset = 0.0;
	private static final int offsetInt = (int) ( offset * MAX_ITERATIONS );
	private Color color;
	int rgb[];
	
	public MandelbrotRunnable() {
		rgb = new int[3];
	}
	

	public static void initConstants(double xmin, double xmax, double ymin, double ymax) {
		XMIN = xmin;
		XMAX = xmax;
		YMIN = ymin;
		YMAX = ymax;
	}
	
	public static void setMandelbrotGraphics(MandelbrotGraphics g) {
		graphics = g;
		
		imgWidth = graphics.getImageWidth();
		imgHeight = graphics.getImageHeight();
	}
	
	public void setBounds(int sr, int sc, int er, int ec) {
		startRow = sr;
		startCol = sc;
		endRow = er;
		endCol = ec;
	}
	
	
	private static double compMultReal(double a, double b, double c, double d) {
		return (a * c) - (b * d);
	}
	
	private static double compMultImag(double a, double b, double c, double d) {
		return (a * d) + (b * c);
	}
	
	private static double compMagnitude(double a, double b) {
		return Math.sqrt( a * a + b * b );
	}
	
	private static double compSquareSum(double a, double b) {
		return a * a + b * b;
	}
	
	private int mandelbrotTest(double a, double bi) {
		
		double atmp, btmp;
		int number = 0;
		double z = 0, zi = 0;
		
		while ( (number != MAX_ITERATIONS) && (compSquareSum(z, zi) < 4.0 )) { // or compMagnitude(z, zi) < 2.0
			number++;
			atmp = compMultReal(z, zi, z, zi);
			btmp = compMultImag(z, zi, z, zi);
			
			z = atmp;
			zi = btmp;
			
			z += a;
			zi += bi; 			
		}
		
		if (number == MAX_ITERATIONS) { // formerly number != 200		
			return -1;
		} else {
			return number;
		}
	}
	
	private int juliaTest(double z, double zi) {
		
		double atmp, btmp;
		int number = 0;
		double a = 0, bi = 0;
		
		while ( (number != MAX_ITERATIONS) && (compSquareSum(z, zi) < 4.0 )) { // or compMagnitude(z, zi) < 2.0
			number++;
			atmp = compMultReal(z, zi, z, zi);
			btmp = compMultImag(z, zi, z, zi);
			
			z = atmp;
			zi = btmp;
			
			z += a;
			zi += bi; 			
		}
		
		if (number == MAX_ITERATIONS) { // formerly number != 200		
			return -1;
		} else {
			return number;
		}
	}
	
	public void run() {
		
		
		double complexX;
		double complexY;
		
		for(int j=startCol; j<imgWidth; j++) {
			
    		complexX = XMIN + j * (XMAX - XMIN) / imgWidth;
    		complexY = YMIN + startRow * (YMAX - YMIN) / imgHeight;
    		int numIterations = mandelbrotTest(complexX, complexY);
    		
    		if(numIterations == -1) {
    			graphics.setPixel(j, startRow, new int[]{0, 0, 0});
    		} else {
	    		graphics.setPixel(j, startRow, getColor(numIterations));
    		}
		}
		startRow++;
		
		for(int i=startRow; i<endRow; i++) {
			for(int j=0; j<imgWidth; j++) {
				
	    		complexX = XMIN + j * (XMAX - XMIN) / imgWidth;
	    		complexY = YMIN + i * (YMAX - YMIN) / imgHeight;
	    		int numIterations = mandelbrotTest(complexX, complexY);	
	    		
	    		if(numIterations == -1) {
	    			graphics.setPixel(j, i, new int[]{0, 0, 0});
	    		} else {
		    		graphics.setPixel(j, i, getColor(numIterations));
	    		}
			}
		}
		
		for(int j=0; j<endCol; j++) {
			
    		complexX = XMIN + j * (XMAX - XMIN) / imgWidth;
    		complexY = YMIN + endRow * (YMAX - YMIN) / imgHeight;
    		int numIterations = mandelbrotTest(complexX, complexY);	    		
    		
    		if(numIterations == -1) {
    			graphics.setPixel(j, endRow, new int[]{0, 0, 0});
    		} else {
	    		graphics.setPixel(j, endRow, getColor(numIterations));
    		}
		}
	}
	
	
	private int[] getColor(int numIterations) {

		int colorInt = ( numIterations + offsetInt ) % MAX_ITERATIONS;
				
		float hsbColor = colorInt / (float) MAX_ITERATIONS;
		
		color = Color.getHSBColor(hsbColor, 0.8f, 1);
		
		rgb[0] = color.getRed();
		rgb[1] = color.getGreen();
		rgb[2] = color.getBlue();
		
		return rgb;
	}

}
