package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.gui.Button;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;

public class ButtonPainter extends Painter<Button>
{   
    @Override
    public void paint(Graphics2D g2d, Button button)
    {
        if(!button.isLabelEmpty())
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
            
            if(button.getSecondLabel() != null || button.isCostButton())
            {
                if(button.getCostColor() == null){g2d.setColor(Colorations.lightOrange);}
                else{g2d.setColor(button.getCostColor());}
                g2d.setFont(de.helicopter_vs_aliens.gui.Menu.fontProvider.getBold(14));
                g2d.drawString(button.getLabel(), (int)button.getBounds().getX() + 7, (int)button.getBounds().getY() + 20);
            }
            else
            {
                if(button.isTranslucent())
                {
                    g2d.setFont(de.helicopter_vs_aliens.gui.Menu.fontProvider.getBold(15));
                    if(button.isEnabled()){g2d.setColor(button.isMarked() ? Colorations.variableMarkedButton : Color.yellow); }
                    else{g2d.setColor(Colorations.lightGray);}
                }
                else
                {
                    g2d.setFont(Menu.fontProvider.getPlain(18));
                    g2d.setColor(Color.black);
                }
                FontMetrics fm = g2d.getFontMetrics();
                int sw = fm.stringWidth(button.getLabel());
                g2d.drawString(button.getLabel(), (int)(button.getBounds().getX() + (button.getBounds().getWidth()-sw)/2), (int)(button.getBounds().getY() + button.getBounds().getHeight() - button.getBounds().getHeight()/2+6));
            }
            g2d.setColor(Color.white);
            if(button.getCosts() != 0)
            {
                g2d.drawString(button.getSecondLabel() + button.getCosts() + " €", (int)button.getBounds().getX() + 7, (int)button.getBounds().getY() + 40);
            }
            else if(!button.isCostButton() && button.getSecondLabel() != null)
            {
                g2d.drawString(button.getSecondLabel(), (int)button.getBounds().getX() + 7, (int)button.getBounds().getY() + 40);
            }
        }
    }
}
