package de.helicopter_vs_aliens.model;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterRepresentation;
import de.helicopter_vs_aliens.util.Calculation;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;


public abstract class GameEntity implements Paintable
// TODO möglichst so vereinheitlichen, dass alle von GameEnitiy erben können
{
	public static final  int
		GROUND_Y = 426;
		
	public Rectangle2D
		bounds = new Rectangle2D.Float();
	
	public Rectangle
        paintBounds = new Rectangle();
	
	protected void setPaintBounds()
	{
		this.setPaintBounds(this.paintBounds.width, this.paintBounds.height);
	}
	
	protected void setPaintBounds(int width, int height)
	{
		this.paintBounds.setBounds((int)this.bounds.getX(),
									(int)this.bounds.getY(), 
									width, 
									height);
	}
}