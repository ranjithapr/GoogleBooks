package com.udacity.ranjitha.googlebooks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.R.id.list;

public class MainActivity extends AppCompatActivity {

    private String userInput = null;
    private String searchUrl = "https://www.googleapis.com/books/v1/volumes?q=";
    ArrayList<BookInfo> bookList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null || !savedInstanceState.containsKey("key")) {
            //DO Nothing
        }
        else {
            bookList = savedInstanceState.getParcelableArrayList("key");
        }


        Button search = (Button) findViewById(R.id.search_button);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout listLayout = (LinearLayout) findViewById(R.id.search_results);
                listLayout.setVisibility(View.INVISIBLE);
                userInput = ((EditText) findViewById(R.id.book_search)).getText().toString().replace(" ", "");
                if (checkNetwork()) {
                    if (userInput.equals("")) {
                        Toast.makeText(MainActivity.this, "Please Enter Book Name!!", Toast.LENGTH_SHORT).show();
                    } else {
                        BookAsyncTask asyncTask = null;
                        try {
                            asyncTask = (BookAsyncTask) new BookAsyncTask();
                            asyncTask.execute(new URL(searchUrl + userInput), null, null);

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                } else
                    Toast.makeText(MainActivity.this, "Please Check Your Internet Connection!!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Checks if network is available
     * @return
     */
    private boolean checkNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", bookList);
        super.onSaveInstanceState(outState);
    }

    /**
     * Async Task for getting books response
     */
    public class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<BookInfo>>

    {
        private ProgressDialog dialog =
                new ProgressDialog(MainActivity.this);


        protected void onPreExecute() {
            dialog.setMessage("Please wait...");
            dialog.show();
        }

        protected ArrayList<BookInfo> doInBackground(URL... urls) {
            URL url;
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            ArrayList<BookInfo> bookDetails = new ArrayList<>();
            String jsonResponse = null;

            try {
                urlConnection = (HttpURLConnection) urls[0].openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                bookDetails = extractDataFromJason(jsonResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return bookDetails;
        }


        @Override
        protected void onPostExecute(ArrayList<BookInfo> bookDetails) {
            dialog.dismiss();
            updateList(bookDetails);
        }

        public void updateList(ArrayList<BookInfo> bookDetails) {
            LinearLayout listLayout = (LinearLayout) findViewById(R.id.search_results);
            listLayout.setVisibility(View.VISIBLE);
            bookList = bookDetails;
            if(bookList != null && bookList.size() > 0) {
                TextView norecords = (TextView) findViewById(R.id.result_textview);
                norecords.setVisibility(View.INVISIBLE);
                BookInfoAdapter bookInfoAdapter = new BookInfoAdapter(MainActivity.this, bookList);
                ListView listView = (ListView) findViewById(R.id.listViewID);
                listView.setAdapter(bookInfoAdapter);
            }
            else
            {
                TextView norecords = (TextView) findViewById(R.id.result_textview);
                norecords.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private ArrayList<BookInfo> extractDataFromJason(String jsonInput) throws JSONException {

            String bookTitle = "";
            String authorName = "";
            JSONObject rootObject = new JSONObject(jsonInput);
            ArrayList bookDetails = new ArrayList<BookInfo>();
            if (rootObject.has("items")) {
                JSONArray itemDetail = rootObject.getJSONArray("items");

                for (int i = 0; i < itemDetail.length(); i++) {
                    JSONObject info = itemDetail.getJSONObject(i);
                    JSONObject books = info.getJSONObject("volumeInfo");
                    bookTitle = books.optString("title");
                    if (books.has("authors")) {
                        JSONArray authorDetail = books.getJSONArray("authors");
                        for (int j = 0; j < authorDetail.length(); j++) {
                            authorName = authorName + authorDetail.optString(j) + "\n";
                        }
                    } else {
                        authorName = "Book has No Author";
                    }
                    bookDetails.add(new BookInfo(bookTitle, authorName));
                    authorName = "";
                }
                TextView listLayout = (TextView) findViewById(R.id.result_textview);
                listLayout.setVisibility(View.INVISIBLE);
            } else {

            }
            return bookDetails;
        }


    }


}
