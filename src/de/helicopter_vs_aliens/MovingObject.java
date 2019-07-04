package de.helicopter_vs_aliens;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public abstract class MovingObject implements Constants
{
	protected final static int
		GROUND_Y = 426;
	
	public Rectangle2D
		bounds = new Rectangle2D.Float();
	
	public Rectangle
        paintBounds = new Rectangle();
	
	
	protected void set_paint_bounds()
	{
		this.set_paint_bounds(this.paintBounds.width,
							  this.paintBounds.height);
	}
	
	protected void set_paint_bounds(int width, int height)
	{
		this.paintBounds.setBounds((int)this.bounds.getX(),
									(int)this.bounds.getY(), 
									width, 
									height);
	}
	
	int get_paintMaxX(){return this.paintBounds.x + this.paintBounds.width;}
 	int get_paintMaxY(){return this.paintBounds.y + this.paintBounds.height;}
}
