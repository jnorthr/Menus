import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyApplication2 {
    public static void main(String[] argv) {
        final JFrame f = new JFrame("Test SwingWorker");

        /* Invoking start() on a SwingWorker causes a new Thread
         * to be created that will run the worker.construct()
         * method we've defined here.  Calls to worker.get() 
         * will wait until the construct() method has returned
         * a value (see below).
         */
        final SwingWorker worker = new SwingWorker() {
        	public Object construct() {                     // doInBackground() {
                return new ExpensiveDialogComponent();
            }
        };
        worker.start();  //new for SwingWorker 3

        /* The ActionListener below gets a component to display 
         * in its message area from the worker, which constructs
         * the component in another thread.  The call to worker.get()
         * will wait if the component is still being constructed.
         */
        ActionListener showSwingWorkerDialog = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(f, worker.get());
            }
        };

        JButton b = new JButton("Click here to show component constructed by SwingWorker");
        b.addActionListener(showSwingWorkerDialog);

        f.getContentPane().add(b);
        f.pack();
        f.show();

        //The following is safe because adding a listener is always safe.
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}

class ExpensiveDialogComponent extends JLabel {
    public ExpensiveDialogComponent() {
        super("This is the world's most expensive label.");
        try {
            Thread.sleep(10000); //sleep for 10 seconds
        } catch (InterruptedException e) {
        }
    }
}
