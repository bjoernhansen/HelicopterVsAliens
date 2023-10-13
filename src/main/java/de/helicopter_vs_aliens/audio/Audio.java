package de.helicopter_vs_aliens.audio;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;
import de.helicopter_vs_aliens.score.Savegame;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static de.helicopter_vs_aliens.gui.WindowType.GAME;
import static de.helicopter_vs_aliens.gui.WindowType.REPAIR_SHOP;
import static de.helicopter_vs_aliens.gui.WindowType.SCORE_SCREEN;


public class Audio
{
    public static final boolean
        MICHAEL_MODE = false;        // Legt fest, ob der Michael-Modus bei der Hintergrundmusikauswahl verfügbar ist

    private static final int
        NUMBER_OF_ANNOUNCERS = PowerUpType.valueCount();

    public static boolean
        isSoundOn = true,            // = true: Hintergrundmusik wird abgespielt
        standardBackgroundMusic = true;    // = true: Verwenden der Standard-Hintergrund-Musikauswahl

    public static Clip
        currentBg,        // Die aktuell abgespielte Hintergrund-Musik

    // Menü-Sounds
    block,
        cash,
        choose,

    // Game-Sounds
    cloak,
        emp,
        explosion1,
        explosion2,
        explosion3,
        explosion4,
        explosion5,
        landing,
        launch1,
        launch2,
        launch3,
        levelUp,
        phaseShift,
        plasmaOn,
        plasmaOff,
        powerUpFade1,
        powerUpFade2,
        rebound,
        shieldUp,
        stun,
        stunActivated,
        teleport1,
        teleport2,
        tractorBeam,

    // Announcer
    applause1,
        applause2,
        niceCatch,
        doubleKill,
        trippleKill,
        multiKill,
        megaKill,
        monsterKill,

    // Hintergrundmusik für den Standard-Modus
    bgMusic1,
        bgMusic2,
        bgMusic3,

    // Hintergrundmusik für Michael-Modus
    mainMenu,
        repairShop,
        scoreScreen,
        level_01_09,
        level_11_19,
        level_21_29,
        level_31_39,
        level_41_48,
        level_49,
        bossLevel,
        finalBossLevel,
        victory;

    public static Clip[]
        powerAnnouncer;

    private Audio()
    {
        throw new UnsupportedOperationException();
    }

    private static Clip getAudioClip(String audioFileName)
    {
        try
        {
            String filePath = "/sounds/" + audioFileName;
            InputStream inputStream = Audio.class.getResourceAsStream(filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(inputStream));

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        }
        catch(UnsupportedAudioFileException | IOException | LineUnavailableException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void initialize()
    {
        launch1 = getAudioClip("launch1.wav");
        explosion1 = getAudioClip("explosion1.wav");
        explosion2 = getAudioClip("explosion2.wav");
        explosion3 = getAudioClip("explosion3.wav");
        explosion4 = getAudioClip("explosion4.wav");
        bgMusic1 = getAudioClip("bg_music1.wav");
        bgMusic2 = getAudioClip("bg_music2_old.wav");
        bgMusic3 = getAudioClip("bg_music3.wav");
        levelUp = getAudioClip("level_up.wav");
        choose = getAudioClip("choose.wav");
        cash = getAudioClip("cash.wav");
        block = getAudioClip("block.wav");
        teleport2 = getAudioClip("teleport2.wav");
        cloak = getAudioClip("cloak.wav");
        launch2 = getAudioClip("launch2.wav");
        rebound = getAudioClip("rebound.wav");
        tractorBeam = getAudioClip("tractor_beam.wav");
        shieldUp = getAudioClip("shield_up.wav");
        applause1 = getAudioClip("applause1.wav");
        launch3 = getAudioClip("launch3.wav");
        explosion5 = getAudioClip("explosion5.wav");
        teleport1 = getAudioClip("teleport1.wav");
        phaseShift = getAudioClip("phase_shift.wav");
        emp = getAudioClip("emp.wav");
        stun = getAudioClip("stun.wav");
        applause2 = getAudioClip("applause2.wav");
        doubleKill = getAudioClip("doublekill.wav");
        trippleKill = getAudioClip("tripplekill.wav");
        megaKill = getAudioClip("megakill.wav");
        multiKill = getAudioClip("multikill.wav");
        monsterKill = getAudioClip("monsterkill.wav");
        niceCatch = getAudioClip("nicecatch.wav");
        landing = getAudioClip("landing.wav");
        plasmaOn = getAudioClip("plasma_on.wav");
        plasmaOff = getAudioClip("plasma_off.wav");
        powerUpFade1 = getAudioClip("pu_fade1.wav");
        powerUpFade2 = getAudioClip("pu_fade2.wav");
        stunActivated = getAudioClip("stun_activated.wav");

        powerAnnouncer = new Clip[NUMBER_OF_ANNOUNCERS];
        powerAnnouncer[PowerUpType.TRIPLE_DAMAGE.ordinal()] = getAudioClip("announcer_triple_dmg.wav");
        powerAnnouncer[PowerUpType.INVINCIBLE.ordinal()] = getAudioClip("announcer_invincible.wav");
        powerAnnouncer[PowerUpType.UNLIMITED_ENERGY.ordinal()] = getAudioClip("announcer_unlimited_energy.wav");
        powerAnnouncer[PowerUpType.BOOSTED_FIRE_RATE.ordinal()] = getAudioClip("announcer_fire_rate_boosted.wav");
        powerAnnouncer[PowerUpType.REPARATION.ordinal()] = getAudioClip("announcer_reparation.wav");
        powerAnnouncer[PowerUpType.BONUS_INCOME.ordinal()] = getAudioClip("announcer_bonus_credit.wav");

        if(MICHAEL_MODE)
        {
            mainMenu = getAudioClip("main_menu.wav");
            repairShop = getAudioClip("repair_shop.wav");
            scoreScreen = getAudioClip("scorescreen.wav");
            level_01_09 = getAudioClip("level_01_09.wav");
            level_11_19 = getAudioClip("level_11_19.wav");
            level_21_29 = getAudioClip("level_21_29.wav");
            level_31_39 = getAudioClip("level_31_39.wav");
            level_41_48 = getAudioClip("level_41_48.wav");
            level_49 = getAudioClip("level_49.wav");
            bossLevel = getAudioClip("boss_level.wav");
            finalBossLevel = getAudioClip("final_boss_level.wav");
            victory = getAudioClip("victory.wav");
        }
        Audio.refreshBackgroundMusic();
    }

    public static void refreshBackgroundMusic()
    {
        if(currentBg != null)
        {
            currentBg.stop();
        }
        if(isSoundOn)
        {
            currentBg = getBgMusic();
            currentBg.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static void changeBgMusicMode(Savegame savegame)
    {
        standardBackgroundMusic = !standardBackgroundMusic;
        savegame.standardBackgroundMusic = standardBackgroundMusic;
        refreshBackgroundMusic();
        Events.settingsChanged = true;
    }

    // Rückgabe der aktuell zu spielenden Hintergrundmusik
    private static Clip getBgMusic()
    {
        if(standardBackgroundMusic)
        {
            if(WindowManager.window == WindowType.GAME && !Events.isCurrentLevelBossLevel())
            {
                return bgMusic2;
            }
            else if(WindowManager.window == REPAIR_SHOP || WindowManager.window == SCORE_SCREEN)
            {
                return bgMusic1;
            }
            return bgMusic3;
        }
        else if(WindowManager.window == REPAIR_SHOP)
        {
            return repairShop;
        }
        else if(WindowManager.window == SCORE_SCREEN)
        {
            if(Events.level == 51)
            {
                return victory;
            }
            return scoreScreen;
        }
        else if(WindowManager.window.isMainWindow())
        {
            return mainMenu;
        }
        else if(Events.level >= 1 && Events.level < 10)
        {
            return level_01_09;
        }
        else if(Events.level == 10)
        {
            return bossLevel;
        }
        else if(Events.level >= 11 && Events.level < 20)
        {
            return level_11_19;
        }
        else if(Events.level == 20)
        {
            return bossLevel;
        }
        else if(Events.level >= 21 && Events.level < 30)
        {
            return level_21_29;
        }
        else if(Events.level == 30)
        {
            return bossLevel;
        }
        else if(Events.level >= 31 && Events.level < 40)
        {
            return level_31_39;
        }
        else if(Events.level == 40)
        {
            return bossLevel;
        }
        else if(Events.level >= 41 && Events.level < 49)
        {
            return level_41_48;
        }
        else if(Events.level == 49)
        {
            return level_49;
        }
        else if(Events.level == Events.MAXIMUM_LEVEL)
        {
            return finalBossLevel;
        }
        return victory;
    }

    public static void play(Clip clip)
    {
        clip.setFramePosition(0);
        if(isSoundOn)
        {
            if(!WindowManager.window.isMainWindow())
            {
                try
                {
                    Thread.sleep(1);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            clip.start();
        }
    }

    public static void loop(Clip clip)
    {
        if(isSoundOn)
        {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // Abspielen eines Lob-Sounds entsprechend der Anzahl mit einem Mal besiegter Gegner
    public static void praise(int nr)
    {
        if(nr == 1)
        {
            play(niceCatch);
            play(applause2);
        }
        else if(nr == 2)
        {
            play(doubleKill);
        }
        else if(nr == 3)
        {
            play(trippleKill);
            play(applause2);
        }
        else if(nr == 4)
        {
            play(megaKill);
            play(applause2);
        }
        else
        {
            stopApplause();
            play(applause1);
            if(nr == 5)
            {
                play(multiKill);
            }
            else if(nr >= 6)
            {
                play(monsterKill);
            }
        }
    }

    private static void stopApplause()
    {
        applause1.stop();
        applause2.stop();
    }

    public static void playSpecialSound(HelicopterType helicopterType)
    {
        play(helicopterType.getSpecialSound());
    }

    public static Clip getEmp()
    {
        return emp;
    }

    public static Clip getPlasmaOn()
    {
        return plasmaOn;
    }

    public static Clip getShieldUp()
    {
        return shieldUp;
    }

    public static Clip getStunActivated()
    {
        return stunActivated;
    }

    public static Clip getTeleport1()
    {
        return teleport1;
    }
}