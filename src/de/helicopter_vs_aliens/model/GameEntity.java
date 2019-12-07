package de.helicopter_vs_aliens.model;

import de.helicopter_vs_aliens.util.Calculation;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;


public abstract class GameEntity implements Paintable
{
	public final static int
		GROUND_Y = 426;
	
	private  static final int []
		OBJECT_ACTIVATION_PROBABILITY = {100, 50, 34, 25, 20, 17, 15, 13, 12, 10, 10, 9, 8, 8, 7};
	
	public Rectangle2D
		bounds = new Rectangle2D.Float();
	
	public Rectangle
        paintBounds = new Rectangle();

	public GraphicalRepresentation
		graphicalRepresentation;


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
	
	protected int getPaintMaxX()
	{
		return this.paintBounds.x + this.paintBounds.width;
	}
	
 	int getPaintMaxY()
	{
		return this.paintBounds.y + this.paintBounds.height;
	}
	
	protected static boolean creationProbability(int difference, int factor)
	{
		int value;
		if(difference > 0 && difference < 16)
		{
			value = factor * OBJECT_ACTIVATION_PROBABILITY[difference-1];
		}
		else value = 6 * factor;
		return Calculation.random(value)==0;
	}
}