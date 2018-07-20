package ru.roboticsnt.opencv.tests.tests_2_0;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;

import org.opencv.calib3d.Calib3d;
import org.opencv.calib3d.StereoBM;
import org.opencv.calib3d.StereoMatcher;
import org.opencv.calib3d.StereoSGBM;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import ru.roboticsnt.opencv.Camera;
import ru.roboticsnt.opencv.ConfigLoader;
import ru.roboticsnt.opencv.gui.swing.ImageWindow;
import ru.roboticsnt.opencv.gui.swing.OpenCVTestBase;
import ru.roboticsnt.utils.MathUtils;


public class DistanceToPoint extends OpenCVTestBase
{

	private static final long serialVersionUID = 3579486583423556376L;

	private Camera _camera1;
	private Camera _camera2;
	private Mat _frameOriginal1;
	private Mat _frameOriginal2;
	private ImageWindow _windowOriginal1;
	private ImageWindow _windowOriginal2;
	private ImageWindow _windowDisparity;
	
	private StereoMatcher _stereo;
	private JSlider _preFilterSizeSlider;
	private JSlider _preFilterCapSlider;
	private JSlider _blockSizeSlider;
	private JSlider _minDisparitySlider;
	private JSlider _numDisparitiesSlider;
	private JSlider _textureThresholdSlider;
	private JSlider _uniquenessRatioSlider;
	private JSlider _speckleWindowSizeSlider;
	private JSlider _speckleRangeSlider;
	private JSlider _disp12maxDiffSlider;
	
	private JSlider _blurSLider;

	private Mat _mapx1;
	private Mat _mapy1;
	private Mat _mapx2;
	private Mat _mapy2;

	private Point _clickPoint = new Point(100, 100);
	
//	private final double _COMPUTE_DISTANCE_KOEF = 15.75221;
	
	
	public DistanceToPoint()
	{
		super("Distance to point");
		
		configLoader.loadStereoCalibrationConfig(ConfigLoader.STEREO_CALIBRATION_FILE);
		
//		_stereo = StereoSGBM.create();
		_stereo = StereoBM.create();
		
		_preFilterSizeSlider = super.addSlider("preFilterSize", 5, 255, 7);
		_preFilterCapSlider = super.addSlider("preFilterCap", 1, 63, 4);
		_blockSizeSlider = super.addSlider("blockSize", 5, 255, 5);
		_minDisparitySlider = super.addSlider("minDisparity", -128, 128, -64);
		_numDisparitiesSlider = super.addSlider("numDisparities", 16, 200, 192);
		_textureThresholdSlider = super.addSlider("textureThreshold", 0, 1000, 0);
		_uniquenessRatioSlider = super.addSlider("uniquenessRatio", 0, 512, 1);
		_speckleWindowSizeSlider = super.addSlider("speckleWindowSize", 0, 200, 150);
		_speckleRangeSlider = super.addSlider("speckleRange", 0, 100, 2);
		_disp12maxDiffSlider = super.addSlider("disp12maxDiff", 0, 100, 10);
		
		_blurSLider = super.addSlider("Blur", 1, 200, 3);
		
		_windowOriginal1 = new ImageWindow("Original left");
		_windowOriginal2 = new ImageWindow("Original right");
		_windowDisparity = new ImageWindow("Disparity");
		
		_windowOriginal1.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				Size origSize = _camera1.getFrameSize();
				Size realSize = new Size(_windowOriginal1.getImageWidth(), _windowOriginal2.getImageHeight());
				
				double sizeKoef = origSize.width / realSize.width;
				
				if (_windowOriginal1.getImageMousePosition() != null)
				{
					_clickPoint.x = _windowOriginal1.getImageMousePosition().getX() * sizeKoef;
					_clickPoint.y = _windowOriginal1.getImageMousePosition().getY() * sizeKoef;
				}
						
				System.out.println("Click on - " + _clickPoint.toString());
			}
		});
		
		addButton("Save").addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				
				configLoader.saveStereoMatcherConfig(_stereo, ConfigLoader.STEREO_BM_CONFIG_FILE);
			}
		});
		
		addButton("Load").addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				
				configLoader.loadStereoMatcherConfig(_stereo, ConfigLoader.STEREO_BM_CONFIG_FILE);
				
				_blockSizeSlider.setValue(_stereo.getBlockSize());
				_minDisparitySlider.setValue(_stereo.getMinDisparity());
				_numDisparitiesSlider.setValue(_stereo.getNumDisparities());
				_speckleWindowSizeSlider.setValue(_stereo.getSpeckleWindowSize());
				_speckleRangeSlider.setValue(_stereo.getSpeckleRange());
				_disp12maxDiffSlider.setValue(_stereo.getDisp12MaxDiff());
			}
		});
		
		_camera1 = Camera.create(Camera.LEFT_CAMERA_IP);
		_camera2 = Camera.create(Camera.RIGHT_CAMERA_IP);
		
		_mapx1 = new Mat();
		_mapy1 = new Mat();
		_mapx2 = new Mat();
		_mapy2 = new Mat();
		
		Size frameSize = _camera1.getFrameSize();
		
		Imgproc.initUndistortRectifyMap(Camera.Stereo.m1, Camera.Stereo.d1, Camera.Stereo.r1, Camera.Stereo.p1, frameSize, CvType.CV_32FC1, _mapx1, _mapy1);
		Imgproc.initUndistortRectifyMap(Camera.Stereo.m2, Camera.Stereo.d2, Camera.Stereo.r2, Camera.Stereo.p2, frameSize, CvType.CV_32FC1, _mapx2, _mapy2);
		
		startLoop();
	}
	
	
	@Override
	protected void loop()
	{
		_frameOriginal1 = _camera1.getNextFrame();
		_frameOriginal2 = _camera2.getNextFrame();
		
		if(!_frameOriginal1.empty() && !_frameOriginal2.empty())
		{
			Mat calibratedFrame1 = new Mat();
			Mat calibratedFrame2 = new Mat();
			
			Imgproc.remap(_frameOriginal1, calibratedFrame1, _mapx1, _mapy1, Imgproc.INTER_LINEAR);
			Imgproc.remap(_frameOriginal2, calibratedFrame2, _mapx2, _mapy2, Imgproc.INTER_LINEAR);
			
			int blurSize = _blurSLider.getValue();
			Imgproc.blur(calibratedFrame1, calibratedFrame1, new Size(blurSize, blurSize));
			Imgproc.blur(calibratedFrame2, calibratedFrame2, new Size(blurSize, blurSize));
			
			drawHorizontalLinesOnFrame(calibratedFrame1);
			drawHorizontalLinesOnFrame(calibratedFrame2);
			
			setStereoParams(_stereo);
			
			Mat calibratedFrameGray1 = new Mat();
			Mat calibratedFrameGray2 = new Mat();
			Imgproc.cvtColor(calibratedFrame1, calibratedFrameGray1, Imgproc.COLOR_BGR2GRAY);
			Imgproc.cvtColor(calibratedFrame2, calibratedFrameGray2, Imgproc.COLOR_BGR2GRAY);
			
//			Imgproc.resize(calibratedFrame1, calibratedFrame1, new Size(calibratedFrame1.width()*0.5, calibratedFrame1.height()*0.5));
//			Imgproc.resize(calibratedFrame2, calibratedFrame2, new Size(calibratedFrame2.width()*0.5, calibratedFrame2.height()*0.5));
			
			Mat disparity = new Mat();
			_stereo.compute(calibratedFrameGray1, calibratedFrameGray2, disparity);

			Mat image3D = new Mat();
			Calib3d.reprojectImageTo3D(disparity, image3D, Camera.Stereo.q, false);
			double[] xyz = image3D.get((int)_clickPoint.y, (int)_clickPoint.x);
			
			Core.normalize(disparity, disparity, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);
			
//			Imgproc.resize(calibratedFrame1, calibratedFrame1, new Size(calibratedFrame1.width() * 2, calibratedFrame1.height() * 2));
//			Imgproc.resize(calibratedFrame2, calibratedFrame2, new Size(calibratedFrame2.width() * 2, calibratedFrame2.height() * 2));
			
			double x = xyz[0];
			double y = xyz[1];
			double z = xyz[2];
			double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
			
//			System.out.println("Distance to object- " + distance + " meters.");
			
			Imgproc.circle(disparity, _clickPoint, 10, new Scalar(10,10,10), 10);
			Imgproc.circle(calibratedFrame1, _clickPoint, 10, new Scalar(50,50,255), 10);
			Imgproc.circle(calibratedFrame2, _clickPoint, 10, new Scalar(50,50,255), 10);
			
			Imgproc.putText(calibratedFrame1, "Dist:" + Double.toString(MathUtils.round(distance * super._COMPUTE_DISTANCE_KOEF, 6)), new Point(_clickPoint.x, _clickPoint.y - 40), Core.FONT_ITALIC, 1.5, new Scalar(50, 50, 200), 3);
//			Imgproc.putText(calibratedFrame1, "X:" + Double.toString(Utils.round(x, 4)), new Point(_clickPoint.x, _clickPoint.y), Core.FONT_ITALIC, 1.5, new Scalar(50, 50, 200), 3);
//			Imgproc.putText(calibratedFrame1, "Y:" + Double.toString(Utils.round(y, 4)), new Point(_clickPoint.x, _clickPoint.y + 40), Core.FONT_ITALIC, 1.5, new Scalar(50, 50, 200), 3);
//			Imgproc.putText(calibratedFrame1, "Z:" + Double.toString(Utils.round(z, 4)), new Point(_clickPoint.x, _clickPoint.y + 80), Core.FONT_ITALIC, 1.5, new Scalar(50, 50, 200), 3);
//			
			_windowDisparity.showFrame(disparity);
			_windowOriginal1.showFrame(calibratedFrame1);
			_windowOriginal2.showFrame(calibratedFrame2);
			
			disparity.release();
			
			calibratedFrameGray1.release();
			calibratedFrameGray2.release();
			
			calibratedFrame1.release();
			calibratedFrame2.release();
			
			_frameOriginal1.release();
			_frameOriginal2.release();
		}
	}
	
	
	private void setStereoParams(StereoMatcher stereo)
	{
		int numberOfDisparities = _numDisparitiesSlider.getValue() - (_numDisparitiesSlider.getValue()%16);
		int blockSize = _blockSizeSlider.getValue() + 1 + (_blockSizeSlider.getValue()%2);
		
		stereo.setBlockSize(blockSize);
		stereo.setMinDisparity(_minDisparitySlider.getValue());
		stereo.setNumDisparities(numberOfDisparities);//16
		stereo.setSpeckleWindowSize(_speckleWindowSizeSlider.getValue());
		stereo.setSpeckleRange(_speckleRangeSlider.getValue());
		stereo.setDisp12MaxDiff(_disp12maxDiffSlider.getValue());
		
		if(stereo.getClass().getSimpleName().equals(StereoBM.class.getSimpleName()))
		{
			int preFilterSize = _preFilterSizeSlider.getValue() + 1 + (_preFilterSizeSlider.getValue()%2);
			
			StereoBM stereoBM = (StereoBM) stereo;
			stereoBM.setPreFilterSize(preFilterSize);
			stereoBM.setPreFilterCap(_preFilterCapSlider.getValue());
			stereoBM.setTextureThreshold(_textureThresholdSlider.getValue());
			stereoBM.setUniquenessRatio(_uniquenessRatioSlider.getValue());
		}
		
		if(stereo.getClass().getSimpleName().equals(StereoSGBM.class.getSimpleName()))
		{
			StereoSGBM stereoSGBM = (StereoSGBM) stereo;
			stereoSGBM.setUniquenessRatio(_uniquenessRatioSlider.getValue());
			stereoSGBM.setP1(600);
			stereoSGBM.setP1(2400);
		}
		
	}
}
