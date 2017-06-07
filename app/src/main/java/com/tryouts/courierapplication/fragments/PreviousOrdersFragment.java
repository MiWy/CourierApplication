package com.tryouts.courierapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tryouts.courierapplication.adapters.OrdersAdapter;
import com.tryouts.courierapplication.presenters.PreviousOrderPresenter;
import com.tryouts.courierapplication.R;


public class PreviousOrdersFragment extends Fragment {

    private ListView mListView;
    private PreviousOrderPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_previous_orders, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = (ListView) view.findViewById(R.id.previous_orders_listview);

        mPresenter = new PreviousOrderPresenter(this);
        mPresenter.initialize();
    }

    public Context getFragmentContext() {
        return getContext();
    }

    public void setListViewWithAdapter(OrdersAdapter ordersAdapter) {
        mListView.setAdapter(ordersAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
