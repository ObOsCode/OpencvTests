package ru.roboticsnt.opencv.tests.tests_2_0;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import ru.roboticsnt.opencv.Camera;
import ru.roboticsnt.opencv.gui.swing.ImageWindow;
import ru.roboticsnt.opencv.gui.swing.OpenCVTestBase;


public class OpenCVFunctionsTest extends OpenCVTestBase
{

	private static final long serialVersionUID = 9018806398903577116L;
	
	private Camera _camera;
	private ImageWindow _originalWindow;
	private ImageWindow _convertedlWindow;
	
	
	public OpenCVFunctionsTest()
	{
		_originalWindow = new ImageWindow("Original");
		_convertedlWindow = new ImageWindow("Converted");
		
		_camera = Camera.create("192.168.3.102");
		
		startLoop();
	}
	
	
	@Override
	protected void loop()
	{
		super.loop();
		
		Mat frame = _camera.getNextFrame();
		
		Mat convertedFrame = new Mat();
		

		if(!frame.empty())
		{
//			Imgproc.blur(frame, convertedFrame, new Size(10, 10));
//			Imgproc.pyrDown(frame, convertedFrame);
			Imgproc.Canny(frame, convertedFrame, 10, 200);
			
			_originalWindow.showFrame(frame);
			_convertedlWindow.showFrame(convertedFrame);
		}
		
		convertedFrame.release();
		frame.release();
	}

}
