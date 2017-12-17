package com.example.mdhvr.memegenerator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/*
   Created by Ishita sharma on 15/12/17
 */

public class PaintActivity extends AppCompatActivity {
    private  ImageView left_to_right_imageView;
    private Bitmap paintBitmap;
    private ImageView paintImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        getSupportActionBar().hide();
        final ImageView pen = findViewById(R.id.paint_pen);
        paintImageView=  findViewById(R.id.paint_image);
        ImageView done= findViewById(R.id.paint_done);
        ImageView cancel = findViewById(R.id.paint_cancel);
        final ImageView undo= findViewById(R.id.paint_undo);
        left_to_right_imageView = findViewById(R.id.paint_nav_right);
        final ImageView pallete = findViewById(R.id.paint_pallete);

        //set image bitmap
        paintImageView.setImageBitmap(ImageActivity.imageBitmap);
        paintBitmap=ImageActivity.imageBitmap;

        left_to_right_imageView.setImageResource(R.mipmap.navigation_right);

        left_to_right_imageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                if(left_to_right_imageView.getDrawable().getConstantState()==
                        getResources().getDrawable(R.mipmap.navigation_left).getConstantState()){
                    Animation leftToRight= AnimationUtils.loadAnimation(PaintActivity.this,R.anim.left_to_right);
                    left_to_right_imageView.startAnimation(leftToRight);
                    left_to_right_imageView.setImageResource(R.mipmap.navigation_right);
                    Animation slide= AnimationUtils.loadAnimation(PaintActivity.this,R.anim.slide);
                    pallete.startAnimation(slide);
                    pallete.setVisibility(View.GONE);
                    //Toast.makeText(PaintActivity.this,"180 to 0",Toast.LENGTH_SHORT).show();
                    //Hide the utility icons
                    pen.setVisibility(View.GONE);
                    undo.setVisibility(View.GONE);
                }
                else if(left_to_right_imageView.getDrawable().getConstantState()
                        ==getResources().getDrawable(R.mipmap.navigation_right).getConstantState()){
                    Animation RightTOLeft= AnimationUtils.loadAnimation(PaintActivity.this,R.anim.right_to_left);
                    left_to_right_imageView.startAnimation(RightTOLeft);
                    left_to_right_imageView.setImageResource(R.mipmap.navigation_left);
                    //Toast.makeText(PaintActivity.this,"0 to 180",Toast.LENGTH_SHORT).show();

                    //Show the utility icons
                    pen.setVisibility(View.VISIBLE);
                    pallete.setVisibility(View.VISIBLE);
                    undo.setVisibility(View.VISIBLE);
                }
            }
        });

    //set onclick for done

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alteredBitmap!=null)
                ImageActivity.imageBitmap= paintBitmap;
                finish();
            }
        });

     //set onclick for cancel
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
      //Todo add sense for hiding done and cancel while user is drawing.

        //set onclick for pen
        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation leftToRight= AnimationUtils.loadAnimation(PaintActivity.this,R.anim.left_to_right);
                left_to_right_imageView.startAnimation(leftToRight);
                left_to_right_imageView.setImageResource(R.mipmap.navigation_right);
                Animation slide= AnimationUtils.loadAnimation(PaintActivity.this,R.anim.slide);
                pallete.startAnimation(slide);
                pallete.setVisibility(View.GONE);
                //Toast.makeText(PaintActivity.this,"180 to 0",Toast.LENGTH_SHORT).show();
                //Hide the utility icons
                pen.setVisibility(View.GONE);
                undo.setVisibility(View.GONE);
                //start drawing
                drawOnBitmap();
            }
        });

    }

    private  Canvas canvas;
    private Matrix matrix;
    private Paint paint;
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;
    Bitmap alteredBitmap;

    private void drawOnBitmap() {

        alteredBitmap= Bitmap.createBitmap(paintBitmap.getWidth(),paintBitmap.getHeight(),paintBitmap.getConfig());
       canvas =new Canvas(alteredBitmap);
        paint= new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        matrix= new Matrix();
        canvas.drawBitmap(paintBitmap,matrix,paint);
        paintImageView.setImageBitmap(alteredBitmap);
        paintBitmap=alteredBitmap;
        paintImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        downx = event.getX();
                        downy = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        upx = event.getX();
                        upy = event.getY();
                        canvas.drawLine(downx, downy, upx, upy, paint);
                        paintImageView.invalidate();
                        downx = upx;
                        downy = upy;
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        upx = event.getX();
                        upy = event.getY();
                        canvas.drawLine(downx, downy, upx, upy, paint);
                        paintImageView.invalidate();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }
                return true;
            }

        });

    }

}
