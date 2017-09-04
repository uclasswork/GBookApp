package com.example.android.gbookapp;

// an individual book
class Book {
    private String title;
    private String author;

    Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    String Title() {
        return (title);
    }

    String Author() {
        return (author);
    }
}
