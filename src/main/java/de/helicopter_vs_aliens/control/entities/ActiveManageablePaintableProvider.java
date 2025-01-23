package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.enemy.Enemy;

import java.util.Queue;

public interface ActiveManageablePaintableProvider
{
    // TODO hier werden anderen Methoden aus ManageablePaintableController aufgenommen und dann immer gegen das Interface entwickelt
    Queue<Enemy> getIntactEnemies();
    
    Queue<Enemy> getDestroyedEnemies();
}
