package yicheng.retina;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class ImageReader {
	private String fileName;
	private Mat matrix;
	
	public ImageReader(String fileName){	
		this.fileName = fileName;
		this.matrix = Highgui.imread(this.fileName, Highgui.CV_LOAD_IMAGE_COLOR);	
	}
	
	public Mat getImageMatrix(){
		return this.matrix;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
