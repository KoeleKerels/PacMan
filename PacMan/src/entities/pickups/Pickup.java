package entities.pickups;

import entities.GameObject;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public abstract class Pickup extends GameObject {
    public Pickup(BufferedImage image, Point2D position, int objectWidth, int objectHeight) {
        super(image, position, objectWidth, objectHeight);
    }
}
