/* autogenerated by Processing revision 1283 on 2022-05-17 */
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class nyan_cat_adventure extends PApplet {

PImage playerImg;
PImage[] tree=new PImage[4];
int landX, landY;
float tranX=0, tranY=0;
Player player;
int playerState;
final int PLAYER_IDLE=0, PLAYER_UP=1, PLAYER_DOWN=2, PLAYER_RIGHT=3, PLAYER_LEFT=4;

boolean debugMode=false;
Map[] maps=new Map[40];
final int ROAD=1, GRASS=0;
final int TREE=1;


 public void setup() {
  /* size commented out by preprocessor */;
  noStroke();
  playerImg = loadImage("img/player.png");
  //loading Tree Image
  for(int i=0;i<4;i++){
    tree[i] = loadImage("img/tree" + i + ".png") ;
  }
  
  for (int i=0; i<maps.length; i++) {
    if (i<16) {
      maps[i]=new Grass(20-i);
    } else {
      switch(floor(random(2))){
          case 0:maps[i]=new Grass(20-i);break;
          case 1:maps[i]=new Road(20-i);break;
      }
    }
  }
  
  player = new Player();
}


 public void draw() {
  pushMatrix();
  //Adjust Rolling Speed
  for (int i=550; i>=0; i=i-25) {
    if (tranY+player.y<=i) {
      tranX-=0.125f;
      tranY+=0.25f;
    }
  }
  tranX-=0.125f;
  tranY+=0.25f;
  
  //Rolling the screen
  translate(tranX, tranY);


  //draw map

  for (int j=39; j>=0; j--) {
    maps[j].display();
  }
  for (int j=39; j>0; j--) {
    roadMarkingLine(j);
  }

  //drawPlayer
  player.update();
  
  //draw tree
  for (int j=39; j>=0; j--) {
    maps[j].displayObjects();
  }


  popMatrix();
}

 public void keyPressed() {
  // Add your moving input code here
  if (key ==CODED) {
    switch(keyCode) {
    case UP:
      if (playerState==PLAYER_IDLE && maps[13].checkObjects(player.offsetX)!=TREE) {
        playerState=PLAYER_UP;
        player.movingTimer=0;
        player.offsetY--;
        //offset map forward and create new map
        for (int i=0; i<39; i++) {
          maps[i]=maps[i+1];
        }
        switch(floor(random(3))){
          case 0:maps[39]=new Grass(player.offsetY-27);break;
          case 1:maps[39]=new Road(player.offsetY-27);break;
          case 2:maps[39]=new Road(player.offsetY-27);break;
        }
        
      }
      break;
    case RIGHT:
      if (playerState==PLAYER_IDLE && player.offsetX<8 && maps[12].checkObjects(player.offsetX+1)!=TREE) {
        playerState=PLAYER_RIGHT;
        player.movingTimer=0;
        player.offsetX++;
      }
      break;
    case LEFT:
      if (playerState==PLAYER_IDLE && player.offsetX>0 && maps[12].checkObjects(player.offsetX-1)!=TREE) {
        playerState=PLAYER_LEFT;
        player.movingTimer=0;
        player.offsetX--;
      }
      break;
    case DOWN:
      if (playerState==PLAYER_IDLE && maps[11].checkObjects(player.offsetX)!=TREE) {
        playerState=PLAYER_DOWN;
        player.movingTimer=0;
        player.offsetY++;
        
        //offset map backward and create new map
        for (int i=39; i>0; i--) {
          maps[i]=maps[i-1];
        }
      }
      break;
    }
  }else{
    if(key=='b'){
      // Press B to toggle demo mode
      debugMode = !debugMode;
    }
  }
}
//code for draw road marking line
 public void roadMarkingLine(int y) {
  if (maps[y].type==ROAD && maps[y-1].type==ROAD) {
    for (int i=0; i<19; i++) {
      landX=i*80+maps[y].y*(-30);
      landY=i*20+maps[y].y*60;
      
      fill(100);
      //draw line
      if (i%2==0) {
        quad(landX+1, landY-2, landX+81, landY+18, landX+79, landY+22, landX-1, landY+2);
      }
    }
  }
}

//code for draw image form the position point
 public void drawImage(PImage img,float x,float y){
  if(img.width%110==0){
    int n=PApplet.parseInt(img.width/110);
    image(img,x,y-(img.height-20*n));
  }
}
class Grass extends Map {
  Tree[] trees;
  Grass(int y) {
    super(y);
    type=GRASS;
    //generate tree
    trees=new Tree[floor(random(5))];
    int newTreeX;
    for (int i=0; i<trees.length; i++) {
      if (floor(random(2))==0) {
        newTreeX=floor(random(3));
        while (checkTreeXreuse(newTreeX, i)) {
          newTreeX=6+floor(random(3));
        }
      } else {
        newTreeX=6+floor(random(3));
        while (checkTreeXreuse(newTreeX, i)) {
          newTreeX=floor(random(3));
        }
      }
      trees[i]=new Tree(newTreeX, y);
    }
  }
   public void display() {
    for (int i=0; i<19; i++) {
      if (i<6 || i>14) {
        fill(0xFF0CB43C);
      } else {
        fill(0xFF0ED145);
      }
      landX=i*80+y*(-30);
      landY=i*20+y*60;
      text(y, landX, landY);
      quad(landX, landY-5, landX+80, landY+20-5, landX+110, landY-40-5, landX+30, landY-60-5);
      if (i<6 || i>14) {
        fill(0xFF0A9030);
      } else {
        fill(0xFF0CB43C);
      }
      quad(landX, landY, landX+80, landY+20, landX+80, landY+20-5, landX, landY-5);
      if (debugMode) {
        fill(255);
        textSize(30);
        text(y, landX, landY);
      }
    }
  }

   public void displayObjects() {
    for (int i=0; i<trees.length; i++) {
      trees[i].display();
    }
  }

   public int checkObjects(int x) {
    //check tree
    for (int i=0; i<trees.length; i++) {
      if (trees[i].x==x) {
        return TREE;
      }
    }
    return -1;
  }

   public boolean checkTreeXreuse(int newTreeX, int n) {
    for (int i=0; i<n; i++) {
      if (trees[i].x==newTreeX) {
        return true;
      }
    }
    return false;
  }
}
class Map{
  int y;
  int type;
   public void display(){}
   public void displayObjects(){}
   public int checkObjects(int x){
    return -1;
  }
  Map(int y){
    this.y=y;
  }
  
}
class Player {
  float x, y;
  int offsetX, offsetY;
  int movingTimer;

  Player() {
    x=560;
    y=680;
    offsetX=4;
    offsetY=8;
    movingTimer=0;
  }

   public void update() {

    switch(playerState) {
    case PLAYER_IDLE:
      if(maps[12].type==GRASS){
        drawImage(playerImg, x, y-5);
      }else{
        drawImage(playerImg, x, y);
      }
      break;
    case PLAYER_UP:
      movingTimer+=1;
      x+=3;
      y-=6;
      drawImage(playerImg, x, y-10);
      break;
    case PLAYER_DOWN:
      movingTimer+=1;
      x-=3;
      y+=6;
      drawImage(playerImg, x, y-10);
      break;
    case PLAYER_LEFT:
      movingTimer+=1;
      x-=8;
      y-=2;
      drawImage(playerImg, x, y-10);
      break;
    case PLAYER_RIGHT:
      movingTimer+=1;
      x+=8;
      y+=2;
      drawImage(playerImg, x, y-10);
      break;
    }
    
    if (debugMode) {
        fill(0);
        textSize(30);
        text(offsetX+","+offsetY,x+55,y);
      }
      
    if (movingTimer==10) {
      playerState=PLAYER_IDLE;
      movingTimer=0;
    }
  }
}
class Road extends Map {

  Road(int y) {
    super(y);
    type=ROAD;
  }
   public void display() {
    for (int i=0; i<19; i++) {
      fill(0xFF404040);
      landX=i*80+y*(-30);
      landY=i*20+y*60;
      quad(landX, landY, landX+80, landY+20, landX+110, landY-40, landX+30, landY-60);

      if (debugMode) {
        fill(255);
        textSize(30);
        text(y, landX, landY);
      }
      //draw line
      //if (i%2==0) {
      //  quad(landX-14, landY+28, landX+66, landY+48, landX+64, landY+52, landX-14, landY+32);
      //}
    }
  }
}
class Tree{
  int x,y;
  int treeX,treeY;
  PImage treeImg;
  Tree(int x,int y){
    this.x=x;
    this.y=y;
    treeX=(x+6)*80+y*(-30);
    treeY=(x+6)*20+y*60;
    treeImg=tree[floor(random(4))];
  }
  
   public void display(){
    drawImage(treeImg,treeX,treeY);
  }
  
}


  public void settings() { size(1280, 720, P2D); }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "nyan_cat_adventure" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
