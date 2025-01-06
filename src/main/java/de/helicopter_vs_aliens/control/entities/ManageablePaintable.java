package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceAcceptor;
import de.helicopter_vs_aliens.model.Paintable;


public interface ManageablePaintable extends Paintable, GameRessourceAcceptor
{
    ManageablePaintableGroupType getGroupType();
}
