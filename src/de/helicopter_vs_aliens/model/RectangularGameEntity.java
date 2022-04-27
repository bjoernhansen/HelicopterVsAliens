package de.helicopter_vs_aliens.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;


public abstract class RectangularGameEntity extends GameEntity
{
	public static final  int
        // TODO diese Kontante sollte nicht in GameEntity sein
		GROUND_Y = 426;
		
	// TODO paintBound sind vermutlich nur da, da in floats gerechnet aber in int gezeichnet wird, diese Funktionalität in eigene Klasse auslagern
	// TODO bounds sollten nicht public sein, lieber einen Accessor schreiben
	protected final Rectangle2D
		bounds = new Rectangle2D.Float();
	
	//TODO paintBounds sollten nicht public sein, lieber einen Accessor schreiben
	protected final Rectangle
        paintBounds = new Rectangle();
	
	protected void setPaintBounds()
	{
		this.setPaintBounds(this.paintBounds.width, this.paintBounds.height);
	}
	
	protected void setPaintBounds(int width, int height)
	{
		this.paintBounds.setBounds(	(int)Math.round(this.bounds.getX()),
									(int)Math.round(this.bounds.getY()),
									width, 
									height);
	}
	
	public Rectangle getPaintBounds()
	{
		return paintBounds;
	}
	
	public Rectangle2D getBounds()
	{
		return bounds;
	}
}