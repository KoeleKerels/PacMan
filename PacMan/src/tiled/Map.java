package tiled;

import javax.imageio.ImageIO;
import javax.json.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * @author Ian Vink
 */

public class Map {


    private int mapHeight;
    private int mapWidth;
    private int tileHeight;
    private int tileWidth;

    private java.util.Map<Integer, Tile> tiles = new TreeMap<>();
    private ArrayList<Layer> layers = new ArrayList();

    private boolean[][] collisionlayer;


    public Map(String filename) {
        JsonReader reader = Json.createReader(getClass().getResourceAsStream(filename));
        JsonObject map = (JsonObject) reader.read();

        mapHeight = map.getInt("height");
        mapWidth = map.getInt("width");
        tileHeight = map.getInt("tileheight");
        tileWidth = map.getInt("tilewidth");


        JsonArray tilesets = map.getJsonArray("tilesets");
        try {
            for (int i = 0; i < tilesets.size(); i++) {
                JsonObject tileset = tilesets.getJsonObject(i);

                String tileFile = tileset.getString("image");
                tileFile = tileFile.replaceAll("\\.\\./", "/");


                BufferedImage tileImage = ImageIO.read(getClass().getResource(tileFile));

                int tilesetWidth = tileset.getInt("imagewidth");
                int tilesetHeight = tileset.getInt("imageheight");
                int tileWidth = tileset.getInt("tilewidth");
                int tileHeight = tileset.getInt("tileheight");

                int index = tileset.getInt("firstgid");
                for (int y = 0; y + tileHeight <= tilesetHeight; y += tileHeight) {
                    for (int x = 0; x + tileWidth <= tilesetWidth; x += tileWidth) {
                        tiles.put(index, new Tile(tileImage.getSubimage(x, y, tileWidth, tileHeight)));
                        index++;
                    }
                }

                if (tileset.containsKey("tileproperties")) {
                    index = tileset.getInt("firstgid");
                    for (int j = 0; j < tileset.getInt("tilecount"); j++) {
                        JsonObject properties = tileset.getJsonObject("tileproperties").getJsonObject(j + "");
                        if (properties != null) {
                            Tile tile = tiles.get(index);
                            if (properties.containsKey("walkable")) {
                                tile.setWalkable(true);
                            }
                            if (properties.containsKey("startarea")) {
                                tile.setStartArea(true);
                            }
                        }
                        index++;
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        collisionlayer = new boolean[mapHeight][mapWidth];

        JsonArray layers = map.getJsonArray("layers");

        for (int i = 0; i < layers.size(); i++) {
            if (layers.getJsonObject(i).getString("type").equals("tilelayer")) {
                this.layers.add(new TileLayer(layers.getJsonObject(i), this));
            }

        }


    }

    public java.util.Map<Integer, Tile> getTiles() {
        return tiles;
    }

    public boolean[][] getCollisionlayer() {
        return collisionlayer;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public void draw(Graphics2D g2d) {
        for (Layer layer : layers) {
            layer.draw(g2d);
        }
    }

}
