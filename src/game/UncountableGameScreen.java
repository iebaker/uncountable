package game;

import core.rendering.Graphics;

import core.App;
import core.Screen;
import game.world.Treadmill;
import game.world.World;

public class UncountableGameScreen extends Screen {

    World m_world;

    public UncountableGameScreen(App parent) {
        super(parent);
    }

    @Override
    public void initialize(long windowHandle) {
        Treadmill.importModuleTemplates("/Users/ibaker/modules_test_1.xml");
        m_world = new World();
    }

    @Override
    public void update(float seconds) {
        m_world.update(seconds);
    }

    @Override
    public void render() {
        m_world.render();
    }

}
