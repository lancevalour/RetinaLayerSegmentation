package yicheng.retina;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.graphstream.graph.Node;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageProcessor {
	public static final String fileName = "C:\\Users\\Yicheng\\Desktop\\work\\retina\\retina layer segmentation\\exampleOCTimage0001.tif";
	private Mat matrix;
	private Mat originalMatrix;

	public ImageProcessor(Mat matrix){
		this.matrix = matrix;

	}

	public void preprocess(){
		resize(0.2);
		grayscale();

		trim();	

		filter();
		gradient();
		binaryImage();

		flatten(this.secondDerivVGradMatrix);


	}



	private List<Integer> shiftColDownStepArray = new ArrayList<Integer>();
	private Mat binaryMatrix;
	private Mat flattenedMatrix;
	private int referenceGap = 0;
	public void flatten(Mat gradientImage){


		for (int i = 0; i < this.binaryMatrix.rows(); i++){
			if (this.binaryMatrix.get(i, 0)[0] == 0){
				referenceGap ++;
			}
			else {
				break;
			}
		}

		shiftColDownStepArray.add(0);

		this.flattenedMatrix = gradientImage.clone();

		for (int j = 1; j < this.binaryMatrix.cols(); j++){
			int gap = 0;
			for (int i = 0; i < this.binaryMatrix.rows(); i++){
				if (this.binaryMatrix.get(i, j)[0] == 0){
					gap++;
				}
				else {
					shiftColDown(j, this.referenceGap - gap);
					shiftColDownStepArray.add(this.referenceGap - gap);
					break;
				}
			}
		}

		//	System.out.println(shiftColDownStepArray);

	}



	private void shiftColDown(int col, int step){
		if (step > 0){
			Mat gap = this.flattenedMatrix.col(col).rowRange(this.flattenedMatrix.rows() - step, this.flattenedMatrix.rows());
			Mat remain = this.flattenedMatrix.col(col).rowRange(0, this.flattenedMatrix.rows() - step);
			Mat newCol = new Mat(this.flattenedMatrix.rows(), this.flattenedMatrix.cols(), 1);

			List<Mat> list = new ArrayList<Mat>();

			list.add(gap);
			list.add(remain);

			Core.vconcat(list, newCol);

			newCol.copyTo(this.flattenedMatrix.col(col));
		}
		else if (step < 0 ){
			Mat gap = this.flattenedMatrix.col(col).rowRange(0, -step);
			Mat remain = this.flattenedMatrix.col(col).rowRange(-step, this.flattenedMatrix.rows());
			Mat newCol = new Mat(this.flattenedMatrix.rows(), this.flattenedMatrix.cols(), 1);



			List<Mat> list = new ArrayList<Mat>();

			list.add(remain);
			list.add(gap);

			Core.vconcat(list, newCol);

			newCol.copyTo(this.flattenedMatrix.col(col));
		}


	}


	public void resize(double scale){
		Imgproc.resize(matrix, matrix, new Size(matrix.width() * scale, matrix.height() * scale));
	//	ImageShowUtil.show(matrix, "resized image");
	}

	public void grayscale(){	
		Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_RGB2GRAY);

	}

	public void binaryImage(){
		//	Imgproc.threshold(matrix, matrix, 20, 255, Imgproc.THRESH_BINARY);
		binaryMatrix = this.firstDerivVGradMatrix.clone();
		/*Imgproc.morphologyEx(matrix, matrix, Imgproc.MORPH_DILATE, 
		Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));*/
		Imgproc.threshold(firstDerivVGradMatrix, binaryMatrix, 55, 255, Imgproc.THRESH_BINARY);

		//ImageShowUtil.show(binaryMatrix, "binary image");
		Imgproc.morphologyEx(binaryMatrix, binaryMatrix, Imgproc.MORPH_OPEN, 
				Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
		//ImageShowUtil.show(binaryMatrix, "binary open image");

		Imgproc.morphologyEx(binaryMatrix, binaryMatrix, Imgproc.MORPH_CLOSE, 
				Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5)));

		//ImageShowUtil.show(binaryMatrix, "binary close image");
		/*Imgproc.morphologyEx(binaryMatrix, binaryMatrix, Imgproc.MORPH_ERODE, 
		Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
		 */

		//Imgproc.adaptiveThreshold(matrix, binaryMatrix, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 3, 1);
	}

	private Mat firstDerivVGradMatrix;
	private Mat secondDerivVGradMatrix;

	public void gradient(){
		firstDerivVGradMatrix = this.matrix.clone();
		secondDerivVGradMatrix = this.matrix.clone();
		Mat kernel = new Mat(9,9, CvType.CV_32F){
			{
				put(0,0,-1);
				put(0,1,0);
				put(0,2,1);

				put(1,0-2);
				put(1,1,0);
				put(1,2,2);

				put(2,0,-1);
				put(2,1,0);
				put(2,2,1);
			}
		};	      
		//Imgproc.filter2D(matrix, matrix, -1, kernel);

		//Imgproc.threshold(matrix, matrix, 110, 255, Imgproc.THRESH_BINARY);
		//	Imgproc.adaptiveThreshold(matrix, matrix, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 6);

		Imgproc.Sobel(this.matrix, this.firstDerivVGradMatrix, -1, 0, 1);

		//Imgproc.Sobel(this.firstDerivVGradMatrix, this.secondDerivVGradMatrix, -1, 0, 2);
		Imgproc.Sobel(this.matrix, this.secondDerivVGradMatrix, -1, 0, 2);
		Imgproc.Sobel(this.temp, this.temp, -1, 0, 2);

	}

	public void equalizeHist(){
		Imgproc.equalizeHist(matrix, matrix);
	}

	private Mat temp;
	public Mat getTemp(){
		return this.temp;
	}

	public void filter(){
		Mat filteredMat = new Mat();
		this.temp = new Mat();

		Imgproc.GaussianBlur(this.matrix, filteredMat, new Size(7,7), 1);



		Imgproc.adaptiveBilateralFilter(this.matrix, temp, new Size(3,3), 152);

		this.matrix = filteredMat;		
	}


	private final int darkBackgroundThreadshold = 6;

	public void trim(){
		int rowIndex = 0;
		for (int i = 0; i < this.matrix.rows(); i++){
			if (Core.mean(this.matrix.row(i)).val[0] < this.darkBackgroundThreadshold){
				rowIndex++;
			}
		}

		//drawDarkBackgroundThreadsholding(rowIndex);


		this.matrix = this.matrix.submat(new Rect(0, rowIndex, this.matrix.cols(), this.matrix.rows() - rowIndex));
		//ImageShowUtil.show(this.matrix, "background threshholding image");
		originalMatrix = this.matrix;

	}

	public void drawDarkBackgroundThreadsholding(int rowIndex){
		Core.line(this.matrix, new Point(0 , rowIndex), new Point( this.matrix.cols() - 1, rowIndex), new Scalar(255, 0, 0));
	}


	public void markLayer(List<List<Node>> pathList, Mat mat){
		grayToRGB();
		for (List<Node> list : pathList){
			for (Node node : list){
				String[] s = node.getId().split(",");
				int x = Integer.parseInt(s[0]);
				int y = mat.rows() - Integer.parseInt(s[1]);

				mat.put(y, x, new double[]{255, 0, 0});				
			}
		}
	}


	public void removeLayer(Mat mat, List<Node> pathList, int index){

		RGBToGray();
		

		for (Node node : pathList){
			String[] s = node.getId().split(",");
			int x = Integer.parseInt(s[0]);
			int y = mat.rows() - Integer.parseInt(s[1]);
			if (index != 7){

				for (int i = y - 2; i <= y + 1; i++){
					mat.put(i, x, new double []{0});
				}

			}
			else {
				if (x > this.matrix.cols() / 3 -55  && x < this.matrix.cols() / 3 * 2){
					for (int i = y - 2; i <= y + 1; i++){
						mat.put(i, x, new double []{0});
					}
				}
			}

		}



	}

	private void RGBToGray(){
		if (this.matrix.get(0, 0).length == 3){		
			Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_RGB2GRAY);
		}
		if (this.flattenedMatrix.get(0, 0).length == 3){
			Imgproc.cvtColor(this.flattenedMatrix, this.flattenedMatrix, Imgproc.COLOR_RGB2GRAY);
		}
		if (this.firstDerivVGradMatrix.get(0, 0).length == 3){
			Imgproc.cvtColor(firstDerivVGradMatrix, firstDerivVGradMatrix, Imgproc.COLOR_RGB2GRAY);
		}
		if (this.secondDerivVGradMatrix.get(0, 0).length == 3){
			Imgproc.cvtColor(secondDerivVGradMatrix, secondDerivVGradMatrix, Imgproc.COLOR_RGB2GRAY);
		}
		if (this.temp.get(0, 0).length == 3){
			Imgproc.cvtColor(temp, temp, Imgproc.COLOR_RGB2GRAY);
		}
	}

	private void grayToRGB(){
		if (this.matrix.get(0, 0).length == 1){
			Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_GRAY2RGB);
		}
		if (this.flattenedMatrix.get(0, 0).length == 1){
			Imgproc.cvtColor(this.flattenedMatrix, this.flattenedMatrix, Imgproc.COLOR_GRAY2RGB);
		}
		if (this.firstDerivVGradMatrix.get(0, 0).length == 1){
			Imgproc.cvtColor(firstDerivVGradMatrix, firstDerivVGradMatrix, Imgproc.COLOR_GRAY2RGB);
		}
		if (this.secondDerivVGradMatrix.get(0, 0).length == 1){
			Imgproc.cvtColor(secondDerivVGradMatrix, secondDerivVGradMatrix, Imgproc.COLOR_GRAY2RGB);
		}
		if (this.temp.get(0, 0).length == 1){
			Imgproc.cvtColor(temp, temp, Imgproc.COLOR_GRAY2RGB);
		}
		if (this.originalMatrix.get(0, 0).length == 1){
			Imgproc.cvtColor(originalMatrix, originalMatrix, Imgproc.COLOR_GRAY2RGB);
		}
	}


	public Mat getImageMatrix(){
		return this.matrix;
	}

	public Mat getGradientImageMatrix(){
		return this.firstDerivVGradMatrix;
	}

	public Mat getSecondGradientImageMatrix(){
		return this.secondDerivVGradMatrix;
	}

	public Mat getBinaryImageMatrix(){
		return this.binaryMatrix;
	}

	public Mat getFlattenedImageMatrix(){
		return this.flattenedMatrix;
	}

	public Mat getOriginalMatrix(){
		return this.originalMatrix;
	}

	public void printMatrix(){
		for (int i = 0; i < this.matrix.rows(); i++){
			for (int j = 0; j < this.matrix.cols(); j++){
				System.out.print(this.matrix.get(i, j)[0]);
				System.out.print(" ");
			}
			System.out.println();
		}
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		ImageReader imageReader = new ImageReader(fileName);
		Mat origMat = imageReader.getImageMatrix();
		ImageProcessor imageProcessor = new ImageProcessor(origMat);



		imageProcessor.preprocess();

		ImageShowUtil.show(imageProcessor.getImageMatrix(), "preprocessed image");

		//imageProcessor.printMatrix();

		ImageShowUtil.show(imageProcessor.getGradientImageMatrix(), "gradient image");

		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "second gradient image");




		ImageShowUtil.show(imageProcessor.getFlattenedImageMatrix(), "flattened image");




	}

}
