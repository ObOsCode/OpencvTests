package ru.roboticsUMK.opencv.tests.tests_3;

import ru.roboticsUMK.opencv.OpenCVTestBase;

import javax.swing.*;


public class SelectColor extends OpenCVTestBase
{
    public SelectColor()
    {
        super("Select color");

        JSlider h1 = addSlider("h1", 0, 255, 0);


        startLoop();
    }


    @Override
    protected void loop()
    {
        super.loop();


    }
}
