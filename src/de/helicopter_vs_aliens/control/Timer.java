package de.helicopter_vs_aliens.control;

import java.util.*;

// TODO Timer-Klasse verwenden
public class Timer
{
    private static Set<Timer> activeTimers = new HashSet<>();

    private static final int
        EXPIRED = 0;

    private int
        timeLeft,
        timeInterval;


    public static void countDownActiveTimers()
    {
        Iterator<Timer> iterator = activeTimers.iterator();
        while (iterator.hasNext())
        {
            Timer timer = iterator.next();
            timer.countDown();
            if(timer.hasExpired())
            {
                iterator.remove();
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

    public void restart()
    {
        this.start(this.timeInterval);
    }

    public void start(int duration)
    {
        this.timeInterval = duration;
        this.timeLeft = duration;
        activeTimers.add(this);
    }

    public void reset()
    {
        this.timeLeft = EXPIRED;
        activeTimers.remove(this);
    }

    public boolean isActive()
    {
        return timeLeft > EXPIRED;
    }

    public boolean hasExpired()
    {
        return timeLeft <= EXPIRED;
    }
}