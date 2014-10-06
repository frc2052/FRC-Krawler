package com.team2052.frckrawler.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.team2052.frckrawler.view.SquareImageView;

import java.util.List;

import frckrawler.RobotPhoto;

/**
 * @author Adam
 * @since 10/4/2014
 */
public class RobotPhotoAdapter extends BaseAdapter
{

    private final List<RobotPhoto> photos;
    private final Context mContext;

    public RobotPhotoAdapter(Context context, List<RobotPhoto> photos)
    {
        this.mContext = context;
        this.photos = photos;
    }

    @Override
    public int getCount()
    {
        return photos.size();
    }

    @Override
    public Object getItem(int i)
    {
        return photos.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return photos.get(i).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = new SquareImageView(mContext);
        }

        Picasso.with(mContext).load("file:" + photos.get(position).getLocation()).centerCrop().fit().into((ImageView) convertView);
        return convertView;
    }

}
