package application;

public class UncountableGame extends Application {
    public static void main(String[] args) {
        new UncountableGame() {
            @Override
            public void initialize() {
                pushScreen(new UncountableGameScreen(this));
            }
        };
    }
}
