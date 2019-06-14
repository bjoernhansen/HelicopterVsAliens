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
		paint_bounds = new Rectangle();
	
	
	protected void set_paint_bounds()
	{
		this.set_paint_bounds(this.paint_bounds.width, 
							  this.paint_bounds.height);
	}
	
	protected void set_paint_bounds(int width, int height)
	{
		this.paint_bounds.setBounds((int)this.bounds.getX(), 
									(int)this.bounds.getY(), 
									width, 
									height);
	}
	
	int get_paintMaxX(){return this.paint_bounds.x + this.paint_bounds.width;}
 	int get_paintMaxY(){return this.paint_bounds.y + this.paint_bounds.height;}
}
