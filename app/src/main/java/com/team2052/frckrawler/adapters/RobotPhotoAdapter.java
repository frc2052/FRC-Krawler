package com.team2052.frckrawler.adapters;

import android.content.Context;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.db.RobotPhoto;
import com.team2052.frckrawler.views.SquareImageView;

import java.util.Date;
import java.util.List;

/**
 * @author Adam
 * @since 10/4/2014
 */
public class RobotPhotoAdapter extends BaseAdapter {

    private final List<RobotPhoto> photos;
    private final Context mContext;

    public RobotPhotoAdapter(Context context, List<RobotPhoto> photos) {
        this.mContext = context;
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int i) {
        return photos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return photos.get(i).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_image, parent, false);
        }

        final SquareImageView imageView = (SquareImageView) convertView.findViewById(R.id.robot_photo);
        final View view = convertView.findViewById(R.id.title_bar);
        final TextView title = (TextView) convertView.findViewById(R.id.title);
        final TextView secondary = (TextView) convertView.findViewById(R.id.secondary);

        if (photos.get(position).getTitle() != null && !photos.get(position).getTitle().isEmpty()) {
            title.setText(photos.get(position).getTitle());
        } else {
            title.setText("None");
        }

        if (photos.get(position).getDate() != null) {
            secondary.setText(DateFormat.getDateFormat(mContext).format(photos.get(position).getDate()));
        } else {
            secondary.setText(DateFormat.getDateFormat(mContext).format(new Date()));
        }
        Uri uri = Uri.parse("file://" + photos.get(position).getLocation());
        Picasso.with(mContext).load(uri).centerCrop().fit().into(imageView, new Callback() {
            @Override
            public void onSuccess() {

                /*Palette.generateAsync(((BitmapDrawable) imageView.getDrawable()).getBitmap(), new Palette.PaletteAsyncListener()
                {
                    @Override
                    public void onGenerated(Palette palette)
                    {
                        Palette.Swatch swatch = palette.getDarkVibrantSwatch();
                        if (swatch != null) {
                            view.setBackgroundColor(swatch.getRgb());
                            title.setTextColor(swatch.getTitleTextColor());
                            secondary.setTextColor(swatch.getBodyTextColor());
                        }
                    }
                });*/
            }

            @Override
            public void onError() {
            }
        });


        return convertView;
    }

}
