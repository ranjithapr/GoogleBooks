package com.udacity.ranjitha.googlebooks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class BookInfoAdapter extends ArrayAdapter <BookInfo> {

    public BookInfoAdapter(Context context, ArrayList <BookInfo> bookDetails ) {
        super(context, 0 , bookDetails);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list, parent, false);
        }

        BookInfo currentItem = getItem(position);

        //find the text view of the book name id
        TextView bookNameView = (TextView) listItemView.findViewById(R.id.book_name);
        bookNameView.setText(currentItem.getBookTitle());

        //find the text view of the author name
        TextView authorNameView = (TextView) listItemView.findViewById(R.id.author_name);
        authorNameView.setText(currentItem.getAuthor());

        return listItemView;
    }
}
