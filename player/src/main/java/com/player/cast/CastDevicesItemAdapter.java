package com.player.cast;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.player.R;

public class CastDevicesItemAdapter<Device> extends ArrayAdapter<CastDeviceItem<Device>> {

    public CastDevicesItemAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceItemHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_cast_device, parent, false);
            holder = new DeviceItemHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (DeviceItemHolder) convertView.getTag();
        }
        CastDeviceItem<Device> castDeviceItem = getItem(position);
        holder.textPrimary.setText(castDeviceItem.getDeviceName());
        holder.textSecondary.setText(castDeviceItem.getServiceName());
        return convertView;
    }

    private class DeviceItemHolder {

        public final TextView textPrimary;
        public final TextView textSecondary;

        public DeviceItemHolder(@NonNull View view) {
            textPrimary = (TextView) view.findViewById(R.id.textPrimary);
            textSecondary = (TextView) view.findViewById(R.id.textSecondary);
        }
    }
}