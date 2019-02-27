package com.example.arafat.genderfromname;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText name;
    FloatingActionButton search;
    TextView result;
    public static String BASE_URL = "https://api.genderize.io/?name=";
    ProgressDialog progressDialog;
    String textName, gender, probability, count;
    MyAsyncTask myAsyncTask;
    SpannableString spannableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (EditText) findViewById(R.id.name);
        search = (FloatingActionButton) findViewById(R.id.fab);
        result = (TextView) findViewById(R.id.result);


        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (name.getText().toString() == "") {
                    Toast.makeText(MainActivity.this, "Enter some text", Toast.LENGTH_SHORT).show();
                } else {
                    String n = name.getText().toString();
                    myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute(BASE_URL + n);

                }
            }
        });

    }

    public class MyAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Fetching Data");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder().url(params[0]).build();
            Log.e("TAG", "doInBackground: " + params[0]);
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(MainActivity.this, "Network Call Failure" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        textName = jsonObject.getString("name");

                        gender = jsonObject.getString("gender");
                        probability = jsonObject.getString("probability");
                        count = jsonObject.getString("count");


                    } catch (JSONException e) {

                        e.printStackTrace();

                    }

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            spannableString = new SpannableString("Name " + textName + "\n" + "Gender " + gender + "\n" + "Probability " + probability + "\n"
                    + "Count " + count);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), 0);
            result.setText(spannableString);
            result.setText(spannableString);
            progressDialog.hide();

        }
    }
}

