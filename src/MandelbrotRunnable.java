import java.awt.Color;

public class MandelbrotRunnable implements Runnable {

	private static  MandelbrotGraphics graphics;
	
	private int startRow, startCol;
	private int endRow, endCol;
	
	private static double XMIN, XMAX, YMIN, YMAX;
	private static int imgWidth, imgHeight;
	
	private static final int MAX_ITERATIONS = 200;
	
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
		
		imgWidth = graphics.imageWidth();
		imgHeight = graphics.imageHeight();
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
	
	private int getNumIterations(double real, double imag) {
		return mandelbrotTest(real, imag);
	}
	
	private int mandelbrotTest(double cReal, double cImag) {
		
		// z[n] = z[n-1]^2 + c
		
		double tempReal, tempImag;
		int numIterations = 0;
		double zReal = 0, zImag = 0;
		
		while ( (numIterations != MAX_ITERATIONS) && (compSquareSum(zReal, zImag) < 4.0 )) { // or compMagnitude(z, zi) < 2.0
			numIterations++;

/*			
 * 			Need temp variables because 
 *				zReal = compMultReal(zReal, zImag, zReal, zImag) 
 *			would cause 
 *				compMultImag(zReal, zImag, zReal, zImag)
 *			to use the new zReal value in the calculation
 */
			
			tempReal = compMultReal(zReal, zImag, zReal, zImag);
			tempImag = compMultImag(zReal, zImag, zReal, zImag);
			
			zReal = tempReal;
			zImag = tempImag;
			
			zReal += cReal;
			zImag += cImag; 			
		}
		
		return numIterations;
	}
	
	private int juliaTest(double zReal, double zImag) {
		
		double tempReal, tempImag;
		int numIterations = 0;
		double cReal = (double) -10/12, cImag = 0;
		
		while ( (numIterations != MAX_ITERATIONS) && (compSquareSum(zReal, zImag) < 4.0 )) { // or compMagnitude(z, zi) < 2.0
			numIterations++;
			tempReal = compMultReal(zReal, zImag, zReal, zImag);
			tempImag = compMultImag(zReal, zImag, zReal, zImag);
			
			zReal = tempReal;
			zImag = tempImag;
			
			zReal += cReal;
			zImag += cImag; 			
		}
		
		return numIterations;
	}
	
	public void run() {
		
		// Rows = y increment
		// Columns = x increment
		
		double complexX;
		double complexY;
		
		for(int j=startCol; j<imgWidth; j++) {
			
    		complexX = XMIN + j * (XMAX - XMIN) / imgWidth;
    		complexY = YMIN + startRow * (YMAX - YMIN) / imgHeight;
    		int numIterations = getNumIterations(complexX, complexY);
    		
    		setMandelbrotColor(j, startRow, numIterations);
		}
		startRow++;
		
		for(int i=startRow; i<endRow; i++) {
			for(int j=0; j<imgWidth; j++) {
				
	    		complexX = XMIN + j * (XMAX - XMIN) / imgWidth;
	    		complexY = YMIN + i * (YMAX - YMIN) / imgHeight;
	    		int numIterations = getNumIterations(complexX, complexY);	
	    		
	    		setMandelbrotColor(j, i, numIterations);
			}
		}
		
		for(int j=0; j<endCol; j++) {
			
    		complexX = XMIN + j * (XMAX - XMIN) / imgWidth;
    		complexY = YMIN + endRow * (YMAX - YMIN) / imgHeight;
    		int numIterations = getNumIterations(complexX, complexY);	    		
    		
    		setMandelbrotColor(j, endRow, numIterations);
		}
	}
	
	
	public void setMandelbrotColor(int row, int col, int numIterations) {
		
		if(numIterations == MAX_ITERATIONS) {
			graphics.setPixel(row, col, new int[]{0, 0, 0});
		} else {
    		graphics.setPixel(row, col, getColor(numIterations));
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
