package com.example.android.gbookapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class BookAdapter extends ArrayAdapter<Book> {

    BookAdapter(Activity context, Books b) {
        super(context, 0, b);
    }

    @Override
    @NonNull
    public View getView(
            int array_index,
            View convertView,
            @NonNull ViewGroup parent
    ) {
        if (convertView == null) {
            // create a new view from XML template
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book,
                    parent,
                    false
            );

            // save direct references to view's fields to avoid repeated
            // calls to expensive findViewById
            convertView.setTag(new ViewHolder(convertView));
        }

        Book b = getItem(array_index);
        if (b != null) {
            ((ViewHolder) convertView.getTag()).update(b);
        }

        return (convertView);
    }

    private class ViewHolder {
        private TextView title;
        private TextView author;

        ViewHolder(View v) {
            title = (TextView) v.findViewById(R.id.title);
            author = (TextView) v.findViewById(R.id.author);
        }

        void update(final Book b) {
            if (title != null) {
                title.setText(b.Title());
            }
            if (author != null) {
                author.setText(b.Author());
            }
        }
    }
}
