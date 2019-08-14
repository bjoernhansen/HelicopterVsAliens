package de.helicopter_vs_aliens.model;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public abstract class MovingObject
{
	public final static int
		GROUND_Y = 426;
	
	public Rectangle2D
		bounds = new Rectangle2D.Float();
	
	public Rectangle
        paintBounds = new Rectangle();
	
	
	protected void setPaintBounds()
	{
		this.setPaintBounds(this.paintBounds.width,
							  this.paintBounds.height);
	}
	
	protected void setPaintBounds(int width, int height)
	{
		this.paintBounds.setBounds((int)this.bounds.getX(),
									(int)this.bounds.getY(), 
									width, 
									height);
	}
	
	protected int getPaintMaxX(){return this.paintBounds.x + this.paintBounds.width;}
 	int getPaintMaxY(){return this.paintBounds.y + this.paintBounds.height;}
}
