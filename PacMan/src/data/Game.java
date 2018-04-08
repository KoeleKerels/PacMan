package data;

import entities.active_objects.ActiveGameObject;
import entities.GameObject;
import entities.active_objects.PacMan;
import entities.active_objects.Ghost;
import tiled.Map;
import tiled.ObjectLayer;
import tiled.TileLayer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ian Vink
 */

public class Game {

    private static Game instance;

    private Map map;

    private ArrayList<GameObject> gameObjects = new ArrayList<>();

    private int screenWidth;
    private int screenHeight;

    private Game() {

    }

    public static Game getInstance() {
        if (instance == null)
            instance = new Game();

        return instance;
    }


    public void setGameObjects() {

        ObjectLayer objLayer = (ObjectLayer) map.getLayers().stream()
                .filter(layer -> layer instanceof ObjectLayer)
                .findFirst()
                .get();


        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResource("/textures/pacman.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GameObject pacMan = new PacMan(image, objLayer.getStartPosPacMan(), 25, 25, 52, 52, 0.1);

        gameObjects.add(pacMan);


        image = null;
        try {
            image = ImageIO.read(getClass().getResource("/textures/ghost.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Point startPosGhost : objLayer.getStartPosGhosts()) {
            gameObjects.add(new Ghost(image, startPosGhost, 25, 25, 52, 52, 0.5));
        }

    }


    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void setScreenDimensions(int width, int height) {
        this.screenHeight = height;
        this.screenWidth = width;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    public List<ActiveGameObject> getActiveGameObjects() {
        return gameObjects.stream()
                .filter(object -> object instanceof ActiveGameObject)
                .map(activeobj -> (ActiveGameObject) activeobj)
                .collect(Collectors.toList());
    }

    public PacMan getPacMan() {
        return gameObjects.stream()
                .filter(object -> object instanceof PacMan)
                .map(pacman -> (PacMan) pacman)
                .findFirst()
                .orElse(null);
    }

    public List<Ghost> getGhosts() {
        return gameObjects.stream()
                .filter(object -> object instanceof Ghost)
                .map(ghost -> (Ghost) ghost)
                .collect(Collectors.toList());
    }


    public TileLayer getDataLayer() {
        return map.getLayers().stream()
                .filter(layer -> layer instanceof TileLayer)
                .map(tilelayer -> (TileLayer) tilelayer)
                .filter(tileLayer -> tileLayer.getName().equals("data"))
                .findFirst()
                .orElse(null);
    }

    private ObjectLayer getObjectsLayer() {
        return map.getLayers().stream()
                .filter(layer -> layer instanceof ObjectLayer)
                .map(objectlayer -> (ObjectLayer) objectlayer)
                .filter(objectlayer -> objectlayer.getName().equals("objects layer"))
                .findFirst()
                .orElse(null);
    }

    public Loop getLoop(Point mapPos) {
        return getObjectsLayer().getLoops().stream()
                .filter(loop -> loop.getEntrance().equals(mapPos) || loop.getExit().equals(mapPos))
                .findFirst()
                .orElse(null);
    }
}
