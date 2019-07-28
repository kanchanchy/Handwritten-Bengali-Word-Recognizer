/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handwritingrecognizer;

import javax.swing.JTabbedPane;

/**
 *
 * @author Kanchan
 */
public class WelcomePanelClass {
    public JTabbedPane pane;

    public WelcomePanelClass(JTabbedPane pane)
    {
        this.pane=pane;
        call();
    }
    public void call()
    {
        Option panel = new Option(this.pane);
        pane.addTab("Welcome", panel);
    }
}
