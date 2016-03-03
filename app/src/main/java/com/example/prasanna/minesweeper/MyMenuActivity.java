package com.example.prasanna.minesweeper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.View;

/**
 * Created by Prasanna on 3/1/16.
 */
public class MyMenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_menu);
    }

    public void onExitButtonClick(View view) {
        finish();
    }

    public void onStartGameButtonClick(View view) {
        Intent getGameActivityIntent=new Intent(this,GameActivity.class);
        startActivity(getGameActivityIntent);
    }
}
