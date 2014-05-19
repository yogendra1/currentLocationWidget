package com.yogee.widgets.CurrentLocationWidget.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.yogee.widgets.CurrentLocationWidget.R;

import java.util.List;

/**
 * Created by yogendra on 21/04/14.
 */
 /* class to showing custom cells in theme list */
public class ThemeListAdapter extends ArrayAdapter<WidgetTheme> {

    /* context */
    private Context mContext;

    /* widget themes */
    private List<WidgetTheme> themes;

    /* layout inflater */
    private LayoutInflater mInflater;

    /**
     * @param context
     * @param textViewResourceId
     * @param objects
     */
    public ThemeListAdapter(Context context, int textViewResourceId, List<WidgetTheme> objects) {
        super(context, textViewResourceId, objects);
        mContext = context;
        themes = objects;
        mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder {
        public TextView lblNearBy;
        public TextView txtAddress;
        public TextView txtTime;
        public ImageView imgSelectedTick;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder;
        WidgetTheme theme = themes.get(position);

        if (convertView == null) {
            v = mInflater.inflate(R.layout.location_widget_theme_sample, null);
            holder = new ViewHolder();
            holder.lblNearBy = (TextView) v.findViewById(R.id.lbl_theme_nearby);
            holder.txtAddress = (TextView) v.findViewById(R.id.txt_theme_address);
            holder.txtTime = (TextView) v.findViewById(R.id.txt_theme_time);
            holder.imgSelectedTick = (ImageView) v.findViewById(R.id.img_selected_theme_tick);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        /* setting theme colors */
        holder.lblNearBy.setTextColor(getContext().getResources().getColor(theme.getTextColorResourceId()));
        holder.txtAddress.setTextColor(getContext().getResources().getColor(theme.getTextColorResourceId()));
        holder.txtTime.setTextColor(getContext().getResources().getColor(theme.getTextColorResourceId()));
        v.setBackgroundResource(theme.getThemeResourceId());

        /* show/hide selected icon */
        if (WidgetThemeCollection.getInstance().getSelectedThemeId(mContext) == position) {
            holder.imgSelectedTick.setVisibility(View.VISIBLE);
        } else {
            holder.imgSelectedTick.setVisibility(View.GONE);
        }
        return v;
    }
}