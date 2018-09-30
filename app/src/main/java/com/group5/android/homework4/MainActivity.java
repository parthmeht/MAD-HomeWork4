package com.group5.android.homework4;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements GetAPI.KeywordData{

    private HashMap<String,ArrayList<String>> keywordData;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> keywords;
    private String selectedKeyword;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        keywordData = new HashMap<>();
        new GetAPI(MainActivity.this).execute("http://dev.theappsdr.com/apis/photos/keywords.php");

        findViewById(R.id.buttonGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keywordData!=null && keywordData.size()>0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    keywords = new ArrayList<String>(keywordData.keySet());
                    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,keywords){
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            TextView text_view = (TextView) super.getView(position, convertView, parent);
                            text_view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                            return text_view;
                        }
                    };
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("Keyword", keywords.get(which));
                            selectedKeyword = keywords.get(which);
                            textView.setText(selectedKeyword);
                        }
                    });
                    builder.setTitle("Choose a Keyword");
                    AlertDialog dialog = builder.create();
                    ListView listView = dialog.getListView();
                    listView.setDivider(new ColorDrawable(Color.BLUE));
                    listView.setDividerHeight(5);
                    dialog.show();
                } else {
                    Toast.makeText(v.getContext(), "No Keyword Present in the database!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    Bitmap getImageBitmap(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void handleData(HashMap<String,ArrayList<String>> data) {
        keywordData = data;
    }
}
