package ru.roboticsUMK.opencv.tests.tests_1_Old;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;


public class DefaultTest extends JFrame
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9041725460815862028L;

	private Image _img;
	
	private JPanel panel;
	
	private JLabel _label;

	public DefaultTest()
	{
		
		setSize(600, 400);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new JPanel();
		getContentPane().add(panel);
		
		_label = new JLabel();
		panel.add(_label);
		
		JSlider slider = new JSlider(0, 256, 100);
		
		panel.add(slider);
		
		JSlider slider2 = new JSlider(0, 256, 100);
		panel.add(slider2);


		
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
      
//        String filename = "/home/user/Загрузки/pot.mp4";
        VideoCapture video = new VideoCapture(0);
        
//        VideoCapture video = new VideoCapture(0);  

//        video.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 320);  
//        video.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 240); 
        
        
        Mat frame = new Mat(640, 480, CvType.CV_32FC3);
        
        
        while(true)
        {
        	
            if(video.isOpened())
            {
            	video.read(frame);
            	
            	Mat addFrame = new Mat(frame.rows(), frame.cols(), CvType.CV_32FC(frame.channels()));
            	
            	Mat addFrame2 = frame.clone();
            	
            	System.out.println(" 1 -" + addFrame.channels());
            	System.out.println(" 2 -" + frame.channels());
            	System.out.println(" 3 -" + addFrame.size().toString());
            	System.out.println(" 4 -" + frame.size().toString());
            	
            	Imgproc.accumulateWeighted(frame, addFrame, 0.5);
            	
//            	Imgproc.resize(frame, frame, new Size(320, 240));
            	
//            	Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2HSV_FULL);
//            	Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2HSV);
//            	Imgproc.threshold(frame, frame, slider.getValue(), slider2.getValue(), Imgproc.THRESH_BINARY);

            	
            	//Write image
//            	Imgcodecs.imwrite("fileName.jpeg", frame);
            	
            	//Write video
//            	vidWriter.write(frame);
            	
            	
//            	frame.setTo(new Scalar(100, 100, 150));
//            	frame.get(1, 45);
            	
//            	Imgproc.floodFill(frame, new Mat(), new Point(50, 120), new Scalar(10, 20));
            	
            	//Наложение с прозрачностью
//            	Imgproc.accumulateWeighted(frame, frame, 0.2); 
            	
            	//Рисование
//            	Imgproc.line(frame, new Point(10, 10), new Point(80,145), new Scalar(20, 100, 75));
//            	Imgproc.circle(frame, new Point(100, 200), 50,  new Scalar(0,45,23));
//            		
//            	
            	
            
//            	frame = frame.submat(new Rect(50, 50, 100, 100)); 
            	
//            	Imgproc.resize(frame, frame, new Size(200, 80));
            	
//            	List<Mat> chanels = new ArrayList<Mat>();
//            	Core.split(frame, chanels);
//            	frame = chanels.get(0);
            	
//            	Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2GRAY);
            	
//            	Imgproc.blur(frame, frame, new Size(3, 3));
//            	Imgproc.GaussianBlur(frame, frame, new Size(45, 45), 0);

//            	Core.inRange(frame, new Scalar(10), new Scalar(60), frame);
            	
//            	int thresholdMin = slider.getValue();
//            	int thresholdMax = slider2.getValue();
//            	Imgproc.Canny(frame, frame, thresholdMin, thresholdMax, 3, true);
//            	
//            	Imgproc.hougli
            	
            	
//            	Imgproc.threshold(frame, frame, 100, 60, Imgproc.THRESH_TOZERO);
//            	Imgproc.threshold(frame, frame, 100, 60, Imgproc.THRESH_BINARY);
//            	Imgproc.adaptiveThreshold(src, dst, maxValue, adaptiveMethod, thresholdType, blockSize, C);
            	
//            	Imgproc.erode(frame, frame, new Mat());
//            	Imgproc.dilate(frame, frame, new Mat());
//            	Imgproc.morphologyEx(frame, frame, Imgproc.MORPH_GRADIENT, new Mat());
            	
            	
            	
            	MatOfByte buffer = new MatOfByte();
            	
            	Imgcodecs.imencode(".jpeg", frame, buffer);
            	
            	try
				{
        			_img = ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
					_label.setIcon(new ImageIcon(_img));

				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

            }
        }
	}
	
}
