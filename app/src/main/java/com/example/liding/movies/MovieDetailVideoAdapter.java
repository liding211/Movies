package com.example.liding.movies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MovieDetailVideoAdapter extends ArrayAdapter {
    private HashMap<String, String> data = new HashMap<String, String>();
    private String[] keys;
    private Activity activity;

    public MovieDetailVideoAdapter(Context context, int resource, Activity activity) {
        super(context, resource);
        this.activity = activity;
    }

    public void add(HashMap<String, String> data) {
        this.data = data;
        keys = data.keySet().toArray(new String[data.size()]);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public void clear() {
        super.clear();
        data.clear();
        keys = null;
    }

    @Override
    public Object getItem(int position) {
        return data.get(keys[position]);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {
        TextView name;
        TextView id;
        if (convertView != null) {
            name = (TextView) convertView.findViewById(R.id.movie_detail_video_name);
            id = (TextView) convertView.findViewById(R.id.movie_detail_video_id);
        } else {
            convertView = View.inflate(getContext(), R.layout.movie_detail_video_item, null);
            name = (TextView) convertView.findViewById(R.id.movie_detail_video_name);
            id = (TextView) convertView.findViewById(R.id.movie_detail_video_id);
        }
        name.setText(getItem(pos).toString());
        id.setText(keys[pos]);

        parent.invalidate();
        return convertView;
    }
}
