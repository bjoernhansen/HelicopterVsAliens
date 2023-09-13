package de.helicopter_vs_aliens.gui;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.Main;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

public class Label extends JTextPane
{
    @Serial
    private static final long serialVersionUID = 1L;
    
    public Label()
    {
        setBounds(Main.displayShift.width  + 42,
            Main.displayShift.height + 83, 940, 240);
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
        //TODO funktioniert so nicht für javafx
        final var g2d = (Graphics2D) g;
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
            Controller.getInstance().isAntialiasingActivated()
                ? RenderingHints.VALUE_ANTIALIAS_ON
                : RenderingHints.VALUE_ANTIALIAS_OFF);
        super.paintComponent(g2d);
    }
}