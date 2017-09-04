package com.example.android.gbookapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

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

class FetchBooks extends AsyncTaskLoader<Books> {
    private static final int DEBUG_SLEEP = 0;
    private static final boolean DEBUG_EMPTY = false;
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private String query_string = "";

    FetchBooks(Context context) {
        super(context);
        query_string = ((MainActivity) context).query_string;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Books loadInBackground() {
        return createBooksFromJSON(fetchData());
    }

    private String fetchData() {
        if (DEBUG_SLEEP > 0) {
            try {
                Thread.sleep(DEBUG_SLEEP);
            } catch (InterruptedException e) {
                Log.e("BookLog", "Sleep Interrupted", e);
                return "";
            }
        }
        URL url;
        HttpURLConnection server = null;
        InputStream data_source = null;
        String JSONdata = "";
        if (query_string.length() == 0) {
            return JSONdata;
        }
        try {
            url = new URL(BASE_URL + URLEncoder.encode(query_string, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e("BookLog", "fetchData: Unable to encode query string", e);
            return JSONdata;
        } catch (MalformedURLException e) {
            Log.e("BookLog", "fetchData: Problem creating URL", e);
            return JSONdata;
        }
        try {
            server = (HttpURLConnection) url.openConnection();
            server.setRequestMethod("GET");
            server.connect();
            data_source = server.getInputStream();
            JSONdata = readFromStream(data_source);
        } catch (IOException e) {
            Log.e("BookLog", "fetchData: Problem fetching data", e);
        } finally {
            if (server != null) {
                server.disconnect();
            }
            if (data_source != null) {
                try {
                    data_source.close();
                } catch (Exception e) {
                    Log.e("BookLog", "fetchData: Problem closing data stream", e);
                }
            }
        }
        return JSONdata;
    }

    private String readFromStream(InputStream i) throws IOException {
        StringBuilder output = new StringBuilder();
        if (i != null) {
            InputStreamReader isr = new InputStreamReader(i, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            while (line != null) {
                output.append(line);
                line = br.readLine();
            }
        }
        return output.toString();
    }

    private Books createBooksFromJSON(String json) {
        Books books = new Books();
        if (DEBUG_EMPTY) {
            return books;
        }
        try {
            JSONObject root = new JSONObject(json);
            JSONArray items = root.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject volume_info = items.getJSONObject(i).getJSONObject(
                        "volumeInfo"
                );
                String title = volume_info.getString("title");
                String attribution = "by ";
                try {
                    JSONArray authors = volume_info.getJSONArray("authors");
                    for (int a = 0; a < authors.length(); a++) {
                        attribution += authors.getString(a) + ", ";
                    }
                    if (attribution.length() > 3) {
                        attribution = attribution.substring(0, attribution.length() - 2);
                    }
                } catch (JSONException e) {
                    // this book has no author listed
                    attribution = "author unknown";
                }
                books.add(new Book(title, attribution));
            }
        } catch (JSONException e) {
            Log.e("BookLog", "Problem parsing JSON data", e);
        }
        return books;
    }
}
