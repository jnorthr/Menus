/*
 * @(#)MyApplication.java  2.0  01 April 2005
 *
 * Copyright (c) 2003-2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
/**
 * MyApplication.
 *
 * @author  Werner Randelshofer
 * @version 2.0  01 April 2005  Revised.
 */
public class MyApplication {
    /** Starts the application and returns when it is ready to use.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
        	Thread.sleep( 8000 );
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    JFrame f = new JFrame("My Application");
                    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    f.getContentPane().add(new JLabel("Hello World"));
                    f.pack();
                    f.setVisible(true);
                }
            });
        } catch (InterruptedException e) {
            // Ignore: If this exception occurs, we return too early, which
            // makes the splash window go away too early.
            // Nothing to worry about. Maybe we should write a log message.
        } catch (InvocationTargetException e) {
            // Error: Startup has failed badly. 
            // We can not continue running our application.
            InternalError error = new InternalError();
            error.initCause(e);
            throw error;
        }
    }
}
