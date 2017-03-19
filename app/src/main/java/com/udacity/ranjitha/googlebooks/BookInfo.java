package com.udacity.ranjitha.googlebooks;


public class BookInfo {

    private String bookTitle;

    private String author;


    public BookInfo(String book, String author) {
        bookTitle = book;
        this.author = author;

    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getAuthor() {
        return author;
    }


}
