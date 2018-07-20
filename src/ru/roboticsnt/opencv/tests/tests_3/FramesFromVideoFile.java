package ru.roboticsnt.opencv.tests.tests_3;

import javax.swing.JSlider;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import ru.roboticsnt.opencv.Camera;
import ru.roboticsnt.opencv.gui.swing.ImageWindow;
import ru.roboticsnt.opencv.gui.swing.OpenCVTestBase;


public class FramesFromVideoFile extends OpenCVTestBase
{

	private static final long serialVersionUID = 2718658866670789302L;
	
	private final String _FILE_PATH_1 = "/home/user/DataSets/22.02.2018/Кабачек/video1.avi";
	private final String _FILE_PATH_2 = "/home/user/DataSets/22.02.2018/Кабачек/video2.avi";
	private final String _FILE_PATH_3 = "/home/user/DataSets/22.02.2018/Кабачек/video3.avi";
	private final String _FILE_PATH_4 = "/home/user/DataSets/22.02.2018/Кабачек/video4.avi";
	
	private String[] _videoLinks;
	private int _currentVideoIndex = 0;
	
	private Camera _video;
	private ImageWindow _window;

	private JSlider _minThreshSlider;
	private JSlider _maxThreshSlider;
	private JSlider _cannyThreshold1Slider;
	private JSlider _cannyThreshold2Slider;
	private JSlider _blurSlider;
	
	private JSlider _rMinSlider;
	private JSlider _rMaxSlider;
	private JSlider _gMinSlider;
	private JSlider _gMaxSlider;
	private JSlider _bMinSlider;
	private JSlider _bMaxSlider;
	
	
	public FramesFromVideoFile()
	{
		super("Frames from video");
		
		_videoLinks = new String[]{_FILE_PATH_1, _FILE_PATH_2, _FILE_PATH_3, _FILE_PATH_4};
		
//		_video = Camera.create(Camera.LEFT_CAMERA_IP);
		
		_window = new ImageWindow("Video");
		
		_minThreshSlider = addSlider("Thresh min", 0, 255, 0);
		_maxThreshSlider = addSlider("Thresh max", 0, 255, 255);
		_cannyThreshold1Slider = addSlider("Canny thresh 1", 0, 255, 0);
		_cannyThreshold2Slider = addSlider("Canny thresh 2", 0, 255, 255);
		
		_blurSlider = addSlider("Blur", 1, 100, 5);
		
		_rMinSlider = addSlider("Red min", 0, 255, 0);
		_rMaxSlider = addSlider("Red max", 0, 255, 255);
		_gMinSlider = addSlider("Green min", 0, 255, 0);
		_gMaxSlider = addSlider("Green max", 0, 255, 255);
		_bMinSlider = addSlider("Blue min", 0, 255, 0);
		_bMaxSlider = addSlider("Blue max", 0, 255, 255);
		
		playVideo();
		
		startLoop();
	}
	
	
	private void playVideo()
	{
		if(_video != null)
		{
			_video.release();
		}
		
		_video = new Camera(_videoLinks[_currentVideoIndex]);
		
		if(_currentVideoIndex < (_videoLinks.length - 1))
		{			
			_currentVideoIndex++;
		}else
		{
			_currentVideoIndex = 0;
		}
	}
	
	
	@Override
	protected void loop()
	{
		super.loop();
		
		Mat frame = _video.getNextFrame();
		
		if(!frame.empty())
		{	
			detectByColor(frame);
			
//			detectByContur(frame);
        	
        	_window.showFrame(frame);
        	
    		frame.release();
		}else
		{
			playVideo();
		}
	}
	
	
	private void detectByContur(Mat frame)
	{
		int blur = _blurSlider.getValue();
		int minThresh = _minThreshSlider.getValue();
		int maxThresh = _maxThreshSlider.getValue();
		int cannyThresh1 = _cannyThreshold1Slider.getValue();
		int cannyThresh2 = _cannyThreshold2Slider.getValue();
		
		Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
		
		Imgproc.blur(frame, frame, new Size(blur, blur));
		
		Imgproc.threshold(frame, frame, minThresh, maxThresh, Imgproc.THRESH_BINARY);
		
    	Imgproc.Canny(frame, frame, cannyThresh1, cannyThresh2, 3, true);
    	
    	Mat lines = new Mat();
    	
    	Imgproc.HoughLinesP(frame, lines, 1, Math.PI/180, 70,100,10);
    	
    	for (int i = 0; i < lines.cols(); i++)
		{
    		double[] vec = lines.get(0, i);
    		
    		
    		if(vec==null)
    		{
    			break;
    		}
    		
            double x1 = vec[0];
            double y1 = vec[1];
            double x2 = vec[2];
            double y2 = vec[3];
        	
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            
            Imgproc.line(frame, start, end, new Scalar(255,255,0),5);
		}
    	
    	lines.release();
	}
	
	
	private void detectByColor(Mat frame)
	{
//    	Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);
		
		int blur = _blurSlider.getValue();
		
		Imgproc.blur(frame, frame, new Size(blur, blur));
    	
    	int rMin = _rMinSlider.getValue();
    	int gMin = _gMinSlider.getValue();
    	int bMin = _bMinSlider.getValue();
    	int rMax = _rMaxSlider.getValue();
    	int gMax = _gMaxSlider.getValue();
    	int bMax = _bMaxSlider.getValue();
    	
    	//Custom
    	Core.inRange(frame, new Scalar(rMin, gMin, bMin), new Scalar(rMax, gMax, bMax), frame);
	}

}
