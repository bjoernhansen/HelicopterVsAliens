package de.helicopter_vs_aliens.model;

import de.helicopter_vs_aliens.Constants;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public abstract class MovingObject implements Constants
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
 	int get_paintMaxY(){return this.paintBounds.y + this.paintBounds.height;}
}
