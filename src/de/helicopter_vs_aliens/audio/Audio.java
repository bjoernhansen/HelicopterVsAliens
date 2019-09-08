package de.helicopter_vs_aliens.audio;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.model.helicopter.HelicopterTypes;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

import static de.helicopter_vs_aliens.model.powerup.PowerUpTypes.*;
import static de.helicopter_vs_aliens.gui.WindowTypes.*;


public class Audio
{
    public static final boolean
        MICHAEL_MODE = false;        // Legt fest, ob der Michael-Modus bei der Hintergrundmusikauswahl verfügbar ist

    private static final int
        NUMBER_OF_ANNOUNCERS = 6;

    public static boolean
        isSoundOn = true,            // = true: Hintergrundmusik wird abgespielt
        standardBackgroundMusic = false;    // = true: Verwenden der Standard-Hintergrund-Musikauswahl

    public static AudioClip
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
        nicecatch,
        doublekill,
        tripplekill,
        multikill,
        megakill,
        monsterkill,
        powerAnnouncer[],

        // Hintergrundmusik für den Standard-Modus
        bgMusic1,
        bgMusic2,
        bgMusic3,

        // Hintergrundmusik für Michael-Modus
        mainMenu,
        repairShop,
        scorescreen,
        level_01_09,
        level_11_19,
        level_21_29,
        level_31_39,
        level_41_48,
        level_49,
        bossLevel,
        finalBossLevel,
        victory;


    private static AudioClip getAudioClip(String string)
    {
        URL url = Audio.class.getResource("sounds/" + string);
        return Applet.newAudioClip(url);
    }

    public static void initialize()
    {
        launch1 = getAudioClip("launch1.wav");
        explosion1 = getAudioClip("explosion1.wav");
        explosion2 = getAudioClip("explosion2.wav");
        explosion3 = getAudioClip("explosion3.wav");
        explosion4 = getAudioClip("explosion4.wav");
        bgMusic1 = getAudioClip("bg_music1.wav");
        bgMusic2 = getAudioClip("bg_music2.wav");
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
        doublekill = getAudioClip("doublekill.wav");
        tripplekill = getAudioClip("tripplekill.wav");
        megakill = getAudioClip("megakill.wav");
        multikill = getAudioClip("multikill.wav");
        monsterkill = getAudioClip("monsterkill.wav");
        nicecatch = getAudioClip("nicecatch.wav");
        landing = getAudioClip("landing.wav");
        plasmaOn = getAudioClip("plasma_on.wav");
        plasmaOff = getAudioClip("plasma_off.wav");
        powerUpFade1 = getAudioClip("pu_fade1.wav");
        powerUpFade2 = getAudioClip("pu_fade2.wav");
        stunActivated = getAudioClip("stun_activated.wav");

        powerAnnouncer = new AudioClip[NUMBER_OF_ANNOUNCERS];
        powerAnnouncer[TRIPLE_DAMAGE.ordinal()] = getAudioClip("announcer_triple_dmg.wav");
        powerAnnouncer[INVINCIBLE.ordinal()] = getAudioClip("announcer_invincible.wav");
        powerAnnouncer[UNLIMITRED_ENERGY.ordinal()] = getAudioClip("announcer_unlimited_energy.wav");
        powerAnnouncer[BOOSTED_FIRE_RATE.ordinal()] = getAudioClip("announcer_fire_rate_boosted.wav");
        powerAnnouncer[REPARATION.ordinal()] = getAudioClip("announcer_reparation.wav");
        powerAnnouncer[BONUS_INCOME.ordinal()] = getAudioClip("announcer_bonus_credit.wav");

        if (MICHAEL_MODE)
        {
            mainMenu = getAudioClip("main_menu.wav");
            repairShop = getAudioClip("repair_shop.wav");
            scorescreen = getAudioClip("scorescreen.wav");
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
    }

    public static void refreshBackgroundMusic()
    {
        if (currentBg != null)
        {
            currentBg.stop();
        }
        if (isSoundOn)
        {
            currentBg = getBgMusic();
            currentBg.loop();
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
    private static AudioClip getBgMusic()
    {
        if (!standardBackgroundMusic)
        {
            if (Menu.window == GAME && !Events.isBossLevel())
            {
                return bgMusic2;
            } else if (Menu.window == REPAIR_SHOP || Menu.window == SCORESCREEN)
            {
                return bgMusic1;
            }
            return bgMusic3;
        } else if (Menu.window == REPAIR_SHOP)
        {
            return repairShop;
        } else if (Menu.window == SCORESCREEN)
        {
            if (Events.level == 51)
            {
                return victory;
            }
            return scorescreen;
            // TODO Lösen über Zugegörigkeit zu einem EnumSet und innerhalb einer Methode
        } else if (Menu.window == INFORMATIONS
                || Menu.window == DESCRIPTION
                || Menu.window == SETTINGS
                || Menu.window == CONTACT
                || Menu.window == HELICOPTER_TYPES
                || Menu.window == HIGHSCORE
                || Menu.window == STARTSCREEN)
        {
            return mainMenu;
        } else if (Events.level >= 1 && Events.level < 10)
        {
            return level_01_09;
        } else if (Events.level == 10)
        {
            return bossLevel;
        } else if (Events.level >= 11 && Events.level < 20)
        {
            return level_11_19;
        } else if (Events.level == 20)
        {
            return bossLevel;
        } else if (Events.level >= 21 && Events.level < 30)
        {
            return level_21_29;
        } else if (Events.level == 30)
        {
            return bossLevel;
        } else if (Events.level >= 31 && Events.level < 40)
        {
            return level_31_39;
        } else if (Events.level == 40)
        {
            return bossLevel;
        } else if (Events.level >= 41 && Events.level < 49)
        {
            return level_41_48;
        } else if (Events.level == 49)
        {
            return level_49;
        } else if (Events.level == 50)
        {
            return finalBossLevel;
        }
        return victory;
    }

    public static void play(AudioClip clip)
    {
        clip.stop();
        if(isSoundOn){clip.play();}
    }

    public static void loop(AudioClip clip)
    {
        if(isSoundOn){clip.loop();}
    }

    // Abspielen eines Lob-Sounds entsprechend der Anzahl mit einem Mal besiegter Gegner
    public static void praise(int nr)
    {
        if (nr == 1)
        {
            play(nicecatch);
            play(applause2);
        } else if (nr == 2)
        {
            play(doublekill);
        } else if (nr == 3)
        {
            play(tripplekill);
            play(applause2);
        } else if (nr == 4)
        {
            play(megakill);
            play(applause2);
        } else
        {
            stopApplause();
            play(applause1);
            if (nr == 5)
            {
                play(multikill);
            } else if (nr >= 6)
            {
                play(monsterkill);
            }
        }
    }

    private static void stopApplause()
    {
        applause1.stop();
        applause2.stop();
    }

    public static void playSpecialSound(HelicopterTypes helicopterType)
    {
        play(getSpecialSound(helicopterType));
    }

    private static AudioClip getSpecialSound(HelicopterTypes helicopterType)
    {
        if(helicopterType == null)
        {
            return shieldUp;
        }
        else switch (helicopterType)
        {
            case PHOENIX:
                return teleport1;
            case OROCHI:
                return stunActivated;
            case KAMAITACHI:
                return plasmaOn;
            case PEGASUS:
                return emp;
            default:
                return shieldUp;
        }
    }
}