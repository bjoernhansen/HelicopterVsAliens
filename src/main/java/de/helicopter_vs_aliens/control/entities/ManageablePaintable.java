package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceAcceptor;
import de.helicopter_vs_aliens.model.Paintable;


public interface ManageablePaintable extends Paintable, GameRessourceAcceptor
{
    ManageablePaintableGroupType getGroupType();
    
    // TODO hier könnte noc eine reset/preset-Methode rein, die dann implementiert wird von den 6 betroffenen Klassen
}
