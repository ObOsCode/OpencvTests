package ru.roboticsUMK.opencv.tests.tests_2_0;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JSlider;

import org.opencv.calib3d.Calib3d;
import org.opencv.calib3d.StereoBM;
import org.opencv.calib3d.StereoMatcher;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import ru.roboticsUMK.Utils;
import ru.roboticsUMK.opencv.Camera;
import ru.roboticsUMK.opencv.ConfigLoader;
import ru.roboticsUMK.opencv.ImageWindow;
import ru.roboticsUMK.opencv.OpenCVTestBase;
import ru.roboticsUMK.opencv.gui.StereoMatcherPanel;


public class DetectObjectAndCalcDistance extends OpenCVTestBase
{
	private static final long serialVersionUID = 8775149604238396669L;
	
	private ImageWindow _windowLeft;
	private ImageWindow _windowRight;
	private ImageWindow _windowDisparity;
	private Camera _cameraLeft;
	private Camera _cameraRight;

	private CascadeClassifier _detector;
	
	private Mat _mapx1;
	private Mat _mapy1;
	private Mat _mapx2;
	private Mat _mapy2;

	private StereoMatcher _stereo;
	
	private JSlider _blurSLider;

	private StereoMatcherPanel _matcherPanel;

	
	public DetectObjectAndCalcDistance()
	{
		super.configLoader.loadStereoCalibrationConfig(ConfigLoader.STEREO_CALIBRATION_FILE);
		
//		_stereo = StereoSGBM.create();
		_stereo = StereoBM.create();
		
		_matcherPanel = addStereoMatcherPanel();
		
		_matcherPanel.getSaveButton().addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				configLoader.saveStereoMatcherConfig(_stereo, ConfigLoader.STEREO_BM_CONFIG_FILE);			
			}
		});
		
		_matcherPanel.getLoadButton().addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				configLoader.loadStereoMatcherConfig(_stereo, ConfigLoader.STEREO_BM_CONFIG_FILE);			
				_matcherPanel.setValues(_stereo);
			}
		});
		
		_blurSLider = super.addSlider("Blur", 1, 200, 3);
		
		if(new File(ConfigLoader.STEREO_BM_CONFIG_FILE).exists())
		{
			super.configLoader.loadStereoMatcherConfig(_stereo, ConfigLoader.STEREO_BM_CONFIG_FILE);
			_matcherPanel.setValues(_stereo);
		}
		
		_windowLeft = new ImageWindow("Left camera");
		_windowRight = new ImageWindow("Right camera");
		_windowDisparity = new ImageWindow("Disparity");
		
		_cameraLeft = Camera.create(Camera.LEFT_CAMERA_IP);
		_cameraRight = Camera.create(Camera.RIGHT_CAMERA_IP);
		
//		String cascadePath = "/home/user/Изображения/detectObjectImages/Roulette/cascade_roulette(p2000n4500).xml";
		String cascadePath = "/home/user/Изображения/detectObjectImages/Apple/3/classifier/cascade.xml";
		_detector = new CascadeClassifier(cascadePath); 
		
		_mapx1 = new Mat();
		_mapy1 = new Mat();
		_mapx2 = new Mat();
		_mapy2 = new Mat();
		
		Size frameSize = _cameraLeft.getFrameSize();
		Imgproc.initUndistortRectifyMap(Camera.Stereo.m1, Camera.Stereo.d1, Camera.Stereo.r1, Camera.Stereo.p1, frameSize, CvType.CV_32FC1, _mapx1, _mapy1);
		Imgproc.initUndistortRectifyMap(Camera.Stereo.m2, Camera.Stereo.d2, Camera.Stereo.r2, Camera.Stereo.p2, frameSize, CvType.CV_32FC1, _mapx2, _mapy2);
		
		startLoop();
	}
	
	
	@Override
	protected void loop()
	{
		Mat frameLeft = _cameraLeft.getNextFrame();
		Mat frameRight = _cameraRight.getNextFrame();
		
		if(!frameLeft.empty() && !frameRight.empty())
		{
			Imgproc.remap(frameLeft, frameLeft, _mapx1, _mapy1, Imgproc.INTER_LINEAR);
			Imgproc.remap(frameRight, frameRight, _mapx2, _mapy2, Imgproc.INTER_LINEAR);
			
			int blurSize = _blurSLider.getValue();
			Imgproc.blur(frameLeft, frameLeft, new Size(blurSize, blurSize));
			Imgproc.blur(frameRight, frameRight, new Size(blurSize, blurSize));
			
			_matcherPanel.getValues(_stereo);
			
			Mat frameLeftGray = new Mat();
			Mat frameRightGray = new Mat();
			
			Imgproc.cvtColor(frameLeft, frameLeftGray, Imgproc.COLOR_BGR2GRAY);
			Imgproc.cvtColor(frameRight, frameRightGray, Imgproc.COLOR_BGR2GRAY);
			
			Mat disparity = new Mat();
			_stereo.compute(frameLeftGray, frameRightGray, disparity);
			
			Point objectPoint = getObjectPoint(frameLeft);
			
			if (objectPoint != null)
			{
				double distance = getDistanceToPoint(objectPoint, disparity);
				
				drawDistance(distance, objectPoint, frameLeft);
			}
			
			Core.normalize(disparity, disparity, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);
				
			_windowDisparity.showFrame(disparity);
			_windowLeft.showFrame(frameLeft);
			_windowRight.showFrame(frameRight);
			
			disparity.release();
		}
		
		frameLeft.release();
		frameRight.release();
	}
	
	
	private void drawDistance(double distance, Point objectPoint, Mat frame)
	{
		Point textPoint = new Point();
		textPoint.y = Math.max(100, objectPoint.y - 100);
		textPoint.x = Math.min(objectPoint.x + 100, frame.width() - 400);
		
		Imgproc.putText(frame, "Dist:" + Double.toString(Utils.round(distance, 2)) + "(m)", textPoint, Core.FONT_ITALIC, 2, new Scalar(50, 50, 200), 3);
	}
	
	
	private Point getObjectPoint(Mat frame)
	{
		MatOfRect detectionRects = new MatOfRect();  
		Scalar drawColor = new Scalar(0, 0, 255);
		int lineThikness = 4;
		
		_detector.detectMultiScale(frame, detectionRects);
		
		for (Rect rect : detectionRects.toArray()) 
		{
			Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), drawColor, lineThikness);  
			
			Point centerPoint = new Point(rect.x + rect.width/2, rect.y + rect.height/2);
			
			Imgproc.circle(frame, centerPoint, 10, drawColor, lineThikness);
			
			return centerPoint;
		}
		
		return null;
	}
	
	
	private double getDistanceToPoint(Point point, Mat disparity)
	{
		Mat image3D = new Mat();
		Calib3d.reprojectImageTo3D(disparity, image3D, Camera.Stereo.q, false);
		double[] xyz = image3D.get((int)point.y, (int)point.x);
		
		Core.normalize(disparity, disparity, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);
		
		double x = xyz[0];
		double y = xyz[1];
		double z = xyz[2];
		
		double distance = super._COMPUTE_DISTANCE_KOEF * (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)));
		
		return distance;
	}

}
