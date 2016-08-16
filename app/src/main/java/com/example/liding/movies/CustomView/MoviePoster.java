package com.example.liding.movies.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MoviePoster extends ImageView {
    public MoviePoster(Context context) {
        super(context);
    }

    public MoviePoster(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoviePoster(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
