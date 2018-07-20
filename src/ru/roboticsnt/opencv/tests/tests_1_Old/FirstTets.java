package ru.roboticsnt.opencv.tests.tests_1_Old;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import ru.roboticsnt.opencv.Camera;
import ru.roboticsnt.opencv.gui.swing.ImageWindow;
import ru.roboticsnt.opencv.gui.swing.OpenCVTestBase;


public class FirstTets extends OpenCVTestBase
{

	private static final long serialVersionUID = -4826530828271058956L;

	public FirstTets()
	{
		ImageWindow window = new ImageWindow("Window 1"); 
		ImageWindow window2 = new ImageWindow("Window 2");
		
		String codec = "h264";
		String sub_main = "main";
		String cameraStreamURL = "rtsp://admin:123Qwerty@192.168.3.102:554/"+codec+"/ch01/"+sub_main+"/av_stream";
		

		String imagePath = "/home/user/projects/eclipse/OpenCVStart/resources/sbt/src/main/resources/img1.png";
		Mat image = Imgcodecs.imread(imagePath);
		window.showFrame(image);
		
		Camera camera = new Camera(cameraStreamURL);
		
		while (true)
		{
			Mat originalFrame = camera.getNextFrame(); 
			Mat convertedFrame = new Mat();

			if(!originalFrame.empty())
			{
				convertedFrame = originalFrame.clone();
				
//				Imgproc.cvtColor(originalFrame, convertedFrame, CvType.CV_32FC1);
//				Imgproc.blur(frame, frame, new Size(30, 30), new Point(20, 15));
//				Imgproc.GaussianBlur(frame, frame, new Size(15, 15), 0, 16);
//				Imgproc.medianBlur(frame, frame, 15);
//				Imgproc.erode(originalFrame, convertedFrame, new Mat());
//				Imgproc.floodFill(originalFrame, new Mat(), new Point(100, 200), new Scalar(123, 120, 140));
//				Imgproc.threshold(convertedFrame, convertedFrame, 40, 200, Imgproc.THRESH_BINARY);
//				Imgproc.Sobel(src, dst, ddepth, dx, dy, ksize, scale, delta, borderType);
//				Imgproc.HoughLines(originalFrame, convertedFrame, 85, 40, 5);
//				Imgproc.Canny(image, edges, threshold1, threshold2);
//				Imgproc.warpPerspective(src, dst, M, dsize);
		
//				Core.merge(mv, dst);
				
				
//				Imgproc.drawMarker(convertedFrame, new Point(200,200), new Scalar(14,15,200));

				
				window.showFrame(originalFrame);
				window2.showFrame(convertedFrame);
			}
			
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
