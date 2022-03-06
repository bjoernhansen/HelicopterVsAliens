package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.gui.button.Button;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;

public class ButtonPainter extends Painter<Button>
{
    private static final int
        LABEL_OFFSET_X = 7,
        PRIMARY_LABEL_OFFSET_Y = 20,
        SECONDARY_LABEL_OFFSET_Y = 40;
        
    @Override
    public void paint(Graphics2D g2d, Graphics2DAdapter graphics2DAdapter, Button button)
    {
        if(button.isVisible())
        {
            if((button.isHighlighted() && button.isEnabled()) || !button.isTranslucent())
            {
                g2d.setPaint(button.isHighlighted() ? Colorations.gradientVariableGray : Colorations.lightestGray);
                g2d.fill(button.getBounds());
            }
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            if(button.getCostColor() == null){g2d.setColor(button.isEnabled() ? button.isMarked() ? Colorations.variableWhite : Color.white : Colorations.lightGray);}
            else{g2d.setColor(button.getCostColor());}
            if(button.isTranslucent()){g2d.draw(button.getBounds());}
            else
            {
                g2d.drawLine((int)button.getBounds().getX(), (int)button.getBounds().getY(), (int)(button.getBounds().getX() + button.getBounds().getWidth()), (int)button.getBounds().getY());
                g2d.drawLine((int)button.getBounds().getX(), (int)button.getBounds().getY(), (int)(button.getBounds().getX()), (int)(button.getBounds().getY()+button.getBounds().getHeight()));
                if(button.getCostColor() == null){g2d.setColor(Colorations.gray);}
                else{g2d.setColor(button.getCostColor());}
                g2d.drawLine((int)(button.getBounds().getX()+button.getBounds().getWidth()), (int)button.getBounds().getY(), (int)(button.getBounds().getX() + button.getBounds().getWidth()), (int)(button.getBounds().getY()+button.getBounds().getHeight()));
                g2d.drawLine((int)button.getBounds().getX(), (int)(button.getBounds().getY()+button.getBounds().getHeight()), (int)(button.getBounds().getX() + button.getBounds().getWidth()), (int)(button.getBounds().getY()+button.getBounds().getHeight()));
            }
            g2d.setStroke(new BasicStroke(1));
            
            if(button.canHaveSecondaryLabel())
            {
                if(button.getCostColor() == null){g2d.setColor(Colorations.lightOrange);}
                else{g2d.setColor(button.getCostColor());}
                g2d.setFont(Window.fontProvider.getBold(14));
                g2d.drawString(button.getPrimaryLabel(), (int)button.getBounds().getX() + LABEL_OFFSET_X, (int)button.getBounds().getY() + PRIMARY_LABEL_OFFSET_Y);
            }
            else
            {
                if(button.isTranslucent())
                {
                    g2d.setFont(Window.fontProvider.getBold(15));
                    if(button.isEnabled()){g2d.setColor(button.isMarked() ? Colorations.variableMarkedButton : Color.yellow); }
                    else{g2d.setColor(Colorations.lightGray);}
                }
                else
                {
                    g2d.setFont(Window.fontProvider.getPlain(18));
                    g2d.setColor(Color.black);
                }
                FontMetrics fm = g2d.getFontMetrics();
                int sw = fm.stringWidth(button.getPrimaryLabel());
                g2d.drawString(button.getPrimaryLabel(), (int)(button.getBounds().getX() + (button.getBounds().getWidth()-sw)/2), (int)(button.getBounds().getY() + button.getBounds().getHeight() - button.getBounds().getHeight()/2+6));
            }
            g2d.setColor(Color.white);
            if(button.hasSecondaryLabel()){
                g2d.drawString( button.getSecondaryLabel(),
                                (int)button.getBounds().getX() + LABEL_OFFSET_X,
                                (int)button.getBounds().getY() + SECONDARY_LABEL_OFFSET_Y);
            }
            
        }
    }
}
