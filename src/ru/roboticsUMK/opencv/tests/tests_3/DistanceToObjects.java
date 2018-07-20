package ru.roboticsUMK.opencv.tests.tests_3;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JCheckBox;

import org.opencv.calib3d.Calib3d;
import org.opencv.calib3d.StereoBM;
import org.opencv.calib3d.StereoMatcher;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import ru.roboticsUMK.Utils;
import ru.roboticsUMK.opencv.Camera;
import ru.roboticsUMK.opencv.ConfigLoader;
import ru.roboticsUMK.opencv.ImageWindow;
import ru.roboticsUMK.opencv.ObjectDetector;
import ru.roboticsUMK.opencv.OpenCVTestBase;

public class DistanceToObjects extends OpenCVTestBase
{

	private static final long serialVersionUID = -3501020220818216655L;
	
//	private final double _COMPUTE_DISTANCE_KOEF = 15.384615385;
	
	private Camera _leftCamera;
	private Camera _rightCamera;
	
	private StereoMatcher _stereo;

	private ObjectDetector _rouletteDetector;
	private ObjectDetector _appleDetector;
	
	private final Scalar _DRAW_ROULETTE_COLOR = new Scalar(0, 200, 200);
	private final Scalar _DRAW_APPLE_COLOR = new Scalar(0, 0, 200);

	private ImageWindow _originalWindow;

	private ImageWindow _disparityWindow;
	
	private boolean _isDetectApple = false;
	private boolean _isDetectRoulette = false;
	

	public DistanceToObjects()
	{
		
		super("Distance to...");
		
		addCheckBox("Detect apple").addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				_isDetectApple = ((JCheckBox)e.getSource()).isSelected();
			}
		});
		
		addCheckBox("Detect roul").addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				_isDetectRoulette = ((JCheckBox)e.getSource()).isSelected();
			}
		});
		
		_leftCamera = Camera.create(Camera.LEFT_CAMERA_IP);
		_rightCamera = Camera.create(Camera.RIGHT_CAMERA_IP);
		
		super.configLoader.loadStereoCalibrationConfig(ConfigLoader.STEREO_CALIBRATION_FILE);

		_leftCamera.calibrateStereo(Camera.Stereo.m1, Camera.Stereo.d1, Camera.Stereo.r1, Camera.Stereo.p1);
		_rightCamera.calibrateStereo(Camera.Stereo.m2, Camera.Stereo.d2, Camera.Stereo.r2, Camera.Stereo.p2);
		
		_stereo = StereoBM.create();
		
		if(new File(ConfigLoader.STEREO_BM_CONFIG_FILE).exists())
		{
			super.configLoader.loadStereoMatcherConfig(_stereo, ConfigLoader.STEREO_BM_CONFIG_FILE);
		}
		
		String rouletteCascade = "/home/user/Изображения/DataSets/Roulette/cascade_roulette(p2000n4500).xml";
		String appleCascade = "/home/user/Изображения/DataSets/Apple/3/classifier/cascade.xml";
		
		_rouletteDetector = new ObjectDetector(rouletteCascade);
		_appleDetector = new ObjectDetector(appleCascade);
		
		_originalWindow = new ImageWindow("Left camera");
		_disparityWindow = new ImageWindow("Depth map");
		
		startLoop();
	}
	
	
	@Override
	protected void loop()
	{
		Mat frameLeft = _leftCamera.getNextFrameCalibrateStereo();
		Mat frameRight = _rightCamera.getNextFrameCalibrateStereo();
		
		if(!frameLeft.empty() && !frameRight.empty())
		{
			Mat disparity = computeDisparity(frameLeft, frameRight);
			
			double distance;
			Point objectPoint;
			
			if(_isDetectApple)
			{
				Rect appleRect = _appleDetector.getObjectRect(frameLeft);
				
				if(appleRect != null)
				{
					objectPoint = new Point(appleRect.x + appleRect.width/2, appleRect.y + appleRect.height/2);
					
					distance = getDistanceToPoint(objectPoint, disparity);
					
					drawObjectPositionAndDistance(distance, appleRect, frameLeft, _DRAW_APPLE_COLOR);
				}
			}
			
			if(_isDetectRoulette)
			{
				Rect rouletteRect = _rouletteDetector.getObjectRect(frameLeft);
				
				if (rouletteRect != null)
				{
					objectPoint = new Point(rouletteRect.x + rouletteRect.width/2, rouletteRect.y + rouletteRect.height/2);
					
					distance = getDistanceToPoint(objectPoint, disparity);
					
					drawObjectPositionAndDistance(distance, rouletteRect, frameLeft, _DRAW_ROULETTE_COLOR);
				}
			}
			
			Core.normalize(disparity, disparity, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);
			
			_originalWindow.showFrame(frameLeft);
			_disparityWindow.showFrame(disparity);
			
			disparity.release();
			
		}
		
		frameLeft.release();
		frameRight.release();
	}
	
	
	private Mat computeDisparity(Mat frameLeft, Mat frameRight)
	{
		Mat frameLeftGray = new Mat();
		Mat frameRightGray = new Mat();
		
		Imgproc.cvtColor(frameLeft, frameLeftGray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(frameRight, frameRightGray, Imgproc.COLOR_BGR2GRAY);
		
		Mat disparity = new Mat();
		_stereo.compute(frameLeftGray, frameRightGray, disparity);
		
		frameLeftGray.release();
		frameRightGray.release();
		
		return disparity;
	}
	
	
	private double getDistanceToPoint(Point point, Mat disparity)
	{
		Mat image3D = new Mat();
		Calib3d.reprojectImageTo3D(disparity, image3D, Camera.Stereo.q, false);
		double[] xyz = image3D.get((int)point.y, (int)point.x);
		
		double x = xyz[0];
		double y = xyz[1];
		double z = xyz[2];
		
		double distance = super._COMPUTE_DISTANCE_KOEF * (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)));
		
		return distance;
	}
	
	
	private void drawObjectPositionAndDistance(double distance, Rect rect, Mat frame, Scalar color)
	{
		Scalar drawColor = color;
		int lineThikness = 4;
		Point centerPoint = new Point(rect.x + rect.width/2, rect.y + rect.height/2);
		
		Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), drawColor, lineThikness);  
		
		Imgproc.circle(frame, centerPoint, 10, drawColor, lineThikness);
		
		Point textPoint = new Point();
		textPoint.y = Math.max(50, centerPoint.y - 50);
		textPoint.x = Math.min(centerPoint.x + 50, frame.width() - 400);
		double textScale = 1.5;
		
		Imgproc.putText(frame, Double.toString(Utils.round(distance, 2)) + "(m)", textPoint, Core.FONT_ITALIC, textScale, color, lineThikness);
	}

}
