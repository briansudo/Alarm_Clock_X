package com.brianysu.alarmclockx;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class AlarmsAdapter extends CursorAdapter {

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView hourView;
        public final TextView minView;
        public final TextView nameView;
        public final TextView repeatedView;
        public final TextView onOffView;
        public final ImageView deleteView;

        public ViewHolder(View view) {
            hourView = (TextView) view.findViewById(R.id.alarm_hour_textview);
            minView = (TextView) view.findViewById(R.id.alarm_min_textview);
            nameView = (TextView) view.findViewById(R.id.alarm_name_textview);
            repeatedView = (TextView) view.findViewById(R.id.alarm_repeated_days_textview);
            onOffView = (TextView) view.findViewById(R.id.alarm_enable_textview);
            deleteView = (ImageView) view.findViewById(R.id.delete_alarm_button);
        }
    }

    public AlarmsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.list_item_alarm;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        final int id = cursor.getInt(AlarmsFragment.COL_ALARM_ID);
        String name = cursor.getString(AlarmsFragment.COL_ALARM_NAME);
        int hour = cursor.getInt(AlarmsFragment.COL_ALARM_HOUR);
        int min = cursor.getInt(AlarmsFragment.COL_ALARM_MIN);
        String tone = cursor.getString(AlarmsFragment.COL_ALARM_TONE);
        int repeated = cursor.getInt(AlarmsFragment.COL_ALARM_REPEATED);
        final int enabled = cursor.getInt(AlarmsFragment.COL_ALARM_ENABLED);

        viewHolder.hourView.setText(Utility.formatHour(hour));
        viewHolder.minView.setText(Utility.formatMin(min));
        viewHolder.nameView.setText(name);

        BinaryRepeatedDate daysRepeated = new BinaryRepeatedDate(repeated);

        if (enabled == 1) {
            configOnTextView(context, viewHolder);
        } else {
            configOffTextView(context, viewHolder);
        }

        final Context c = context;

        viewHolder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmUtility.deleteAlarmById(c, id);
            }
        });

        viewHolder.onOffView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enabled == 1) {
                    configOffTextView(c, viewHolder);
                } else {
                    configOnTextView(c, viewHolder);
                }
            }
        });
    }

    private void configOffTextView(Context context, ViewHolder viewHolder) {
        viewHolder.onOffView.setText(R.string.list_item_alarm_off);
        viewHolder.onOffView.setTextColor(context.getResources().getColor(R.color.off));
    }

    private void configOnTextView(Context context, ViewHolder viewHolder) {
        viewHolder.onOffView.setText(R.string.list_item_alarm_on);
        viewHolder.onOffView.setTextColor(context.getResources().getColor(R.color.on));
    }

}
