package ru.roboticsUMK.opencv.tests.tests_1_Old.camerasControllerTest;

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

import ru.roboticsUMK.Utils;

public class CamerasController2
{
	private CameraPanel _panel;
	
	private Mat _frame;
	
	private Mat _displayFrame;
	
	private VideoCapture _video;
	
	private final int _VIDEO_WIDTH = 1280;
	private final int _VIDEO_HEIGHT = 720;
	
	private final int _CAMERA_VERTICAL_ANGLE = 48;//Degrees
	private final int _CAMERA_HORIZONTAL_ANGLE = 85;//Degrees
	
	private int _cameraRotation = 38;
	
	//Must be calculated 
	private final double _VISIBLE_AREA_WIDTH = 1.4;//Meters
	private final double _VISIBLE_AREA_HEIGHT = 2.6;//Meters
	private int _rulerCenterY = 240;//Pixel
	
	
	public CamerasController2(CameraPanel panel)
	{
		_panel = panel;
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		String codec = "h264";
		
		String sub_main = "main";
		
		String cameraStreamURL = "rtsp://admin:123Qwerty@192.168.0.202:554/"+codec+"/ch01/"+sub_main+"/av_stream";
		
		_video = new VideoCapture(cameraStreamURL); 
        
        _frame = new Mat();
        
    	ReadVideoThread readVideoThread = new ReadVideoThread();
    	readVideoThread.start();
        
        ShowFrameThread showFrameThread = new ShowFrameThread();
        showFrameThread.start();
	}
	
	
	private void framePerspectiveTransform(Mat frame, List<Point> target, List<Point> corners)
	{
		Mat cornersMat = Converters.vector_Point2f_to_Mat(corners); 
		Mat targetMat = Converters.vector_Point2f_to_Mat(target); 
		Mat transform = Imgproc.getPerspectiveTransform(cornersMat, targetMat); 
		Imgproc.warpPerspective(frame, frame, transform, frame.size()); 
	}
	
	
	private void gridPerspectiveTransform(Mat frame)
	{
		_cameraRotation = _panel.cameraRotationSlider.getValue();
		
		double K = (_VIDEO_HEIGHT/2) / Math.tan(Math.toRadians(_CAMERA_VERTICAL_ANGLE/2));
		
		double vanishingPointDistance = _VIDEO_HEIGHT/2 + K * Math.tan(Math.toRadians(90 - _cameraRotation));
		
		double perspectiveOffsetX = _VIDEO_HEIGHT * Math.tan(Math.toRadians(90 - Math.toDegrees(Math.atan(vanishingPointDistance/(_VIDEO_WIDTH/2)))));
		
		List<Point> target = new ArrayList<Point>();
		List<Point> corners = new ArrayList<Point>();
		
		target.add(new Point(perspectiveOffsetX, 0)); 
		target.add(new Point(_VIDEO_WIDTH-perspectiveOffsetX, 0)); 
		target.add(new Point(_VIDEO_WIDTH, _VIDEO_HEIGHT)); 
		target.add(new Point(0, _VIDEO_HEIGHT));
		
		corners.add(new Point(0, 0));
		corners.add(new Point(frame.cols(), 0)); 
		corners.add(new Point(frame.cols(), frame.rows())); 
		corners.add(new Point(0, frame.rows())); 
		
		framePerspectiveTransform(frame,target, corners);
	}
	
	
	private void displayPerspectiveTransform(Mat frame)
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
		
		
		double resultWidth = _VISIBLE_AREA_WIDTH *100;
		double resultHeight = _VISIBLE_AREA_HEIGHT *100;
		
//		System.out.println("resultWidth " + resultWidth);
//		System.out.println("resultHeight " + resultHeight);
		
		target.add(new Point(0, 0)); 
		target.add(new Point(resultWidth, 0)); 
		target.add(new Point(resultWidth, resultHeight)); 
		target.add(new Point(0, resultHeight)); 
		
		
		Mat cornersMat = Converters.vector_Point2f_to_Mat(corners); 
		Mat targetMat = Converters.vector_Point2f_to_Mat(target); 
		Mat transform = Imgproc.getPerspectiveTransform(cornersMat, targetMat); 
		Imgproc.warpPerspective(frame, frame, transform, new Size(resultWidth, resultHeight)); 

//		_cameraRotation = _panel.cameraRotationSlider.getValue();
//		
//		double K = (_VIDEO_HEIGHT/2) / Math.tan(Math.toRadians(_CAMERA_VERTICAL_ANGLE/2));
//		
//		double vanishingPointDistance = _VIDEO_HEIGHT/2 + K * Math.tan(Math.toRadians(90 - _cameraRotation));
//		
//		double perspectiveOffsetX = _VIDEO_HEIGHT * Math.tan(Math.toRadians(90 - Math.toDegrees(Math.atan(vanishingPointDistance/(_VIDEO_WIDTH/2)))));
//		
//		
//		List<Point> target = new ArrayList<Point>();
//		List<Point> corners = new ArrayList<Point>();
//		
//		corners.add(new Point(perspectiveOffsetX, 0)); 
//		corners.add(new Point(_VIDEO_WIDTH-perspectiveOffsetX, 0));
//		corners.add(new Point(_VIDEO_WIDTH, _VIDEO_HEIGHT));
//		corners.add(new Point(0, _VIDEO_HEIGHT));
//		
//		target.add(new Point(0, 0)); 
//		target.add(new Point(frame.cols(), 0)); 
//		target.add(new Point(frame.cols(), frame.rows())); 
//		target.add(new Point(0, frame.rows())); 
//		
//		framePerspectiveTransform(frame,target, corners);
	}
	
	
	private void drawGrid(Mat frame)
	{
		Scalar color = new Scalar(150,200,150); 
		int cols = 7;
		int cellWidth = _VIDEO_WIDTH/cols;
		
		Rect[] rectList = new Rect[cols];
		
		for (int i = 0; i < rectList.length; i++)
		{
			Rect rect = new Rect(i*cellWidth, 0, cellWidth, _VIDEO_HEIGHT);
				
			rectList[i] = rect;
			
			Point pt1 = new Point(rect.x, rect.y);
			Point pt2 = new Point(rect.x + rect.width, rect.y + rect.height);
			
			Imgproc.rectangle(frame, pt1, pt2, color, 2);
		}
		
		//Center line
		Imgproc.line(frame, new Point(_VIDEO_WIDTH/2, 0), new Point(_VIDEO_WIDTH/2, _VIDEO_HEIGHT), color, 1);
	}
	
	
	private boolean detectObject(Mat frame, Rect objRect)
	{
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
					
					if(objPixcount>1000)
					{
						isDetect = true;
						
						break;
					}
				}
			}
		}
		return isDetect;
	}
	
	
	private void drawObjectCoords(Rect objRect, Mat frame)
	{
		_rulerCenterY = _panel.rulerCenterSlider.getValue();
		
		
		Point objCenterPoint = new Point(objRect.x + objRect.width/2, objRect.y + objRect.height/2);
		
		int lineThikness = 4;
		Scalar color = new Scalar(100,100,255);
		
		//Show Y distance
		double K = (_VIDEO_HEIGHT - _rulerCenterY)/_rulerCenterY;
		double distance = _VISIBLE_AREA_HEIGHT - _VISIBLE_AREA_HEIGHT * K/(_VIDEO_HEIGHT/objCenterPoint.y - 1 + K);
		String distanceString = "Y " + Double.toString(Utils.round(distance,4)) + "(m)";
		
		System.out.println("objCenterPointY - " + objCenterPoint.y);
		
		Imgproc.putText(frame, distanceString, new Point(objCenterPoint.x + 50, objCenterPoint.y + (_VIDEO_HEIGHT-objCenterPoint.y)/2), Core.FONT_ITALIC, 1.3, color,lineThikness);

		//Show X distance
		distance = _VISIBLE_AREA_WIDTH/2 * ((objCenterPoint.x - _VIDEO_WIDTH/2)/(_VIDEO_WIDTH/2 - (_VIDEO_HEIGHT - objCenterPoint.y)/Math.tan(Math.toRadians(_rulerCenterY))));	
		distanceString = "X " + Double.toString(Utils.round(distance,2)) + "(m)";
		
		Imgproc.putText(frame, distanceString, new Point(objCenterPoint.x - (objCenterPoint.x - _VIDEO_WIDTH/2)/2 - 70, objCenterPoint.y-30), Core.FONT_ITALIC, 1.3, color,lineThikness);
		
		//Y line
		Imgproc.line(frame, objCenterPoint, new Point(objCenterPoint.x, _VIDEO_HEIGHT), color, lineThikness);
		
		//X line
		Imgproc.line(frame, objCenterPoint, new Point(_VIDEO_WIDTH/2, objCenterPoint.y), color, lineThikness);
		
		//Object Center point
		Imgproc.circle(frame, objCenterPoint, 10, color, 2);
		
		//Draw object rect
		Imgproc.rectangle(frame, new Point(objRect.x, objRect.y), new Point(objRect.x+objRect.width, objRect.y+objRect.height), new Scalar(100,100,255), lineThikness);
	
		//Rule Center Y
		Imgproc.circle(frame, new Point(_VIDEO_WIDTH/2, _rulerCenterY), 12, color, 8);
	
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
				_video.read(_frame);
			}
		}
	}
	
	
	private class ShowFrameThread extends Thread
	{
		
		@Override
		public void run()
		{
			super.run();
			
//			Mat gridFrame = Mat.zeros(_VIDEO_HEIGHT, _VIDEO_WIDTH, CvType.CV_8UC3);
//			drawGrid(gridFrame);
//			gridPerspectiveTransform(gridFrame);
			
			
	        while (true)
			{	
	        	if(!_frame.empty())
	        	{
	        		Mat gridFrame = Mat.zeros(_VIDEO_HEIGHT, _VIDEO_WIDTH, CvType.CV_8UC3);
	    			drawGrid(gridFrame);
	    			gridPerspectiveTransform(gridFrame);
	        		
	        		Mat convertedFrame = _frame.clone();
	        		Mat originalFrame = _frame.clone();
	        		
					Imgproc.cvtColor(convertedFrame, convertedFrame, Imgproc.COLOR_RGB2HSV);
		    		
					
					
//					int rMIn = _panel.rMinSlider.getValue();
//					int gMIn = _panel.gMinSlider.getValue();
//					int bMIn = _panel.bMinSlider.getValue();
//					
//					int rMax = _panel.rMaxSlider.getValue();
//					int gMax = _panel.gMaxSlider.getValue();
//					int bMax = _panel.bMaxSlider.getValue();
					
					
//					System.out.println("rMin = " + Integer.toString(rMIn));
//					System.out.println("gMin = " + Integer.toString(gMIn));
//					System.out.println("bMin = " + Integer.toString(bMIn));
//					
//					System.out.println("rMax = " + Integer.toString(rMax));
//					System.out.println("gMax = " + Integer.toString(gMax));
//					System.out.println("bMax = " + Integer.toString(bMax));


					//Custom
//					Core.inRange(convertedFrame, new Scalar(rMIn, gMIn, bMIn), new Scalar(rMax, gMax, bMax), convertedFrame);	            	
					
					//Red
					Core.inRange(convertedFrame, new Scalar(119, 218, 166), new Scalar(245, 255, 237), convertedFrame);	            	
					
					
//					Imgproc.GaussianBlur(convertedFrame, convertedFrame, new Size(5,5), 0);
					
					//convert to bgr to display
					Imgproc.cvtColor(convertedFrame, convertedFrame, Imgproc.COLOR_GRAY2BGR);
					

//					System.out.println("_displayFrame.rows() - " + _displayFrame.rows());
//					System.out.println("_displayFrame.cols() - " + _displayFrame.cols());
//					System.out.println("_displayFrame.type() - " + _displayFrame.type());
//					
//					System.out.println("gridFrame.rows() - " + gridFrame.rows());
//					System.out.println("gridFrame.cols() - " + gridFrame.cols());
//					System.out.println("gridFrame.type() - " + gridFrame.type());
					
					
					_displayFrame = Mat.zeros(_frame.rows(), _frame.cols(), CvType.CV_32FC3);
					Imgproc.accumulate(originalFrame, _displayFrame);
					
//					displayPerspectiveTransform(convertedFrame);
					
					
					Rect objRect = new Rect();
					
					if(detectObject(convertedFrame, objRect))
					{
						drawObjectCoords(objRect, _displayFrame);
					}
					
					Imgproc.accumulate(gridFrame, _displayFrame);
					
					
					showFrame(_displayFrame, new Size(960, 540));
//					showFrame(convertedFrame, null);
		        	
		        	convertedFrame.release();
		        	originalFrame.release();
		        	_displayFrame.release();
	        	}
				
	        	
	        	try
				{
					Thread.sleep(200);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}
