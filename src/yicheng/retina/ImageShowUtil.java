package yicheng.retina;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;

public class ImageShowUtil {
	private static Image toBufferedImage(Mat matrix){
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( matrix.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
		byte [] b = new byte[bufferSize];
		matrix.get(0,0,b); // get all the pixels
		BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);  
		return image;
	}

	public static void show(Mat matrix, String imgName){
		Image image = toBufferedImage(matrix);
		JFrame frame = new JFrame(imgName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setResizable(false);
		frame.setLocationRelativeTo(null);

		ImageIcon imageIcon = new ImageIcon(image);
		frame.setSize(imageIcon.getIconWidth() + 10, imageIcon.getIconHeight() + 35);

		JLabel label = new JLabel(" ", imageIcon, JLabel.CENTER);
		frame.getContentPane().add(label);
		frame.validate();
		frame.setVisible(true);


	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
