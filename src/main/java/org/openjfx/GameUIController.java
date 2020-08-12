package org.openjfx;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GameUIController {
    private Game game = new Game(1, 4, 6, 0);

    private Button[] pitsSeedsCounter;
    //private final String green = "-fx-background-color: #000000, linear-gradient(#8affa9, #2f8f82), linear-gradient(#41c487, #2b8258);";
    private final String green = "-fx-background-color: green;";

    AtomicReference<String> stylePitStart = new AtomicReference<>("");
    AtomicReference<String> stylePitEnd = new AtomicReference<>("");
    AtomicInteger indexStorage = new AtomicInteger();

    @FXML
    public TextField notificationBoard;
    @FXML
    public Button scoreComputer;
    @FXML
    public Button scoreHuman;

    @FXML
    public Button mancalaComputer;
    @FXML
    public Button mancalaHuman;
    @FXML
    public Button pit1Human;
    @FXML
    public Button pit2Human;
    @FXML
    public Button pit3Human;
    @FXML
    public Button pit4Human;
    @FXML
    public Button pit5Human;
    @FXML
    public Button pit6Human;
    @FXML
    public Button pit1Computer;
    @FXML
    public Button pit2Computer;
    @FXML
    public Button pit3Computer;
    @FXML
    public Button pit4Computer;
    @FXML
    public Button pit5Computer;
    @FXML
    public Button pit6Computer;


    private void displayLastPit(int index) {
        stylePitStart.set(pitsSeedsCounter[index].getStyle());
        pitsSeedsCounter[index].setStyle(green);

        if(this.game.kalah_board.getPitStones(index) > 0){
            indexStorage.set(this.game.kalah_board.indexOfLastStoneForUIDisplay(index));
            //System.out.println(indexStorage);
            stylePitEnd.set(pitsSeedsCounter[indexStorage.get()].getStyle());
            pitsSeedsCounter[indexStorage.get()].setStyle(green);
        }
    }

    private void resetDisplayLastPit(int index) {
        pitsSeedsCounter[index].setStyle(stylePitStart.get());
        if(this.game.kalah_board.getPitStones(index) > 0){
            pitsSeedsCounter[indexStorage.get()].setStyle(stylePitEnd.get());
        }
    }

    private synchronized void updateSeedsDisplaying(byte[] toUpdateButtons){
        this.game.kalah_board.printBoard();
        for (int i = 0; i < toUpdateButtons.length; i++) {
            if (toUpdateButtons[i] > -1){
                pitsSeedsCounter[i].setText(String.valueOf(toUpdateButtons[i]));
            }
        }
    }

    private void toogleDisabledButtons(boolean isDisabled){
        for (int i = 0; i < pitsSeedsCounter.length/2-1; i++) {
             pitsSeedsCounter[i].setDisable(isDisabled);
             pitsSeedsCounter[i].disableProperty().setValue(isDisabled);
        }
    }

    private void checkGameOver(){
        if (this.game.getBoard().isGameOver()){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            toogleDisabledButtons(true);
            notificationBoard.setText("GAME OVER!");

            this.scoreComputer.setText( String.valueOf(this.game.kalah_board.getComputerBoardStore()));
            this.scoreHuman.setText( String.valueOf(this.game.kalah_board.getHumanBoardStore()));
        }
    }

    private synchronized  void updateSeedsDisplaying(int index) {
        assert this.game.getBoard().isHumansTurn();
        updateSeedsDisplaying(this.game.playHuman(index));
        checkGameOver();

        if (this.game.getBoard().isComputersTurn()){
            toogleDisabledButtons(true);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notificationBoard.clear();
            notificationBoard.setText("is computers turn!");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (this.game.getBoard().isComputersTurn()){
                updateSeedsDisplaying(this.game.playComputer());
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                checkGameOver();
            }
        }

        notificationBoard.clear();
        notificationBoard.setText("is your turn!");
        toogleDisabledButtons(false);
    }


    public void onClickPit1Human(MouseEvent mouseEvent) {
        updateSeedsDisplaying(0);
    }
    public void onEnterPit1Human(MouseEvent mouseEvent) {
        displayLastPit(0);
    }

    public void onExitPit1Human(MouseEvent mouseEvent) {
        resetDisplayLastPit(0);
    }


    public void onClickPit2Human(MouseEvent mouseEvent) {
        updateSeedsDisplaying(1);
    }

    public void onEnterPit2Human(MouseEvent mouseEvent) {
        displayLastPit(1);
    }

    public void onExitPit2Human(MouseEvent mouseEvent) {
        resetDisplayLastPit(1);
    }



    public void onClickPit3Human(MouseEvent mouseEvent) {
        updateSeedsDisplaying(2);
    }

    public void onEnterPit3Human(MouseEvent mouseEvent) {
        displayLastPit(2);
    }

    public void onExitPit3Human(MouseEvent mouseEvent) {
        resetDisplayLastPit(2);
    }




    public void onClickPit4Human(MouseEvent mouseEvent) { updateSeedsDisplaying(3); }

    public void onEnterPit4Human(MouseEvent mouseEvent) {
        displayLastPit(3);
    }

    public void onExitPit4Human(MouseEvent mouseEvent) {
        resetDisplayLastPit(3);
    }



    public void onClickPit5Human(MouseEvent mouseEvent) { updateSeedsDisplaying(4); }

    public void onEnterPit5Human(MouseEvent mouseEvent) {
        displayLastPit(4);
    }

    public void onExitPit5Human(MouseEvent mouseEvent) { resetDisplayLastPit(4); }


    public void onClickPit6Human(MouseEvent mouseEvent) { updateSeedsDisplaying(5); }

    public void onEnterPit6Human(MouseEvent mouseEvent) {
        displayLastPit(5);
    }

    public void onExitPit6Human(MouseEvent mouseEvent) { resetDisplayLastPit(5); }




    public void onEnterPit1Computer(MouseEvent mouseEvent) {
        displayLastPit(7);
    }

    public void onExitPit1Computer(MouseEvent mouseEvent) {
        resetDisplayLastPit(7);
    }



    public void onEnterPit2Computer(MouseEvent mouseEvent) {
        displayLastPit(8);
    }

    public void onExitPit2Computer(MouseEvent mouseEvent) {
        resetDisplayLastPit(8);
    }



    public void onEnterPit3Computer(MouseEvent mouseEvent) {
        displayLastPit(9);
    }

    public void onExitPit3Computer(MouseEvent mouseEvent) {
        resetDisplayLastPit(9);
    }



    public void onEnterPit4Computer(MouseEvent mouseEvent) {
        displayLastPit(10);
    }

    public void onExitPit4Computer(MouseEvent mouseEvent) {
        resetDisplayLastPit(10);
    }



    public void onEnterPit5Computer(MouseEvent mouseEvent) {
        displayLastPit(11);
    }

    public void onExitPit5Computer(MouseEvent mouseEvent) {
        resetDisplayLastPit(11);
    }




    public void onEnterPit6Computer(MouseEvent mouseEvent) {
        displayLastPit(12);
    }

    public void onExitPit6Computer(MouseEvent mouseEvent) {
        resetDisplayLastPit(12);
    }




    @FXML
    public void initialize() {
        pitsSeedsCounter = new Button[14];
        pitsSeedsCounter[0] = pit1Human;
        pitsSeedsCounter[1] = pit2Human;
        pitsSeedsCounter[2] = pit3Human;
        pitsSeedsCounter[3] = pit4Human;
        pitsSeedsCounter[4] = pit5Human;
        pitsSeedsCounter[5] = pit6Human;
        pitsSeedsCounter[6] = mancalaHuman;
        pitsSeedsCounter[7] = pit1Computer;
        pitsSeedsCounter[8] = pit2Computer;
        pitsSeedsCounter[9] = pit3Computer;
        pitsSeedsCounter[10] = pit4Computer;
        pitsSeedsCounter[11] = pit5Computer;
        pitsSeedsCounter[12] = pit6Computer;
        pitsSeedsCounter[13] = mancalaComputer;


        for (int i = 0; i < pitsSeedsCounter.length; i++) {
            pitsSeedsCounter[i].setText(String.valueOf(this.game.kalah_board.getPitStones(i)));
        }
        notificationBoard.setText("is your turn!");
        scoreComputer.setText("0");
        scoreHuman.setText("0");

    }




}
