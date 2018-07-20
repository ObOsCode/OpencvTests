package ru.roboticsUMK.opencv.tests.tests_1_Old.camerasControllerTest;

import java.awt.Color;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;


public class CameraPanel extends JPanel
{
	
	private static final long serialVersionUID = -4462365071713911390L;
	
	Image _cameraImage;
	
	private JLabel _label;

	public JSlider rMinSlider;

	public JSlider rMaxSlider;

	public JSlider gMinSlider;

	public JSlider gMaxSlider;

	public JSlider bMinSlider;

	public JSlider bMaxSlider;
	
	
	public JSlider cameraRotationSlider;
	
	public JSlider rulerCenterSlider;
	
//	private int _VIDEO_WIDTH = 320;
//	private int _VIDEO_HEIGHT = 240;
//	
//	private final int _RECT_COUNT = 7;
//	
//	private final int _RECT_HEIGHT = 80;
//	
//	private List<Rect> _rectList = new ArrayList<Rect>();
	
	
	public CameraPanel()
	{
		
//		setPreferredSize(new Dimension(360, 2000));
//		setMaximumSize(new Dimension(32767, 200));
//		setMinimumSize(new Dimension(360, 10));
		
		_label = new JLabel();
		add(_label);
		
		
		setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Cameras", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel slidersPanel = new JPanel();
		slidersPanel.setSize(200, 400);
		slidersPanel.setLayout(new BoxLayout(slidersPanel, BoxLayout.Y_AXIS));
		add(slidersPanel);
		
		rMinSlider = new JSlider(0, 255, 100);
		rMaxSlider = new JSlider(0, 255, 100);
		gMinSlider = new JSlider(0, 255, 100);
		gMaxSlider = new JSlider(0, 255, 100);
		bMinSlider = new JSlider(0, 255, 100);
		bMaxSlider = new JSlider(0, 255, 100);
		
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
		
		//Camera rotation
		JLabel cameraRotLabel = new JLabel("Camera rotations");
		cameraRotationSlider = new JSlider(0, 89, 38);
		slidersPanel.add(cameraRotLabel);
		slidersPanel.add(cameraRotationSlider);
		
		//Ruler center
		JLabel rulerCenterLabel = new JLabel("Ruler center position");
		rulerCenterSlider = new JSlider(100, 280, 220);
		slidersPanel.add(rulerCenterLabel);
		slidersPanel.add(rulerCenterSlider);
	}
	
	
	public void showFrame(ByteArrayInputStream is)
	{
    	try
		{
			Image img = ImageIO.read(is);
//			img = img.getScaledInstance(_VIDEO_WIDTH, _VIDEO_HEIGHT, Image.SCALE_SMOOTH);
			_label.setIcon(new ImageIcon(img));
			
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
//	public void showFrame(Mat frame)
//	{
//    	MatOfByte buffer = new MatOfByte();
//    	
//    	Imgcodecs.imencode(".jpeg", frame, buffer);
//    	
//    	try
//		{
//			Image img = ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
//			_label.setIcon(new ImageIcon(img));
//
//		} catch (IOException e1)
//		{
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	}
	
	
//	private void drawRect(Mat frame, Rect rect, Mat frame2)
//	{
//		Point point1 = new Point(rect.x, rect.y);
//		Point point2 = new Point(rect.width + rect.x, rect.height + rect.y);
//		
//		Imgproc.rectangle(frame, point1, point2, new Scalar(255,0,0), 2);
//		
//		double sum = Core.sumElems(frame2).val[0];
//		
//		double maxSum = (_VIDEO_WIDTH/_RECT_COUNT) * _RECT_HEIGHT * 255;
//		
//		double percents = Utils.round((sum/maxSum) * 100, 2);
//		
//		Imgproc.putText(frame, Double.toString(percents) + "%", new Point(rect.x + 5, rect.y + 20), Core.FONT_ITALIC, 0.3, new Scalar(255, 255, 255));
//	}
	
	
//	@Override
//	protected void paintComponent(Graphics g)
//	{
//		int x = (getWidth() - _VIDEO_WIDTH)/2;
////		int y = (getHeight() - _VIDEO_HEIGHT)/2;
//		int y = 20;
//		
//		g.drawImage(_cameraImage, x, y, null);
//	}

}//class
