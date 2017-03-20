package com.udacity.ranjitha.googlebooks;


import android.os.Parcel;
import android.os.Parcelable;

public class BookInfo implements Parcelable {

    private String bookTitle;

    private String author;


    public BookInfo(String book, String author) {
        bookTitle = book;
        this.author = author;

    }

    private BookInfo(Parcel in) {
        bookTitle = in.readString();
        author = in.readString();
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getAuthor() {
        return author;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bookTitle);
        parcel.writeString(author);
    }

    public static final Parcelable.Creator<BookInfo> CREATOR = new Parcelable.Creator<BookInfo>() {
        public BookInfo createFromParcel(Parcel in) {
            return new BookInfo(in);
        }

        public BookInfo[] newArray(int size) {
            return new BookInfo[size];
        }
    };
}
