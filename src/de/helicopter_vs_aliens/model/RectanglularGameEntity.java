package de.helicopter_vs_aliens.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;


public abstract class RectanglularGameEntity extends GameEntity
// TODO möglichst so vereinheitlichen, dass alle von GameEnitiy erben können
{
	public static final  int
        // TODO diese Kontante sollte nicht in GameEntity sein
		GROUND_Y = 426;
		
	// TODO paintBound sind vermutlich nur da, da in floats gerechnet aber in int gezeichnet wird, diese Funktionalität in eigene Klasse auslagern
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
		this.paintBounds.setBounds(	(int)Math.round(this.bounds.getX()),
									(int)Math.round(this.bounds.getY()),
									width, 
									height);
	}
}