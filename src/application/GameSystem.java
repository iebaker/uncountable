package application;

import joml.Vector2f;
import world.World;

public class GameSystem {

    protected GameSystem() {}

    public void tick(World world, float seconds) {}

    public void onKeyDown(int key) {}
    public void onKeyHeld(int key) {}
    public void onKeyRepeat(int key) {}
    public void onKeyUp(int key) {}

    public void onMouseMove(Vector2f position, Vector2f delta) {}
    public void onMouseDown(Vector2f position, int button) {}
    public void onMouseUp(Vector2f position, int button) {}

    public void onScroll(float x, float y) {}

    public void onResize(Vector2f newSize) {}


}
