package xyz.izaak.gamesystems;

import org.joml.Vector2f;

/**
 * A GameSystem is a specific set of things the game must do each frame, as well as in response
 * to input from the mouse or keyboard. Actual GameSystems must subclass this class to fill in
 * the appropriate methods with desired functionality. For this reason, the constructor is
 * protected (no raw GameSystem objects can be externally created, except by subclasses of
 * GameSystem).
 */
@SuppressWarnings("unused")
public class GameSystem {

    protected GameSystem() {}

    public void initialize() {}             // Called at the beginning of the game
    public void tick(float seconds) {}      // Called every frame

    public void onKeyDown(int key) {}       // Called when a key is first pushed down
    public void onKeyHeld(int key) {}       // Called when a key is pushed down, and repeated
    public void onKeyRepeat(int key) {}     // Called when a key is held, producing repeat events
    public void onKeyUp(int key) {}         // Called when a key is released

    public void onMouseMove(Vector2f delta) {}   // Called when the mouse moves
    public void onMouseDown(Vector2f position, int button) {}       // Called when a mouse button is pressed
    public void onMouseUp(Vector2f position, int button) {}         // Called when a mouse button is released

    public void onScroll(float x, float y) {}   // Called when the scroll wheel or track pad is scrolled
    public void onResize(Vector2f newSize) {}   // Called when the window is resized

}
