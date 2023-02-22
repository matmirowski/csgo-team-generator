package pl.mateusz.csgoteamgenerator;

import android.graphics.drawable.Drawable;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents basic information about the player, used in AsyncTasks to pass information
 * about the player to another task.
 */
@Getter
@Setter
public class Player {
    private String name;
    private String url;
    private int index;
    private String imageSource;
    private Drawable image;

    /**
     * Creates a player.
     * @param name nickname of a player
     * @param url url address of player's image (can be null)
     * @param index index of location, where player should appear (0 - sniper, 1-3 - rifler,
     *             4 - igl)
     * @param imageSource type of image source (toString of enum ImageSource)
     */
    public Player(String name, String url, int index, String imageSource) {
        this.name = name;
        this.url = url;
        this.index = index;
        this.imageSource = imageSource;
    }

}
