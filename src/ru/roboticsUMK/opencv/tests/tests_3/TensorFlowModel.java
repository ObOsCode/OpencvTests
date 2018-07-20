package ru.roboticsUMK.opencv.tests.tests_3;

import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;

import ru.roboticsUMK.opencv.Camera;
import ru.roboticsUMK.opencv.ImageWindow;
import ru.roboticsUMK.opencv.OpenCVTestBase;

public class TensorFlowModel extends OpenCVTestBase
{

	private static final long serialVersionUID = 6182756456938333993L;
	private Camera _camera;
	private Net _net;
	private ImageWindow _window;
	private String[] _objectClasses;
	
	private final String MODEL_FILE = "/home/user/code/keras_to_tensorflow/dogs_vs_cats_model.h5.pb";
	
	private final String CONFIG_FILE = "/home/user/code/tensorFlowImageClassification/target/classes/models/inception5h/imagenet_comp_graph_label_strings.txt";
	
	
	public TensorFlowModel()
	{
		super("TensorFLow model test");
		
		_camera = new Camera(0);

		_window = new ImageWindow("Camera");
		_window.setSize(1024, 700);
		
		_net = Dnn.readNetFromTensorflow(MODEL_FILE);
		
		
	}

}
