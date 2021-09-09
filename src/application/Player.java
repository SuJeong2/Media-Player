package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Line;

public class Player extends BorderPane {

	Media media;
	MediaPlayer player;
	MediaView view;
	HBox mpane;
	HBox graph_pane;
	MediaBar bar;

	XYChart.Series<Number, Number> data = new XYChart.Series<>();
	XYChart.Series<Number, Number> data1 = new XYChart.Series<>();
	XYChart.Series<Number, Number> data2 = new XYChart.Series<>();

	static int i = 0;

	static float[] xValue = new float[100000];
	static float[] yValue = new float[100000];
	static float[] zValue = new float[100000];

	public Player(String file) throws Exception {

		media = new Media(file);
		player = new MediaPlayer(media);
		view = new MediaView(player);
		mpane = new HBox();

		mpane.getChildren().add(view); // 비디오를 Pane에 붙임
		mpane.setAlignment(Pos.CENTER);
		mpane.setSpacing(10);
		mpane.setPadding(new Insets(10));

		setTop(mpane); // 비디오를 가장 위에 배치

		bar = new MediaBar(player); // player에 맞는 재생바를 생성
		setCenter(bar);

		// Load the graph
		HBox graph_pane = new HBox();

		// int i = 0;
		String value;

		float[] timestamp = new float[100000];

		try {
			File file1 = new File("test.txt");
			FileReader filereader = new FileReader(file1);
			BufferedReader bufReader = new BufferedReader(filereader);
			String line = "";

			i = 0;
			int index;

			while ((line = bufReader.readLine()) != null) {

				// x value
				index = line.indexOf(",");
				value = line.substring(0, index);
				line = line.substring(index + 1);
				xValue[i] = (float) Float.parseFloat(value);

				// y value
				index = line.indexOf(",");
				value = line.substring(0, index);
				line = line.substring(index + 1);
				yValue[i] = (float) Float.parseFloat(value);

				// z value
				zValue[i] = (float) Float.parseFloat(line);

				i++;
			}
			bufReader.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			System.out.println(e);
		}

		// NumberAxis xAxis = new NumberAxis(0,i,200);
		NumberAxis xAxis = new NumberAxis(0, i, 10000);
		xAxis.setLabel("Number of data");

		/// NumberAxis yAxis = new NumberAxis(-25, 150, 1);
		NumberAxis yAxis = new NumberAxis(-25, 25, 1);
		yAxis.setLabel("Value");

		LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		lineChart.setTitle("Line Chart");

		lineChart.setCreateSymbols(false);

		data.setName("X");
		data1.setName("Y");
		data2.setName("Z");

		int n = i / 1000 + 1;
		System.out.println(n);
		MultiThread[] runnable = new MultiThread[n];
		Thread[] thread = new Thread[n];
		for (int m = 0; m < n; m++) {
			runnable[m] = new MultiThread(m * 1000);
			thread[m] = new Thread(runnable[m]);
			thread[m].start();
		}

		lineChart.getData().add(data);
		lineChart.getData().add(data1);
		lineChart.getData().add(data2);

		lineChart.setCreateSymbols(false);

		lineChart.setPrefWidth(1500);

		graph_pane.setPrefWidth(1500);
		graph_pane.setPrefHeight(400);
		HBox.setHgrow(lineChart, Priority.ALWAYS);

		graph_pane.getChildren().add(lineChart);
		setBottom(graph_pane);

		setStyle("-fx-background-color: #bfc2c7");

		player.play();

		Line line = new Line();

		line.setStrokeWidth(3);

		// 75~1485
		// 0을 75에 매칭하고 100을 1485에 매칭하고.
		// 1410
		player.currentTimeProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				line.setStartX(75 + player.getCurrentTime().toMillis() / player.getTotalDuration().toMillis() * 1410);
				line.setStartY(450);
				line.setEndX(75 + player.getCurrentTime().toMillis() / player.getTotalDuration().toMillis() * 1410);
				line.setEndY(900);
			}
		});

		getChildren().add(line);

	}

	class MultiThread implements Runnable {
		int start = 0;

		public MultiThread() {
			start = 0;
		}

		public MultiThread(int startPoint) {
			start = startPoint;
		}

		public void run() {
			try {
				Platform.runLater(() -> {

					for (int j = start; j < start + 1000; j += 50) {

						data.getData().add(new XYChart.Data<Number, Number>(j, xValue[j]));
						data1.getData().add(new XYChart.Data<Number, Number>(j, yValue[j]));
						data2.getData().add(new XYChart.Data<Number, Number>(j, zValue[j]));
					}

				});

			} catch (Exception e) {
				System.out.println(e);
			}

		}
	}
}