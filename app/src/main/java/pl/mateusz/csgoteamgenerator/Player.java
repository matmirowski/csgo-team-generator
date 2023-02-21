package pl.mateusz.csgoteamgenerator;

import android.graphics.drawable.Drawable;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents basic information about the player, used in AsyncTasks to pass information
 * about the player to another task.
 */
// todo lombok
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
     */
    public Player(String name, String url, int index, String imageSource) {
        this.name = name;
        this.url = url;
        this.index = index;
        this.imageSource = imageSource;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getIndex() {
        return index;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
