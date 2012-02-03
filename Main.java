
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main {
  private static JButton good = new JButton("Good");
  private static JLabel resultLabel = new JLabel("Ready", JLabel.CENTER);
  public static void main(String[] args) {
    JFrame f = new JFrame();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel p = new JPanel();
    p.setOpaque(true);
    p.setLayout(new FlowLayout());
    p.add(good);

    f.add(p, BorderLayout.CENTER);
    f.add(resultLabel, BorderLayout.SOUTH);
    good.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        resultLabel.setText("Working . . .");
        good.setEnabled(false);
        Thread worker = new Thread() {
          public void run() {
            try {
              Thread.sleep(5000);
            } catch (InterruptedException ex) {
            }
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                resultLabel.setText("Ready");
                good.setEnabled(true);
              }
            });
          }
        };
        worker.start(); // So we don't hold up the dispatch thread.
      }
    });
    f.setSize(300, 100);
    f.setVisible(true);
  }
}
