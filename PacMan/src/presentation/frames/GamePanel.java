package presentation.frames;

import business.SoundPlayer;
import data.Game;
import entities.GameObject;
import javafx.scene.input.KeyCode;
import presentation.components.Camera;
import presentation.components.Controls;
import presentation.components.DebugDraw;
import sun.awt.SunHints;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Key;
import java.util.TimerTask;

/**
 * @author Ian Vink
 */

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private Game game;
    private Camera camera;
    private DebugDraw debugDraw;
    private Controls controls;

    private long startTime;
    private long endTime;
    private long deltaTime;

    private boolean pacManHasWon = false;
    private boolean ghostHasWon = false;

    private Font pacManFont;


    public GamePanel() {
        game = Game.getInstance();
        camera = new Camera();
        debugDraw = new DebugDraw(this);
        controls = new Controls();


        try {
            pacManFont = Font.createFont(Font.TRUETYPE_FONT, new File(getClass().getResource("/fonts/crackmanfront.ttf").toURI()));
            GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            graphicsEnvironment.registerFont(pacManFont);
        } catch (FontFormatException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        setBackground(Color.BLACK);


        addKeyListener(this);

        new Timer(1000 / 60, this).start();
        startTime = System.currentTimeMillis();



        Game.getInstance().getSoundPlayer().getClip(SoundPlayer.Sound.GAME_MUSIC).start();


    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setTransform(camera.getTransform());

        game.getMap().draw(g2d);
        debugDraw.draw(g2d);

        for (GameObject gameObject : game.getGameObjects()) {
            gameObject.draw(g2d);
        }

        if(ghostHasWon){
            g2d.setFont(pacManFont.deriveFont(70f));
            g2d.setColor(Color.yellow);
            g2d.drawString("Ghosts has won!", 0, getHeight()/2);
        }
        else if(pacManHasWon){
            g2d.setFont(pacManFont.deriveFont(70f));
            g2d.setColor(Color.yellow);
            g2d.drawString("Pacman has won!", 0, getHeight()/2);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (game.isPaused()) {
            deltaTime = System.currentTimeMillis() - startTime;
            //3 Seconds  before you can start
            if (deltaTime >= 3000) {
                game.setPaused(false);
                startTime = System.currentTimeMillis();
            }
            return;
        }

        endTime = System.currentTimeMillis();
        deltaTime = endTime - startTime;
        startTime = System.currentTimeMillis();

        controls.update();

        game.getPacMan().move(deltaTime);
        game.getGhosts().forEach(ghost -> ghost.move(deltaTime));

        if(game.isAllPicksUpPickedUp()){
            pacManHasWon = true;
            game.setPaused(true);
            java.util.Timer timer = new java.util.Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    game.reset();
                    pacManHasWon = false;
                    repaint();
                }
            }, 5000);
        }
        else if(game.hasGhostWon()){
            ghostHasWon = true;
            game.setPaused(true);
            java.util.Timer timer = new java.util.Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Game.getInstance().reset();
                    ghostHasWon = false;
                    repaint();
                }
            }, 5000);

        }


        repaint();
    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        String key = KeyEvent.getKeyText(e.getKeyCode());

        if (key.equals("W") || key.equals("Up"))
            controls.setCurrentKey(Controls.Key.UP);
        else if (key.equals("A") || key.equals("Left"))
            controls.setCurrentKey(Controls.Key.LEFT);
        else if (key.equals("S") || key.equals("Down"))
            controls.setCurrentKey(Controls.Key.DOWN);
        else if (key.equals("D") || key.equals("Right"))
            controls.setCurrentKey(Controls.Key.RIGHT);

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
