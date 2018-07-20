package ru.roboticsUMK.opencv.tests.tests_3;

import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import ru.roboticsUMK.Utils;
import ru.roboticsUMK.opencv.Camera;
import ru.roboticsUMK.opencv.ImageWindow;
import ru.roboticsUMK.opencv.OpenCVTestBase;

public class CaffeModel extends OpenCVTestBase
{

	private static final long serialVersionUID = -6462597935326681160L;
	private Camera _camera;
	private Net _net;
	private ImageWindow _window;
	private String[] _objectClasses;
	
	private final int _BLOB_SIZE = 300;
	
	private final double _CONFIDENSE_THRESHOLD = 0.6;
	
	private Scalar[] _colors;
	

	public CaffeModel()
	{
		super("Neural network model");
		
//		_camera = Camera.create(Camera.LEFT_CAMERA_IP);
		_camera = new Camera(0);

		_window = new ImageWindow("Camera");
		_window.setSize(1024, 700);
		
		_objectClasses = new String[] {"background", "aeroplane", "bicycle", "bird", "boat",
				"bottle", "bus", "car", "cat", "chair", "cow", "table",
				"dog", "horse", "motorbike", "person", "pottedplant", "sheep",
				"sofa", "train", "tvmonitor"};
		
//		_objectClasses = new String[] {"Фон", "Самолет", "Велосипед", "Птица", "Лодка",
//				"Бутылка", "Автобус", "Автомобиль", "Кот", "Стул", "Корова", "Стол",
//				"Собака", "Лошадь", "Мотоцикл", "Человек", "Сажены", "Овца",
//				"Диван", "Поезд", "Монитор"};
		
		_colors = new Scalar[_objectClasses.length];
		
		for (int i = 0; i < _objectClasses.length; i++)
		{
			Random rand = new Random();
			
			double r = rand.nextDouble() * 255;
			double g = rand.nextDouble() * 255;
			double b = rand.nextDouble() * 255;
			
			_colors[i] = new Scalar(b, g, r);
		}
		
//	    String prototxt = "/home/user/projects/eclipse/OpenCVStart/resources/dnn/models/cnn_age_gender_models_and_data.0.0.2/deploy_gender.prototxt";
//	    String caffeModel = "/home/user/projects/eclipse/OpenCVStart/resources/dnn/models/cnn_age_gender_models_and_data.0.0.2/gender_net.caffemodel";
//	    String prototxt = "/home/user/projects/eclipse/OpenCVStart/resources/dnn/models/cnn_age_gender_models_and_data.0.0.2/deploy_age.prototxt";
//	    String caffeModel = "/home/user/projects/eclipse/OpenCVStart/resources/dnn/models/cnn_age_gender_models_and_data.0.0.2/age_net.caffemodel";
	    String prototxt = "/home/user/projects/eclipse/OpenCVProjects/resources/models/caffe/object-detection-deep-learning/MobileNetSSD_deploy.prototxt.txt";
	    String caffeModel = "/home/user/projects/eclipse/OpenCVProjects/resources/models/caffe/object-detection-deep-learning/MobileNetSSD_deploy.caffemodel";
	    
	    _net = Dnn.readNetFromCaffe(prototxt, caffeModel);
	    
//	    configLoader.loadSingleCalibrationConfig(_camera, ConfigLoader.LEFT_CAMERA_CALIBRATION_FILE);
	    
		startLoop();
	}
	
	
	@Override
	protected void loop()
	{
		super.loop();
		
		Mat image;
		
		if(_camera.isCalibrarted())
		{
			image = _camera.getNextFrameUndistort();
		}else
		{
			image = _camera.getNextFrame();
		}
	    
	    if(!image.empty())
	    {
	    	Mat cropedImage = cropImage(image, new Size(_BLOB_SIZE, _BLOB_SIZE));
	    	
		    Mat detectResult = detectObjectsOnFrame(cropedImage);
		    
		    drawObjectsPositionOnFrame(image, detectResult);

		    _window.showFrame(image);
	    }
	    
	    image.release();
	}
	
	
	private double xOnCropToXOnFull(double x)
	{
		Size fullSize = _camera.getFrameSize();
		
		double scale = fullSize.height/_BLOB_SIZE;
		
		double newX = (fullSize.width - _BLOB_SIZE * scale)/2 + x * scale;
		
		return newX;
	}
	
	
	private Mat detectObjectsOnFrame(Mat frame)
	{
		Mat detectResult = new Mat();
		
		Mat frameCopy = frame.clone();
		
    	Mat inputBlob = Dnn.blobFromImage(frameCopy, 0.007843, new Size(_BLOB_SIZE, _BLOB_SIZE), new Scalar(127.5), false, false);
    	
    	_net.setInput(inputBlob);
    	
    	detectResult = _net.forward();
    	
    	detectResult = detectResult.reshape(1, (int) (detectResult.total()/7));

		return detectResult;
	}
	
	
	private void drawObjectsPositionOnFrame(Mat frame, Mat result)
	{
        int rows = frame.rows();
    	
    	 for (int i = 0; i < result.rows(); ++i) 
    	 {
    		 double confidence = result.get(i, 2)[0];
    		 
    		 if(confidence > _CONFIDENSE_THRESHOLD)
    		 {
        		 int classId = (int)result.get(i, 1)[0];
                 
                 int xLeftBottom = (int)xOnCropToXOnFull(result.get(i, 3)[0] * _BLOB_SIZE);
                 int yLeftBottom = (int)(result.get(i, 4)[0] * rows);
                 int xRightTop   = (int)xOnCropToXOnFull(result.get(i, 5)[0] * _BLOB_SIZE);
                 int yRightTop   = (int)(result.get(i, 6)[0] * rows);
                 
                 Scalar color = _colors[classId];
                 String text = _objectClasses[classId] + " " + Utils.round(confidence * 100, 2) + "%";
                 Point textPoint = new Point(xLeftBottom, yLeftBottom - 10);
                 
                 if(textPoint.y < 0)
                 {
                	 textPoint.y+=80;
                 }
                 
                 int thickness = 2;
                 
                 Imgproc.rectangle(frame, new Point(xLeftBottom, yLeftBottom), new Point(xRightTop, yRightTop), color, thickness);
                 
                 Imgproc.putText(frame, text, textPoint, Core.FONT_HERSHEY_SIMPLEX, 1.5, color, thickness);
    		 }
    	 }
	}

}
