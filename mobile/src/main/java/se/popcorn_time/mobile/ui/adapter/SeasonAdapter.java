package se.popcorn_time.mobile.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import se.popcorn_time.base.model.video.info.Season;
import se.popcorn_time.mobile.R;

public class SeasonAdapter extends BaseAdapter {

    private int mPosition = 0;
    private Context context;
    private List<Season> seasons;
    private LayoutInflater inflater;

    public SeasonAdapter(Context context, List<Season> seasons) {
        this.context = context;
        this.seasons = seasons;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return seasons.size();
    }

    @Override
    public Season getItem(int position) {
        return seasons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SeasonHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_season, parent, false);
            holder = new SeasonHolder();
            holder.name = (TextView) convertView.findViewById(R.id.season_name);
            convertView.setTag(holder);
        } else {
            holder = (SeasonHolder) convertView.getTag();
        }

        String name = context.getResources().getString(R.string.season) + " " + getItem(position).getNumber();

        if (mPosition == position) {
            holder.name.setText(Html.fromHtml("<b>" + name + "</b>"));
            holder.name.setTextColor(Color.parseColor("#3aa2d0"));
        } else {
            holder.name.setText(name);
            holder.name.setTextColor(Color.parseColor("#666666"));
        }

        return convertView;
    }

    public void setSelectedItem(int position) {
        mPosition = position;
        notifyDataSetInvalidated();
    }

    private class SeasonHolder {
        public TextView name;
    }
}