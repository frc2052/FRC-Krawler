package com.team2052.frckrawler.adapters.items.smart;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.models.Season;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class SeasonItemView extends BindableFrameLayout<Season> {
    @BindView(R.id.game_name)
    TextView mGameName;

    public SeasonItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_game;
    }

    @Override
    public void bind(Season game) {
        setOnClickListener(v -> notifyItemAction(SmartAdapterInteractions.EVENT_CLICKED));
        this.setFocusable(true);
        this.setClickable(true);

        mGameName.setText(game.getName());
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
