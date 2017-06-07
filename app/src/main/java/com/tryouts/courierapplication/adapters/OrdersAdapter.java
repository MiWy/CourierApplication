package com.tryouts.courierapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tryouts.courierapplication.items.OrderReceived;
import com.tryouts.courierapplication.R;

import java.util.ArrayList;


public class OrdersAdapter extends ArrayAdapter<OrderReceived> {

    private class ViewHolder {
        TextView mDate;
        TextView mFrom;
        TextView mTo;
        TextView mType;
    }

    public OrdersAdapter(ArrayList<OrderReceived> data, Context context) {
        super(context, R.layout.item_previous_orders_listview, data);
        ArrayList<OrderReceived> orderSet = data;
        Context context1 = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        OrderReceived order = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        OrdersAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;

        if(convertView == null) {
            viewHolder = new OrdersAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_previous_orders_listview, parent, false);
            viewHolder.mDate = (TextView) convertView.findViewById(R.id.order_date);
            viewHolder.mFrom = (TextView) convertView.findViewById(R.id.order_from);
            viewHolder.mTo = (TextView) convertView.findViewById(R.id.order_to);
            viewHolder.mType = (TextView) convertView.findViewById(R.id.order_status);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OrdersAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.mDate.setText(order.getDate());
        viewHolder.mFrom.setText(order.getFrom());
        viewHolder.mTo.setText(order.getTo());
        String type = order.getType();
        switch (type) {
            case "finished":
                viewHolder.mType.setText(getContext().getString(R.string.type_finished));
                break;
            case "new":
                viewHolder.mType.setText(getContext().getString(R.string.type_new));
                break;
            case "canceled":
                viewHolder.mType.setText(getContext().getString(R.string.type_canceled));
                break;
            case "taken":
                viewHolder.mType.setText(getContext().getString(R.string.type_taken));
                break;
            default:
                viewHolder.mType.setText("");
                break;
        }
        return convertView;
    }
}