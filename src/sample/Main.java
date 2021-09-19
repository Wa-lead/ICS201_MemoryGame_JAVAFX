package sample;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.util.ArrayList;
import java.util.Collections;

public class Main extends Application {
    //-------------------------------- Class setOnAction variables
    private Images selected = null; // setOnAction for class
    private int clickCount = 2; // setOnAction for class


    //-------------------------------- right-side trackers
    private int score = 0; // update each time the user scores
    private int attempts=0 ; // updates each time the user flips an image
    private Text scor = new Text(String.valueOf("Score: "+score));
    private Text attmpt = new Text(String.valueOf("Moves: "+attempts));
    private Text high = new Text("No Best Score");
    private ArrayList best = new ArrayList();
    private Button reset = new Button("Reset"); // resets the game without losing the highest score
    private Text time; // holds the value of the time
    private Timeline timeline;
    private int hours = 0, minutes = 0, seconds = 0;
    private boolean sos = true;

    //-------------------------------- win - lose sound effect
    Media lost = new Media("http://freesoundeffect.net/sites/default/files/casual-game-lose-sound-effect-45947266.mp3");
    MediaPlayer lose = new MediaPlayer(lost);
    Media won = new Media("http://freesoundeffect.net/sites/default/files/3x-bright-bonus-s-sound-effect-45593262.mp3");
    MediaPlayer win = new MediaPlayer(won);


    //-------------------------------- Create a complete separate pane for the images
    private Parent imagepane() {
        GridPane pane = new GridPane(); // to hold them in a matrix
        pane.setGridLinesVisible(true);

        ArrayList<Images> swr = new ArrayList<>();

        int p = 1;
        for (int i = 0; i < 8; i++) { // the array holds each image twice
            String x = "file:" + p + ".png";
            swr.add(new Images(new Image(x)));
            swr.add(new Images(new Image(x)));
            p++;
        }

        reset.setOnAction(e->{  // resets everything to 0;
                    for (int i = 0; i < swr.size(); i++) {
                        swr.get(i).close();
                    }
                    score=0;
                    scor.setText(String.valueOf("Score: "+score));
                    attempts=0;
                    attmpt.setText(String.valueOf("Moves: "+attempts));
                    lose.stop();
                    win.stop();
                    seconds=0;
                    minutes=0;
                    hours=0;
                    timeline.stop();
                    time.setText((((hours/10) == 0) ? "0" : "") + hours + ":"
                            + (((minutes/10) == 0) ? "0" : "") + minutes + ":"
                            + (((seconds/10) == 0) ? "0" : "") + seconds);

                    for (int i = 0; i < 16; i++) { //remove images to re-shuffle
                        pane.getChildren().remove(swr.get(i));

                    }


                    Collections.shuffle(swr); // re-shuffle

                    for (int i = 0; i < 4; i++) { // simple formula to hold images in a 4x4 grid
                        for (int j = 0, k=4*i; j < 4; j++) {
                            pane.add(swr.get(k),i,j);
                            k++;

                        }

                    }

                }

        );

        Collections.shuffle(swr); // to provide random order of image

        for (int i = 0; i < 4; i++) { // simple formula to hold images in a 4x4 grid
            for (int j = 0, k=4*i; j < 4; j++) {
                pane.add(swr.get(k),i,j);
                k++;

            }

        }

        return pane;
    }


    @Override
    public void start(Stage primaryStage) throws Exception{

        time = new Text("00:00:00"); // default value
        timeline = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                change(time);
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);

        //-------------------------------- win - lose sound effect
        VBox box = new VBox();
        box.setSpacing(1);

        scor.setFill(Color.BLACK);
        scor.setStrokeWidth(6);
        scor.setFont(new Font(15));


        high.setFill(Color.BLACK);
        high.setStrokeWidth(6);
        high.setFont(new Font(15));

        attmpt.setFill(Color.BLACK);
        attmpt.setStrokeWidth(6);
        attmpt.setFont(new Font(15));

        box.getChildren().addAll(scor,high,attmpt);
        time.setFill(Color.RED);
        time.setFont(new Font(30));

        //--------------------------------

        VBox timeBox = new VBox(time); // pane for time
        HBox resetBox = new HBox(reset); // pane for button
        resetBox.setAlignment(Pos.CENTER); // to set it at center

        BorderPane timeANDtrack = new BorderPane();

        timeANDtrack.setLeft(box);
        timeANDtrack.setRight(time);

        VBox master = new VBox(); // the pane that holds both images and trackers
        master.getChildren().addAll(imagepane(),timeANDtrack,resetBox);
        master.setPadding(new Insets(10,10,10,10));

        primaryStage.setTitle("ICS201 - PROJECT");
        primaryStage.setScene(new Scene(master));
        primaryStage.show();
    }

    public class Images extends StackPane { // create A class that puts each image in a separate StackPane

        ImageView img = new ImageView();

        public Images(Image image) {
            img.setImage(image);
            img.setFitWidth(80);
            img.setFitHeight(80);

            setAlignment(Pos.CENTER);
            getChildren().addAll(img);

            setOnMouseClicked(this::handleMouseClick); // triggers each time the mouse clicks on the image (StackPane)
            close(); // set the default value of the image to closed
        }

        void open(Runnable action) {
            FadeTransition ft = new FadeTransition(Duration.seconds(0.5), img);
            img.setVisible(true);
            ft.setOnFinished(e -> action.run());
            ft.play();
        }

        void close() {
            FadeTransition ft = new FadeTransition(Duration.seconds(0.5), img);
            img.setVisible(false);
            ft.play();         }

        public boolean isOpen() { // method created to avoid action if th image is already flipped
            return img.isVisible();
        }

        public boolean checkSame(Images other) {

            String x = img.getImage().getUrl(); // comparing the path of both images to detect if they match
            String y = other.img.getImage().getUrl();

            if(x.equals(y)) { // if same
                score++; // increase score
                win.play(); // play win sound effect

                if (score == 8) { // if the score is 8 is the maximus
                    timeline.stop(); // stop time
                    best.add(attempts);
                    Collections.sort(best);
                    high.setText("Best: "+best.get(0) +"Moves");
                }

            }

            else { // if not same
                lose.play(); // play lose sound effect
            }

            scor.setText(String.valueOf("Score: "+score)); // updates score each time the user flips two images
            return x.equals(y);

        }

        void handleMouseClick(MouseEvent event) {
            if (isOpen() || clickCount == 0)
                return; // don't do anything if you click an already flipped image

            clickCount--;

            if (selected == null) { // if no image is flipped yet
                selected = this;
                open(() -> {}); // flip the image

                lose.stop(); // stop audio when going to the next attempt
                win.stop(); // stop audio when going to the next attempt
                attempts++; // attempts increase each time the user flips an image
                timeline.play(); // once an image is flipped the timing starts
                attmpt.setText(String.valueOf("Moves: "+attempts)); // updates the attempts each time the image flips
            }
            else { // if an image is flipped prior to this
                open(() -> { // flip the second image
                    if (!checkSame(selected)) { // check if both images are the same (via path)
                        selected.close(); // close both images
                        this.close();
                    }

                    selected = null; // reset values
                    clickCount = 2;
                });
            }
        }

    }
    void change(Text text) {
        if( seconds== 60) {
            minutes++;
            seconds= 0;
        }
        if(minutes == 60) {
            hours++;
            minutes= 0;
        }
        text.setText((((hours/10) == 0) ? "0" : "") + hours + ":"
                + (((minutes/10) == 0) ? "0" : "") + minutes + ":"
                + (((seconds++/10) == 0) ? "0" : "") + seconds);
    }



    public static void main(String[] args) {
        launch(args);
    }



}