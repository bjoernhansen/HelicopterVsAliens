package de.helicopter_vs_aliens.control.timer;

import java.util.*;

// TODO Timer-Klasse verwenden
public class Timer
{
    private static final Set<Timer>
        activeTimers = new HashSet<>();

    private static final int
        EXPIRED = 0;

    private int
        timeLeft = EXPIRED;

    private boolean
        isActive = false;

    int
        timeInterval;


    public Timer(int duration)
    {
        this.timeInterval = duration;
    }

    public static void countDownActiveTimers()
    {
        Iterator<Timer> iterator = activeTimers.iterator();
        while (iterator.hasNext())
        {
            Timer timer = iterator.next();
            if(timer.hasExpired())
            {
                timer.isActive = false;
                iterator.remove();
            }
            else
            {
                timer.countDown();
            }
        }
    }

    public int getTimeLeft()
    {
        return this.timeLeft;
    }

    private void countDown()
    {
        this.timeLeft = Math.max(0, this.timeLeft-1);
    }

    public void start()
    {
        this.timeLeft = this.timeInterval;
        this.isActive = true;
        activeTimers.add(this);
    }

    public void reset()
    {
        this.timeLeft = EXPIRED;
        this.isActive = false;
        activeTimers.remove(this);
    }

    public boolean isActive()
    {
        return this.isActive;
    }

    public boolean hasExpired()
    {
        return timeLeft <= EXPIRED && this.isActive;
    }
}