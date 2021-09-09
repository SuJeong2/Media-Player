package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

import java.io.BufferedWriter;
import java.io.PrintWriter;

public class MediaBar extends VBox {

	HBox mark_pane = new HBox();
	HBox player_pane = new HBox();
	Slider time = new Slider();

	Button playButton = new Button("||");
	Button TagButton = new Button("TAG");

	MediaPlayer player;

	int i = 0;

	int tagIdx = 0;

	int totalnum = Player.i;

	public MediaBar(MediaPlayer play) {
		player = play;
		setAlignment(Pos.CENTER);

		HBox.setHgrow(time, Priority.ALWAYS);

		TagButton.setPrefWidth(100);
		playButton.setPrefWidth(50);
		time.setPrefWidth(getMaxWidth());

		NumberAxis xAxis_mark = new NumberAxis(0, 92531, 10000);
		NumberAxis yAxis_mark = new NumberAxis(0, 0, 1);
		final ScatterChart<Number, Number> sc = new ScatterChart<Number, Number>(xAxis_mark, yAxis_mark);

		xAxis_mark.setTickLabelsVisible(false);
		yAxis_mark.setTickLabelsVisible(false);

		XYChart.Series series2 = new XYChart.Series();

		sc.getData().add(series2);

		sc.setPrefWidth(1700);
		sc.setPrefHeight(5);
		mark_pane.getChildren().addAll(sc);

		mark_pane.setPadding(new Insets(0, 0, 0, 47)); // 아래 linechart와 위치 맞추기 위해 여백 설정
		mark_pane.setAlignment(Pos.CENTER_LEFT);

		// Add the playButton and player bar
		player_pane.getChildren().add(TagButton);
		player_pane.getChildren().add(playButton);
		player_pane.getChildren().add(time);

		player_pane.setSpacing(10);
		player_pane.setPadding(new Insets(10));

		getChildren().add(player_pane);
		getChildren().add(mark_pane);

		playButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				Status status = player.getStatus();

				if (status == Status.PLAYING) {
					if (player.getCurrentTime().greaterThanOrEqualTo(player.getTotalDuration())) {
						player.seek(player.getStartTime());
						player.play();
					}

					else {
						player.pause();
						playButton.setText(">");
					}
				}

				if (status == Status.PAUSED || status == Status.HALTED || status == Status.STOPPED) {
					player.play();
					playButton.setText("||");
				}
			}
		});

		TagButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				// TODO Auto-generated method stub

				// Tag 버튼이 눌린 시간 : player.getCurrentTime().toSeconds()
				System.out.println((int) player.getCurrentTime().toSeconds() / 60 + " : "
						+ (int) player.getCurrentTime().toSeconds() % 60);

				int value = (int) ((int) Player.i
						* (player.getCurrentTime().toSeconds() / player.getTotalDuration().toSeconds()));
				System.out.println(Player.xValue[value] + " " + Player.yValue[value] + " " + Player.zValue[value]);
				series2.getData().add(new XYChart.Data(value, 0.5));

				File file = new File("out.txt");

				try {
					if (i == 0) {
						file.delete();
					}

					FileWriter fw = new FileWriter(file, true);

					fw.write((int) player.getCurrentTime().toSeconds() / 60 + " : "
							+ (int) player.getCurrentTime().toSeconds() % 60 + " " + value+"\r\n");
					
			

					i++;

					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		});

		player.currentTimeProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				updatesValues();
			}
		});

		time.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable ov) {
				// TODO Auto-generated method stub

				if (time.isPressed()) {
					player.seek(player.getMedia().getDuration().multiply(time.getValue() / 100));
				}
			}

		});
	}

	protected void updatesValues() {
		Platform.runLater(new Runnable() {
			public void run() {
				time.setValue(player.getCurrentTime().toMillis() / player.getTotalDuration().toMillis() * 100);

			}
		});
	}
}