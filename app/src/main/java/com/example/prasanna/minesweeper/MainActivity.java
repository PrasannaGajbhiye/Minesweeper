package com.example.prasanna.minesweeper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public boolean onHomeScreenClick(View view) {
        Intent getMyMenuScreenIntent=new Intent(this,MyMenuActivity.class);
        startActivity(getMyMenuScreenIntent);
        finish();
        return true;
    }
}
