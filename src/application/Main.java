package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application {
	Player player;

	@Override
	public void start(final Stage primaryStage) throws Exception {
		String string1 = new String();
		string1 = "file:///C:/test.mp4";
		
		player = new Player(string1);

		Scene scene = new Scene(player, 1500, 1000);

		VBox box = new VBox();
		scene.setFill(null);

		primaryStage.setTitle("Media Player");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}