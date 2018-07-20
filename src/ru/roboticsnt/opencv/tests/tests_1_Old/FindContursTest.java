package ru.roboticsnt.opencv.tests.tests_1_Old;

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
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class FindContursTest extends JFrame
{

	private static final long serialVersionUID = -172080434752612329L;
	
	private JLabel _label;
	

	public FindContursTest()
	{

		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		
		_label = new JLabel();
		panel.add(_label);
		
		
		JSlider slider = new JSlider(0, 256, 100);
		
		panel.add(slider);
		
		JSlider slider2 = new JSlider(0, 256, 100);
		panel.add(slider2);
		
		
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		VideoCapture video = new VideoCapture(1);
		
	    video.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 640);  
	    video.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 480); 
		
		Mat frame = new Mat(640, 480, CvType.CV_8UC3);
		
		
		while (true)
		{
			if(video.isOpened())
			{
				
				
				video.read(frame);
				
				
				Mat showFrame = frame.clone();
				
				
				Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
				
				Imgproc.blur(frame, frame, new Size(5, 5));
				
				Imgproc.threshold(frame, frame, 110, 255, Imgproc.THRESH_BINARY);
				
            	Imgproc.Canny(frame, frame, 0, 255, 3, true);
            	
            	Mat lines = new Mat();
            	
            	Imgproc.HoughLinesP(frame, lines, 1, Math.PI/180, 70,100,10);
            	
            	
//            	System.out.println("Rows - " + lines.rows());
//            	System.out.println("Cols - " + lines.cols());
            	
//        		System.out.println("VEC - " + vec);

            	
            	for (int i = 0; i < lines.cols(); i++)
				{
            		double[] vec = lines.get(0, i);
            		
            		
            		if(vec==null)
            		{
            			break;
            		}
            		
                    double x1 = vec[0];
                    double y1 = vec[1];
                    double x2 = vec[2];
                    double y2 = vec[3];
                	
                    Point start = new Point(x1, y1);
                    Point end = new Point(x2, y2);
                    
                    Imgproc.line(showFrame, start, end, new Scalar(255,255,0),5);
				}
            	
				
            	showFrame(showFrame);
				
			}
			
		}
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
