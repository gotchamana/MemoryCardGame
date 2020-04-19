package memoryCardGame;

import javafx.application.Preloader;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class GamePreloader extends Preloader {

	private Stage stage;

	@Override
	public void start(Stage stage) {
		BorderPane root = new BorderPane();

		Scene scene = new Scene(root, 100, 100);

		this.stage = stage;
		stage.setScene(scene);
		stage.show();
	}

	@Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
			System.out.println("pre");
            stage.hide();
        }
    }
}
