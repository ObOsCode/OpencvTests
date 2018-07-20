package ru.roboticsUMK.opencv.tests.tests_1_Old;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.opencv.videoio.VideoCapture;

public class PerspectiveTransformTest extends JFrame
{
	
//	private static final long serialVersionUID = 8755766328125413932L;
	
	private JLabel _label;
	

	public PerspectiveTransformTest()
	{
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		
		_label = new JLabel();
		panel.add(_label);
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		
		
		VideoCapture video = new VideoCapture(0);
		Mat frame = new Mat(200, 200, CvType.CV_8UC3);
		
		while (true)
		{
			video.read(frame);
		
			Imgproc.line(frame, new Point(50, 0), new Point(50,200), new Scalar(125, 147, 120), 3);
			Imgproc.line(frame, new Point(150, 0), new Point(150,200), new Scalar(125, 147, 120), 3);
			
	
			List<Point> target = new ArrayList<Point>(); 
			target.add(new Point(0, 0)); 
			target.add(new Point(frame.cols(), 0)); 
			target.add(new Point(frame.cols(), frame.rows())); 
			target.add(new Point(0, frame.rows())); 
			
			List<Point> corners = new ArrayList<Point>(); 
			corners.add(new Point(20, 20)); 
			corners.add(new Point(180, 20)); 
			corners.add(new Point(100, 180)); 
			corners.add(new Point(20, 180)); 
			
			Mat proj; 
			Mat trans; 
			 
			Mat cornersMat = Converters.vector_Point2f_to_Mat(corners); 
			Mat targetMat = Converters.vector_Point2f_to_Mat(target); 
			trans = Imgproc.getPerspectiveTransform(cornersMat, targetMat); 
	//		 invTrans = Imgproc.getPerspectiveTransform(targetMat, cornersMat); 
			Imgproc.warpPerspective(frame, frame, trans, new Size(frame.cols(), frame.rows())); 
			
			showFrame(frame);
		
		}
	}
	
	
	private void showFrame(Mat frame)
	{
		
    	MatOfByte buffer = new MatOfByte();
    	
    	String ext = ".jpeg";
//    	String ext = ".png";
    	
//    	Imgproc.resize(frame, frame, new Size(_VIDEO_WIDTH, _VIDEO_HEIGHT));
    	
    	Imgcodecs.imencode(ext, frame, buffer);
    	
    	try
		{
    		
			Image img = ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
			_label.setIcon(new ImageIcon(img));
			
//			frame.release();
			

		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
