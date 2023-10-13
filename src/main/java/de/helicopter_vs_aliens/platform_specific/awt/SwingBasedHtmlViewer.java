package de.helicopter_vs_aliens.platform_specific.awt;

import de.helicopter_vs_aliens.gui.window.AbstractHtmlViewer;
import de.helicopter_vs_aliens.util.geometry.Dimension;

import javax.swing.JTextPane;
import java.awt.Color;


class SwingBasedHtmlViewer extends AbstractHtmlViewer<JTextPane>
{
    private final JTextPane
        textPane;

    private final Dimension
        displayShift;


    public static SwingBasedHtmlViewer makeInstance(Dimension displayShift)
    {
        var htmlViewer = new SwingBasedHtmlViewer(displayShift);
        htmlViewer.setHorizontalBounds(HORIZONTAL_BOUNDS);
        htmlViewer.hide();
        return htmlViewer;
    }

    private SwingBasedHtmlViewer(Dimension displayShift)
    {
        this.displayShift = displayShift;
        textPane = getConfiguredTextPane();
    }

    private JTextPane getConfiguredTextPane()
    {
        var jTextPane = new JTextPane();
        jTextPane.setEditable(false);
        jTextPane.setEnabled(true);
        jTextPane.setBackground(Color.black);
        jTextPane.setContentType("text/html");
        jTextPane.setOpaque(true);
        return jTextPane;
    }

    @Override
    public JTextPane getComponent()
    {
        return textPane;
    }

    @Override
    public void setText(String htmlContent)
    {
        textPane.setText(htmlContent);
    }

    @Override
    public void show()
    {
        textPane.setVisible(true);
    }

    @Override
    public void hide()
    {
        textPane.setVisible(false);
    }

    @Override
    protected void setHorizontalBounds(HorizontalBounds horizontalBounds)
    {
        textPane.setBounds(
            displayShift.getWidth() + horizontalBounds.x(),
            displayShift.getHeight() + VERTICAL_BOUNDS.y(),
            horizontalBounds.width(),
            VERTICAL_BOUNDS.height());
    }
}
