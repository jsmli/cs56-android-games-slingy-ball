package jsmli.slingyball;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// The main Activity for the Android app.
// Contains methods related to score-saving and starting the game.
public class MainActivity extends AppCompatActivity {

    public static Button buttonView;
    public static TextView textView;
    private static MainActivity mainAcivityInstance;


    public static int highScore;

    public static MainActivity getMainAcivityInstance(){

        return mainAcivityInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonView = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        textView.setText("Shots Remaining: ");

        SharedPreferences prefs = this.getSharedPreferences("highScore", Context.MODE_PRIVATE);
        MainActivity.highScore = prefs.getInt("highScoreKey", 0);


        mainAcivityInstance = this;

    }

    public void clickStartButton(View v){

        if(!(buttonView.getAlpha() < 0.001 && buttonView.getAlpha() > -0.001)){

            buttonView.setAlpha(0);

            GameView.getInstance().startGame();

        }

    }

    public void setHighScore(int newScore){

        highScore = newScore;

        SharedPreferences prefs = this.getSharedPreferences("highScore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("highScoreKey", newScore);
        editor.apply();

    }

    public void updateTextView(String s) {
        textView.setText(s);
    }

    // unimplemented
    public void clickPauseButton(View v){

    }
}
