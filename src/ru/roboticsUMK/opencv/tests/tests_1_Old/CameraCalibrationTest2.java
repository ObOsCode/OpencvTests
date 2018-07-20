package ru.roboticsUMK.opencv.tests.tests_1_Old;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import ru.roboticsUMK.opencv.Camera;
import ru.roboticsUMK.opencv.ImageWindow;
import ru.roboticsUMK.opencv.OpenCVTestBase;

public class CameraCalibrationTest2 extends OpenCVTestBase
{

	private static final long serialVersionUID = 2522062278168169275L;

	private final int _AUTO_SNAPSHOT_DELAY = 2000;
	
	private final int _BOARDS_COUNT = 10;
	private int _success = 0;
	private int _numCornersHor = 9;
	private int _numCornersVer = 6;
	
	private MatOfPoint3f _objCorners;
	private MatOfPoint2f _imageCorners;
	private List<Mat> _imagePoints;
	private List<Mat> _objectPoints;
	
	private Mat _currentFrame;
	
	private Mat _cameraMatrix;
	private Mat _distCoeffs;
	
	
	private boolean _isCalibrated = false;
	
	private boolean _isSnapshotClick = false;
	
	
	public CameraCalibrationTest2()
	{
		
		String codec = "h264";
		String sub_main = "main";
		String user = "admin";
		String password = "123Qwerty";
		String ip = "192.168.0.202";
		String port = "55401";
		
		
		String cameraStreamURL = "rtsp://" + user + ":"+ password +"@"+ ip +":"+ port +"/"+codec+"/ch01/"+sub_main+"/av_stream";
		
		Camera camera = new Camera(cameraStreamURL);
		
		ImageWindow originalFrameWindow = new ImageWindow();

		ImageWindow calibrateFrameWindow = new ImageWindow();
		
		ImageWindow window3 = new ImageWindow("Window 3");
//		ImageWindow grayFrameWindow = new ImageWindow("Frame 3");

		
		
		//Timer
		Timer timer = new Timer();
		
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				_isSnapshotClick = true;
			}
			
		}, _AUTO_SNAPSHOT_DELAY, _AUTO_SNAPSHOT_DELAY);
		
		
		JButton snapShotButton = addButton("Snapshot");
		snapShotButton.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_isSnapshotClick = true;
			}
		});
		
		
		JButton restartButton = addButton("Restart");
		restartButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				init();
			}
		});
		
		
		init();

		
		while (true)
		{
			_currentFrame = camera.getNextFrame();
			
			Mat originalFrame = _currentFrame.clone();
			
			boolean found = findAndDrawPoints(originalFrame);
			
			
			if(_isSnapshotClick && found && !_isCalibrated)
			{
				takeSnapshot();
			}
			
			Imgproc.putText(originalFrame, Integer.toString(_success), new Point(120,120), Core.FONT_ITALIC, 4, new Scalar(100,100,255), 6);
			originalFrameWindow.showFrame(originalFrame);
			
			if(_isCalibrated)
			{
				Mat undistoredFrame = new Mat();
				
				Mat newMatrixFrame = new Mat();
				
				Mat newCameraMatrix = Calib3d.getOptimalNewCameraMatrix(_cameraMatrix, _distCoeffs, originalFrame.size(), 0);
//				
				Imgproc.undistort(originalFrame, undistoredFrame, _cameraMatrix, _distCoeffs);
				Imgproc.undistort(originalFrame, newMatrixFrame, _cameraMatrix, _distCoeffs, newCameraMatrix);

//				Mat map1 = new Mat();
//				Mat map2 = new Mat();
//				Imgproc.initUndistortRectifyMap(_cameraMatrix, _distCoeffs, null, newCameraMatrix, originalFrame.size(), CvType.CV_32FC1, map1, map2);
//				Imgproc.remap(originalFrame, undistoredFrame, map1, map2, Imgproc.INTER_LINEAR);
				
				calibrateFrameWindow.showFrame(undistoredFrame);
				window3.showFrame(newMatrixFrame);
				
				undistoredFrame.release();
				newMatrixFrame.release();
				newCameraMatrix.release();
			}
			
			_isSnapshotClick = false;
			
			_currentFrame.release();
			originalFrame.release();

			
			try
			{
				Thread.sleep(33);
			} catch (InterruptedException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	
	private void init()
	{
		_success = 0;
		_isCalibrated = false;
		
		_imageCorners = new MatOfPoint2f();
		_objCorners = new MatOfPoint3f();
		_imagePoints = new ArrayList<>();
		_objectPoints = new ArrayList<>();
		_distCoeffs = new Mat();
		
		_currentFrame = new Mat();
		
//		int numSquares = _numCornersHor * _numCornersVer;
		
//		for (int j = 0; j < numSquares; j++)
//		{
//			Point3 pt = new Point3(j / _numCornersHor, j % this._numCornersVer, 0.0f);
//			_objCorners.push_back(new MatOfPoint3f(pt));
//		}
		
		double squareSize = 0.027d;
		
		for (int i = 0; i < _numCornersVer; ++i) 
		{
			for (int j = 0; j < _numCornersHor; ++j)
			{
				Point3 pt = new Point3(j * squareSize, i * squareSize, 0.0d);
				_objCorners.push_back(new MatOfPoint3f(pt));
			}
		}
	}
	
	
	private boolean findAndDrawPoints(Mat frame)
	{
		Mat grayImage = new Mat();
		
		Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
		
		Size boardSize = new Size(this._numCornersHor, this._numCornersVer);

		boolean found = Calib3d.findChessboardCorners(grayImage, boardSize, _imageCorners, Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
		
		if(found)
		{
			TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
			Imgproc.cornerSubPix(grayImage, _imageCorners, new Size(11, 11), new Size(-1, -1), term);

			Calib3d.drawChessboardCorners(frame, boardSize, _imageCorners, found);
		}
		
//		grayImage.release();
	
		return found;
	}
	
	
	private void takeSnapshot()
	{
		if(_success < _BOARDS_COUNT)
		{
			_imagePoints.add(_imageCorners);
			_objectPoints.add(_objCorners);
			_imageCorners = new MatOfPoint2f();
			_success++;
		}	
		
		if(_success == _BOARDS_COUNT)
		{
			calibrateCamera();
		}
	}
	
	
	private void calibrateCamera()
	{
		List<Mat> rvecs = new ArrayList<>();
		List<Mat> tvecs = new ArrayList<>();
		_cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
		_cameraMatrix.put(0, 0, 1);
		_cameraMatrix.put(1, 1, 1);
		
		Calib3d.calibrateCamera(_objectPoints, _imagePoints, _currentFrame.size(), _cameraMatrix, _distCoeffs, rvecs, tvecs);

		System.out.println("-----------------------------------------------");
		System.out.println("_distCoeffs:");
		System.out.println("____________________________________");
		
		for (int i = 0; i < _distCoeffs.rows(); i++)
		{
			for (int j = 0; j < _distCoeffs.cols(); j++)
			{
				double[] val = _distCoeffs.get(i, j);
				
				System.out.println(val[0]);
			}
		}
		
		System.out.println("____________________________________");
		System.out.println("Camera matrix:");
		System.out.println("____________________________________");
		System.out.println(_cameraMatrix.dump());
		System.out.println("____________________________________");
		System.out.println("-------------------------------------------------"); 
		
		System.out.println("rvecs - " + rvecs);
		System.out.println("tvecs - " + tvecs);
		
//		double totalError = 0;
//		MatOfPoint2f imagePoints2 = new MatOfPoint2f();;
//		
//		for (int c = 0; c < _objectPoints.size(); c++)
//		{
//			Calib3d.projectPoints(_objCorners, rvecs.get(c), tvecs.get(c), _cameraMatrix, new MatOfDouble(_distCoeffs), imagePoints2);
//			double error = Core.norm(_imagePoints.get(c), imagePoints2, Core.NORM_L2 );
//			
//			totalError+=error/imagePoints2.elemSize();
//		}
//		
//		System.out.println("totalError - " + (totalError/imagePoints2.elemSize()));
		

		
		_isCalibrated = true;
	}

}
