package yicheng.retina;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.algorithm.AStar;
import org.graphstream.algorithm.DStar;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.Viewer;
import org.opencv.core.Core;
import org.opencv.core.Mat;



public class ImageGraph {

	private Graph graph;
	private Mat matrix;
	private List<List<Node>> dijkstraAllPaths;
	private List<List<Node>> astarAllPaths;	

	private int width;
	private int height;

	public ImageGraph(Mat matrix){
		this.height = matrix.rows();
		this.width = matrix.cols();
		this.matrix = matrix;



		dijkstraAllPaths = new ArrayList<List<Node>>();
		astarAllPaths = new ArrayList<List<Node>>();


		graph = new SingleGraph("imageGraph");
		//graph.addAttribute("ui.antialias", true);
		addNode();
		addEdge();
	}



	private void addNode(){
		for (int i = this.height - 1; i >= 0; i--){
			for (int j = this.width - 1; j >= 0; j--){
				graph.addNode("" + (this.width - j -1) + "," + (this.height - i - 1))
				.addAttribute("xy", (this.width - j - 1 ), (this.height - i - 1));	
			}
		}

		for (Node n : graph){
			//n.addAttribute("label", n.getId());
			n.addAttribute("ui.style", " size: 1px, 1px;");
		}


	}



	private void addEdge(){
		for (int i = 0; i < this.width - 1; i ++){
			for (int j = 0; j < this.height ; j++){
				if (this.matrix.get(this.height - j - 1, i+1)[0] == 0 && Math.abs(this.matrix.get(this.height - j - 1, i + 1)[0] - this.matrix.get(this.height - j - 1, i)[0]) < 20){
					graph.addEdge("" + i + "," + j + "->" + (i + 1) + "," + j, "" + i + "," + j, "" + (i+1) + "," + j, true)
					//.addAttribute("ui.style", "fill-color : yellow;");
					.addAttribute("weight", Integer.MAX_VALUE);
				}
				else {
					graph.addEdge("" + i + "," + j + "->" + (i + 1) + "," + j, "" + i + "," + j, "" + (i+1) + "," + j, true)
					//.addAttribute("ui.style", "fill-color : yellow;");
					.addAttribute("weight", 255 * 2 - (int)this.matrix.get(this.height - j - 1, i + 1)[0] - this.matrix.get(this.height - j - 1, i)[0]);
				}


				if (j < this.height - 1){
					if (this.matrix.get(this.height - j - 2, i+1)[0] == 0 && Math.abs(this.matrix.get(this.height - j - 2, i + 1)[0] - this.matrix.get(this.height - j - 1, i)[0]) < 20){
						graph.addEdge("" + i + "," + j + "->" + (i + 1) + "," + (j + 1), "" + i + "," + j, "" + (i+1) + "," + (j + 1), true)
						//.addAttribute("ui.style", "fill-color : yellow;");
						.addAttribute("weight", Integer.MAX_VALUE);
					}
					else {
						graph.addEdge("" + i + "," + j + "->" + (i + 1) + "," + (j + 1), "" + i + "," + j, "" + (i + 1) + "," + (j + 1), true)
						//.addAttribute("ui.style", "fill-color : red;");
						.addAttribute("weight", 255 * 2 - (int)this.matrix.get(this.height - j - 2, i + 1)[0] - this.matrix.get(this.height - j - 1, i)[0]);
					}

				}
				if (j >= 1){
					if (this.matrix.get(this.height - j, i+1)[0] == 0 && Math.abs(this.matrix.get(this.height - j , i + 1)[0] - this.matrix.get(this.height - j - 1, i)[0]) < 20){
						graph.addEdge("" + i + "," + j + "->" + (i + 1) + "," + (j - 1), "" + i + "," + j, "" + (i + 1) + "," + (j - 1), true)
						//.addAttribute("ui.style", "fill-color : yellow;");
						.addAttribute("weight", Integer.MAX_VALUE);
					}
					else {
						graph.addEdge("" + i + "," + j + "->" + (i + 1) + "," + (j - 1), "" + i + "," + j, "" + (i + 1) + "," + (j -1), true)
						//.addAttribute("ui.style", "fill-color : blue;");
						.addAttribute("weight", 255 * 2 - (int)this.matrix.get(this.height - j, i + 1)[0] - this.matrix.get(this.height - j - 1, i)[0]);

					}
				}
			}
		}
		/*
		for (Edge e : graph.getEdgeSet()){
			e.addAttribute("ui.label", e.getAttribute("weight"));
		}*/

	}


	private Dijkstra dijkstra;

	public void findAllShortestPathDijkstra(){


		/*for (int i = 10; i <= 50; i = i + 3){
			findShortestPathDijkstra(i, i);
		}*/

		findShortestPathDijkstra(10, 5);
	}
	
	private Path DijkstraPath;

	public void findShortestPathDijkstra(int x_start, int x_end){
		dijkstraAllPaths.clear();
		//graph.getNode("0," + (this.matrix.rows() - x_start))
		dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "weight");
		dijkstra.init(graph);

		dijkstra.setSource(graph.getNode("0," + (this.matrix.rows() - x_start)));
		dijkstra.compute();

		DijkstraPath = dijkstra.getPath(graph.getNode("" + (this.width - 1) + "," + (this.matrix.rows() - x_end)));
		/*for (Edge e : path.getEdgeSet()){
			e.addAttribute("ui.style", "arrow-shape: none; fill-color: red;");
		}*/

		for (Node node : DijkstraPath.getNodeSet()){
			node.addAttribute("ui.style", "fill-color: blue;");
			node.addAttribute("ui.style", " size: 5px, 5px;");
		}

		this.dijkstraAllPaths.add(DijkstraPath.getNodePath());

		//	System.out.println(path.getNodePath());

	}



	private AStar astar;
	public void findAllShortestPathAstar(){


		/*for (int i = 10; i <= 50; i = i + 3){
			findShortestPathDijkstra(i, i);
		}*/

		findShortestPathAStar(10, 5);
	}


	private Path AStarPath;

	public void findShortestPathAStar(int x_start, int x_end){
		astarAllPaths.clear();
		astar = new AStar(this.graph);

		astar.compute("0," + (this.matrix.rows() - x_start), "" + (this.width - 1) + "," + (this.matrix.rows() - x_end));
		AStarPath = astar.getShortestPath();

		for (Node node : AStarPath.getNodeSet()){
			node.addAttribute("ui.style", "fill-color: red;");
			node.addAttribute("ui.style", " size: 5px, 5px;");
		}

		this.astarAllPaths.add(AStarPath.getNodePath());
	}


	public List<List<Node>> getDijkstraAllPaths(){
		return this.dijkstraAllPaths;
	}

	public List<List<Node>> getAstarAllPaths(){
		return this.astarAllPaths;
	}



	public Mat getImageMatrix(){
		return this.matrix;
	}

	private Viewer viewer;

	public void display(){
		viewer = graph.display();
		
		viewer.disableAutoLayout();
	}
	
	public void clear(){
		viewer.close();
		/*for (Node node : AStarPath.getNodeSet()){
			node.addAttribute("ui.style", "fill-color: black;");
			node.addAttribute("ui.style", " size: 2px, 2px;");
		}
		
		for (Node node : DijkstraPath.getNodeSet()){
			node.addAttribute("ui.style", "fill-color: black;");
			node.addAttribute("ui.style", " size: 2px, 2px;");
		}*/

	}

	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		ImageReader imageReader = new ImageReader(Test.fileName);
		Mat origMat = imageReader.getImageMatrix();
		ImageProcessor imageProcessor = new ImageProcessor(origMat);

		imageProcessor.preprocess();

		ImageShowUtil.show(imageProcessor.getImageMatrix(), "preprocessed image");

		ImageGraph graph = new ImageGraph(imageProcessor.getTemp());
		
		long a = System.currentTimeMillis();
		graph.display();
		graph.findShortestPathAStar(58, 50);
		System.out.print(System.currentTimeMillis() - a);
		a = System.currentTimeMillis();
		graph.findShortestPathDijkstra(58, 50);
		System.out.print("   " + (System.currentTimeMillis() - a));
		//graph.findAllShortestPathDijkstra();

		System.out.println("Choroid");

		//imageProcessor.markLayer(graph.getDijkstraAllPaths());


		imageProcessor.markLayer(graph.getDijkstraAllPaths(), imageProcessor.getTemp());
		imageProcessor.markLayer(graph.getDijkstraAllPaths(), imageProcessor.getOriginalMatrix());

		ImageShowUtil.show(imageProcessor.getTemp(), "segmented image 1");

		imageProcessor.removeLayer(imageProcessor.getSecondGradientImageMatrix(), graph.getDijkstraAllPaths().get(0), 1);
		
		imageProcessor.removeLayer(imageProcessor.getGradientImageMatrix(), graph.getDijkstraAllPaths().get(0), 1);
			
		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "remove layer image 1");
		ImageShowUtil.show(imageProcessor.getGradientImageMatrix(), "remove layer image 11");

		

		try {
			graph.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		
		
		ImageGraph graph2 = new ImageGraph(imageProcessor.getSecondGradientImageMatrix());
		
		long a2 = System.currentTimeMillis();
		//graph2.display();
		graph2.findShortestPathAStar(15, 5);
		System.out.print(System.currentTimeMillis() - a2);
		a2 = System.currentTimeMillis();
		graph2.findShortestPathDijkstra(15, 5);
		System.out.print("   " + (System.currentTimeMillis() - a2));

		System.out.println("Vitreous");

		
		
		imageProcessor.markLayer(graph2.getDijkstraAllPaths(), imageProcessor.getSecondGradientImageMatrix());
		imageProcessor.markLayer(graph2.getDijkstraAllPaths(), imageProcessor.getOriginalMatrix());

		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "segmented image 2");

		imageProcessor.removeLayer(imageProcessor.getSecondGradientImageMatrix(), graph2.getDijkstraAllPaths().get(0), 1);

		imageProcessor.removeLayer(imageProcessor.getGradientImageMatrix(), graph2.getDijkstraAllPaths().get(0), 1);
		
		
		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "remove layer image 2 ");
		ImageShowUtil.show(imageProcessor.getGradientImageMatrix(), "remove layer image 22");

		

		try {
			graph2.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		ImageGraph graph3 = new ImageGraph(imageProcessor.getSecondGradientImageMatrix());
		//graph3.display();
		long a3 = System.currentTimeMillis();
		graph3.findShortestPathAStar(20, 15);
		System.out.print(System.currentTimeMillis() - a3);
		a3 = System.currentTimeMillis();
		graph3.findShortestPathDijkstra(20, 15);
		System.out.print("   " + (System.currentTimeMillis() - a3));


		System.out.println("NFL");
		

		imageProcessor.markLayer(graph3.getDijkstraAllPaths(), imageProcessor.getSecondGradientImageMatrix());
		imageProcessor.markLayer(graph3.getDijkstraAllPaths(), imageProcessor.getOriginalMatrix());



		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "segmented image 3");

		imageProcessor.removeLayer(imageProcessor.getSecondGradientImageMatrix(), graph3.getDijkstraAllPaths().get(0), 2);

		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "remove layer image 3 ");

		try {
			graph3.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		ImageGraph graph4 = new ImageGraph(imageProcessor.getGradientImageMatrix());
		//graph4.display();
		long a4 = System.currentTimeMillis();
		graph4.findShortestPathAStar(30, 20);
		System.out.print(System.currentTimeMillis() - a4);
		a4 = System.currentTimeMillis();
		graph4.findShortestPathDijkstra(30, 20);
		System.out.print("   " + (System.currentTimeMillis() - a4));


		System.out.println("GCL-IPL");
		
		
		imageProcessor.markLayer(graph4.getDijkstraAllPaths(), imageProcessor.getGradientImageMatrix());
		imageProcessor.markLayer(graph4.getDijkstraAllPaths(), imageProcessor.getOriginalMatrix());



		ImageShowUtil.show(imageProcessor.getGradientImageMatrix(), "segmented image 4");

		imageProcessor.removeLayer(imageProcessor.getGradientImageMatrix(), graph4.getDijkstraAllPaths().get(0), 1);

		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "remove layer image 4 ");

		
		try {
			graph4.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


		ImageGraph graph5 = new ImageGraph(imageProcessor.getSecondGradientImageMatrix());
		//graph5.display();
		long a5 = System.currentTimeMillis();
		graph5.findShortestPathAStar(35, 20);
		System.out.print(System.currentTimeMillis() - a5);
		a5 = System.currentTimeMillis();
		graph5.findShortestPathDijkstra(35, 20);
		System.out.print("   " + (System.currentTimeMillis() - a5));


		System.out.println("INL");
		
		imageProcessor.markLayer(graph5.getDijkstraAllPaths(), imageProcessor.getSecondGradientImageMatrix());
		imageProcessor.markLayer(graph5.getDijkstraAllPaths(), imageProcessor.getOriginalMatrix());



		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "segmented image 5");

		imageProcessor.removeLayer(imageProcessor.getSecondGradientImageMatrix(), graph5.getDijkstraAllPaths().get(0), 1);

		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "remove layer image 5 ");


		try {
			graph5.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		ImageGraph graph6 = new ImageGraph(imageProcessor.getSecondGradientImageMatrix());
		
		long a6 = System.currentTimeMillis();
		graph6.findShortestPathAStar(45, 40);
		System.out.print(System.currentTimeMillis() - a6);
		a6 = System.currentTimeMillis();
		graph6.findShortestPathDijkstra(45, 40);
		System.out.print("   " + (System.currentTimeMillis() - a6));

		System.out.println("ONL-IS");
		

		imageProcessor.markLayer(graph6.getDijkstraAllPaths(), imageProcessor.getSecondGradientImageMatrix());
		imageProcessor.markLayer(graph6.getDijkstraAllPaths(), imageProcessor.getOriginalMatrix());



		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "segmented image 6");

		imageProcessor.removeLayer(imageProcessor.getSecondGradientImageMatrix(), graph6.getDijkstraAllPaths().get(0), 1);

		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "remove layer image 6 ");

		try {
			graph6.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		


		ImageGraph graph7 = new ImageGraph(imageProcessor.getSecondGradientImageMatrix());
	
		long a7 = System.currentTimeMillis();
		graph7.findShortestPathAStar(40, 30);
		System.out.print(System.currentTimeMillis() - a7);
		a7 = System.currentTimeMillis();
		graph7.findShortestPathDijkstra(40, 30);
		System.out.print("   " + (System.currentTimeMillis() - a7));

		System.out.println("OPL1");
		
		
		imageProcessor.markLayer(graph7.getDijkstraAllPaths(), imageProcessor.getSecondGradientImageMatrix());
		//imageProcessor.markLayer(graph7.getDijkstraAllPaths(), imageProcessor.getOriginalMatrix());



		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "segmented image 7");

		imageProcessor.removeLayer(imageProcessor.getSecondGradientImageMatrix(), graph7.getDijkstraAllPaths().get(0), 7);

		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "remove layer image 7 ");


		try {
			graph7.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		ImageGraph graph8 = new ImageGraph(imageProcessor.getSecondGradientImageMatrix());

		long a8 = System.currentTimeMillis();
		graph8.findShortestPathAStar(40, 30);
		System.out.print(System.currentTimeMillis() - a8);
		a8 = System.currentTimeMillis();
		graph8.findShortestPathDijkstra(40, 30);
		System.out.print("   " + (System.currentTimeMillis() - a8));
		
		System.out.println("OPL2");

		imageProcessor.markLayer(graph8.getDijkstraAllPaths(), imageProcessor.getSecondGradientImageMatrix());
		imageProcessor.markLayer(graph8.getDijkstraAllPaths(), imageProcessor.getOriginalMatrix());

	
		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "segmented image 8");

		imageProcessor.removeLayer(imageProcessor.getSecondGradientImageMatrix(), graph8.getDijkstraAllPaths().get(0), 1);

		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "remove layer image 8 ");

		
		
		long a9 = System.currentTimeMillis();
		graph8.findShortestPathAStar(55, 43);
		System.out.print(System.currentTimeMillis() - a9);
		a9 = System.currentTimeMillis();
		graph8.findShortestPathDijkstra(55, 43);
		System.out.print("   " + (System.currentTimeMillis() - a9));

		System.out.println("OS");

		imageProcessor.markLayer(graph8.getDijkstraAllPaths(), imageProcessor.getSecondGradientImageMatrix());
		imageProcessor.markLayer(graph8.getDijkstraAllPaths(), imageProcessor.getOriginalMatrix());
		
		ImageShowUtil.show(imageProcessor.getSecondGradientImageMatrix(), "segmented image 9");
		
	
		
		try {
			graph8.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ImageShowUtil.show(imageProcessor.getOriginalMatrix(), "result ");

	}

}
