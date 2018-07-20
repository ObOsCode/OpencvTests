package ru.roboticsUMK.opencv.tests.tests_1_Old;

import org.opencv.calib3d.StereoBM;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import ru.roboticsUMK.opencv.ImageWindow;
import ru.roboticsUMK.opencv.OpenCVTestBase;

public class DepthMatFromTwoImages extends OpenCVTestBase
{

	private static final long serialVersionUID = 1429863127366966481L;

	ImageWindow disparityWindow = new ImageWindow("Depth map");
	
	public DepthMatFromTwoImages()
	{
		Mat leftImage = Imgcodecs.imread("/home/user/Изображения/calibrateCameras/Tsukuba_L-300x225.png");
		Mat rightImage = Imgcodecs.imread("/home/user/Изображения/calibrateCameras/Tsukuba_R-300x225.png");
		
//		Mat leftImage = Imgcodecs.imread("/home/user/Изображения/calibrateCameras/left_camera/Image_1.jpeg");
//		Mat rightImage = Imgcodecs.imread("/home/user/Изображения/calibrateCameras/right_camera/Image_1.jpeg");
		
		Imgproc.cvtColor(leftImage, leftImage, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(rightImage, rightImage, Imgproc.COLOR_BGR2GRAY);
		
		Mat disparity = new Mat();
		
		StereoBM stereo = StereoBM.create(16, 15);
		stereo.compute(leftImage, rightImage, disparity);
		
		disparityWindow.showFrame(disparity);
	}

}
