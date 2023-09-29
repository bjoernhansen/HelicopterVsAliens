package de.helicopter_vs_aliens.gui;

import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.util.geometry.Dimension;

import javax.swing.JTextPane;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.Serial;


public class Label extends JTextPane
{
    @Serial
    private static final long serialVersionUID = 1L;

    public Label(Dimension displayShift)
    {
        setBounds(
            displayShift.getWidth() + 42,
            displayShift.getHeight() + 83,
            940,
            240);

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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g2d);
    }
}