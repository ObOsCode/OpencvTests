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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class SelectColorTest implements ChangeListener
{
	
	private JLabel _label;
	private int _rMin;
	private int _rMax;
	private int _gMin;
	private int _gMax;
	private int _bMin;
	private int _bMax;
	private JSlider _rMinSlider;
	private JSlider _rMaxSlider;
	private JSlider _gMinSlider;
	private JSlider _gMaxSlider;
	private JSlider _bMinSlider;
	private JSlider _bMaxSlider;
	private Mat _cameraFrame;

	public SelectColorTest()
	{
		JFrame window = new JFrame("Select color test");
		window.setVisible(true);
		window.setSize(1000, 600);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.setLayout(new FlowLayout());
		
		JPanel framePanel = new JPanel();
		framePanel.setLayout(new FlowLayout());
		window.getContentPane().add(framePanel);
		
		_label = new JLabel();
		framePanel.add(_label);
		
		
		JPanel slidersPanel = new JPanel();
		slidersPanel.setSize(200, 400);
		slidersPanel.setLayout(new BoxLayout(slidersPanel, BoxLayout.Y_AXIS));
		window.getContentPane().add(slidersPanel);

		
		_rMin = 35;
		_rMax = 100;
		_gMin = 210;
		_gMax = 255;
		_bMin = 30;
		_bMax = 90;
		
		
		_rMinSlider = new JSlider(0, 255, _rMin);
		_rMaxSlider = new JSlider(0, 255, _rMax);
		_gMinSlider = new JSlider(0, 255, _gMin);
		_gMaxSlider = new JSlider(0, 255, _gMax);
		_bMinSlider = new JSlider(0, 255, _bMin);
		_bMaxSlider = new JSlider(0, 255, _bMax);
		
		JLabel rMinSliderLabel = new JLabel("Red min");
		JLabel rMaxSliderLabel = new JLabel("Red max");
		JLabel gMinSliderLabel = new JLabel("Green min");
		JLabel gMaxSliderLabel = new JLabel("Green max");
		JLabel bMinSliderLabel = new JLabel("Blue min");
		JLabel bMaxSliderLabel = new JLabel("Blue max");
		
		_rMinSlider.addChangeListener(this);
		_rMaxSlider.addChangeListener(this);
		_gMinSlider.addChangeListener(this);
		_gMaxSlider.addChangeListener(this);
		_bMinSlider.addChangeListener(this);
		_bMaxSlider.addChangeListener(this);
		
		
		slidersPanel.add(rMinSliderLabel);
		slidersPanel.add(_rMinSlider);
		
		slidersPanel.add(rMaxSliderLabel);
		slidersPanel.add(_rMaxSlider);
		
		slidersPanel.add(gMinSliderLabel);
		slidersPanel.add(_gMinSlider);
		
		slidersPanel.add(gMaxSliderLabel);
		slidersPanel.add(_gMaxSlider);
		
		slidersPanel.add(bMinSliderLabel);
		slidersPanel.add(_bMinSlider);
		
		slidersPanel.add(bMaxSliderLabel);
		slidersPanel.add(_bMaxSlider);
		
		
		
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        System.loadLibrary("opencv_ffmpeg2413_64");

//        VideoCapture video = new VideoCapture();
//        video.open("http://192.168.1.64");
        
        VideoCapture video = new VideoCapture(0); 
        video.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 640);
        video.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 480);
      
        
        _cameraFrame = new Mat(640, 480, CvType.CV_8UC3);
        
//        Mat frameRGB = new Mat(640, 480, CvType.CV_8UC3);
//        Mat frameR = new Mat(640, 480, CvType.CV_8UC1);
//        Mat frameG = new Mat(640, 480, CvType.CV_8UC1);
//        Mat frameB = new Mat(640, 480, CvType.CV_8UC1);
        
        while(true)
        {
        	
            if(video.isOpened())
            {
            	video.read(_cameraFrame);
            	
            	Imgproc.cvtColor(_cameraFrame, _cameraFrame, Imgproc.COLOR_BGR2HSV);
            	
            	//Custom
            	Core.inRange(_cameraFrame, new Scalar(_rMin, _gMin, _bMin), new Scalar(_rMax, _gMax, _bMax), _cameraFrame);
            	
            	//Red
//            	Core.inRange(_cameraFrame, new Scalar(160, 100, 100), new Scalar(179, 255, 255), _cameraFrame);
            	
            	//Green
//            	Core.inRange(cameraFrame, new Scalar(35, 210, 30), new Scalar(100, 255, 90), cameraFrame);
                
                
            	MatOfByte buffer = new MatOfByte();
            	
            	
            	Imgcodecs.imencode(".jpeg", _cameraFrame, buffer);
            	
            	try
				{
        			Image img = ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
					this._label.setIcon(new ImageIcon(img));

				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	  
            }
            
        }//while true
		
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		JSlider slider = (JSlider)e.getSource();
		
		System.out.println("Value - " + slider.getValue());
		
		_rMin = _rMinSlider.getValue();
		_rMax = _rMaxSlider.getValue();
		_gMin = _gMinSlider.getValue();
		_gMax = _gMaxSlider.getValue();
		_bMin = _bMinSlider.getValue();
		_bMax = _bMaxSlider.getValue();
		
//    	Core.inRange(_cameraFrame, new Scalar(_rMin, _gMin, _bMin), new Scalar(_rMax, _gMax, _bMax), _cameraFrame);
//    	
		
	}

}
