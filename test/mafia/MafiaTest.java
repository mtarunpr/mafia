/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mafia;

import java.util.HashMap;
import mafia.Mafia.Role;
import mafia.Mafia.Team;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Tarun Prasad
 */
public class MafiaTest {

    static Player maf, gf, escort, sk, doctor, vig, vet, invest;

    public MafiaTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() {

        Mafia.players = new Player[8];
        Mafia.namesShuffled = new String[8];
        Mafia.namesShuffled = new String[8];

        for (int i = 0; i < 8; i++) {
            Mafia.players[i] = new Player(Mafia.Role.values()[i] + "", Mafia.Role.values()[i]);
            Mafia.names[i] = Mafia.namesShuffled[i] = Mafia.players[i].name;
        }

        maf = Mafia.getPlayer(Role.MAFIOSO);
        gf = Mafia.getPlayer(Role.GODFATHER);
        escort = Mafia.getPlayer(Role.ESCORT);
        sk = Mafia.getPlayer(Role.SERIAL_KILLER);
        doctor = Mafia.getPlayer(Role.DOCTOR);
        invest = Mafia.getPlayer(Role.INVESTIGATOR);
        vig = Mafia.getPlayer(Role.VIGILANTE);
        vet = Mafia.getPlayer(Role.VETERAN);

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testPerformKillings() {
        HashMap<Player, Player> test = new HashMap();

        // TEST 1 - BASIC TEST
        test.put(sk, vig);
        test.put(maf, doctor);
        Mafia.performKillings(test, null, null);
        assertTrue("Vig is dead", vig.isDead);
        assertTrue("Doc is dead", doctor.isDead);
        assertFalse("Maf should not be dead", maf.isDead);
        setUp();
        test.clear();

        // // TEST 2 - ESCORT ON SK
        // test.put(sk, vig);
        // Mafia.performKillings(test, null, sk);
        // assertTrue("Escort is dead", escort.isDead);
        // assertFalse("SK target is alive", vig.isDead);
        // setUp();
        // test.clear();
    }

    @Test
    public void testCheckWin() {
        System.out.println("TESTING checkWin");

        assertFalse("Game is not over", Mafia.checkWin());

        escort.lynch();
        doctor.lynch();
        sk.lynch();
        gf.lynch();
        assertFalse("Game is not over", Mafia.checkWin());
        setUp();

        for (Player player : Mafia.players) {
            if (player.team == Team.MAFIA || player.team == Team.SERIAL_KILLER)
                player.lynch();
        }

        assertTrue("Game is over - TOWN should win", Mafia.checkWin());
        setUp();

        for (Player player : Mafia.players) {
            if (player.team == Team.TOWN || player.team == Team.SERIAL_KILLER)
                player.lynch();
        }

        assertTrue("Game is over - MAFIA should win", Mafia.checkWin());
        setUp();

        for (Player player : Mafia.players) {
            if (player.team == Team.TOWN || player.team == Team.MAFIA)
                player.lynch();
        }

        assertTrue("Game is over - SERIAL_KILLER should win", Mafia.checkWin());
        setUp();

        for (Player player : Mafia.players) {
            player.lynch();
        }

        assertTrue("Game is over - should be DRAW", Mafia.checkWin());
        setUp();

        System.out.println(escort.isDead);

    }
}