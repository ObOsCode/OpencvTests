package ru.roboticsUMK.opencv.tests.tests_3;

import org.opencv.core.Mat;

import ru.roboticsUMK.opencv.Camera;
import ru.roboticsUMK.opencv.ImageWindow;
import ru.roboticsUMK.opencv.OpenCVTestBase;

public class CompareTwoStreams extends OpenCVTestBase
{

	private static final long serialVersionUID = -9206136963842006410L;
	private Camera _mainCamera;
	private Camera _subCamera;
	private ImageWindow _mainWindow;
	private ImageWindow _subWindow;
	

	public CompareTwoStreams()
	{
		super("Main and sub stream");
		
		//Main and sub streams
//		_mainCamera = Camera.create(Camera.LEFT_CAMERA_IP, Camera.PORT, Camera.USER, Camera.PASSWORD, Camera.STREAM_TYPE_MAIN, Camera.CODEC_H264);
//		_subCamera = Camera.create(Camera.LEFT_CAMERA_IP, Camera.PORT, Camera.USER, Camera.PASSWORD, Camera.STREAM_TYPE_SUB, Camera.CODEC_H264);
		
		//Left and right cameras streams
//		_mainCamera = Camera.create(Camera.LEFT_CAMERA_IP);
//		_subCamera = Camera.create(Camera.RIGHT_CAMERA_IP);
		
		//IP and WEB cameras streams
		_subCamera = new Camera(0);
		_mainCamera = Camera.create(Camera.LEFT_CAMERA_IP);
		
		_mainWindow = new ImageWindow("Main stream");
		_subWindow = new ImageWindow("Sub stream");
		
		startLoop();
	}
	
	@Override
	protected void loop()
	{
		// TODO Auto-generated method stub
		super.loop();
		
		Mat mainFrame = _mainCamera.getNextFrame();
		Mat subFrame = _subCamera.getNextFrame();
		
		if(!mainFrame.empty())
		{			
			_mainWindow.showFrame(mainFrame);
		}
		
		if(!subFrame.empty())
		{			
			_subWindow.showFrame(subFrame);
		}
		
		
		mainFrame.release();
		subFrame.release();
		
		try
		{
			Thread.sleep(33);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
