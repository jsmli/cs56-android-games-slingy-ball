package jsmli.slingyball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class GameView extends View {

    private static GameView gameViewInstance = null;

    public static GameView getInstance(){

        return gameViewInstance;
    }

    PlayerBall player;
    ArrayList<Platform> platforms = new ArrayList<>();

    private Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static float gravity = 0.0f;
    private static float scrollVelocity = 0.0f;
    public static final float GAMEGRAVITY = 4f;
    private static boolean gameInProgress = false;

    GameThread thread;

    public void startGame(){
        
        createPlayer();

        platforms.add(new Platform(this.getWidth()+270, this.getHeight()-20, 260, (16.0f/1059f)*this.getHeight(), Color.WHITE));// invisible platform for index purposes
        platforms.add(new Platform(
                (float) Math.random()*this.getWidth()*0.75f,
                this.getHeight()/2,
                (float) Math.random()*this.getWidth()*0.1f + this.getWidth()*0.15f,
                (16.0f/1059f)*this.getHeight(),
                Color.WHITE)
        );

        player.setVx(0);
        player.setVy(0);



        GameView.gameInProgress = true;




    }

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        startThread();

        gameViewInstance = this;
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        gameViewInstance = this;
    }

//    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    public void startThread() {
        thread = new GameThread(this);
        thread.setRunning(true);
        thread.start();
    }

    public void createPlayer() {
        player = new PlayerBall(
                this.getWidth()/2,
                this.getHeight()-((35.0f/986.0f)*this.getWidth()),
                (35.0f/986.0f)*this.getWidth(),
                0.90f,Color.WHITE
        );
    }

    public void createPlatform() {
        platforms.add(new Platform(
                (float) Math.random()*this.getWidth()*0.75f,
                (float) Math.random()*(-100),
                (float) Math.random()*this.getWidth()*0.1f + this.getWidth()*0.15f,
                (16.0f/1059f)*this.getHeight(),
                Color.WHITE)
        );
    }

    protected void onLayout (boolean changed,
                   int left,
                   int top,
                   int right,
                   int bottom){


        //startGame();


    }

    public void update(int delta) {

        if(GameView.gameInProgress){

            player.setVy(player.getVy() + gravity );
            player.setX(player.getX() + delta / 100f * player.getVx());
            player.setY(player.getY() + delta / 100f * (player.getVy()+scrollVelocity));

            for(Platform plat: platforms){
                plat.setY(plat.getY() + delta / 100f * scrollVelocity);
            }

            if(platforms.get(0).getY() >= this.getHeight()-platforms.get(0).getHeight()){

                scrollVelocity = 0;
                gravity = GAMEGRAVITY;
                platforms.get(0).setY(this.getHeight()-platforms.get(0).getHeight());
            }

            if (player.getX() < player.getRadius()) {
                player.setVx(-player.getVx() * player.getElasticity());
                player.setX(player.getRadius());
            } else if (player.getX() > this.getWidth() - player.getRadius()) {
                player.setVx(-player.getVx() * player.getElasticity());
                player.setX(this.getWidth() - player.getRadius());
            }

//        if (player.getY() < player.getRadius()) {
//            player.setVy(-player.getVy() * player.getElasticity());
//            player.setY(player.getRadius()); }
//        if (player.getY() > this.getHeight() - player.getRadius()) {
//            player.setVy(-player.getVy() * player.getElasticity());
//            player.setY(this.getHeight() - player.getRadius());
//        }

            for(int i = 0; i < platforms.size(); i++){
                Platform plat = platforms.get(i);
                if(player.getX() < plat.getX() + plat.getLength() && player.getX() > plat.getX()){

                    if((player.getY()+player.getRadius() > plat.getY() && player.getY()-player.getRadius() < plat.getY()) ||
                            (player.getY()-player.getRadius() < plat.getY() + plat.getHeight() && player.getY()+player.getRadius() > plat.getY() + plat.getHeight())) {

                        player.setVy(-player.getVy() * player.getElasticity());

                        if(player.getY()+player.getRadius() > plat.getY() && player.getY()-player.getRadius() < plat.getY()) {


                            if(platforms.indexOf(plat) == 1){

                                player.setVy(0);
                                player.setVx(0);
                                scrollVelocity = 100;
                                gravity = 0;

                                createPlatform();
                                platforms.remove(0);

                            }else{
                                player.setY(plat.getY() - player.getRadius());
                            }

                        } else {
                            player.setY(plat.getY() + plat.getHeight() + player.getRadius());
                        }
                    }
                }

                if (player.getY() > plat.getY() && player.getY() < plat.getY()+plat.getHeight()) {

                    if ((player.getX()+player.getRadius() > plat.getX() && player.getX()-player.getRadius() < plat.getX()) ||
                            (player.getX()-player.getRadius() < plat.getX()+plat.getLength() && player.getX()+player.getRadius() > plat.getX()+plat.getHeight())) {

                        player.setVx(-player.getVx() * player.getElasticity());

                        if (player.getX()+player.getRadius() > plat.getX() && player.getX()-player.getRadius() < plat.getX()) {
                            player.setX(plat.getX()-player.getRadius());
                        } else {
                            player.setX(plat.getX()+plat.getLength()+player.getRadius());
                        }
                    }
                }
            }

        }


        postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(GameView.gameInProgress){

            if (player.getY() + player.getRadius() > this.getHeight()) {

                gameOver();


            }else{

                p.setColor(player.getColor());
                canvas.drawCircle(player.getX(), player.getY(), player.getRadius(), p);

                for(Platform x: platforms){
                    p.setColor(x.getColor());
                    canvas.drawRect(x.getX(), x.getY(),x.getX()+x.getLength(), x.getY()+x.getHeight(), p);
                }

                if(ControlBall.getInstance().isDrawing()){

                    p.setColor(Color.WHITE);
                    p.setStyle(Paint.Style.FILL_AND_STROKE);

                    p.setStrokeWidth(0.01f*this.getWidth());

                    Path path = new Path();
                    PointF start = new PointF(player.getX(), player.getY());
                    PointF end = new PointF(start.x- (ControlBall.getInstance().getEndX() - ControlBall.getInstance().getStartX()) , start.y - (ControlBall.getInstance().getEndY() - ControlBall.getInstance().getStartY()));

                    path.moveTo(start.x, start.y);
                    path.lineTo(end.x, end.y);

                    path.close();

                    canvas.drawPath(path, p);

                    float triangleHeight = 0.001f*this.getWidth();
                    float triangleWidth  = 0.0003f*this.getWidth();

                    float cx = end.x - ((end.x-start.x)/triangleHeight) * 2 * triangleWidth;
                    float cy = end.y - ((end.y-start.y  )/triangleHeight) * 2 * triangleWidth;

                    PointF arrowpoint1 = new PointF(((cx + end.x)/2)-((end.y-cy)/triangleHeight)*triangleWidth, ((cy + end.y)/2)+((end.x-cx)/triangleHeight)*triangleWidth);

                    PointF arrowpoint2 = new PointF(((cx + end.x)/2)+((end.y-cy)/triangleHeight)*triangleWidth, ((cy + end.y)/2)-((end.x-cx)/triangleHeight)*triangleWidth);

                    Path trianglePath = new Path();
                    trianglePath.moveTo(end.x,end.y);
                    trianglePath.lineTo(arrowpoint1.x, arrowpoint1.y);
                    trianglePath.lineTo(arrowpoint2.x, arrowpoint2.y);

                    trianglePath.close();

                    canvas.drawPath(trianglePath, p);

                    p.setStrokeWidth(0.0f);

                }

                if (player.getY() < 0) {

                    p.setColor(Color.WHITE);
                    p.setStyle(Paint.Style.FILL_AND_STROKE);

                    p.setStrokeWidth(0.01f*this.getWidth());

                    Path indicatorPath = new Path();

                    PointF start = new PointF(player.getX(), 50);
                    PointF end = new PointF( player.getX() , 10);

                    indicatorPath.moveTo(start.x, start.y);
                    indicatorPath.lineTo(end.x, end.y);

                    indicatorPath.close();

                    canvas.drawPath(indicatorPath, p);

                    float triangleHeight = 0.001f*this.getWidth();
                    float triangleWidth  = 0.0003f*this.getWidth();

                    float cx = end.x - ((end.x-start.x)/triangleHeight) * 2 * triangleWidth;
                    float cy = end.y - ((end.y-start.y  )/triangleHeight) * 2 * triangleWidth;

                    PointF arrowpoint1 = new PointF(((cx + end.x)/2)-((end.y-cy)/triangleHeight)*triangleWidth, ((cy + end.y)/2)+((end.x-cx)/triangleHeight)*triangleWidth);

                    PointF arrowpoint2 = new PointF(((cx + end.x)/2)+((end.y-cy)/triangleHeight)*triangleWidth, ((cy + end.y)/2)-((end.x-cx)/triangleHeight)*triangleWidth);

                    Path trianglePath = new Path();
                    trianglePath.moveTo(end.x,end.y);
                    trianglePath.lineTo(arrowpoint1.x, arrowpoint1.y);
                    trianglePath.lineTo(arrowpoint2.x, arrowpoint2.y);

                    trianglePath.close();

                    canvas.drawPath(trianglePath, p);

                    p.setStrokeWidth(0.0f);

                }



            }





        }


    }

    public static void setGravity(float newGravity){
        gravity = newGravity;
    }

    public static float getGravity(){
        return gravity;
    }

    public static float getScrollVelocity(){
        return scrollVelocity;
    }


    public void gameOver(){

        gameInProgress = false;

        //thread.suspend(); deprecated
        //thread = null;
        player = null;
        platforms.clear();
        gravity = 0.0f;

        (MainActivity.buttonView).setAlpha(1);

    }




}
