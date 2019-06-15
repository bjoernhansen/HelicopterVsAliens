package de.helicopter_vs_aliens;

import javax.swing.*;
import java.awt.*;

public class MyLabel extends JTextPane
{
    private static final long serialVersionUID = 1L;
    
    MyLabel()
    {
        setBounds(Main.display_shift.width  + 42,
            Main.display_shift.height + 83, 940, 240);
        setEditable(false);
        setEnabled(true);
        setBackground(Color.black);
        setContentType("text/html");
        setOpaque(true);
        setVisible(false);
    }
    
    @Override
    protected void paintComponent(final Graphics g)
    {
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
            Controller.antialiasing ?
                RenderingHints.VALUE_ANTIALIAS_ON :
                RenderingHints.VALUE_ANTIALIAS_OFF);
        super.paintComponent(g2d);
    }
}