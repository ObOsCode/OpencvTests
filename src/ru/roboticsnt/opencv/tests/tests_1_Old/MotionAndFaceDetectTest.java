package ru.roboticsnt.opencv.tests.tests_1_Old;
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
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;


public class MotionAndFaceDetectTest {

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		

//        VideoCapture camera = new VideoCapture("/home/user/VID_20170627_090923.mp4");  
        VideoCapture camera = new VideoCapture(0); 
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 1280);  
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 720);  
        
        
        Mat frame = new Mat();
//        Mat convertedframe = new Mat();
        
        
        System.out.println("DDDDDDD");
    	JFrame window=new JFrame();
		window.setTitle("OpenCV java test");
		window.setSize(1000,1000);
		JPanel panel=new JPanel();
		window.getContentPane().add(panel);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel label=new JLabel();
		panel.add(label);
		

    	int index = 0;
    	
    	Mat frameCurrent = new Mat();
    	Mat framePrev = new Mat();
    	Mat frameResult = new Mat();
    	
    	
        while(true)
        {
            if(camera.isOpened())
            {
            	
            	camera.read(frame);
                         
            	//Motion detect

            	frame.copyTo(frameCurrent);
            	
            	if(index>1)
            	{
                    Core.subtract(framePrev, frameCurrent, frameResult);    
                    
                    Imgproc.cvtColor(frameResult, frameResult, Imgproc.COLOR_RGB2GRAY);    
                        
                    Imgproc.threshold(frameResult, frameResult, 30, 255, Imgproc.THRESH_BINARY);        
                        
                    List<MatOfPoint>contours = new ArrayList<MatOfPoint>();    
                    Imgproc.findContours(frameResult, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);    
 
                    boolean found = false;  
                    
                    for(int i = 0; i < contours.size(); i++) 
                    {                              
                        Mat contour = contours.get(i);                              
                        double contourarea = Imgproc.contourArea(contour);                            
                        
                        if(contourarea > 40) 
                        {    
                            found = true;    

                            Rect r = Imgproc.boundingRect(contours.get(i));    
                            Imgproc.drawContours(frame, contours, i, new Scalar(0, 0, 255));    
                            Imgproc.rectangle(frame, r.br(), r.tl(), new Scalar(0, 0, 100), 1);    
                        }    
                        contour.release();  
                    }  
                        
                    if (found) 
                    {    
//                        System.out.println("Moved");    
//                        Imgcodecs.imwrite("new/" + (sdf.format(new Date())) + ".jpg", frame);      
                    }  

            	}
            	
                frameCurrent.copyTo(framePrev);   
                
                
            	//Face detect
                CascadeClassifier faceDetector = new CascadeClassifier("resources/lbpcascade_frontalface.xml");  
                
                MatOfRect faceDetections = new MatOfRect();  
                faceDetector.detectMultiScale(frame, faceDetections);
                  
                for (Rect rect : faceDetections.toArray()) 
                {  
                    Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));  
                }  
            	
            	
            	//Output image
            	MatOfByte buffer = new MatOfByte();
            	
            	Imgcodecs.imencode(".png", frame, buffer);

        	    try 
        	    {	
            		label.setIcon(new ImageIcon(ImageIO.read(new ByteArrayInputStream(buffer.toArray()))));
            		
        		} catch (IOException e) 
        	    {
        			e.printStackTrace();
        		}
        	    
//            	try
//				{
//					Thread.currentThread().sleep(66);
//				} catch (InterruptedException e1)
//				{
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
        	    
        	    index++;
        	    
        	    if(index>1000)
        	    {
        	    	break;
        	    }
        	    
            }else
            {
            	System.out.println("Camera not open");
            }
            
        }//while
        
        frame.release();
	    frameResult.release();  
	    frameCurrent.release(); 
        camera.release();

	}
}
