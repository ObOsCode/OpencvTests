package ru.roboticsnt.opencv.tests.tests_3;

import javax.swing.JSlider;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import ru.roboticsnt.opencv.Camera;
import ru.roboticsnt.opencv.gui.swing.ImageWindow;
import ru.roboticsnt.opencv.gui.swing.OpenCVTestBase;


public class HaarCascadeClassifier extends OpenCVTestBase
{
	
	private static final long serialVersionUID = 1864516923425069928L;
	private Camera _camera;
	private ImageWindow _window;
	private CascadeClassifier _detector;
	private JSlider _scaleFactorSlider;
	private JSlider _minNeighborsSlider;
	private JSlider _minSizeWidthSlider;
	private JSlider _minSizeHeightSlider;
	private JSlider _maxSizeWidthSlider;
	private JSlider _maxSizeHeightSlider;
	private JSlider _frameWidthSlider;
	
	
	public HaarCascadeClassifier()
	{
		super("Detect face.");
		
		_window = new ImageWindow("Camera");
		
		_scaleFactorSlider = addSlider("Scale factor", 101, 200, 105);
		_minNeighborsSlider = addSlider("Min neighbors", 1, 10, 5);
		_minSizeWidthSlider = addSlider("Min size width", 20, 1000, 30);
		_minSizeHeightSlider = addSlider("Min size height", 20, 1000, 30);
		_maxSizeWidthSlider = addSlider("Max size width", 10, 1000, 600);
		_maxSizeHeightSlider = addSlider("Max size height", 10, 1000, 600);
		_frameWidthSlider = addSlider("Frame width", 100, 1280, 480);
		
		_camera = new Camera(1);
//		_camera = Camera.create(Camera.LEFT_CAMERA_IP);
		
		String cascadePath = "/home/user/libs/openCV/3.4.1/opencv-3.4.1/data/haarcascades/haarcascade_frontalface_default.xml";
//		String cascadePath = "/home/user/libs/openCV/3.4.1/opencv-3.4.1/data/haarcascades/haarcascade_eye.xml";
//		String cascadePath = "/home/user/libs/openCV/3.4.1/opencv-3.4.1/data/haarcascades/haarcascade_smile.xml";
		
		_detector = new CascadeClassifier(cascadePath);
		
		startLoop();
	}
	
	
	@Override
	protected void loop()
	{
		super.loop();
		
		Mat frame;
		
		if(_camera.isCalibrated())
		{
			frame = _camera.getNextFrameUndistort();
		}else
		{
			frame = _camera.getNextFrame();     
		}
		
		if(!frame.empty())
		{
			int newWidth = _frameWidthSlider.getValue();
			int newHeight = (int)(frame.height() * (double)newWidth/frame.width());
			
			Size frameNewSize = new Size(newWidth, newHeight);
			
			Imgproc.resize(frame, frame, frameNewSize);
			
			MatOfRect detectionRects = new MatOfRect();  
			
			int flags = Objdetect.CASCADE_SCALE_IMAGE;
			double scaleFactor = _scaleFactorSlider.getValue() / 100.00;
			int minNeighbors = _minNeighborsSlider.getValue();
			Size minSize = new Size(_minSizeWidthSlider.getValue(), _minSizeHeightSlider.getValue());
			Size maxSize = new Size(_maxSizeWidthSlider.getValue(), _maxSizeHeightSlider.getValue());
			
			_detector.detectMultiScale(frame, detectionRects, scaleFactor, minNeighbors, flags, minSize, maxSize);
			
			for (Rect rect : detectionRects.toArray()) 
			{
				Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), 5);  
			}
			
			_window.showFrame(frame);
			
			detectionRects.release();
			frame.release();
		}
	}

}
