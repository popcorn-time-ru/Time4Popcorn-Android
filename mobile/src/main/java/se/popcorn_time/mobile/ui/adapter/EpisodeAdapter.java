package se.popcorn_time.mobile.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.popcorn_time.mobile.R;
import se.popcorn_time.base.model.video.info.Episode;

public class EpisodeAdapter extends BaseAdapter {

    private int mPosition = 0;
    private Context context;
    private List<Episode> episodes;
    private LayoutInflater inflater;

    public EpisodeAdapter(Context context, List<Episode> episodes) {
        this.context = context;
        this.episodes = episodes;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public EpisodeAdapter(Context context) {
        this(context, new ArrayList<Episode>());
    }

    @Override
    public int getCount() {
        return episodes.size();
    }

    @Override
    public Episode getItem(int position) {
        return episodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EpisodeHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_episode, parent, false);
            holder = new EpisodeHolder();
            holder.name = (TextView) convertView.findViewById(R.id.episode_name);
            convertView.setTag(holder);
        } else {
            holder = (EpisodeHolder) convertView.getTag();
        }

        String name = context.getResources().getString(R.string.episode) + " " + getItem(position).getNumber();

        if (mPosition == position) {
            holder.name.setText(Html.fromHtml("<b>" + name + "</b>"));
        } else {
            holder.name.setText(name);
        }

        return convertView;
    }

    public void setSelectedItem(int position) {
        mPosition = position;
        notifyDataSetInvalidated();
    }

    public void replaceData(List<Episode> episodes) {
        this.episodes = episodes;
        mPosition = 0;
        notifyDataSetChanged();
    }

    private class EpisodeHolder {
        public TextView name;
    }
}