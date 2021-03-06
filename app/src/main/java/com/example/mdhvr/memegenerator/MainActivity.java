package com.example.mdhvr.memegenerator;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<String>>{

    private static final String LOG_TAG = "MainActivity";
    private Context context;
    public static Bitmap mutableBitmap;
    private ImageView imageView;
    private Spinner top_spinner, bottom_spinner;
    private int top_text_size, bottom_text_size;
    private int top_text_color= Color.WHITE, bottom_text_color= Color.WHITE;
    private ListView listView;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private ImageAdapter imageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        context= getApplicationContext();
        hideKeyBoard();
        Button choose_image_button= findViewById(R.id.choose_image_button);
        Button preview_button= findViewById(R.id.preview_buttton);
        Button edit_button= findViewById(R.id.edit_button);
        Button add_text_button= findViewById(R.id.add_text_button);
        Button top_color_picker = findViewById(R.id.top_text_color_picker);
        Button bottom_color_picker= findViewById(R.id.bottom_text_color_picker);

        listView= findViewById(R.id.list_view);
        linearLayout=findViewById(R.id.linear_layout_main);
        progressBar= findViewById(R.id.progress_bar);
        imageAdapter= new ImageAdapter(context,new ArrayList<String>());
        listView.setAdapter(imageAdapter);

        final EditText top_text= findViewById(R.id.top_text);
        final EditText bottom_text= findViewById(R.id.bottom_text);
        imageView= findViewById(R.id.preview_image);

        top_spinner=findViewById(R.id.top_text_spinner);
        bottom_spinner= findViewById(R.id.bottom_text_spinner);

        setupSpinners();
        //when there is no image chosen
        Bitmap bitmap1 = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap bitmap2 = bitmap1.copy(Bitmap.Config.ARGB_8888, true);
        mutableBitmap=bitmap2;

        choose_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage(view);
            }
        });

        add_text_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyBoard();

                Bitmap bitmap1 = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                Bitmap bitmap2 = bitmap1.copy(Bitmap.Config.ARGB_8888, true);
                if(bitmap2!=null){

                Canvas canvas = new Canvas(bitmap2);
                //for top text
                    Paint paint= new Paint();
                    paint.setColor(top_text_color);

                    paint.setTextSize(top_text_size);
                    Typeface typeface= Typeface.createFromAsset(getAssets(),"impact.ttf");
                    paint.setTypeface(typeface);

                    paint.setTextAlign(Paint.Align.CENTER);
                    TextPaint t= new TextPaint(paint);
                    t.setShadowLayer(5,2,2, Color.BLACK);
                    StaticLayout mTopTextLayout = new StaticLayout(top_text.getText().toString(),t,
                            canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);


                    int textX = canvas.getWidth()/2;
                    int textY = 50;

                    canvas.translate(textX, textY);
                    mTopTextLayout.draw(canvas);

                 //for bottom text
                    Paint bottomPaint= new Paint();
                    bottomPaint.setColor(bottom_text_color);
                    bottomPaint.setTextSize(bottom_text_size);
                    bottomPaint.setTypeface(typeface);
                    bottomPaint.setTextAlign(Paint.Align.CENTER);
                    TextPaint t2= new TextPaint(bottomPaint);
                    t2.setShadowLayer(5,2,2,Color.BLACK);
                    StaticLayout mBottomTextLayout = new StaticLayout(bottom_text.getText().toString(),t2,
                            canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL,1.0f,0.0f,false);
                    int y= canvas.getHeight()-200;
                    canvas.translate(1,y);
                    mBottomTextLayout.draw(canvas);

                //now set the image view and store the bitmap for further use.
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
                Intent intent= new Intent(context, ImageActivity.class);
                intent.putExtra("purpose","EditImage");
                startActivity(intent);
            }
        });

        preview_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context, ImageActivity.class);
                intent.putExtra("purpose","PreviewImage");
                startActivity(intent);
            }
        });

        top_color_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ColorPicker c= new ColorPicker(MainActivity.this,89,77,255);
                c.show();
                c.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(int color) {
                        top_text_color=color;
                        c.dismiss();
                    }
                });
            }
        });

        bottom_color_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ColorPicker c= new ColorPicker(MainActivity.this,89,77,255);
                c.show();
                c.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(int color) {
                        bottom_text_color=color;

                        c.dismiss();
                    }
                });
            }
        });

    }


    private void setupSpinners() {
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(context,
                R.array.textSizeArray,android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        top_spinner.setAdapter(spinnerAdapter);
        bottom_spinner.setAdapter(spinnerAdapter);
        top_spinner.setSelection(6);
        top_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                top_text_size = Integer.parseInt(adapterView.getItemAtPosition(i).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                top_text_size= 70;
            }
        });
        bottom_spinner.setSelection(6);
        bottom_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                bottom_text_size = Integer.parseInt( adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                bottom_text_size= 70;
            }
        });
    }

    private SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu,menu);

        final MenuItem searchItem= menu.findItem(R.id.menu_search);

        searchView = (SearchView) searchItem.getActionView();

        //searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.v(LOG_TAG,"Inside text submit");
                startSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if(!queryTextFocused) {
                    // hide the search box if the user focuses on something else
                    searchView.setIconified(true);
                    //searchItem.collapseActionView();
                }
            }
        });

        return true;

    }
    private String base_url= "https://api.qwant.com/api/search/images?offset=0&q=";
    private String query;

    private void startSearch(String query) {
        this.query=query;
        Log.v(LOG_TAG,"Inside start search");
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);

        //if(jsonResponse==null){
            //TODO add empty view

        ConnectivityManager ccmgr =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ccmgr.getActiveNetworkInfo();

        if(networkInfo!=null&&networkInfo.isConnected()){
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(1, null, MainActivity.this);
            Log.v(LOG_TAG,"loader initiated");
        }else{

            Log.v(LOG_TAG,"No internet connection");
        }


    }

    @Override
    public Loader<List<String>> onCreateLoader(int i, Bundle bundle) {
        Log.v(LOG_TAG,"Inside onCreateLoader");

        return new AsyncTaskLoader<List<String>>(context) {
            @Override
            public List<String> loadInBackground() {
                Log.v(LOG_TAG,"Inside load in background");
                String url= base_url+ encodeURIComponent(query);
                URL u=null;
                String jsonResponse=null;
                List<String> image_list;
                try {
                    u = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    jsonResponse = makeconnect(u);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                image_list= extractImageURLs(jsonResponse);
                return image_list;
            }
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> stringList) {
        Log.v(LOG_TAG,"Inside onLoadFinished");
        progressBar.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        imageAdapter.addAll(stringList);
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {
        imageAdapter.clear();
    }

    private String makeconnect(URL url) throws IOException {

        Log.v(LOG_TAG,"inside makeconnect");
        String jsonResponse = "";
        if(url==null){
            return jsonResponse;
        }
        InputStream inputstream = null;
        HttpURLConnection httpurl = null;
        try {
            Log.v(LOG_TAG,url.toString());
            httpurl = (HttpURLConnection) url.openConnection();
            httpurl.setRequestMethod("GET");
            httpurl.setConnectTimeout(3000);
            httpurl.setReadTimeout(3000);
            httpurl.connect();
            if (httpurl.getResponseCode() == 200) {
                inputstream = httpurl.getInputStream();
                jsonResponse = readFromInputStream(inputstream);
            }
            else {
                Log.e(LOG_TAG, "Error response code: " + httpurl.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "unable to connect",e);
        } finally {
            if (httpurl != null)
                httpurl.disconnect();
            if (inputstream != null)

                inputstream.close();

        }
        return jsonResponse;
    }

    private String readFromInputStream(InputStream inputstream) throws IOException {
        Log.v(LOG_TAG,"inside readfrominputstream");
        StringBuilder output = new StringBuilder();
        if (inputstream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputstream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line=reader.readLine();
            }
        }
        return output.toString();
    }

    private List<String> extractImageURLs(String Jsonresponse) {
        // If the JSON string is empty or null, then return early.
        Log.v(LOG_TAG,"inside extractImageURLs method");

        if (TextUtils.isEmpty(Jsonresponse)) {
            Log.v(LOG_TAG,"jsonresponse was empty");
            return null;
        }

        List<String> listOfURLs = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(Jsonresponse);
            JSONObject data= jsonObject.getJSONObject("data");
            JSONObject results= data.getJSONObject("result");
            JSONArray items= results.getJSONArray("items");
            for(int i=0;i<items.length();i++){

                String image_url= items.getJSONObject(i).getString("media");
                listOfURLs.add(image_url);
            }

        }
        catch (JSONException e) {

            Log.v(LOG_TAG, "Problem parsing the JSON results", e);
        }

        return listOfURLs;
    }

    private String encodeURIComponent(String s) {
        String result;
        try
        {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e)
        {
            result = s;
            Log.v(LOG_TAG,e.toString());
        }

        return result;
    }

    private void hideKeyBoard(){
        View view= MainActivity.this.getCurrentFocus();
        if(view!=null) {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mutableBitmap!=null)
            imageView.setImageBitmap(mutableBitmap);
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
