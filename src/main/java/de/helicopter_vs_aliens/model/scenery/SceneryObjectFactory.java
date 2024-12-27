package de.helicopter_vs_aliens.model.scenery;

import de.helicopter_vs_aliens.control.entities.PaintableEntityFactory;


public final class SceneryObjectFactory implements PaintableEntityFactory<SceneryObject>
{
    @Override
    public SceneryObject makeInstance()
    {
        return new SceneryObject();
    }
    
    @Override
    public Class<? extends SceneryObject> getCorrespondingClass()
    {
        return SceneryObject.class;
    }
}
