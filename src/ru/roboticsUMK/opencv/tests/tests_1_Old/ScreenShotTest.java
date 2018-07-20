package ru.roboticsUMK.opencv.tests.tests_1_Old;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import ru.roboticsUMK.opencv.Camera;
import ru.roboticsUMK.opencv.ImageWindow;
import ru.roboticsUMK.opencv.OpenCVTestBase;

public class ScreenShotTest extends OpenCVTestBase
{
	
	private static final long serialVersionUID = 66714805163725732L;
	
	private Mat _leftFrame;
	private Mat _rightFrame;
	
	private boolean _isScreenShotClick = false;
	
	private int _imagesCount = 0;
	
	private final String _LEFT_CAMERA_IMAGES_FOLDER = "/home/user/Изображения/calibrateCameras/left_camera/";
	private final String _RIGHT_CAMERA_IMAGES_FOLDER = "/home/user/Изображения/calibrateCameras/right_camera/";
	

	public ScreenShotTest()
	{
		String codec = "h264";
		String sub_main = "main";
		String user = "admin";
		String password = "123Qwerty";
		String leftCameraIP = "192.168.3.101";
		String rightCameraIP = "192.168.3.102";
		String port = "554";
		
		String leftCameraStreamURL = "rtsp://" + user + ":"+ password +"@"+ leftCameraIP +":"+ port +"/"+codec+"/ch01/"+sub_main+"/av_stream";
		String rightCameraStreamURL = "rtsp://" + user + ":"+ password +"@"+ rightCameraIP +":"+ port +"/"+codec+"/ch01/"+sub_main+"/av_stream";
		
		Camera leftCamera = new Camera(leftCameraStreamURL);
		Camera rightCamera = new Camera(rightCameraStreamURL);
		
		JButton button = addButton("Screenshot");
		
		_leftFrame = new Mat();
		
		ImageWindow rightCameraWindow = new ImageWindow("Right Camera");
		ImageWindow leftCameraWindow = new ImageWindow("Left Camera");
		
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_isScreenShotClick = true;
			}
		});
		

		while (true)
		{
			_leftFrame = leftCamera.getNextFrame();
			_rightFrame = rightCamera.getNextFrame();
			
			if(!_leftFrame.empty() && !_rightFrame.empty())
			{
				if(_isScreenShotClick)
				{
					screenShot();
					_isScreenShotClick = false;
				}
				
				leftCameraWindow.showFrame(_leftFrame);
				rightCameraWindow.showFrame(_rightFrame);
			}
			
			_leftFrame.release();
			_rightFrame.release();
		}
		
	}
	
	private void screenShot()
	{
		_imagesCount++;
		
		saveImage(_LEFT_CAMERA_IMAGES_FOLDER, _leftFrame);
		saveImage(_RIGHT_CAMERA_IMAGES_FOLDER, _rightFrame);
	}
	
	
	protected void saveImage(String path, Mat frame)
	{
		File imagesDir = new File(path);
		
		if(!imagesDir.exists())
		{
			imagesDir.mkdirs();
		}
		String fileName = "Image_" + Integer.toString(_imagesCount) + ".jpeg";
		
		Imgcodecs.imwrite(path + fileName, frame);
	}

}
