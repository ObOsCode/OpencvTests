package ru.roboticsnt.opencv.tests.tests_1_Old.camerasControllerTest;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.opencv.videoio.VideoCapture;



public class CamerasController
{
	
	private CameraPanel _panel;
	
	private Mat _currentFrame;
	
	private VideoCapture _video;
	
	private final int _VIDEO_WIDTH = 1280;
	private final int _VIDEO_HEIGHT = 720;
	
	private final int _CAMERA_VERTICAL_ANGLE = 48;//Degrees
	private final int _CAMERA_HORIZONTAL_ANGLE = 85;//Degrees
	
	private int _cameraRotation = 38;
	
	//Must be calculated
	private final double _VISIBLE_AREA_WIDTH = 1.4;//Meters
	private final double _VISIBLE_AREA_HEIGHT = 2.6;//Meters

	
	public CamerasController(CameraPanel panel)
	{
		_panel = panel;
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		String codec = "h264";
		
		String sub_main = "main";
		
		String cameraStreamURL = "rtsp://admin:123Qwerty@192.168.0.202:55401/"+codec+"/ch01/"+sub_main+"/av_stream";
		
		_video = new VideoCapture(cameraStreamURL); 
        
        _currentFrame = new Mat();
        
    	ReadVideoThread readVideoThread = new ReadVideoThread();
    	readVideoThread.start();
        
        ShowFrameThread showFrameThread = new ShowFrameThread();
        showFrameThread.start();
	}
	
	
	private boolean detectObject(Mat frame, Rect objRect)
	{
		
		Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2HSV);
		
		//Red
		Core.inRange(frame, new Scalar(119, 218, 166), new Scalar(245, 255, 237), frame);	            	
		
		
		//convert to bgr to display
		Imgproc.cvtColor(frame, frame, Imgproc.COLOR_GRAY2BGR);
		
		boolean isDetect = false;
		
		Point objPoint = new Point(0, 0);
		
		for (int i = 0; i < frame.rows(); i++)
		{
			for (int j = 0; j < frame.cols(); j++)
			{
				double[] pixel = frame.get(i, j);
				
				if(pixel[0]!=0)
				{
					objPoint = new Point(j, i);
												
					Mat mask = Mat.zeros(frame.rows()+2, frame.cols()+2, CvType.CV_8U);
//					
					int objPixcount = Imgproc.floodFill(frame, mask, objPoint, new Scalar(200), objRect, new Scalar(0), new Scalar(0), Imgproc.FLOODFILL_MASK_ONLY);  
//					System.out.println(objPixcount);
					if(objPixcount>20)
					{
						isDetect = true;
						
						break;
					}
				}
			}
		}
		
		return isDetect;
	}
	
	
	private void perspectiveToPlane(Mat frame)
	{
		
		double K = (_VIDEO_HEIGHT/2) / Math.tan(Math.toRadians(_CAMERA_VERTICAL_ANGLE/2));
		
		double vanishingPointDistance = _VIDEO_HEIGHT/2 + K * Math.tan(Math.toRadians(90 - _cameraRotation));
		
		double perspectiveOffsetX = _VIDEO_HEIGHT * Math.tan(Math.toRadians(90 - Math.toDegrees(Math.atan(vanishingPointDistance/(_VIDEO_WIDTH/2)))));
		
		
		List<Point> target = new ArrayList<Point>();
		List<Point> corners = new ArrayList<Point>();
		
		corners.add(new Point(perspectiveOffsetX, 0)); 
		corners.add(new Point(_VIDEO_WIDTH-perspectiveOffsetX, 0));
		corners.add(new Point(_VIDEO_WIDTH, _VIDEO_HEIGHT));
		corners.add(new Point(0, _VIDEO_HEIGHT));
		
		
		double resultWidth = _VISIBLE_AREA_WIDTH * 200;
		double resultHeight = _VISIBLE_AREA_HEIGHT * 200;
		
		
		target.add(new Point(0, 0)); 
		target.add(new Point(resultWidth, 0)); 
		target.add(new Point(resultWidth, resultHeight)); 
		target.add(new Point(0, resultHeight)); 
		
		
		Mat cornersMat = Converters.vector_Point2f_to_Mat(corners); 
		Mat targetMat = Converters.vector_Point2f_to_Mat(target); 
		Mat transform = Imgproc.getPerspectiveTransform(cornersMat, targetMat); 
		
		
		System.out.println("__________________________________________");
		System.out.println("transform - " + transform.dump());
		System.out.println("__________________________________________");
		
		Imgproc.warpPerspective(frame, frame, transform, new Size(resultWidth, resultHeight)); 
	}
	
	
	private void showFrame(Mat frame, Size newSize)
	{
		if(newSize!=null)
		{
			Imgproc.resize(frame, frame, newSize);
		}
		
		MatOfByte _buffer = new MatOfByte();
    	
    	Imgcodecs.imencode(".jpeg", frame, _buffer);
    	
    	_panel.showFrame(new ByteArrayInputStream(_buffer.toArray()));
	}
	
	
	
	
	private class ReadVideoThread extends Thread
	{
		@Override
		public void run()
		{
			super.run();
			
			while (true)
			{
				_video.read(_currentFrame);
			}
		}
	}
	
	
	private class ShowFrameThread extends Thread
	{
		
		@Override
		public void run()
		{
			super.run();
			
	        while (true)
			{	
	        	if(!_currentFrame.empty())
	        	{
	        		Mat detectObjectFrame = _currentFrame.clone();
	        		Mat originalFrame = _currentFrame.clone();
	        		
	        		Mat drawFrame = Mat.zeros(_VIDEO_HEIGHT, _VIDEO_WIDTH, CvType.CV_8UC3);
	        		
					Mat displayFrame = Mat.zeros(_currentFrame.rows(), _currentFrame.cols(), CvType.CV_32FC3);
					
					Imgproc.accumulate(originalFrame, displayFrame);	
					
					perspectiveToPlane(detectObjectFrame);
					perspectiveToPlane(displayFrame);
					
					Rect objRect = new Rect();
					
//					Mat cameraMatrix = new Mat();
//					Mat distCoeffs = new Mat();
//					Calib3d.calibrateCamera(objectPoints, imagePoints, imageSize, cameraMatrix, distCoeffs, rvecs, tvecs)
//					Imgproc.undistort(displayFrame, displayFrame, cameraMatrix, distCoeffs);
//					Calib3d.findChessboardCorners(image, patternSize, corners);

					if(detectObject(detectObjectFrame, objRect))
					{
//						drawObjectCoords(objRect, _displayFrame);
						
						Imgproc.circle(displayFrame, new Point(objRect.x, objRect.y), 10, new Scalar(120,120,255));
//						System.out.println("objRect.y - " + (detectObjectFrame.height() - objRect.y)/200.00);
//						System.out.println("objRect.x - " + objRect.x/200.00);
					}
	        		
					showFrame(displayFrame, null);

		        	detectObjectFrame.release();
		        	originalFrame.release();
		        	displayFrame.release();
	        	}
				
	        	try
				{
					Thread.sleep(30);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
