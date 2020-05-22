/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mafia;

import static mafia.Mafia.NO_OF_PLAYERS;
import mafia.Mafia.Role;
import mafia.Mafia.Team;
import static mafia.Mafia.namesShuffled;

/**
 * A player of the game of Mafia.
 *
 * @author Tarun Prasad
 */
public class Player {

    String name;
    Role role;
    Team team;
    boolean isDead;
    int splPowerLeft;
    boolean isOnAlert;
    boolean hasWon;

    /**
     * Creates an instance of the <code>Player</code> <code>class</code> with 
     * the specified name and role.
     *
     * @param name the name of this <code>Player</code>
     * @param role the role of this <code>Player</code>
     */
    public Player(String name, Role role) {
        this.name = name;
        this.role = role;
        isDead = false;
        isOnAlert = false;
        hasWon = false;

        switch (role) {
            case VIGILANTE:
            case VETERAN:
                splPowerLeft = 3;
                break;
            case DOCTOR:
                splPowerLeft = 1;
                break;
            default:
                splPowerLeft = 0;
        }

        switch (role) {
            case VIGILANTE:
            case VETERAN:
            case INVESTIGATOR:
            case DOCTOR:
            case ESCORT:
                team = Team.TOWN;
                break;

            case MAFIOSO:
            case GODFATHER:
                team = Team.MAFIA;
                break;

            case SERIAL_KILLER:
                team = Team.SERIAL_KILLER;
                break;
            
            default:
                team = null;
                break;
        }
    }

    /**
     * Kills this <code>Player</code>.
     *
     * @param killer the <code>Player</code> who killed this <code>Player</code>
     */
    void kill(Player killer) {
        isDead = true;
        System.out.println(name + " is dead. He was the " + role + ". He was killed by the "
                + (killer.team == Team.MAFIA ? killer.team : killer.role) + ".");
        removeName();
        performPromotions();
    }

    /**
     * Sets this <code>Player</code> on alert if he is a <code>VETERAN</code>.
     */
    void setOnAlert() {
        if (role.equals(Role.VETERAN)) {
            isOnAlert = true;
            splPowerLeft--;
        }
    }

    /**
     * Sets this <code>Player</code> off alert if he is a <code>VETERAN</code>.
     */
    void setOffAlert() {
        if (role.equals(Role.VETERAN)) {
            isOnAlert = false;
        }
    }

    /**
     * Lynches this <code>Player</code> and displays his name and role.
     */
    void lynch() {
        isDead = true;
        System.out.println(name + " was lynched. He was the " + role + ".");
        if (role==Role.JESTER) {
            hasWon = true;
            splPowerLeft = 1;
            System.out.println("He has won the game, and will get his revenge from the grave.");
        }
        removeName();
        performPromotions();
    }

    /**
     * Removes the name of this <code>Player</code> from <code>namesShuffled</code>,
     * indicating that he is dead.
     */
    public void removeName() {
        for (int j = 0; j < NO_OF_PLAYERS; j++) {
            if (namesShuffled[j] != null) {
                if (namesShuffled[j].equals(name)) {
                    namesShuffled[j] = null;
                }
            }
        }
    }

    /**
     * Checks if any promotions are to be made, and if so, carries them out.
     * Currently only promotes <code>MAFIOSO</code> to <code>GODFATHER</code>.
     */
    private void performPromotions() {
        if (Mafia.getPlayer(Role.MAFIOSO) != null) {
            if (role == Role.GODFATHER && !Mafia.getPlayer(Role.MAFIOSO).isDead) {
                Mafia.getPlayer(Role.MAFIOSO).role = Role.GODFATHER;
            }
        }
    }
}
