package com.example.prasanna.minesweeper;

/**
 * Created by Prasanna on 3/1/16.
 */
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class Tile extends Button
{
    public int posX;
    public int posY;

    public Tile(Context context)
    {
        super(context);
    }

    public Tile(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public Tile(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    // set default properties for the tile
    public void setDefaults(int i, int j)
    {
        posX=i;
        posY=j;
        setBackgroundResource(R.drawable.closedtile);
    }
}
