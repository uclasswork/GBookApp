package com.example.android.gbookapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Books> {
    private Books books = null;
    private ListView book_list = null;
    private BookAdapter adapter = null;
    private TextView empty = null;
    private ProgressBar progress = null;

    public String query_string = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (books == null) {
            books = new Books();
        }
        if (progress == null) {
            progress = (ProgressBar) findViewById(R.id.progress);
            progress.setVisibility(View.GONE);
        }
        if (empty == null) {
            empty = (TextView) findViewById(R.id.empty);
            empty.setVisibility(View.GONE);
        }
        if (adapter == null) {
            adapter = new BookAdapter(this, books);
            book_list = (ListView) findViewById(R.id.list);
            book_list.setAdapter(adapter);
            book_list.setEmptyView(empty);
            book_list.setVisibility(View.GONE);
        }
        getLoaderManager().initLoader(0, null, this);
        findViewById(R.id.run_query).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        query_string = ((EditText) findViewById(R.id.query_string)).getText().toString();
                        ConnectivityManager m = (ConnectivityManager) getSystemService(
                                Context.CONNECTIVITY_SERVICE
                        );
                        if (m == null || m.getActiveNetworkInfo() == null) {
                            empty.setText(R.string.no_network_text);
                        } else {
                            empty.setText(R.string.no_results_text);
                        }
                        progress.setVisibility(View.VISIBLE);
                        book_list.setVisibility(View.GONE);
                        empty.setVisibility(View.GONE);
                        getLoaderManager().restartLoader(0, null, MainActivity.this);
                    }
                }
        );
    }

    @Override
    public Loader<Books> onCreateLoader(int id, Bundle args) {
        return new FetchBooks(this);
    }

    @Override
    public void onLoaderReset(Loader<Books> loader) {
        progress.setVisibility(View.GONE);
        books.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadFinished(Loader<Books> l, Books b) {
        progress.setVisibility(View.GONE);
        book_list.setVisibility(View.VISIBLE);
        books.clear();
        books.addAll(b);
        adapter.notifyDataSetChanged();
    }
}
