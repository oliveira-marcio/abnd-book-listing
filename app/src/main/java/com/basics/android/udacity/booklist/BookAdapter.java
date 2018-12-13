package com.basics.android.udacity.booklist;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;


public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.books_list_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView titleView = (TextView) listItemView.findViewById(R.id.title_text_view);
        titleView.setText(currentBook.getTitle());

        TextView subtitleView = (TextView) listItemView.findViewById(R.id.subtitle_text_view);
        if(TextUtils.isEmpty(currentBook.getSubtitle())){
            subtitleView.setVisibility(View.GONE);
        } else {
            subtitleView.setVisibility(View.VISIBLE);
            subtitleView.setText(currentBook.getSubtitle());
        }

        TextView authorsView = (TextView) listItemView.findViewById(R.id.authors_text_view);
        authorsView.setText(currentBook.getAuthors());

        RatingBar ratingBar = (RatingBar) listItemView.findViewById(R.id.ratingBar);
        ratingBar.setRating(currentBook.getAverageRating());

        TextView ratingCountView = (TextView) listItemView.findViewById(R.id.rating_count_text_view);
        ratingCountView.setText(" " + currentBook.getRatingsCount());

        return listItemView;
    }
}
