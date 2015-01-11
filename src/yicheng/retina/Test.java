package yicheng.retina;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class Test {
	public static final String fileName = "C:\\Users\\Yicheng\\Desktop\\work\\retina\\retina layer segmentation\\exampleOCTimage0001.tif";
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		ImageReader imageReader = new ImageReader(fileName);
		
		Mat matrix = imageReader.getImageMatrix();
		
		ImageShowUtil.show(matrix, "img 1");
	}

}
