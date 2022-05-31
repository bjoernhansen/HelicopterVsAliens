package de.helicopter_vs_aliens.model;

import de.helicopter_vs_aliens.control.entities.GroupTypeOwner;

import java.awt.*;
import java.awt.geom.Rectangle2D;


public abstract class RectangularGameEntity extends GameEntity
{
	public static final  int
        // TODO diese Kontante sollte nicht in GameEntity sein
		GROUND_Y = 426;
		
	// TODO paintBound sind vermutlich nur da, da in floats gerechnet aber in int gezeichnet wird, diese Funktionalität in eigene Klasse auslagern
	// TODO bounds sollten nicht public sein, lieber einen Accessor schreiben
	private final Rectangle2D
		bounds = new Rectangle2D.Float();
	
	//TODO paintBounds sollten nicht public sein, lieber einen Accessor schreiben
	protected final Rectangle
        paintBounds = new Rectangle();
	
	protected final void setPaintBounds()
	{
		setPaintBounds(paintBounds.width, paintBounds.height);
	}
	
	protected final void setPaintBounds(int width, int height)
	{
		paintBounds.setBounds(	(int)Math.round(getX()),
									(int)Math.round(getY()),
									width, 
									height);
	}
	
	public final Rectangle getPaintBounds()
	{
		return paintBounds;
	}

	public final Rectangle2D getBounds()
	{
		return bounds;
	}
	
	public boolean isRightOf(RectangularGameEntity gameEntity)
	{
		return getX() > gameEntity.getX();
	}
	
	protected final void setBounds(Rectangle2D rectangle)
	{
		setBounds(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}
	
	protected final void setBounds(double x, double y, double width, double height)
	{
		bounds.setRect(x, y, width, height);
	}
	
	protected final void setLocation(double x, double y)
	{
		setBounds(x,
				  y,
				  bounds.getWidth(),
				  bounds.getHeight());
	}
	
	protected final void setX(double x)
	{
		setBounds(x,
			      bounds.getY(),
			      bounds.getWidth(),
			      bounds.getHeight());
	}
	
	protected final void setY(double y)
	{
		setBounds(bounds.getX(),
			      y,
			      bounds.getWidth(),
			      bounds.getHeight());
	}
	
	protected final void setDimension(double width, double height)
	{
		setBounds(bounds.getX(),
				  bounds.getY(),
				  width,
				  height);
	}
	
	protected final void setHeight(double height)
	{
		setBounds(bounds.getX(),
			      bounds.getY(),
			      bounds.getWidth(),
			      height);
	}
	
	protected final void setWidth(double width)
	{
		setBounds(bounds.getX(),
			      bounds.getY(),
			      width,
			      bounds.getHeight());
	}
	
	public final boolean intersects(double x, double y, double width, double height)
	{
		return bounds.intersects(x, y, width, height);
	}
	
	public final boolean intersects(Rectangle2D rectangle)
	{
		return bounds.intersects(rectangle);
	}
	
	public final boolean intersects(RectangularGameEntity rectangularGameEntity)
	{
		return bounds.intersects(rectangularGameEntity.getBounds());
	}
	
	public final boolean intersectsLine(double x1, double y1, double x2, double y2)
	{
		return bounds.intersectsLine(x1, y1, x2, y2);
	}
	
	public final double getX()
	{
		return bounds.getX();
	}
	
	public final double getMinX()
	{
		return bounds.getMinX();
	}
	
	public final double getMaxX()
	{
		return bounds.getMaxX();
	}
	
	public final double getCenterX()
	{
		return bounds.getCenterX();
	}
	
	public final double getY()
	{
		return bounds.getY();
	}
	
	public final double getMinY()
	{
		return bounds.getMinY();
	}
	
	public final double getMaxY()
	{
		return bounds.getMaxY();
	}
	
	public final double getCenterY()
	{
		return bounds.getCenterY();
	}
	
	public final double getWidth()
	{
		return bounds.getWidth();
	}
	
	public final double getHeight()
	{
		return bounds.getHeight();
	}
}
