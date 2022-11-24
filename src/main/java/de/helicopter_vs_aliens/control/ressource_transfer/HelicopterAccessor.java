package de.helicopter_vs_aliens.control.ressource_transfer;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;

public interface HelicopterAccessor
{
    Helicopter getHelicopter();
    
    void setHelicopter(Helicopter helicopter);
}
