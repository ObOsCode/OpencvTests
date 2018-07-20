package ru.roboticsnt.opencv.tests.tests_3;

import org.opencv.core.Mat;
import ru.roboticsnt.opencv.Camera;
import ru.roboticsnt.opencv.gui.swing.ImageWindow;
import ru.roboticsnt.opencv.gui.swing.OpenCVTestBase;


public class TestCamera extends OpenCVTestBase
{

	private static final long serialVersionUID = -1664078302739592279L;
	
	private Camera _camera1;
	private Camera _camera2;
	private Camera _camera3;

	private ImageWindow _window1;
	private ImageWindow _window2;
	private ImageWindow _window3;

	
	public TestCamera()
	{
		super("Test camera");

		String codec = "h264";
		String sub_main = "main";
		//		String streamURL1 = "rtsp://admin:123Qwerty@192.168.3.104/cam/realmonitor?channel=1&subtype=0";
//		String streamURL1 = "rtsp://192.168.3.105:554/ch01.264?ptype=udp";
		String streamURL1 = "rtsp://admin:123Qwerty@192.168.3.106:554/"+codec+"/ch01/"+sub_main+"/av_stream";
		String streamURL2 = "rtsp://admin:123Qwerty@192.168.3.107:554/"+codec+"/ch01/"+sub_main+"/av_stream";

		_camera1 = new Camera(streamURL1);
		_camera2 = new Camera(streamURL2);
//		_camera3 = new Camera(0);
		
		_window1 = new ImageWindow("Camera1");
		_window2 = new ImageWindow("Camera2");
//		_window3 = new ImageWindow("Camera3");
		
		startLoop();
	}
	
	
	@Override
	protected void loop()
	{
		super.loop();
		
		showCameraFrame(_camera1, _window1);
		showCameraFrame(_camera2, _window2);
//		showCameraFrame(_camera3, _window3);
	}
	
	
	private void showCameraFrame(Camera camera, ImageWindow window)
	{
		Mat frame = camera.getNextFrame();
		
		if(!frame.empty())
		{			
			window.showFrame(frame);
		}
		
		frame.release();
	}
}
