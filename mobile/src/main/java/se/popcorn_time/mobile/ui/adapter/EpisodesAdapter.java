package se.popcorn_time.mobile.ui.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import se.popcorn_time.VibrantUtils;
import se.popcorn_time.base.model.video.info.Episode;
import se.popcorn_time.mobile.R;
import se.popcorn_time.model.details.IDetailsUseCase;

public final class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {

    private final IDetailsUseCase detailsUseCase;
    private Episode[] episodes;
    private int position;

    public EpisodesAdapter(@NonNull IDetailsUseCase detailsUseCase) {
        this.detailsUseCase = detailsUseCase;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(episodes[position], position);
    }

    @Override
    public int getItemCount() {
        return episodes != null ? episodes.length : 0;
    }

    public void setEpisodes(@Nullable Episode[] episodes, int position) {
        this.episodes = episodes;
        this.position = position;
        notifyDataSetChanged();
    }

    final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private int pos;

        ViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_episode, parent, false));
            itemView.setOnClickListener(this);
        }

        void onBind(Episode episode, int position) {
            this.pos = position;
            ((AppCompatTextView) itemView).setText(String.format(Locale.ENGLISH, "%d", episode.getNumber()));
            if (EpisodesAdapter.this.position == position) {
                ((AppCompatTextView) itemView).setTextColor(Color.WHITE);
                ((GradientDrawable) itemView.getBackground()).setStroke((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, itemView.getResources().getDisplayMetrics()), VibrantUtils.getAccentColor());
                ((AppCompatTextView) itemView).setSupportBackgroundTintList(ColorStateList.valueOf(VibrantUtils.getAccentColor()));
            } else {
                final int color = ColorUtils.setAlphaComponent(Color.WHITE, 128);
                ((AppCompatTextView) itemView).setTextColor(color);
                ((GradientDrawable) itemView.getBackground()).setStroke((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, itemView.getResources().getDisplayMetrics()), color);
                ((AppCompatTextView) itemView).setSupportBackgroundTintList(null);
            }
        }

        @Override
        public void onClick(View v) {
            detailsUseCase.getEpisodeChoiceProperty().setPosition(pos);
        }
    }
}
