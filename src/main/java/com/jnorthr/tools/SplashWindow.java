package com.jnorthr.tools;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class SplashWindow extends Window {

    /**
     * The current instance of the splash window.
     * (Singleton design pattern).
     */
    private static SplashWindow instance;
    
    /**
     * The splash image which is displayed on the splash window.
     */
    private Image image;
    private boolean paintCalled = false;
    
    /**
     * Creates a new instance.
     * @param parent the parent of the window.
     * @param image the splash image.
     */
    private SplashWindow(Frame parent, Image image) {
        super(parent);
        this.image = image;

        // Load the image
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image,0);
        try 
	{
            mt.waitForID(0);
        } 
	catch(InterruptedException ie)
	{}
        
        // Center the window on the screen
        int imgWidth = image.getWidth(this);
        int imgHeight = image.getHeight(this);
        setSize(imgWidth, imgHeight);

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
	        (screenDim.width - imgWidth) / 2,
        	(screenDim.height - imgHeight) / 2
        );
        
        // User should be able to close the splash window by
        // clicking on its display area. This mouse listener
        // listens for mouse clicks and disposes the splash window.
        MouseAdapter disposeOnClick = new MouseAdapter() 
	{
            public void mouseClicked(MouseEvent evt) {
                // Note: To avoid that method splash hangs, we
                // must set paintCalled to true and call notifyAll.
                // This is necessary because the mouse click may
                // occur before the contents of the window
                // has been painted.
                synchronized(SplashWindow.this) {
                    SplashWindow.this.paintCalled = true;
                    SplashWindow.this.notifyAll();
                }
                dispose();
            }
        };	// end of MouseAdapter

        addMouseListener(disposeOnClick);
    }
    

    /**
     * Updates the display area of the window.
     */
    public void update(Graphics g) 
    {
        // Note: Since the paint method is going to draw an
        // image that covers the complete area of the component we
        // do not fill the component with its background color
        // here. This avoids flickering.
        paint(g);
    }  // end of update


    /**
     * Paints the image on the window.
     */
    public void paint(Graphics g) 
    {
        g.drawImage(image, 0, 0, this);
        
        // Notify method splash that the window
        // has been painted.
        // Note: To improve performance we do not enter
        // the synchronized block unless we have to.
        if (! paintCalled) 
	{
            paintCalled = true;
            synchronized (this) { notifyAll(); }
        } // end of if
    } // end of paint
    
    /**
     * Open's a splash window using the specified image.
     * @param image The splash image.
     */
    public static void splash(Image image) 
    {
        if (instance == null && image != null) 
	{
            Frame f = new Frame();
            
            // Create the splash image
            instance = new SplashWindow(f, image);
            
            // Show the window.
            instance.setVisible(true);
            
            // Note: To make sure the user gets a chance to see the
            // splash window we wait until its paint method has been
            // called at least once by the AWT event dispatcher thread.
            // If more than one processor is available, we don't wait,
            // and maximize CPU throughput instead.
            if (! EventQueue.isDispatchThread() && Runtime.getRuntime().availableProcessors() == 1) 
	    {
                synchronized (instance) 
                {
                    while (! instance.paintCalled) 
		    {
                        try { instance.wait(); } catch (InterruptedException e) {}
                    } // end of while
                } // end of synch
            } // end of if
        } // end of if
    }  // end of splash method

    /**
     * Open's a splash window using the specified image.
     * @param imageURL The url of the splash image.
     */
    public static void splashU(URL imageURL) 
    {
        if (imageURL != null) 
	{
            splash(Toolkit.getDefaultToolkit().createImage(imageURL));
        } // end of if
    } // end of splash method
    
    /**
     * Closes the splash window.
     */
    public static void disposeSplash() 
    {
        if (instance != null) 
	{
            instance.getOwner().dispose();
            instance = null;
        }  // end of if
    } // end of disposeSplash method
   

    /**
     * Invokes the main method of the provided class name.
     * @param args the command line arguments
     */
    public static void invokeMain(String className, String[] args) 
    {
    	System.out.println("... invokeMain starting "+className);

        try 
	{
            Class.forName(className)
            .getMethod("main", new Class[] {String[].class})
            .invoke(null, new Object[] {args});
    	} 
	catch (ClassNotFoundException x) 
	{
      		x.printStackTrace();
    	} 
	catch (IllegalAccessException x) 
	{
      		x.printStackTrace();
        } 
	catch (Exception e) 
	{
            InternalError error = new InternalError("Failed to invoke main method");
            error.initCause(e);
            throw error;
        }
    } // end of method
} // end of class
