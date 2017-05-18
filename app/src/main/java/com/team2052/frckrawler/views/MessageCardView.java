package com.team2052.frckrawler.views;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.team2052.frckrawler.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageCardView extends CardView {
    public static final int ERROR = 0;
    public static final int GOOD = 2;
    @BindView(R.id.message_icon)
    ImageView imageView;
    @BindView(R.id.message_text)
    TextView messageText;
    @BindView(R.id.message_title)
    TextView messageTitle;
    @BindView(R.id.container)
    CardView container;
    @MessageType
    int messageType = GOOD;

    public MessageCardView(Context context) {
        super(context);
    }

    public MessageCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.message_card_view, this);
        ButterKnife.bind(this);
        setMessageType(messageType);
    }

    public void setMessageText(String string) {
        messageText.setText(string);
    }

    public void setMessageText(@StringRes int res) {
        messageText.setText(getResources().getString(res));
    }

    public void setMessageTitle(String string) {
        messageTitle.setText(string);
    }

    public void setMessageTitle(@StringRes int res) {
        messageTitle.setText(res);
    }

    @MessageType
    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(@MessageType int messageType) {
        @DrawableRes
        int messageIcon = R.drawable.ic_check_white_24dp;
        @ColorRes
        int messageColor = R.color.green_800;

        switch (messageType) {
            case ERROR:
                messageIcon = R.drawable.ic_close_white_24dp;
                messageColor = R.color.red_800;
                break;
            case GOOD:
                messageIcon = R.drawable.ic_check_white_24dp;
                messageColor = R.color.green_800;
                break;
        }

        container.setBackgroundColor(getResources().getColor(messageColor));
        imageView.setImageDrawable(getResources().getDrawable(messageIcon));

        this.messageType = messageType;
    }

    @IntDef({ERROR, GOOD})
    public @interface MessageType {
    }
}
