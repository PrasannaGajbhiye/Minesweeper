package com.example.prasanna.minesweeper;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;


/**
 * Created by Prasanna on 3/1/16.
 */
public class GameActivity extends AppCompatActivity{
    private final int rows=12;
    private final int cols=8;
    private final int mines=30;
    private int notMines=0;

    private final int blockDimension=121;
    private final int blockPadding=0;

    private int[][] grid=new int[rows][cols];
    private int[][] displayGrid=new int[rows][cols];
    private int[][] revealedGrid=new int[rows][cols];

    private Tile blocks[][];
    private Set<MineCoordinate> set;

    private TableLayout mineField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        createGrid();
        set=getMinePositions(mines);
        setMines(set);
        floodFill();

        createMineField();
        showMineField();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            finishAffinity();
            return true;
        }else if(id == R.id.action_restart){
            Intent getGameActivityIntent=new Intent(this,GameActivity.class);
            startActivity(getGameActivityIntent);
            finishAffinity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void createGrid(){
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                grid[i][j]=0;
                displayGrid[i][j]=0;
                revealedGrid[i][j]=0;
            }
        }
    }

    private Set<MineCoordinate> getMinePositions(int mines){
        Set<MineCoordinate> set=new HashSet<>();
        while(set.size()<mines){
            Random rand=new Random();
            int x=rand.nextInt(rows);
            int y=rand.nextInt(cols);
            MineCoordinate mine=new MineCoordinate(x,y);
            boolean alreadyPresent=false;
            for(Iterator<MineCoordinate> it=set.iterator();it.hasNext();){
                MineCoordinate alreadyAMine=it.next();
                if(mine.xCoord==alreadyAMine.xCoord && mine.yCoord==alreadyAMine.yCoord){
                    alreadyPresent=true;
                    break;
                }
            }
            if(!alreadyPresent)
                set.add(mine);

        }
        return set;
    }

    private void setMines(Set<MineCoordinate> set){
        for(Iterator<MineCoordinate> it=set.iterator();it.hasNext();){
            MineCoordinate mine=it.next();
            grid[mine.xCoord][mine.yCoord]=-1;
        }
    }

    private void floodFill(){
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                if( grid[i][j]==-1){
                    addOneToAdjacentCell(i-1,j);
                    addOneToAdjacentCell(i+1,j);
                    addOneToAdjacentCell(i,j-1);
                    addOneToAdjacentCell(i,j+1);
                    addOneToAdjacentCell(i-1,j-1);
                    addOneToAdjacentCell(i-1,j+1);
                    addOneToAdjacentCell(i+1,j-1);
                    addOneToAdjacentCell(i+1,j+1);
                }
            }
        }

    }

    private void addOneToAdjacentCell(int i, int j) {
        if (i<0||i>=rows)
            return;
        if(j<0||j>=cols)
            return;
        if(grid[i][j]==-1)
            return;
        grid[i][j]=grid[i][j]+1;
    }

    private void createMineField() {
        mineField=(TableLayout)findViewById(R.id.MineField);
        blocks=new Tile[rows+2][cols+2];
        for (int row = 1; row < rows + 1; row++)
        {
            for (int column = 1; column < cols + 1; column++) {
                blocks[row][column]=new Tile(this);
                blocks[row][column].setDefaults(row, column);
                blocks[row][column].setOnClickListener(tileClicked);
            }
        }
    }

    private void showMineField() {
        for (int row = 1; row < rows + 1; row++)
        {
            TableRow tableRow = new TableRow(this);
            Display disp = this.getWindowManager().getDefaultDisplay();
            Point SizeOfScreen = new Point();
            disp.getSize(SizeOfScreen);

            int cellWidth = 0;
            int cellHeight = 0;
            cellWidth = Math.min(SizeOfScreen.x,SizeOfScreen.y)/cols;
            cellHeight =  Math.max(SizeOfScreen.x,SizeOfScreen.y)/(rows+1);
//            getResources().getDisplayMetrics().widthPixels

            tableRow.setLayoutParams(new TableRow.LayoutParams((cellWidth * cols), cellHeight));

            for (int column = 1; column < cols + 1; column++)
            {
                blocks[row][column].setLayoutParams(
                        new TableRow.LayoutParams(cellWidth,cellHeight));
                tableRow.addView(blocks[row][column]);
            }
            mineField.addView(tableRow);
        }
    }

    public View.OnClickListener tileClicked=new View.OnClickListener(){
        @Override
        public void onClick(View v){
            onTileClicked((Tile)v);
        }

        public void onTileClicked(Tile tile){

            int row=tile.posX;
            int column=tile.posY;
            blocks[row][column].setClickable(false);
            blocks[row][column].setEnabled(false);
            revealedGrid[row-1][column-1]=1;

            if (grid[row - 1][column-1]==-1){
                blocks[row][column].setBackgroundResource(R.drawable.minetwo);
                gameOver();

            }else{

                notMines+=1;
                if(grid[row-1][column-1]!=0){
                    blocks[row][column].setText(Integer.toString(grid[row-1][column-1]));
                    switch (grid[row-1][column-1]) {
                        case 1:
                            blocks[row][column].setTextColor(Color.BLUE);
                            blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                            break;
                        case 2:
                            blocks[row][column].setTextColor(Color.rgb(0, 100, 0));
                            blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                            break;
                        case 3:
                            blocks[row][column].setTextColor(Color.RED);
                            blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                            break;
                        case 4:
                            blocks[row][column].setTextColor(Color.rgb(85, 26, 139));
                            blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                            break;
                        case 5:
                            blocks[row][column].setTextColor(Color.rgb(139, 28, 98));
                            blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                            break;
                        case 6:
                            blocks[row][column].setTextColor(Color.rgb(238, 173, 14));
                            blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                            break;
                        case 7:
                            blocks[row][column].setTextColor(Color.rgb(47, 79, 79));
                            blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                            break;
                        case 8:
                            blocks[row][column].setTextColor(Color.rgb(71, 71, 71));
                            blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                            break;
                        case 9:
                            blocks[row][column].setTextColor(Color.rgb(205, 205, 0));
                            blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                            break;
                    }
                }
                else{

                    blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                    int i=row-1;
                    int j=column-1;
                    revealAdjacentCell(i-1,j);
                    revealAdjacentCell(i+1,j);
                    revealAdjacentCell(i, j-1);
                    revealAdjacentCell(i,j+1);
                    revealAdjacentCell(i-1,j-1);
                    revealAdjacentCell(i-1,j+1);
                    revealAdjacentCell(i+1,j-1);
                    revealAdjacentCell(i+1,j+1);
                }
                if(notMines==((rows*cols)-mines)){
                    for(Iterator<MineCoordinate> it=set.iterator();it.hasNext();){
                        MineCoordinate mine=it.next();
                        if(revealedGrid[mine.xCoord][mine.yCoord]!=1){
                            revealedGrid[mine.xCoord][mine.yCoord]=1;
                            blocks[mine.xCoord+1][mine.yCoord+1].setBackgroundResource(R.drawable.minethree);
                        }
                    }
                    youWin();

                    disableAllTiles();
                }

            }

        }
    };
    private void youWin(){
        new AlertDialog.Builder(this)
                .setTitle("Congrats")
                .setMessage("You Won!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
    private void gameOver() {
        for(Iterator<MineCoordinate> it=set.iterator();it.hasNext();){
            MineCoordinate mine=it.next();
            if(revealedGrid[mine.xCoord][mine.yCoord]!=1){
                revealedGrid[mine.xCoord][mine.yCoord]=1;
                blocks[mine.xCoord+1][mine.yCoord+1].setBackgroundResource(R.drawable.minethree);
            }
        }
        disableAllTiles();

        new AlertDialog.Builder(this)
                .setTitle("Restart")
                .setMessage("Do you want to restart the game?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(((Dialog) dialog).getContext(), GameActivity.class));
                        finishAffinity();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.cancel();
                    }
                })
                .show();

    }

    private void disableAllTiles() {
        for (int row = 1; row < rows + 1; row++)
        {
            for (int column = 1; column < cols + 1; column++) {
                blocks[row][column].setClickable(false);
                blocks[row][column].setEnabled(false);
            }
        }
    }

    private void revealAdjacentCell(int i,int j){
        if(i<0||i>=rows)
            return;
        if(j<0||j>=cols)
            return;
        if(revealedGrid[i][j]==1)
            return;

        revealedGrid[i][j]=1;
        notMines+=1;

        int row = i + 1;
        int column=j+1;
        blocks[row][column].setClickable(false);
        blocks[row][column].setEnabled(false);

        if(grid[i][j]!=0){

            blocks[row][column].setText(Integer.toString(grid[i][j]));
            switch (grid[i][j])
            {
                case 1:
                    blocks[row][column].setTextColor(Color.BLUE);
                    blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                    break;
                case 2:
                    blocks[row][column].setTextColor(Color.rgb(0, 100, 0));
                    blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                    break;
                case 3:
                    blocks[row][column].setTextColor(Color.RED);
                    blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                    break;
                case 4:
                    blocks[row][column].setTextColor(Color.rgb(85, 26, 139));
                    blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                    break;
                case 5:
                    blocks[row][column].setTextColor(Color.rgb(139, 28, 98));
                    blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                    break;
                case 6:
                    blocks[row][column].setTextColor(Color.rgb(238, 173, 14));
                    blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                    break;
                case 7:
                    blocks[row][column].setTextColor(Color.rgb(47, 79, 79));
                    blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                    break;
                case 8:
                    blocks[row][column].setTextColor(Color.rgb(71, 71, 71));
                    blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                    break;
                case 9:
                    blocks[row][column].setTextColor(Color.rgb(205, 205, 0));
                    blocks[row][column].setBackgroundResource(R.drawable.zerotile);
                    break;
            }

        }else{
            blocks[i+1][j+1].setBackgroundResource(R.drawable.zerotile);
            revealAdjacentCell(i-1,j);
            revealAdjacentCell(i+1,j);
            revealAdjacentCell(i, j-1);
            revealAdjacentCell(i,j+1);
            revealAdjacentCell(i-1,j-1);
            revealAdjacentCell(i-1,j+1);
            revealAdjacentCell(i+1,j-1);
            revealAdjacentCell(i+1,j+1);
        }

    }


}
