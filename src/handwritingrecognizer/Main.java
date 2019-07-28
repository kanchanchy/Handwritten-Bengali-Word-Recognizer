/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handwritingrecognizer;

/**
 *
 * @author Kanchan
 */
import java.awt.Color;
import javax.swing.JTabbedPane;
import javax.swing.JFrame;

public class Main extends JFrame {

    public JTabbedPane tab;

    Main() {
        setTitle("Online Handwritten Word Recognition");
        tab = new JTabbedPane();
        Option panel = new Option(tab);
        tab.addTab("Welcome", panel);
        getContentPane().add(tab);
        panel.setBackground(Color.white);
        //setBackground(Color.red);
        setSize(1366, 730);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
           }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new Main();
    }
}
