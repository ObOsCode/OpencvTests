package ru.roboticsnt.opencv.tests.tests_1_Old.camerasControllerTest;

import java.awt.FlowLayout;

import javax.swing.JFrame;

public class CamerasControllerTest extends JFrame
{

	private static final long serialVersionUID = 5332360167524426848L;

	public CamerasControllerTest()
	{

		setSize(1000, 600);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		
		CameraPanel panel = new CameraPanel();
		
		this.getContentPane().add(panel);
		
		new CamerasController(panel);
		
		
		
	}

}
