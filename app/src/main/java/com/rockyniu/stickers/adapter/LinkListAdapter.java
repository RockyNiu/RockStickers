package com.rockyniu.stickers.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockyniu.stickers.R;
import com.rockyniu.stickers.model.Link;

import java.util.List;

public class LinkListAdapter extends ArrayAdapter<Link> {

    private final List<Link> list;
    //	private final Context context;
    private static LayoutInflater inflater = null;

    public LinkListAdapter(Activity context, List<Link> list) {
        super(context, R.layout.row_link, list);
//		this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Link getItem(int position) {
        return list.get(position);
    }

    public void updateList(List<Link> newlist) {
        list.clear();
        list.addAll(newlist);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.row_link, null);
        }

        ImageView imagePriorityBar = (ImageView) view.findViewById(R.id.priority_bar);
        TextView titleTextView = (TextView) view.findViewById(R.id.row_title);

        Link currentItem = list.get(position);

        String title = currentItem.getTitle();

        titleTextView.setText(title);
        return view;
    }
}
