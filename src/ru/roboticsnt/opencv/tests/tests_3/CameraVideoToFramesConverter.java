package ru.roboticsnt.opencv.tests.tests_3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import ru.roboticsnt.opencv.Camera;
import ru.roboticsnt.opencv.gui.swing.ImageWindow;
import ru.roboticsnt.opencv.gui.swing.OpenCVTestBase;


public class CameraVideoToFramesConverter extends OpenCVTestBase
{

	private static final long serialVersionUID = 1002302397297754285L;
	
	private final String _DEFAULT_IMAGES_PATH = "/home/user/Изображения/DataSets/temp/";

	private int _DEFAULT_IMAGE_WIDTH = 128;
	private int _DEFAULT_IMAGE_HEIGHT = 128;
	private int _DEFAULT_FRAME_INTERVAL = 3;
	
	private ImageWindow _originalWindow;

	private Camera _camera;

	private JTextField _imagesPathTF;
	
	private int _imagesCount = 0;
	
	private int _nextImageIndex = -1;
	
	private boolean _isConvertedNow = false;

	private JTextField _imageWidthTF;

	private JTextField _imageHeightTF;

	private JTextField _frameIntervalTF;

	private JTextField _imagesCountTF;

	private ImageWindow _croperWindow;
	
	
	public CameraVideoToFramesConverter()
	{
		_imagesPathTF = addSelectFolder(_DEFAULT_IMAGES_PATH);
		
		_imageWidthTF = addTextFieldWithLabel("Image width", true);
		_imageHeightTF = addTextFieldWithLabel("Image height", true);
		_frameIntervalTF = addTextFieldWithLabel("Frame interval", true);
		_imagesCountTF = addTextFieldWithLabel("Images count", true);

		_imageWidthTF.setText(Integer.toString(_DEFAULT_IMAGE_WIDTH));
		_imageHeightTF.setText(Integer.toString(_DEFAULT_IMAGE_HEIGHT));
		_frameIntervalTF.setText(Integer.toString(_DEFAULT_FRAME_INTERVAL));
		_imagesCountTF.setText(Integer.toString(0));
		
		final JButton startButton = addButton("Start");
		
		startButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(_isConvertedNow)
				{
					startButton.setText("Start");
				}
				else
				{
					startButton.setText("Pause");
				}
				
				_isConvertedNow = !_isConvertedNow;
			}
		});
		
		_originalWindow = new ImageWindow("Original image");
		_croperWindow = new ImageWindow("Croper window");
		
//		_camera = Camera.create(Camera.LEFT_CAMERA_IP);
//		
//		configLoader.loadSingleCalibrationConfig(_camera, ConfigLoader.LEFT_CAMERA_CALIBRATION_FILE);
		
		_camera = new Camera(0);
		
		startLoop();
	}
	
	
	@Override
	protected void loop()
	{
		super.loop();
		
		Mat originalFrame;
		
		if(_camera.isCalibrated())
		{
			originalFrame = _camera.getNextFrameUndistort();
		}else
		{
			originalFrame = _camera.getNextFrame();
		}
		
		if(!originalFrame.empty())
		{
			
			Mat croperFrame = originalFrame.clone();
			
			Rect cropQuad = getCropQuad(originalFrame);
			croperFrame = new Mat(croperFrame, cropQuad);
			
			Imgproc.resize(croperFrame, croperFrame, new Size(Integer.valueOf(_imageWidthTF.getText()), Integer.valueOf(_imageHeightTF.getText())));
			
			Point pt1 = new Point(cropQuad.x, cropQuad.y);
			Point pt2 = new Point(cropQuad.x + cropQuad.width, cropQuad.y + cropQuad.height);
			Imgproc.rectangle(originalFrame, pt1, pt2, new Scalar(20, 20, 200), 5);

			if(_isConvertedNow)
			{				
				screenShot(croperFrame);
			}
			
			_originalWindow.showFrame(originalFrame);
			_croperWindow.showFrame(croperFrame);
			
			croperFrame.release();
		}
		
		originalFrame.release();
	}
	
	
	private Rect getCropQuad(Mat frame)
	{	
		Rect rect;
		int size;
		
		if(frame.width()>frame.height())
		{
			size = frame.height();
			rect = new Rect((frame.width() - size)/2, 0, size, size);
		}else
		{
			size = frame.width();
			rect = new Rect(0, (frame.height() - size)/2, size, size);
		}
		
		return rect;
	}
	
	
	private void screenShot(Mat frame)
	{
		_nextImageIndex++;

		int interval = Integer.valueOf(_frameIntervalTF.getText());
		
		if((_nextImageIndex % interval) != 0)
		{
			return;
		}
		
		String imagesCountString = Integer.toString(_imagesCount);
		String path = _imagesPathTF.getText();
		String fileName = "/Img_" + imagesCountString + ".jpeg";
		
		saveImage(frame, path, fileName);

		_imagesCount++;
		_imagesCountTF.setText(imagesCountString);
	}

}
