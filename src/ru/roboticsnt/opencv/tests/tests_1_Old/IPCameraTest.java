package ru.roboticsnt.opencv.tests.tests_1_Old;

import java.awt.FlowLayout;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;


public class IPCameraTest extends JFrame
{

	private static final long serialVersionUID = -4458862508073615317L;
	
	private JLabel _label;
	
	private int _VIDEO_WIDTH = 320;
	private int _VIDEO_HEIGHT = 240;

	public IPCameraTest()
	{
		setSize(1000, 600);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		
		_label = new JLabel();
		panel.add(_label);
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
//		String codec = "mjpeg";
//		String codec = "mpeg4";
		String codec = "h264";
		
//		String sub_main = "sub";
		String sub_main = "main";
		
       
//		String cameraStreamURL = "rtsp://admin:123Qwerty@192.168.0.202:554/"+codec+"/ch01/"+sub_main+"/av_stream?resolution=640x4800&req_fps=25&.mjpg";
//		String cameraStreamURL = "rtsp://admin:123Qwerty@192.168.0.202:55402/"+codec+"/ch01/"+sub_main+"/av_stream";
		String cameraStreamURL = "rtsp://admin:123Qwerty@192.168.3.104/cam/realmonitor?channel=1&subtype=0";
		
        final VideoCapture video = new VideoCapture(cameraStreamURL); 

//	    video.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, _VIDEO_WIDTH);
//	    video.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, _VIDEO_HEIGHT);
	    
//	    video.set(Videoio.CV_CAP_PROP_BUFFERSIZE, 50);
	  
//	    Mat displayFrame = new Mat(_VIDEO_WIDTH, _VIDEO_HEIGHT, CvType.CV_8UC3);
	    
	    final Mat displayFrame = new Mat();
		
	    Thread readThread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
			    while (true)
				{
					if(!video.isOpened())
					{
						System.out.println("!video.isOpened");
						return;
					}
					
			    	if(!video.read(displayFrame))
			    	{
			    		System.out.println("!video.read");
			    		return;
			    	}
				}
			}
		});
	    
	    
	    readThread.start();
	    
	    
	    while (true)
		{
	    	if(!displayFrame.empty())
	    	{
	    		
//	    		synchronized (displayFrame)
//				{
//		    		Imgproc.cvtColor(displayFrame, displayFrame, Imgproc.COLOR_RGB2HSV);
//		    		
//				}
            	
	    		//Green
//            	Core.inRange(displayFrame, new Scalar(35, 210, 30), new Scalar(100, 255, 90), displayFrame);
                
	    		showFrame(displayFrame);
	    		
	    	}
	    	
			try
			{
				Thread.sleep(20);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
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
	
	
//	public static BufferedImage Mat2bufferedImage(Mat image) 
//	{
//	    MatOfByte bytemat = new MatOfByte();
//	    Imgcodecs.imencode(".jpg", image, bytemat);
//	    byte[] bytes = bytemat.toArray();
//	    InputStream in = new ByteArrayInputStream(bytes);
//	    BufferedImage img = null;
//	    try 
//	    {
//	        img = ImageIO.read(in);
//	    } catch (IOException e) 
//	    {
//	        e.printStackTrace();
//	    } finally {
//	        bytemat.release();
//	        bytes = null;
//	    }
//	    return img;
//	}

}
