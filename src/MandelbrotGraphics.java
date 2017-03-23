import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MandelbrotGraphics implements ActionListener {
	
	private JFrame frame;
	private BufferedImage image;
	private WritableRaster raster;
	
	private int imgWidth;
	private int imgHeight;
	
	public MandelbrotGraphics(int width, int height) {
		
		imgWidth = width;
		imgHeight = height;
		
		frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if(imgWidth > screenSize.width) {
        	imgWidth = screenSize.width;
        }
        
        if(imgHeight > screenSize.height) {
        	imgHeight = screenSize.height;
        }
        frame.setSize(imgWidth, imgHeight);
        frame.setTitle("Mandelbrot Set (left click to zoom in and right click to zoom out): " + imgWidth + "x" + imgHeight);
        
		System.out.println(imgWidth + "\t" + imgHeight);

		
        image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        frame.setContentPane(new JLabel(new ImageIcon(image)));
        
        raster = image.getRaster();
        
        
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem menuItem1 = new JMenuItem(" Save...   ");
        menuItem1.addActionListener(this);
        menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                 Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        menu.add(menuItem1);
        frame.setJMenuBar(menuBar);
	}
	
	public void addMouseListener(MouseListener m) {
		frame.addMouseListener(m);
	}
	
	public void setPixel(int x, int y, int rgb[]) {
		raster.setPixel(x, y, rgb);
	}
	
	public int getImageWidth() {
		return imgWidth;
	}
	
	public int getImageHeight() {
		return imgHeight;
	}
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	public void update() {
		frame.repaint();
	}
	
//	public void pack() {
//		frame.pack();
//		imgWidth = frame.getContentPane().getWidth();
//		imgHeight = frame.getContentPane().getHeight();
//		
//		System.out.println(imgWidth + "\t" + imgHeight);
//	}
	
    private void save(String filename) {
        if (filename == null) throw new IllegalArgumentException("argument to save() is null");
        save(new File(filename));
    }


    private void save(File file) {
        if (file == null) throw new IllegalArgumentException("argument to save() is null");
        String filename = file.getName();
//        if (frame != null) frame.setTitle(filename);
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);
        if ("jpg".equalsIgnoreCase(suffix) || "png".equalsIgnoreCase(suffix)) {
            try {
                ImageIO.write(image, suffix, file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Error: filename must end in .jpg or .png");
        }
    }


    public void actionPerformed(ActionEvent e) {
        FileDialog chooser = new FileDialog(frame,
                             "Use a .png or .jpg extension", FileDialog.SAVE);
        chooser.setVisible(true);
        if (chooser.getFile() != null) {
            save(chooser.getDirectory() + File.separator + chooser.getFile());
        }
    }
	
}
