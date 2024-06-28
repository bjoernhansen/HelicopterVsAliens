package de.helicopter_vs_aliens.control.timer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class TimerManager
{
    private static TimerManager
        timerManager = new TimerManager();
    
    public static TimerManager getInstance()
    {
        return timerManager;
    }
    
    private final Set<Timer>
        activeTimers = new HashSet<>();
    
    private TimerManager() {}
    
    public void countDownActiveTimers()
    {
        Iterator<Timer> iterator = activeTimers.iterator();
        while(iterator.hasNext())
        {
            // TODO gehört Algorithmus nicht teilweise in Timer
            Timer timer = iterator.next();
            if(timer.hasExpired())
            {
                timer.disable();
                iterator.remove();
            }
            else
            {
                timer.countDown();
            }
        }
    }
    
    void addTimer(Timer timer)
    {
        activeTimers.add(timer);
    }
    
    void removeTimer(Timer timer)
    {
        activeTimers.remove(timer);
    }
}
