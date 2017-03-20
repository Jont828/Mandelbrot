import java.awt.Color;

public class MandelbrotRunnable implements Runnable {

	private MandelbrotGraphics graphics;
	
	private int start;
	private int end;
	private int row;
	private double y;
	
	private static double XMIN, XMAX, CENTER_X;
	
	private static final int MAX_ITERATIONS = 200; // Don't forget to change MAX_ITERATIONS in MandelbrotCalculator
	Color color;
	
	public MandelbrotRunnable(MandelbrotGraphics mg, int s, int e, int c, double y) {
		graphics = mg;
		
		start = s;
		end = e;
		row = c;
		this.y = y;
		
	}
	
	public static void initConstants(double xmin, double xmax, double center_x) {
		XMIN = xmin;
		XMAX = xmax;
		CENTER_X = center_x;
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
	
	@Override
	public void run() {
		
		for(int i=start; i<end; i++) {
    		double cx = XMIN + i * (XMAX - XMIN) / graphics.getImageWidth();
			int numIterations = mandelbrotTest(cx, y);
			
    		if(numIterations == -1) {
    			graphics.setPixel(i, row, new int[]{0, 0, 0}); // graphics.setPixel(x, y, rgb) -> row is the y value and i is the x value
    		} else {
	    		graphics.setPixel(i, row, getColor(numIterations));
    		}
		}
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

}
