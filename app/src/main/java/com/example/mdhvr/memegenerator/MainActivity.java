package com.example.mdhvr.memegenerator;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 100;
    private Context context;
    public static Bitmap mutableBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context= getApplicationContext();

        Button choose_image_button= findViewById(R.id.choose_image_button);
        Button preview_button= findViewById(R.id.preview_buttton);
        Button edit_button= findViewById(R.id.edit_button);
        Button add_text_button= findViewById(R.id.add_text_button);

        final EditText top_text= findViewById(R.id.top_text);
        final EditText bottom_text= findViewById(R.id.bottom_text);
        final ImageView imageView= findViewById(R.id.preview_image);
        choose_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage(view);
            }
        });
        add_text_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkPermissionForReadExtertalStorage()){
                    try {
                        requestPermissionForReadExtertalStorage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Bitmap bitmap1 = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                Bitmap bitmap2 = bitmap1.copy(Bitmap.Config.ARGB_8888, true);
                if(bitmap2!=null){
                Log.e("value",bitmap2.isMutable()+" ");
                Canvas canvas = new Canvas(bitmap2);
                    Paint paint= new Paint();
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.myFontSize));
                    paint.setFakeBoldText(true);
                    Typeface typeface= Typeface.createFromAsset(getAssets(),"impact.ttf");
                    paint.setTypeface(typeface);

                    paint.setTextAlign(Paint.Align.CENTER);
                    TextPaint t= new TextPaint(paint);
                    t.setShadowLayer(3,2,2, Color.BLACK);
                    StaticLayout mTopTextLayout = new StaticLayout(top_text.getText().toString(),t,
                            canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);


                    int textX = canvas.getWidth()/2;
                    int textY = 50;

                    canvas.translate(textX, textY);
                    mTopTextLayout.draw(canvas);
                    StaticLayout mBottomTextLayout = new StaticLayout(bottom_text.getText().toString(),t,
                            canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL,1.0f,0.0f,false);

                    int y= canvas.getHeight()-200;
                    canvas.translate(1,y);
                    mBottomTextLayout.draw(canvas);
                imageView.setImageBitmap(bitmap2);
                mutableBitmap=bitmap2;
                }
                else
                    Log.e("error","null");
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), ImageActivity.class);
                intent.putExtra("purpose","EditImage");
                startActivity(intent);
            }
        });

        preview_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), ImageActivity.class);
                intent.putExtra("purpose","PreviewImage");
                startActivity(intent);
            }
        });

    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "MainActivity";


    public void pickImage (View v){
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            String[] projection = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();

            Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor));

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex); // returns null
            cursor.close();

            try {
               Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView imageView = findViewById(R.id.preview_image);
                mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
