package de.helicopter_vs_aliens.control.timer;

import java.util.*;

// TODO Timer-Klasse überall verwenden, wo integer-Counter verwendet werden
// TODO es muss festgelegt werden können, wann ein Timer heruntergezählt wird, jeder Timer braucht somit eine eigene
// Bedingung (eine Methode) welche bei jedem Timer abgefragt wird. Zwei Ansätze sind denkbar: 1. übergeben von Lambda
// Ausdruck an Methode
// oder verschiedene Unterklassen
// TODO bei Fensterwechsel Bedingung, die bestimmt, ob Timer wegfällt
public class Timer
{
    private static final Set<Timer>
        activeTimers = new HashSet<>();

    public static final int
        DISABLED = -1,    // TODO später wieder löschen, wenn nicht mehr nötig
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