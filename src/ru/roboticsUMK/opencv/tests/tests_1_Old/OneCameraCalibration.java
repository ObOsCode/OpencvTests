package ru.roboticsUMK.opencv.tests.tests_1_Old;

import java.util.ArrayList;
import java.util.List;

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
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import ru.roboticsUMK.opencv.Camera;
import ru.roboticsUMK.opencv.ImageWindow;
import ru.roboticsUMK.opencv.OpenCVTestBase;

public class OneCameraCalibration extends OpenCVTestBase
{

	private static final long serialVersionUID = 3904572983536179501L;
	
	private final int _IMAGES_COUNT = 50;
	
	private int _numCornersHor = 9;
	private int _numCornersVer = 6;
	
	
	private final String _IMAGES_FOLDER = "/home/user/Изображения/calibrateCameras/left_camera/";
//	private final String _IMAGES_FOLDER = "/home/user/Изображения/calibrateCameras/right_camera/";

	
	public OneCameraCalibration()
	{
		ImageWindow window = new ImageWindow("Calibrate");
		
		List<Mat> imagePoints = new ArrayList<>();
		List<Mat> objectPoints = new ArrayList<>();
		
		MatOfPoint3f objCorners = new MatOfPoint3f();
		
		float squareSize = 0.027f;
		
		for (int i = 0; i < _numCornersVer; ++i) 
		{
			for (int j = 0; j < _numCornersHor; ++j)
			{
				Point3 pt = new Point3(j * squareSize, i * squareSize, 0.0d);
				objCorners.push_back(new MatOfPoint3f(pt));
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
				
				objectPoints.add(objCorners);
				imagePoints.add(imageCorners);
			}
			
			Imgproc.putText(frame, Integer.toString(i + 1), new Point(frame.width()/2, frame.height()/2), Core.FONT_ITALIC, 10.0, new Scalar(50, 50, 200), 10);
			
			window.showFrame(frame);
		}
		
		
		//Calibrate
		
		Mat distCoeffs = new Mat();
		
		Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
		cameraMatrix.put(0, 0, 1);
		cameraMatrix.put(1, 1, 1);
		
		List<Mat> rvecs = new ArrayList<>();
		List<Mat> tvecs = new ArrayList<>();

		
		double calibrateError = Calib3d.calibrateCamera(objectPoints, imagePoints, frameSize, cameraMatrix, distCoeffs, rvecs, tvecs);
		
//		double calibrateError = Calib3d.calibrateCameraExtended(objectPoints, imagePoints, imageSize, cameraMatrix, distCoeffs, rvecs, tvecs, stdDeviationsIntrinsics, stdDeviationsExtrinsics, perViewErrors, flags, criteria)
		
		System.out.println("-----------------------------------------------");
		System.out.println("calibrateError:");
		System.out.println(calibrateError);
		System.out.println("____________________________________");
		
		System.out.println("-----------------------------------------------");
		System.out.println("_distCoeffs:");
		System.out.println(distCoeffs.dump());
		System.out.println("____________________________________");

		System.out.println("____________________________________");
		System.out.println("Camera matrix:");
		System.out.println("____________________________________");
		System.out.println(cameraMatrix.dump());
		System.out.println("____________________________________");
		System.out.println("-------------------------------------------------"); 
		
		
		System.out.println("rvecs (" + rvecs.size() + "): ");
		System.out.println("____________________________________");
		for (int j = 0; j < rvecs.size(); j++)
		{
			Mat vec = rvecs.get(j);
			System.out.print(vec.dump() + " / ");
		}
		System.out.println("____________________________________");
		System.out.println("-------------------------------------------------"); 
		
		
		System.out.println("tvecs (" + tvecs.size() + "): ");
		System.out.println("____________________________________");
		for (int k = 0; k < tvecs.size(); k++)
		{
			Mat vec = tvecs.get(k);
			System.out.print(vec.dump() + " / ");
		}
		System.out.println("____________________________________");
		System.out.println("-------------------------------------------------"); 
		
		
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
		
		ImageWindow originalFrameWindow = new ImageWindow("Original");
		ImageWindow calibrateFrameWindow = new ImageWindow("Undistort");
		
		Mat map1 = new Mat();
		Mat map2 = new Mat();
		Mat newMatrix = new Mat();
		Mat r = new Mat();
		
		Imgproc.initUndistortRectifyMap(cameraMatrix, distCoeffs, r, newMatrix, frameSize, CvType.CV_32FC1, map1, map2);
//		Calib3d.initUndistortRectifyMap(K, D, r, P, size, m1type, map1, map2);
		
		while(true)
		{
			Mat originalFrame = camera.getNextFrame();
			
			if(!originalFrame.empty())
			{
				Mat undistoredFrame = new Mat();
				
//				Imgproc.undistort(originalFrame, undistoredFrame, cameraMatrix, distCoeffs);
				Imgproc.remap(originalFrame, undistoredFrame, map1, map2, Imgproc.INTER_LINEAR);
				
				originalFrameWindow.showFrame(originalFrame);
				calibrateFrameWindow.showFrame(undistoredFrame);
				
				undistoredFrame.release();
				originalFrame.release();
				
				try
				{
					Thread.sleep(33);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		
		}
		
		
		
	}

}
