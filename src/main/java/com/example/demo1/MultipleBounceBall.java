/*
Spencer Renfro
CMSC 315
week1 Discussion

Coding exercise 20.5 Description:

Extend the example to detect collisions. Once two balls collide, remove the later ball that was added to the pane and add its radius
to the other ball.

Use the Suspend button to suspend the animation and the Resume button to resume the animation.

Add a mouse-pressed handler that removes a ball when the mouse is pressed on the ball.


 */


package com.example.demo1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.time.LocalTime; // import the LocalDate class for the ball identifier
import java.util.ArrayList;
import java.util.List;

public class MultipleBounceBall extends Application {
    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
        MultipleBallPane ballPane = new MultipleBallPane();
        ballPane.setStyle("-fx-border-color: yellow");

        Button btAdd = new Button("+");
        Button btSubtract = new Button("-");
        Button btSuspend = new Button("Suspend");
        Button btResume = new Button("Resume");
        Button btRestart = new Button("Restart");

//        Button btPrintBallList = new Button("Print Ball List");
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll( btSuspend, btResume, btAdd, btSubtract, btRestart);
        hBox.setAlignment(Pos.CENTER);

        // Add or remove a ball // and suspend/resume animation
        btAdd.setOnAction(e -> ballPane.add());
        btSubtract.setOnAction(e -> ballPane.subtract());
        btSuspend.setOnAction(e -> ballPane.pause());
        btResume.setOnAction(e -> ballPane.play());
//        btPrintBallList.setOnAction(e -> ballPane.printBallList());
        btRestart.setOnAction(e -> ballPane.restart());

        // Pause and resume animation
//        ballPane.setOnMousePressed(e -> ballPane.pause());
//        ballPane.setOnMouseReleased(e -> ballPane.play());

        // Use a scroll bar to control animation speed
        ScrollBar sbSpeed = new ScrollBar();
        sbSpeed.setMax(20);
        sbSpeed.setValue(10);
        ballPane.rateProperty().bind(sbSpeed.valueProperty());

        BorderPane pane = new BorderPane();
        pane.setCenter(ballPane);
        pane.setTop(sbSpeed);
        pane.setBottom(hBox);

        // Create a scene and place the pane in the stage
        Scene scene = new Scene(pane, 500, 500);
        primaryStage.setTitle("MultipleBounceBall"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
    }

    private class MultipleBallPane extends Pane {
        private Timeline animation;

        public MultipleBallPane() {
            // Create an animation for moving the ball
            animation = new Timeline(
                    new KeyFrame(Duration.millis(50), e -> moveBall()));
            animation.setCycleCount(Timeline.INDEFINITE);
            animation.play(); // Start animation
        }

        public void add() {
            // adds a random size radius from 10 to 20 inclusive
            // int radius = (int) (Math.floor(Math.random() * 11)  + 10);
            // System.out.println("radius: " + radius);
            Color color = new Color(Math.random(),
                    Math.random(), Math.random(), 0.5);
            Ball ball = new Ball(30, 30, 20, color, LocalTime.now());

            // remove the ball if clicked
            ball.setOnMousePressed(e -> getChildren().remove(ball));

            getChildren().add(ball); // Add ball to the pane


        }

        // Removes the last node
        public void subtract() {
            if (getChildren().size() > 0) {
                getChildren().removeLast(); }// or getChildren().remove(getChildren().size() - 1);
       }

        // Removes the first node instead of the last
        /*
        public void subtract() {
            if (!getChildren().isEmpty()) { // or getChildren().size() > 0
                getChildren().removeFirst(); // or getChildren().remove(0);
            }
        }

         */
        public void removeBall(Ball ball){
            getChildren().remove(ball);
        }

        public void play() {
            animation.play();
        }

        public void pause() {
            animation.pause();
        }

        public void restart() {
            if(this.getChildren().size() > 0) {
                this.getChildren().removeAll(this.getChildren());
            }
        }

        public void increaseSpeed() {
            animation.setRate(animation.getRate() + 0.1);
        }

        public void decreaseSpeed() {
            animation.setRate(
                    animation.getRate() > 0 ? animation.getRate() - 0.1 : 0);
        }

        public DoubleProperty rateProperty() {
            return animation.rateProperty();
        }

        protected void moveBall() {
            for (Node node: this.getChildren()) {
                Ball ball = (Ball)node;
                // Check boundaries
                if (ball.getCenterX() < ball.getRadius() ||
                        ball.getCenterX() > getWidth() - ball.getRadius()) {
                    ball.dx *= -1; // Change ball move direction
                }
                if (ball.getCenterY() < ball.getRadius() ||
                        ball.getCenterY() > getHeight() - ball.getRadius()) {
                    ball.dy *= -1; // Change ball move direction
                }
                // Adjust ball position
                ball.setCenterX(ball.dx + ball.getCenterX());
                ball.setCenterY(ball.dy + ball.getCenterY());
                // check for collision


            }
            if(this.getChildren().size() > 1){
                checkCollision();
            }
        }

        protected void checkCollision() {
            List<Node> ballsToRemove = new ArrayList<>();

            for (int i = 0; i < getChildren().size(); i++) {

                Ball ball1 = (Ball) getChildren().get(i);

                for (int j = i + 1; j < getChildren().size(); j++) {
                    Ball ball2 = (Ball) getChildren().get(j);
                    if (isCollision(ball1, ball2)) {
                        System.out.println("Collision detected between " + ball1 + " and " + ball2);
                        //Remove the new ball if collision is true, and add its radius to the other ball
                        if (ball1.getCreationTime().isAfter(ball2.getCreationTime())) {
                            ballsToRemove.add(ball1);
                            ball2.setRadius(ball2.getRadius() + ball1.getRadius());
                        } else {
                            ballsToRemove.add(ball2);
                            ball1.setRadius(ball1.getRadius() + ball2.getRadius());
                        }
                    }
                }
            }
            getChildren().removeAll(ballsToRemove);
        }

        public Boolean isCollision(Ball b1, Ball b2) {
        double[] ball1 = new double[]{b1.getCenterX(), b1.getCenterY(), b1.getRadius()};
        double[] ball2 = new double[]{b2.getCenterX(), b2.getCenterY(), b2.getRadius()};
        double distance = Math.sqrt(Math.pow(ball1[0] - ball2[0], 2) + Math.pow(ball1[1] - ball2[1], 2));
            return distance <= ball1[2] + ball2[2];
        };

        public void printBallList(){
            for (Node node: this.getChildren()) {
                Ball ball = (Ball)node;
                System.out.println("Ball{" +
                        "x=" + ball.getCenterX() +
                        "y=" + ball.getCenterY() +
                        "radius=" + ball.getRadius() +
                        "color=" + ball.getFill() +
                        "creationTime=" + ball.creationTime +
                        "}");
            }
        }
    }

    class Ball extends Circle {
        private double dx = 1, dy = 1;
        private LocalTime creationTime;

        Ball(double x, double y, double radius, Color color, LocalTime now) {
            super(x, y, radius);
            setFill(color); // Set ball color
            this.creationTime = now;
        }

        public LocalTime getCreationTime(){
            return creationTime;
        }

    }

    /**
     * The main method is only needed for IDEs with limited
     * JavaFX support. It is not needed for running from the command line.
     */
    public static void main(String[] args) {
        launch(args);
    }
}