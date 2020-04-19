package memoryCardGame;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.awt.SplashScreen;

public class Main extends Application {

	@Override
	public void start(Stage stage) {
		GameBoard root = new GameBoard();

		Scene scene = new Scene(root, 400, 500);
		scene.getStylesheets().add("/css/style.css");

		stage.setTitle("Memory Card Game");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/card4.png")));
		stage.setOnShown(e -> {
			SplashScreen splash = SplashScreen.getSplashScreen();
			if (splash != null) {
				splash.close();
			}
		});
		stage.show();
	}

    public static void main(String[] args) {
		launch(args);
    }
}
