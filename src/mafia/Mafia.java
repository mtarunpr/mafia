
package mafia;

import java.awt.AWTException;
import java.awt.Label;
import java.awt.Robot;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simulation of the Role-Playing-Game Mafia.
 * 
 * @author Tarun Prasad
 * @version 1.0
 */
public class Mafia {

    static final int NO_OF_PLAYERS = 9;
    static String[] names = new String[NO_OF_PLAYERS];
    static Player[] players;
    static String[] namesShuffled;
    static Scanner sc = new Scanner(System.in);

    /**
     * The various roles in the game of Mafia.
     */
    public enum Role {
        GODFATHER, MAFIOSO, VIGILANTE, DOCTOR, INVESTIGATOR, ESCORT, VETERAN, SERIAL_KILLER, JESTER
    }

    /**
     * The teams in the game of Mafia.
     */
    public enum Team {
        MAFIA, TOWN, SERIAL_KILLER
    }

    /**
     * Shuffles an array of Strings randomly.
     * 
     * @param array the array of Strings to be shuffled
     */
    private static void shuffle(String[] array) {
        int index;
        String temp;
        Random random = new Random();
        for (int i = NO_OF_PLAYERS - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    /**
     * Pauses the program for a certain number of seconds, as specified in the
     * argument. Primarily to be used in <code>clearScreen</code>.
     * 
     * @param sec number of seconds to pause the program for
     */
    private static void pause(int sec) {
        // Following try-catch block is for waiting a second before next screen.
        try {
            TimeUnit.SECONDS.sleep(sec);
        } catch (InterruptedException ex) {
            Logger.getLogger(Mafia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Waits for Enter key to be pressed before the program continues execution.
     */
    private static void pressEnterToContinue() {
        System.out.println("Press Enter key to continue...");
        try {
            System.in.read();
        } catch (Exception e) {
        }
    }

    /**
     * Clears the NetBeans Output screen. CTRL-L is the shortcut for clearing the
     * output window in NetBeans. This method uses a Robot to simulate key press of
     * Ctrl-L. Change this code to whatever matches the intended output mode.
     */
    public static void clearScreen() {
        try {
            Robot r = new Robot();
            r.keyPress(17);     // Ctrl
            r.keyPress(76);     // L
            r.keyRelease(17);
            r.keyRelease(76);
        } catch (AWTException ex) {
            // Log
        }
        pause(1);
    }

    /**
     * Returns the <code>Player</code> who has the role specified in the argument.
     * Currently only supports the existence of single roles. If duplicate roles
     * exist, returns the first found <code>Player</code> with the specified role.
     * 
     * @param role the <code>Role</code> of the required <code>Player</code>
     * @return the <code>Player</code> with the specified role
     */
    public static Player getPlayer(Role role) {
        for (Player player : players) {
            if (role == player.role)
                return player;
        }
        return null;
    }

    /**
     * Returns the <code>Player</code> who has the role specified in the argument.
     * Currently only supports the existence of single roles. If duplicate roles
     * exist, returns the first found <code>Player</code> with the specified role.
     * 
     * @param name the name of the required <code>Player</code>
     * @return the <code>Player</code> with the specified name
     */
    public static Player getPlayer(String name) {
        for (Player player : players) {
            if (name.equals(player.name))
                return player;
        }
        return null;
    }

    /**
     * Starts the game with day 1. Reads in the names of all the players and stores
     * them in <code>names[]</code>.
     */
    private static void start() {

        System.out.println("Welcome to Mafia!");
        System.out.println((new Label("\u00a9")).getText() + " Tarun Prasad 2017");
        System.out.println("Day 1");
        System.out.println("Enter names of players: ");
        for (int i = 0; i < NO_OF_PLAYERS; i++) {
            String name;
            do {
                System.out.print("Player " + (i + 1) + ": ");
                name = sc.nextLine();
            } while (name.equals("none") || Arrays.asList(names).contains(name));
            names[i] = name;
        }
        clearScreen();

    }

    /**
     * Investigates the <code>Player</code> with name as specified in the argument
     * and displays the results.
     * 
     * @param target the name of the <code>Player</code> to be investigated
     */
    private static String investigate(String target) {
        Role role = getPlayer(target).role;
        Role results[] = null;

        Role options[][] = { { Role.VIGILANTE, Role.VETERAN, Role.MAFIOSO }, { Role.DOCTOR, Role.SERIAL_KILLER },
                { Role.GODFATHER }, { Role.ESCORT }, { Role.INVESTIGATOR }, { Role.JESTER } };

        for (Role roleSet[] : options) {
            if (Arrays.asList(roleSet).contains(role)) {
                results = roleSet;
                break;
            }
        }

        String investResults = "";
        for (Role resultRole : results) {
            investResults += resultRole + ", ";
        }

        return target + " may be a " + investResults.replaceAll(", $", "");

    }

    /**
     * Performs the killings at the end of the night based on the key-value pairs of
     * who kills whom, the <code>Player</code> to be healed and the
     * <code>Player</code> to be role-blocked. Currently only supports one healing /
     * role-blocking.
     * 
     * @param nightKills a <code>HashMap</code> with the killer <code>Player</code>
     *                   as key and the <code>Player</code> to be killed as the
     *                   corresponding value
     * @param toHeal     the <code>Player</code> healed by the doctor
     * @param toBlock    the <code>Player</code> role-blocked by the Escort/Consort
     */
    public static void performKillings(HashMap<Player, Player> nightKills, Player toHeal, Player toBlock) {

        Player vet = getPlayer(Role.VETERAN);

        // Following code block runs only once after each night.
        // It checks if either the doctor or the escort visited the
        // veteran when he was on alert and if so, kills them.

        if (vet.isOnAlert) {
            if (vet.equals(toHeal))
                getPlayer(Role.DOCTOR).kill(vet);
            if (vet.equals(toBlock) && !getPlayer(Role.ESCORT).equals(toHeal))
                getPlayer(Role.ESCORT).kill(vet);
        }

        // The following for loop iterates through HashMap nightKills
        // and kills everyone who deserves to be killed in the HashMap

        for (Player killer : nightKills.keySet()) {
            Player toKill = nightKills.get(killer);

            // Checks if the killer is the jester, in which case
            // the attack is unstoppable and the toKill cannot be
            // saved by the doctor. Therefore, kills immediately.
            if (killer.role == Role.JESTER) {
                toKill.kill(killer);
                continue;
            }

            // Checks if invest has targeted alert veteran, and if so, kills the invest
            // This has a separate if block (and is not combined with the other
            // killing roles targeting veteran because invest does not kill
            // the veteran if he is not on alert, wherease other roles do).
            if (killer.role == Role.INVESTIGATOR) {
                if (toKill.isOnAlert && !getPlayer(Role.INVESTIGATOR).equals(toHeal))
                    killer.kill(toKill);
                continue;
            }

            // Checks if any killing role has targeted the alert veteran and
            // if so, kills the killing role; otherwise falls through and
            // kills the veteran.
            if (toKill.isOnAlert) {
                if (!killer.equals(toHeal))
                    killer.kill(toKill);
                continue;
            }

            if (killer.equals(toBlock) && toBlock.role != Role.SERIAL_KILLER)
                continue;
            if (toKill.equals(toHeal))
                continue;

            toKill.kill(killer);

        }
    }

    /**
     * Checks if the game is over and displays the result of the game. If only
     * players of one <code>Team</code> team are alive, the game ends as a win for
     * that <code>Team</code>. If no one is alive, the game ends as a draw. This
     * method also returns whether the game has ended or not. To be used after
     * <code>performKillings</code> in the night as well as after
     * <code>lynch</code>.
     * 
     * @return <code>true</code> if the game has ended; <code>false</code> otherwise
     */
    public static boolean checkWin() {
        Team team = null;
        for (String name : namesShuffled) {
            if (name != null) {
                team = getPlayer(name).team;
                break;
            }
        }

        for (String name : namesShuffled) {
            if (name != null && team != null)
                if (getPlayer(name).team != team)
                    return false;
        }

        if (team == null) {
            System.out.println("Game over");
            System.out.println("The game has ended in a DRAW.");
            System.out.println();
            pressEnterToContinue();
            clearScreen();
            displayRoleTable();
            return true;
        }

        System.out.println("Game over");
        System.out.println(team + " wins!");
        System.out.print("The winners are: ");

        String winnerList = "";
        for (Player player : players) {
            if (player.team == team)
                player.hasWon = true;
        }

        for (Player player : players) {
            if (player.hasWon == true)
                winnerList += player.name + ", ";
        }
        System.out.println(winnerList.replaceAll(", $", ""));

        System.out.println();
        pressEnterToContinue();
        clearScreen();
        displayRoleTable();

        return true;
    }

    /**
     * Displays the names and corresponding roles of all the players in the game. To
     * be used in <code>checkWin</code> to display the role table after the game has
     * ended.
     */
    private static void displayRoleTable() {
        System.out.println("Roles: ");

        for (String name : names) {
            System.out.println(name + " - " + getPlayer(name).role);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        start();

        players = new Player[NO_OF_PLAYERS];
        namesShuffled = Arrays.copyOf(names, NO_OF_PLAYERS);
        shuffle(namesShuffled);

        for (int i = 0; i < NO_OF_PLAYERS; i++) {
            players[i] = new Player(namesShuffled[i], Role.values()[i]);
        }

        // D/N LOOP STARTS HERE

        // If only a finite no. of nights should be allowed, uncomment the for condition

        for (int nightNo = 1; /* nightNo<=10 */; nightNo++) {

            shuffle(namesShuffled);
            System.out.println("Night " + nightNo);
            System.out.println("All go to sleep");

            // Key - killer; Value - toKill
            HashMap<Player, Player> nightKills = new HashMap<>();

            Player toHeal = null, toBlock = null;
            boolean investBlocked = false;
            String investResults = null;

            // Special case for jester
            Player jester = getPlayer(Role.JESTER);
            if (jester.hasWon && jester.splPowerLeft == 1) {
                System.out.println("Please wake " + jester.name + " up");
                pressEnterToContinue();
                clearScreen();
                System.out.println(jester.name + " wake up");
                pressEnterToContinue();

                System.out.println("Your role is " + jester.role);

                String target;
                do {
                    System.out.println("Whom do you want to kill, among those who lynched you?");
                    target = sc.nextLine();
                } while (!Arrays.asList(namesShuffled).contains(target) || target.equals(jester.name));
                nightKills.put(jester, getPlayer(target));
                jester.splPowerLeft--;
            }

            for (int i = 0; i < NO_OF_PLAYERS; i++) {

                if (namesShuffled[i] == null)
                    continue;

                Player player = getPlayer(namesShuffled[i]);
                String name = player.name;
                Role role = player.role;

                if (player.isDead)
                    continue;

                System.out.println("Please wake " + name + " up");
                pressEnterToContinue();
                clearScreen();

                System.out.println(name + " wake up");
                pressEnterToContinue();

                System.out.println("Your role is " + role);

                String target;
                switch (role) {
                    case GODFATHER:
                        Player mafioso = getPlayer(Role.MAFIOSO);
                        if (mafioso != null)
                            if (!mafioso.isDead)
                                System.out.println(
                                        mafioso.name + " is the mafioso. Please wake him up and decide whom to kill.");
                        do {
                            System.out.println("Whom do you want to kill?");
                            target = sc.nextLine();
                        } while (!Arrays.asList(namesShuffled).contains(target) && !target.equals("none")
                                || target.equals(name) || (mafioso != null ? target.equals(mafioso.name) : false));
                        if (!target.equals("none")) {
                            nightKills.put(mafioso == null || mafioso.isDead ? player : mafioso, getPlayer(target));
                        }
                        break;

                    case DOCTOR:
                        System.out.println("You have " + player.splPowerLeft + " self-heals left.");
                        do {
                            System.out.println("Whom do you want to heal?");
                            target = sc.nextLine();
                        } while (!Arrays.asList(namesShuffled).contains(target) && !target.equals("none")
                                || (player.splPowerLeft == 0 && target.equals(name)));
                        if (!target.equals("none")) {
                            toHeal = getPlayer(target);
                            if (target.equals(name))
                                player.splPowerLeft--;
                        }
                        break;

                    case ESCORT:
                        do {
                            System.out.println("Whom do you want to role-block?");
                            target = sc.nextLine();
                        } while (!Arrays.asList(namesShuffled).contains(target) && !target.equals("none")
                                || target.equals(name));
                        if (!target.equals("none")) {
                            toBlock = getPlayer(target);
                        }
                        break;

                    case SERIAL_KILLER:
                        do {
                            System.out.println("Whom do you want to kill?");
                            target = sc.nextLine();
                        } while (!Arrays.asList(namesShuffled).contains(target) && !target.equals("none")
                                || target.equals(name));
                        if (!target.equals("none")) {
                            nightKills.put(player, getPlayer(target));
                        }
                        break;

                    case VIGILANTE:
                        System.out.println("You have " + player.splPowerLeft + " bullets left.");
                        if (player.splPowerLeft > 0) {
                            do {
                                System.out.println("Whom do you want to kill?");
                                target = sc.nextLine();
                            } while (!Arrays.asList(namesShuffled).contains(target) && !target.equals("none")
                                    || target.equals(name));
                            if (!target.equals("none")) {
                                nightKills.put(player, getPlayer(target));
                                player.splPowerLeft--;
                            }
                        }
                        break;

                    case INVESTIGATOR:
                        do {
                            System.out.println("Whom do you want to investigate?");
                            target = sc.nextLine();
                        } while (!Arrays.asList(namesShuffled).contains(target) && !target.equals("none")
                                || target.equals(name));
                        if (!target.equals("none")) {
                            investResults = investigate(target);
                            if (getPlayer(target).role == Role.VETERAN)
                                nightKills.put(player, getPlayer(target));
                        }
                        break;

                    case VETERAN:
                        System.out.println("You have " + player.splPowerLeft + " alerts left.");
                        if (player.splPowerLeft > 0) {
                            do {
                                System.out.println("Do you want to be on alert tonight? Y/N");
                                target = sc.nextLine();
                            } while (!target.equals("Y") && !target.equals("N"));
                            if (target.equals("Y")) {
                                player.setOnAlert();
                                break;
                            }
                        }
                        player.setOffAlert();
                        break;

                }
            }

            // Blockings
            if (toBlock != null) {
                switch (toBlock.role) {
                    case DOCTOR:
                        toHeal = null;
                        break;

                    case INVESTIGATOR:
                        investBlocked = true;
                        break;

                    case SERIAL_KILLER:
                        nightKills.put(getPlayer(Role.SERIAL_KILLER), getPlayer(Role.ESCORT));
                        break;
                }
            }

            // Invest results
            Player invest = getPlayer(Role.INVESTIGATOR);
            if (!investBlocked && !invest.isDead && investResults != null) {
                System.out.println("Please wake " + invest.name + " up");
                pressEnterToContinue();
                clearScreen();

                System.out.println(invest.name + " wake up");
                pressEnterToContinue();
                System.out.println(investResults);
            }

            System.out.println("The faint beams of the sun are beginning to peek out.");
            System.out.println("Please wake everyone up");
            pressEnterToContinue();

            // NIGHT ENDS, DAY BEGINS
            clearScreen();
            System.out.println("Day " + (nightNo + 1));

            performKillings(nightKills, toHeal, toBlock);
            System.out.println();

            if (checkWin())
                break;

            System.out.println("Time to discuss and vote against suspicious members.");

            String lynch;
            do {
                System.out.println("If anyone was lynched, enter the name (otherwise enter 'none'): ");
                lynch = sc.nextLine();
            } while (!Arrays.asList(namesShuffled).contains(lynch) && !lynch.equals("none"));

            if (!lynch.equals("none")) {
                getPlayer(lynch).lynch();
            }

            if (checkWin())
                break;

            pressEnterToContinue();
            clearScreen();

        }
    }
}
