package com.example.myapplication;

public class ExampleReview{
    private String mLine1;
    private String mLine2;

    public ExampleReview(String getStrTitle, String getStrReviewDesc) {
        mLine1 = getStrTitle;
        mLine2 = getStrReviewDesc;
    }

    public String getStrTitle() {
        return mLine1;
    }

    public String getStrReviewDesc() {
        return mLine2;
    }
}