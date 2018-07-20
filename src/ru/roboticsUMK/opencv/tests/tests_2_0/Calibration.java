package ru.roboticsUMK.opencv.tests.tests_2_0;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.opencv.calib3d.Calib3d;
import org.opencv.calib3d.StereoSGBM;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import ru.roboticsUMK.opencv.Camera;
import ru.roboticsUMK.opencv.ConfigLoader;
import ru.roboticsUMK.opencv.ImageWindow;
import ru.roboticsUMK.opencv.OpenCVTestBase;


public class Calibration extends OpenCVTestBase
{

	private static final long serialVersionUID = -8507745068150623727L;
	
	private Camera _camera1;
	private Camera _camera2;
	private Mat _frameOriginal1;
	private Mat _frameOriginal2;
	private ImageWindow _windowOriginal1;
	private ImageWindow _windowOriginal2;
	private ImageWindow _windowCalibrate1;
	private ImageWindow _windowCalibrate2;
	
	private boolean _isScreenShotClick = false;
	private int _screenShotsCount = 0;
	
	private boolean _isFindChessBoard = false;
	
	private JTextField _leftCameraImagesPathTF;

	private JTextField _rightCameraImagesPathTF;

	private JTextField _screenshotsCountTF;
	
	private MatOfPoint3f _objCorners;
	
	private boolean _isStereoCalibrateNow = false;

	private StereoSGBM _stereo;
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
	
	
	public Calibration()
	{
		super("Calibration");
		
		_screenShotsCount = directoryFilesCount(LEFT_CAMERA_IMAGES_DEFAULT_FOLDER);
		
		_leftCameraImagesPathTF = addSelectFolder(LEFT_CAMERA_IMAGES_DEFAULT_FOLDER);
		_rightCameraImagesPathTF = addSelectFolder(RIGHT_CAMERA_IMAGES_DEFAULT_FOLDER);
		_screenshotsCountTF = addTextFieldWithLabel("Screenshots", true);
		_screenshotsCountTF.setText(Integer.toString(_screenShotsCount));
		
		addCheckBox("Find chessboard:").addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_isScreenShotClick = false;
				_isFindChessBoard = ((JCheckBox)e.getSource()).isSelected();
			}
		});
		
		addButton("Screenshot").addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_isScreenShotClick = true;
			}	
		});
		
		addButton("Delete all").addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				removeAllScreenshots();
			}	
		});
		
		addButton("Calib left").addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				calibrateSingle(_camera1, _leftCameraImagesPathTF.getText(), _windowCalibrate1, ConfigLoader.LEFT_CAMERA_CALIBRATION_FILE);
			}	
		});
		
		addButton("Calib right").addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				calibrateSingle(_camera2, _rightCameraImagesPathTF.getText(), _windowCalibrate2, ConfigLoader.RIGHT_CAMERA_CALIBRATION_FILE);
			}	
		});
		
		addButton("Calib stereo").addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(_camera1.isCalibrarted() && _camera2.isCalibrarted())
				{
					_isStereoCalibrateNow = true;
					calibrateStereo();
				}else
				{
					System.out.println("Cameras not calibrated!!!");
				}

			}	
		});
		
		addButton("Load single").addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				configLoader.loadSingleCalibrationConfig(_camera1, ConfigLoader.LEFT_CAMERA_CALIBRATION_FILE);
				configLoader.loadSingleCalibrationConfig(_camera2, ConfigLoader.RIGHT_CAMERA_CALIBRATION_FILE);
			}	
		});
		
		
		addButton("Load stereo").addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Load stereo calibration configuration");
			}	
		});
		
		_preFilterSizeSlider = super.addSlider("preFilterSize", 5, 255, 7);
		_preFilterCapSlider = super.addSlider("preFilterCap", 1, 63, 1);
		_blockSizeSlider = super.addSlider("blockSize", 5, 255, 19);
		_minDisparitySlider = super.addSlider("minDisparity", 5, 255, 5);
		_numDisparitiesSlider = super.addSlider("numDisparities", 16, 200, 64);
		_textureThresholdSlider = super.addSlider("textureThreshold", 0, 1000, 0);
		_uniquenessRatioSlider = super.addSlider("uniquenessRatio", 0, 512, 5);
		_speckleWindowSizeSlider = super.addSlider("speckleWindowSize", 0, 100, 0);
		_speckleRangeSlider = super.addSlider("speckleRange", 0, 100, 22);
		_disp12maxDiffSlider = super.addSlider("disp12maxDiff", 0, 100, 41);
		
		_windowOriginal1 = new ImageWindow("Original left");
		_windowOriginal2 = new ImageWindow("Original right");
		_windowCalibrate1 = new ImageWindow("Calibrate left");
		_windowCalibrate2 = new ImageWindow("Calibrate right");
		
		_camera1 = Camera.create(Camera.LEFT_CAMERA_IP);
		_camera2 = Camera.create(Camera.RIGHT_CAMERA_IP);
		
		_objCorners = new MatOfPoint3f();
		
		for (int i = 0; i < CHESSBOARD_CORNERS_VER; ++i) 
		{
			for (int j = 0; j < CHESSBOARD_CORNERS_HOR; ++j)
			{
				Point3 pt = new Point3(j * CHESSBOARD_SQUARE_SIZE, i * CHESSBOARD_SQUARE_SIZE, 0.0d);
				_objCorners.push_back(new MatOfPoint3f(pt));
			}
		}
		
		_stereo = StereoSGBM.create();
		
		startLoop();
	}
	
	
	@Override
	protected void loop()
	{
		_frameOriginal1 = _camera1.getNextFrame();
		_frameOriginal2 = _camera2.getNextFrame();
		
		if(!_frameOriginal1.empty() && !_frameOriginal2.empty())
		{
			boolean isLeftFound = false;
			boolean isRightFound = false;
			
			Mat cornersFrame1 = _frameOriginal1.clone();
			Mat cornersFrame2 = _frameOriginal2.clone();
			
			if(_isFindChessBoard)
			{
				MatOfPoint2f imageCorners;
				
				if((imageCorners = getCornersFromFrame(_frameOriginal1)) != null)
				{
					Calib3d.drawChessboardCorners(cornersFrame1, CHESSBOARD_SIZE, imageCorners, true);
					isLeftFound = true;
				}
				
				if((imageCorners = getCornersFromFrame(_frameOriginal2)) != null)
				{
					Calib3d.drawChessboardCorners(cornersFrame2, CHESSBOARD_SIZE, imageCorners, true);
					isRightFound = true;
				}
			}
			
			if(_isScreenShotClick && ((!_isFindChessBoard) || (_isFindChessBoard && isLeftFound && isRightFound)))
			{
				makeScreenshot(_frameOriginal1, _frameOriginal2);
				_isScreenShotClick = false;
			}
			
			if(Camera.Stereo.isCalibrated)
			{
				Mat calibratedFrame1 = new Mat();
				Mat calibratedFrame2 = new Mat();
				
				Mat mapx1 = new Mat();
				Mat mapy1 = new Mat();
				Mat mapx2 = new Mat();
				Mat mapy2 = new Mat();
				
				Size frameSize = _frameOriginal1.size();
				
				Imgproc.initUndistortRectifyMap(_camera1.getM(), _camera1.getD(), Camera.Stereo.r1, Camera.Stereo.p1, frameSize, CvType.CV_32FC1, mapx1, mapy1);
				Imgproc.initUndistortRectifyMap(_camera2.getM(), _camera2.getD(), Camera.Stereo.r2, Camera.Stereo.p2, frameSize, CvType.CV_32FC1, mapx2, mapy2);
				
				Imgproc.remap(_frameOriginal1, calibratedFrame1, mapx1, mapy1, Imgproc.INTER_LINEAR);
				Imgproc.remap(_frameOriginal2, calibratedFrame2, mapx2, mapy2, Imgproc.INTER_LINEAR);
				
				drawHorizontalLinesOnFrame(calibratedFrame1);
				drawHorizontalLinesOnFrame(calibratedFrame2);
				
				int numberOfDisparities = _numDisparitiesSlider.getValue() - (_numDisparitiesSlider.getValue()%16);
				int preFilterSize = _preFilterSizeSlider.getValue() + 1 + (_preFilterSizeSlider.getValue()%2);
				int blockSize = _blockSizeSlider.getValue() + 1 + (_blockSizeSlider.getValue()%2);
				
//				_stereo.setPreFilterSize(preFilterSize);
				_stereo.setPreFilterCap(_preFilterCapSlider.getValue());
				_stereo.setBlockSize(blockSize);
				_stereo.setMinDisparity(_minDisparitySlider.getValue());
				_stereo.setNumDisparities(numberOfDisparities);//16
//				_stereo.setTextureThreshold(_textureThresholdSlider.getValue());
				_stereo.setUniquenessRatio(_uniquenessRatioSlider.getValue());
				_stereo.setSpeckleWindowSize(_speckleWindowSizeSlider.getValue());
				_stereo.setSpeckleRange(_speckleRangeSlider.getValue());
				_stereo.setDisp12MaxDiff(_disp12maxDiffSlider.getValue());
				
				Mat calibratedFrameGray1 = new Mat();
				Mat calibratedFrameGray2 = new Mat();
				Imgproc.cvtColor(calibratedFrame1, calibratedFrameGray1, Imgproc.COLOR_BGR2GRAY);
				Imgproc.cvtColor(calibratedFrame2, calibratedFrameGray2, Imgproc.COLOR_BGR2GRAY);
				
				Mat disparity = new Mat();
				_stereo.compute(calibratedFrameGray1, calibratedFrameGray2, disparity);
				
				Core.normalize(disparity, disparity, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);
				
				_windowOriginal1.showFrame(disparity);
				_windowCalibrate1.showFrame(calibratedFrame1);
				_windowCalibrate2.showFrame(calibratedFrame2);
				
				calibratedFrameGray1.release();
				calibratedFrameGray2.release();
				disparity.release();
				calibratedFrame1.release();
				calibratedFrame2.release();
				
			}else
			{
				if(!_isStereoCalibrateNow)
				{
					if(_camera1.isCalibrarted())
					{
						Mat calibratedFrame1 = new Mat();
						Imgproc.undistort(_frameOriginal1, calibratedFrame1, _camera1.getM(), _camera1.getD());
						_windowCalibrate1.showFrame(calibratedFrame1);
						calibratedFrame1.release();
					}
					
					if(_camera2.isCalibrarted())
					{
						Mat calibratedFrame2 = new Mat();
						Imgproc.undistort(_frameOriginal2, calibratedFrame2, _camera2.getM(), _camera2.getD());
						_windowCalibrate2.showFrame(calibratedFrame2);
						calibratedFrame2.release();
					}
				}
			}
			
			if(!Camera.Stereo.isCalibrated)
			{
				_windowOriginal1.showFrame(cornersFrame1);
				_windowOriginal2.showFrame(cornersFrame2);
			}
			
			cornersFrame1.release();
			cornersFrame2.release();
		}
		
		_frameOriginal1.release();
		_frameOriginal2.release();
	}
	
	
	private void calibrateStereo()
	{
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				List<Mat> imagesPoints1 = new ArrayList<>();
				List<Mat> imagesPoints2 = new ArrayList<>();
				List<Mat> objectsPoints = new ArrayList<>();
				
				int screenshotsCount = new Integer(_screenshotsCountTF.getText());
				int imagesWithChessboardCount = 0;

				Mat frame1 = null;
				Mat frame2 = null;
				
				Size frameSize = null;
				
				for (int i = 0; i < screenshotsCount; i++)
				{
					int imageNumber = i + 1;
					
					String filePath1 = _leftCameraImagesPathTF.getText() + "/Image_" + Integer.toString(imageNumber) + ".jpeg";
					String filePath2 = _rightCameraImagesPathTF.getText() + "/Image_" + Integer.toString(imageNumber) + ".jpeg";
					
					frame1 = Imgcodecs.imread(filePath1);
					frame2 = Imgcodecs.imread(filePath2);
					
//					Imgproc.undistort(frame1.clone(), frame1, _camera1.getM(), _camera1.getD());
//					Imgproc.undistort(frame2.clone(), frame2, _camera2.getM(), _camera2.getD());
					
					MatOfPoint2f imageCorners1;
					MatOfPoint2f imageCorners2;
					
					boolean isLeftFound = false;
					boolean isRightFound = false;
					
					if((imageCorners1 = getCornersFromFrame(frame1)) != null)
					{
						isLeftFound = true;
					}
					
					if((imageCorners2 = getCornersFromFrame(frame2)) != null)
					{
						isRightFound = true;
					}
					
					if(isLeftFound && isRightFound)
					{
						Calib3d.drawChessboardCorners(frame1, CHESSBOARD_SIZE, imageCorners1, true);
						Calib3d.drawChessboardCorners(frame2, CHESSBOARD_SIZE, imageCorners2, true);
						
						imagesPoints1.add(imageCorners1);
						imagesPoints2.add(imageCorners2);
						objectsPoints.add(_objCorners);

						imagesWithChessboardCount++;
					}
					
					Imgproc.putText(frame1, "Image: " + Integer.toString(imageNumber), new Point(frame1.width()/2 - 300, frame1.height()/2 - 100), Core.FONT_ITALIC, 5.0, new Scalar(50, 50, 200), 10);
					Imgproc.putText(frame1, "Found: " + Integer.toString(imagesWithChessboardCount), new Point(frame1.width()/2 - 300, frame1.height()/2 + 100), Core.FONT_ITALIC, 5.0, new Scalar(50, 200, 50), 10);
					Imgproc.putText(frame2, "Image: " + Integer.toString(imageNumber), new Point(frame2.width()/2 - 300, frame2.height()/2 - 100), Core.FONT_ITALIC, 5.0, new Scalar(50, 50, 200), 10);
					Imgproc.putText(frame2, "Found: " + Integer.toString(imagesWithChessboardCount), new Point(frame2.width()/2 - 300, frame2.height()/2 + 100), Core.FONT_ITALIC, 5.0, new Scalar(50, 200, 50), 10);
					
					if(imageNumber == screenshotsCount)
					{
						Imgproc.putText(frame1, "Stereo calibrate... ", new Point(frame1.width()/2 - 500, frame1.height()/2 + 300), Core.FONT_ITALIC, 4.0, new Scalar(50, 200, 50), 10);
						Imgproc.putText(frame2, "Stereo calibrate... ", new Point(frame1.width()/2 - 500, frame2.height()/2 + 300), Core.FONT_ITALIC, 4.0, new Scalar(50, 200, 50), 10);
						
						frameSize = frame1.size();
					}
					
					_windowCalibrate1.showFrame(frame1);
					_windowCalibrate2.showFrame(frame2);
					frame1.release();
					frame2.release();
				}
				
				 Mat rotationMatrix = new Mat(3, 3, CvType.CV_64F);
				 Mat translationVector = new Mat(3, 1, CvType.CV_64F);
				 Mat essentialMatrix = new Mat(3, 3, CvType.CV_64F);
				 Mat fundamentalMatrix = new Mat(3, 3, CvType.CV_64F);
				
				 TermCriteria criteria = new TermCriteria(TermCriteria.COUNT + TermCriteria.EPS, 100, 1e-5);

				 int calibrateFlags = Calib3d.CALIB_FIX_ASPECT_RATIO + Calib3d.CALIB_ZERO_TANGENT_DIST	+ Calib3d.CALIB_SAME_FOCAL_LENGTH ;
				 
				 Calib3d.stereoCalibrate(objectsPoints, imagesPoints1, imagesPoints2, _camera1.getM(), _camera1.getD(), _camera2.getM(), _camera2.getD(), frameSize, rotationMatrix, translationVector, essentialMatrix, fundamentalMatrix, calibrateFlags, criteria);
				 
				 Mat R1 = new Mat();
				 Mat R2 = new Mat();
				 Mat P1 = new Mat();
				 Mat P2 = new Mat();
				 Mat Q = new Mat();
				 
				 Rect roiLeft = new Rect();
				 Rect roiRight = new Rect();
				 Size newSize = frameSize;
				 int rectifyFlag = Calib3d.CALIB_ZERO_DISPARITY;
				 double alpha = 0.0;
				
				 Calib3d.stereoRectify(_camera1.getM(), _camera1.getD(), _camera2.getM(), _camera2.getD(), frameSize, rotationMatrix, translationVector, R1, R2, P1, P2, Q, rectifyFlag, alpha, newSize, roiLeft, roiRight);
				
				 Camera.Stereo.m1 = _camera1.getM();
				 Camera.Stereo.d1 = _camera1.getD();
				 Camera.Stereo.m2 = _camera2.getM();
				 Camera.Stereo.d2 = _camera2.getD();
				 
				 Camera.Stereo.p1 = P1;
				 Camera.Stereo.p2 = P2;
				 Camera.Stereo.r1 = R1;
				 Camera.Stereo.r2 = R2;
				 
				 Camera.Stereo.r = rotationMatrix;
				 Camera.Stereo.t = translationVector;
				 Camera.Stereo.q = Q;
				
				 configLoader.saveStereoCalibrateConfig(ConfigLoader.STEREO_CALIBRATION_FILE);
				 
				 Camera.Stereo.isCalibrated = true;
			}
			
		}).start();
	}
	
	
	private void calibrateSingle(final Camera camera, final String imagesFolderPath, final ImageWindow window, final String dataFilePath)
	{
		
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				List<Mat> imagesPoints = new ArrayList<>();
				List<Mat> objectsPoints = new ArrayList<>();
				
				int screenshotsCount = new Integer(_screenshotsCountTF.getText());
				int imagesWithChessboardCount = 0;

				Mat frame = null;
				
				for (int i = 0; i < screenshotsCount; i++)
				{
					int imageNumber = i + 1;
					
					String filePath = imagesFolderPath + "/Image_" + Integer.toString(imageNumber) + ".jpeg";
					frame = Imgcodecs.imread(filePath);
					MatOfPoint2f imageCorners = new MatOfPoint2f();

					if((imageCorners = getCornersFromFrame(frame)) != null)
					{
						Calib3d.drawChessboardCorners(frame, CHESSBOARD_SIZE, imageCorners, true);
						
						objectsPoints.add(_objCorners);
						imagesPoints.add(imageCorners);

						imagesWithChessboardCount++;
					}
					
					Imgproc.putText(frame, "Image: " + Integer.toString(imageNumber), new Point(frame.width()/2 - 300, frame.height()/2 - 100), Core.FONT_ITALIC, 5.0, new Scalar(50, 50, 200), 10);
					Imgproc.putText(frame, "Found: " + Integer.toString(imagesWithChessboardCount), new Point(frame.width()/2 - 300, frame.height()/2 + 100), Core.FONT_ITALIC, 5.0, new Scalar(50, 200, 50), 10);
					
					if(imageNumber == screenshotsCount)
					{
						Imgproc.putText(frame, "Calibrate... ", new Point(frame.width()/2 - 500, frame.height()/2 + 300), Core.FONT_ITALIC, 4.0, new Scalar(50, 200, 50), 10);
					}
					
					window.showFrame(frame);
				}
				
				Mat distCoeffs = new Mat();
				
				Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
				cameraMatrix.put(0, 0, 1);
				cameraMatrix.put(1, 1, 1);
				
				List<Mat> rvecs = new ArrayList<>();
				List<Mat> tvecs = new ArrayList<>();
				
				Size frameSize = frame.size();
				
				double calibrateError = Calib3d.calibrateCamera(objectsPoints, imagesPoints, frameSize, cameraMatrix, distCoeffs, rvecs, tvecs);
				
				System.out.println("-----------------------------------------------");
				System.out.println("calibrateError:");
				System.out.println(calibrateError);
				System.out.println("____________________________________");
				
				camera.setD(distCoeffs);
				camera.setM(cameraMatrix);
				camera.setCalibrated(true);
				
				configLoader.saveSingleCalibrateConfig(camera, dataFilePath);
			}
		}).start();
	}
	
	
	private MatOfPoint2f getCornersFromFrame(Mat frame)
	{
		MatOfPoint2f imageCorners = new MatOfPoint2f();
		
		Mat frameGray = new Mat();
		
		Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_BGR2GRAY);
		
		boolean found = Calib3d.findChessboardCorners(frameGray, CHESSBOARD_SIZE, imageCorners, Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
		
		if(found)
		{
			TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
			Imgproc.cornerSubPix(frameGray, imageCorners, new Size(11, 11), new Size(-1, -1), term);
			
			frameGray.release();
			
			return imageCorners;
		}else
		{
			frameGray.release();

			return null;
		}
	}
	
	
	private void makeScreenshot(Mat left, Mat right)
	{
		_screenShotsCount++;
		
		String fileName = "/Image_" + Integer.toString(_screenShotsCount) + ".jpeg";
		
		System.out.println("Screeen shot");
		
		super.saveImage(left, _leftCameraImagesPathTF.getText(), fileName);
		super.saveImage(right, _rightCameraImagesPathTF.getText(), fileName);
		
		_screenshotsCountTF.setText(Integer.toString(_screenShotsCount));
	}
	
	
	private void removeAllScreenshots()
	{
		cleanDirectory(_leftCameraImagesPathTF.getText());
		cleanDirectory(_rightCameraImagesPathTF.getText());
		
		_screenShotsCount = 0;
		_screenshotsCountTF.setText(Integer.toString(_screenShotsCount));
	}

}
