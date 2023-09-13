package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.gui.button.Button;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.BasicStroke;
import java.awt.Color;


public class ButtonPainter extends Painter<Button>
{
    private static final int
        LABEL_OFFSET_X = 7;

    private static final int
        PRIMARY_LABEL_OFFSET_Y = 20;

    private static final int
        SECONDARY_LABEL_OFFSET_Y = 40;


    @Override
    public void paint(GraphicsAdapter graphicsAdapter, Button button)
    {
        if(button.isVisible())
        {
            if((button.isHighlighted() && button.isEnabled()) || !button.isTranslucent())
            {
                graphicsAdapter.setPaint(button.isHighlighted() ? Colorations.gradientVariableGray : Colorations.lightestGray);
                graphicsAdapter.fillRectangle(button.getBounds());
            }
            graphicsAdapter.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            if(button.getCostColor() == null){graphicsAdapter.setColor(button.isEnabled() ? button.isMarked() ? Colorations.variableWhite : Color.white : Colorations.lightGray);}
            else{graphicsAdapter.setColor(button.getCostColor());}
            if(button.isTranslucent()){graphicsAdapter.drawRectangle(button.getBounds());}
            else
            {
                graphicsAdapter.drawLine((int)button.getBounds().getX(), (int)button.getBounds().getY(), (int)(button.getBounds().getX() + button.getBounds().getWidth()), (int)button.getBounds().getY());
                graphicsAdapter.drawLine((int)button.getBounds().getX(), (int)button.getBounds().getY(), (int)(button.getBounds().getX()), (int)(button.getBounds().getY()+button.getBounds().getHeight()));
                if(button.getCostColor() == null){graphicsAdapter.setColor(Colorations.gray);}
                else{graphicsAdapter.setColor(button.getCostColor());}
                graphicsAdapter.drawLine((int)(button.getBounds().getX()+button.getBounds().getWidth()), (int)button.getBounds().getY(), (int)(button.getBounds().getX() + button.getBounds().getWidth()), (int)(button.getBounds().getY()+button.getBounds().getHeight()));
                graphicsAdapter.drawLine((int)button.getBounds().getX(), (int)(button.getBounds().getY()+button.getBounds().getHeight()), (int)(button.getBounds().getX() + button.getBounds().getWidth()), (int)(button.getBounds().getY()+button.getBounds().getHeight()));
            }
            graphicsAdapter.setStroke(new BasicStroke(1));
            
            if(button.canHaveSecondaryLabel())
            {
                if(button.getCostColor() == null){graphicsAdapter.setColor(Colorations.lightOrange);}
                else{graphicsAdapter.setColor(button.getCostColor());}
                graphicsAdapter.setFont(Window.fontProvider.getBold(14));
                graphicsAdapter.drawString(button.getPrimaryLabel(), (int)button.getBounds().getX() + LABEL_OFFSET_X, (int)button.getBounds().getY() + PRIMARY_LABEL_OFFSET_Y);
            }
            else
            {
                if(button.isTranslucent())
                {
                    graphicsAdapter.setFont(Window.fontProvider.getBold(15));
                    if(button.isEnabled()){graphicsAdapter.setColor(button.isMarked() ? Colorations.variableMarkedButton : Color.yellow); }
                    else{graphicsAdapter.setColor(Colorations.lightGray);}
                }
                else
                {
                    graphicsAdapter.setFont(Window.fontProvider.getPlain(18));
                    graphicsAdapter.setColor(Color.black);
                }
                int stringWidth = graphicsAdapter.getStringWidth(button.getPrimaryLabel());
                graphicsAdapter.drawString(button.getPrimaryLabel(), (int)(button.getBounds().getX() + (button.getBounds().getWidth()-stringWidth)/2), (int)(button.getBounds().getY() + button.getBounds().getHeight() - button.getBounds().getHeight()/2+6));
            }
            graphicsAdapter.setColor(Color.white);
            if(button.hasSecondaryLabel()){
                graphicsAdapter.drawString( button.getSecondaryLabel(),
                                (int)button.getBounds().getX() + LABEL_OFFSET_X,
                                (int)button.getBounds().getY() + SECONDARY_LABEL_OFFSET_Y);
            }
            
        }
    }
}
