package ru.roboticsUMK.opencv.tests.tests_1_Old;

import java.awt.FlowLayout;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import ru.roboticsUMK.Utils;

public class TractorCorrectionTest extends JFrame
{

	private static final long serialVersionUID = 3383809227814407250L;
	
	private JLabel _label;
	
	private int _VIDEO_WIDTH = 320;
	private int _VIDEO_HEIGHT = 240;
	
	private final int _RECT_COUNT = 4;
	
	private final int _RECT_HEIGHT = 80;
	
	
	
	public TractorCorrectionTest() 
	{
		setSize(1000, 600);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		
		_label = new JLabel();
		panel.add(_label);
		
		JPanel slidersPanel = new JPanel();
		slidersPanel.setSize(200, 400);
		slidersPanel.setLayout(new BoxLayout(slidersPanel, BoxLayout.Y_AXIS));
		getContentPane().add(slidersPanel);
		
		JSlider rMinSlider = new JSlider(0, 255, 100);
		JSlider rMaxSlider = new JSlider(0, 255, 100);
		JSlider gMinSlider = new JSlider(0, 255, 100);
		JSlider gMaxSlider = new JSlider(0, 255, 100);
		JSlider bMinSlider = new JSlider(0, 255, 100);
		JSlider bMaxSlider = new JSlider(0, 255, 100);
		
		JLabel rMinSliderLabel = new JLabel("Red min");
		JLabel rMaxSliderLabel = new JLabel("Red max");
		JLabel gMinSliderLabel = new JLabel("Green min");
		JLabel gMaxSliderLabel = new JLabel("Green max");
		JLabel bMinSliderLabel = new JLabel("Blue min");
		JLabel bMaxSliderLabel = new JLabel("Blue max");
		
		
		slidersPanel.add(rMinSliderLabel);
		slidersPanel.add(rMinSlider);
		
		slidersPanel.add(rMaxSliderLabel);
		slidersPanel.add(rMaxSlider);
		
		slidersPanel.add(gMinSliderLabel);
		slidersPanel.add(gMinSlider);
		
		slidersPanel.add(gMaxSliderLabel);
		slidersPanel.add(gMaxSlider);
		
		slidersPanel.add(bMinSliderLabel);
		slidersPanel.add(bMinSlider);
		
		slidersPanel.add(bMaxSliderLabel);
		slidersPanel.add(bMaxSlider);
		
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		VideoCapture video = new VideoCapture(0);
		
	    video.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, _VIDEO_WIDTH);
	    video.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, _VIDEO_HEIGHT);
	    
	    int rectWidth = _VIDEO_WIDTH/_RECT_COUNT;
	    int rectY = 0;
	    
	    Rect rect1 = new Rect(0, rectY, rectWidth, _RECT_HEIGHT);
	    Rect rect2 = new Rect(rectWidth, rectY, rectWidth, _RECT_HEIGHT);
	    Rect rect3 = new Rect(rectWidth * 2, rectY, rectWidth, _RECT_HEIGHT);
	    Rect rect4 = new Rect(rectWidth * 3, rectY, rectWidth, _RECT_HEIGHT);
		
		Mat showFrame = new Mat(_VIDEO_WIDTH, _VIDEO_HEIGHT, CvType.CV_8UC3);
		
		Mat binaryFrame;
		
		
		while (true)
		{
			if(video.isOpened())
			{
				video.read(showFrame);
				
				binaryFrame = showFrame.clone();
				
            	Imgproc.cvtColor(binaryFrame, binaryFrame, Imgproc.COLOR_BGR2HSV);
            	
            	//Yellow
            	Core.inRange(binaryFrame, new Scalar(7, 104, 115), new Scalar(58, 233, 230), binaryFrame);
//            	Core.inRange(mainFrame, new Scalar(rMinSlider.getValue(), gMinSlider.getValue(), bMinSlider.getValue()), new Scalar(rMaxSlider.getValue(), gMaxSlider.getValue(), bMaxSlider.getValue()), mainFrame);
            	
				
				Mat frame1 = binaryFrame.submat(rect1);
				Mat frame2 = binaryFrame.submat(rect2);
				Mat frame3 = binaryFrame.submat(rect3);
				Mat frame4 = binaryFrame.submat(rect4);
				

				drawRect(showFrame, rect1, frame1);
				drawRect(showFrame, rect2, frame2);
				drawRect(showFrame, rect3, frame3);
				drawRect(showFrame, rect4, frame4);
				
				showFrame(showFrame);
			}
		}
	}
	
	
	
	private void drawRect(Mat frame, Rect rect, Mat frame2)
	{
		Point point1 = new Point(rect.x, rect.y);
		Point point2 = new Point(rect.width + rect.x, rect.height + rect.y);
		
		Imgproc.rectangle(frame, point1, point2, new Scalar(255,0,0), 2);
		
		double sum = Core.sumElems(frame2).val[0];
		
		double maxSum = (_VIDEO_WIDTH/_RECT_COUNT) * _RECT_HEIGHT * 255;
		
		double percents = Utils.round((sum/maxSum) * 100, 2);
		
//		Utils.round(value, scale)
		
		Imgproc.putText(frame, Double.toString(percents) + "%", new Point(rect.x + 30, rect.y + 30), Core.FONT_ITALIC, 0.5, new Scalar(255, 255, 255));
	}
	
	
	private void showFrame(Mat frame)
	{
    	MatOfByte buffer = new MatOfByte();
    	
    	Imgcodecs.imencode(".jpeg", frame, buffer);
    	
    	try
		{
			Image img = ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
			_label.setIcon(new ImageIcon(img));

		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
