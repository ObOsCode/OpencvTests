package ru.roboticsUMK.opencv.tests.tests_1_Old;

import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import ru.roboticsUMK.opencv.Camera;
import ru.roboticsUMK.opencv.ImageWindow;
import ru.roboticsUMK.opencv.OpenCVTestBase;

public class CameraCalibrationTest3 extends OpenCVTestBase
{

	private static final long serialVersionUID = 3904572983536179501L;
	
	private final int _IMAGES_COUNT = 30;
	
	private int _numCornersHor = 9;
	private int _numCornersVer = 6;
	
	
	private final String _IMAGES_FOLDER = "/home/user/Изображения/calibrateCameras/left_camera/";
//	private final String _IMAGES_FOLDER = "/home/user/Изображения/calibrateCameras/right_camera/";

	
	public CameraCalibrationTest3()
	{
		ImageWindow window = new ImageWindow();
		
		List<Mat> _imagePoints = new ArrayList<>();
		List<Mat> _objectPoints = new ArrayList<>();
		
		MatOfPoint3f _objCorners = new MatOfPoint3f();
		
		double squareSize = 0.027d;
		
		for (int i = 0; i < _numCornersVer; ++i) 
		{
			for (int j = 0; j < _numCornersHor; ++j)
			{
				Point3 pt = new Point3(j * squareSize, i * squareSize, 0.0d);
				_objCorners.push_back(new MatOfPoint3f(pt));
			}
		}
		
		Size frameSize = new Size();
		
		for (int i = 0; i < _IMAGES_COUNT; i++)
		{
			int imageNumber = i + 1;
			
			String filename = _IMAGES_FOLDER + "Image_" + Integer.toString(imageNumber) + ".jpeg";
			
			
			System.out.println("Load image + " + filename);
			
			Mat frame = Imgcodecs.imread(filename);
			
			frameSize = frame.size();
			
			Mat frameGray = new Mat();
			
			Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_BGR2GRAY);
			
			MatOfPoint2f imageCorners = new MatOfPoint2f();
			
			Size boardSize = new Size(this._numCornersHor, this._numCornersVer);

			boolean found = Calib3d.findChessboardCorners(frameGray, boardSize, imageCorners, Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
			
			if(found)
			{
				TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
				Imgproc.cornerSubPix(frameGray, imageCorners, new Size(11, 11), new Size(-1, -1), term);

				Calib3d.drawChessboardCorners(frame, boardSize, imageCorners, found);
				
				_objectPoints.add(_objCorners);
				_imagePoints.add(imageCorners);
			}
			
			
			window.showFrame(frame);
			
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		//Calibrate
		
		Mat _distCoeffs = new Mat();
		
		List<Mat> rvecs = new ArrayList<>();
		List<Mat> tvecs = new ArrayList<>();
		Mat _cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
		_cameraMatrix.put(0, 0, 1);
		_cameraMatrix.put(1, 1, 1);
		
		Calib3d.calibrateCamera(_objectPoints, _imagePoints, frameSize, _cameraMatrix, _distCoeffs, rvecs, tvecs);
		
		System.out.println("-----------------------------------------------");
		System.out.println("_distCoeffs:");
		System.out.println("____________________________________");
		
		for (int i = 0; i < _distCoeffs.rows(); i++)
		{
			for (int j = 0; j < _distCoeffs.cols(); j++)
			{
				double[] val = _distCoeffs.get(i, j);
				
				System.out.println("i - " + i + "   j - " + j + "   val - " + val[0]);
			}
		}
		
//		Calib3d.ste
		
		System.out.println("____________________________________");
		System.out.println("Camera matrix:");
		System.out.println("____________________________________");
		System.out.println(_cameraMatrix.dump());
		System.out.println("____________________________________");
		System.out.println("-------------------------------------------------"); 
		
		System.out.println("rvecs - " + rvecs);
		System.out.println("tvecs - " + tvecs);
		
		
		////////////////////
		//Calibrate result
		///////////////////
		
		/////////////////Left camera//////////////
		
		
//		-----------------------------------------------
//		_distCoeffs:
//		____________________________________
//		i - 0   j - 0   val - -0.4273189088474619
//		i - 0   j - 1   val - 0.4074922272840817
//		i - 0   j - 2   val - 0.001022155392726571
//		i - 0   j - 3   val - 9.954957729016649E-4
//		i - 0   j - 4   val - -0.30454337013558924
//		____________________________________
//		Camera matrix:
//		____________________________________
//		[1455,451305438367, 0, 949,6442067569124;
//		 0, 1448,409239405494, 506,2433717899386;
//		 0, 0, 1]
//		____________________________________
//		-------------------------------------------------
		
		
		/////////////////Right camera//////////////
		
//		-----------------------------------------------
//		_distCoeffs:
//		____________________________________
//		i - 0   j - 0   val - -0.4000486253837702
//		i - 0   j - 1   val - 0.33159339833740525
//		i - 0   j - 2   val - -2.0822603992620402E-4
//		i - 0   j - 3   val - 0.0028646308893751655
//		i - 0   j - 4   val - -0.2762086817141912
//		____________________________________
//		Camera matrix:
//		____________________________________
//		[1463,22409287012, 0, 950,5336630504104;
//		 0, 1453,663147072743, 536,7415562869427;
//		 0, 0, 1]
//		____________________________________
//		-------------------------------------------------
		
		
		
		//Undistortion
		
		String codec = "h264";
		String sub_main = "main";
		String user = "admin";
		String password = "123Qwerty";
		String ip = "192.168.3.101";
//		String ip = "192.168.3.102";
		String port = "554";
		
		String cameraStreamURL = "rtsp://" + user + ":"+ password +"@"+ ip +":"+ port +"/"+codec+"/ch01/"+sub_main+"/av_stream";
		
		Camera camera = new Camera(cameraStreamURL);
		
		ImageWindow originalFrameWindow = new ImageWindow();

		ImageWindow calibrateFrameWindow = new ImageWindow();
		
		while(true)
		{
			Mat originalFrame = camera.getNextFrame();
			
			Mat undistoredFrame = new Mat();
			
			Imgproc.undistort(originalFrame, undistoredFrame, _cameraMatrix, _distCoeffs);
			
			originalFrameWindow.showFrame(originalFrame);
			calibrateFrameWindow.showFrame(undistoredFrame);
			
//			Imgproc.initUndistortRectifyMap(_cameraMatrix, _distCoeffs, Mat.eye(frameSize, CvType.CV_32F), newCameraMatrix, frameSize, m1type, map1, map2);
			
//			undistoredFrame.release();
//			originalFrame.release();
		
		}
		
		
		
	}

}
