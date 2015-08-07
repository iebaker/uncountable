package game;


import core.App;

public class UncountableGame extends App {
    public static void main(String[] args) {
        UncountableGame uncountable = new UncountableGame();
        uncountable.start();
    }

    @Override
    public void start() {
        pushScreen(new UncountableGameScreen(this));
        super.start();
    }
}
