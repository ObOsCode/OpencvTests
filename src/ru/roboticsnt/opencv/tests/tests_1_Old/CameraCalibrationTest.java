package ru.roboticsnt.opencv.tests.tests_1_Old;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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

import ru.roboticsnt.opencv.Camera;
import ru.roboticsnt.opencv.gui.swing.ImageWindow;
import ru.roboticsnt.opencv.gui.swing.OpenCVTestBase;


public class CameraCalibrationTest extends OpenCVTestBase
{
	private static final long serialVersionUID = 1593775894721996982L;
	
	
	private int _numCornersHor = 9;
	private int _numCornersVer = 6;
	private int boardsNumber = 5;
	private int successes = 0;
	
//	private Mat _currentFrame;
	
	
	private MatOfPoint3f obj;
	private MatOfPoint2f imageCorners;
	private List<Mat> imagePoints;
	private List<Mat> objectPoints;
	private Mat intrinsic;
	private Mat savedImage;
	private Mat distCoeffs;
	
	private boolean isCalibrated = false;


	private Mat _originalFrame;

	public CameraCalibrationTest()
	{
		super();
		
		String codec = "h264";
		String sub_main = "main";
		String cameraStreamURL = "rtsp://admin:123Qwerty@192.168.0.202:554/"+codec+"/ch01/"+sub_main+"/av_stream";
		
		Camera camera = new Camera(cameraStreamURL);
		
		ImageWindow originalFrameWindow = new ImageWindow();
		originalFrameWindow.setLocation(100, 0);

		
		ImageWindow calibrateFrameWindow = new ImageWindow();
		calibrateFrameWindow.setLocationRelativeTo(originalFrameWindow);

		
		JButton snapshotButton = addButton("Snapshot");
		
		snapshotButton.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				
				System.out.println("Click");
				

				findAndDrawPoints(_originalFrame);
					
				takeSnapshot();
				
			}
		});
		
		obj = new MatOfPoint3f();
		imageCorners = new MatOfPoint2f();
		imagePoints = new ArrayList<>();
		objectPoints = new ArrayList<>();
		intrinsic = new Mat(3, 3, CvType.CV_32FC1);
		savedImage = new Mat();
		distCoeffs = new Mat();
		
		int numSquares = _numCornersHor * _numCornersVer;
		int squareSize = 27;
		
		for (int j = 0; j < numSquares; j++)
		{
			Point3 pt = new Point3(j / _numCornersHor, j % this._numCornersVer, 0.0f);
			obj.push_back(new MatOfPoint3f(pt));
		}

//		
//		System.out.println(obj.dump());
		
//		obj = new MatOfPoint3f();
		
//		for (int i = 0; i < _numCornersVer; ++i) 
//		{
//			for (int j = 0; j < _numCornersHor; ++j)
//			{
//				Point3 pt = new Point3(j * squareSize, i * squareSize, 0.0d);
//				obj.push_back(new MatOfPoint3f(pt));
//			}
//		}
		
		System.out.println(obj.dump());
		
		
		
		while(true)
		{
			Mat frame = camera.getNextFrame();
			
			if(!frame.empty())
			{
				_originalFrame = frame.clone();
				

//				findAndDrawPoints(_originalFrame);
				
				
				Imgproc.putText(_originalFrame, Integer.toString(successes), new Point(120,120), Core.FONT_ITALIC, 4, new Scalar(100,100,255), 6);
				
				originalFrameWindow.showFrame(_originalFrame, new Size(640, 480));
				
				if(isCalibrated)
				{
					Mat undistored = new Mat();
					Imgproc.undistort(frame, undistored, intrinsic, distCoeffs);
					calibrateFrameWindow.showFrame(undistored, new Size(640, 480));
				}

			}
			
//			try
//			{
//				Thread.sleep(33);
//			} catch (InterruptedException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		}

	}
	
	
	private synchronized void findAndDrawPoints(Mat frame)
	{
		Mat grayImage = new Mat();
		
		Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
		
		Size boardSize = new Size(this._numCornersHor, this._numCornersVer);

		boolean found = Calib3d.findChessboardCorners(grayImage, boardSize, imageCorners, Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
		
		if(found)
		{
			TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
			Imgproc.cornerSubPix(grayImage, imageCorners, new Size(11, 11), new Size(-1, -1), term);
			
			grayImage.copyTo(savedImage);

			Calib3d.drawChessboardCorners(frame, boardSize, imageCorners, found);
		}
	}
	
	
	private void takeSnapshot()
	{

		if(successes<boardsNumber)
		{
			imagePoints.add(imageCorners);
			imageCorners = new MatOfPoint2f();
			objectPoints.add(obj);
			successes++;
		}
		
//		System.out.println("successes - " + successes);
		
		if(successes == boardsNumber)
		{
			calibrateCamera();
		}
		
	}
	
	
	private void calibrateCamera()
	{
		List<Mat> rvecs = new ArrayList<>();
		List<Mat> tvecs = new ArrayList<>();
		intrinsic.put(0, 0, 1);
		intrinsic.put(1, 1, 1);
		
		System.out.println("_objectPoints - " + objectPoints);
		System.out.println("_imagePoints - " + imagePoints);
		System.out.println("_currentFrame.size() - " + savedImage.size());
		System.out.println("_cameraMatrix - " + intrinsic);
		System.out.println("_distCoeffs - " + distCoeffs);
		
		Calib3d.calibrateCamera(objectPoints, imagePoints, savedImage.size(), intrinsic, distCoeffs, rvecs, tvecs);

		
		for (int i = 0; i < distCoeffs.rows(); i++)
		{
			for (int j = 0; j < distCoeffs.cols(); j++)
			{
				double[] val = distCoeffs.get(i, j);
				
				System.out.println(val[0]);
			}
		}
		
		isCalibrated = true;
		
	}

}
