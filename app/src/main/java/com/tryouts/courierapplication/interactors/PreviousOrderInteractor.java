package com.tryouts.courierapplication.interactors;

import android.content.Context;

import com.tryouts.courierapplication.adapters.OrdersAdapter;
import com.tryouts.courierapplication.helperclasses.FirebaseOpsHelper;
import com.tryouts.courierapplication.items.OrderReceived;
import com.tryouts.courierapplication.presenters.PreviousOrderPresenter;

import java.util.ArrayList;

public class PreviousOrderInteractor {

    private PreviousOrderPresenter mPresenter;
    private Context context;
    private OrdersAdapter mOrdersAdapter;

    public PreviousOrderInteractor(PreviousOrderPresenter presenter) {
        this.mPresenter = presenter;
    }

    public void fetchPreviousOrdersData() {
        FirebaseOpsHelper fbHelper = new FirebaseOpsHelper();
        fbHelper.onPreviousOrdersCall(this);
    }

    public void onReceivedPreviousOrdersData(ArrayList<OrderReceived> orderList) {
        mPresenter.sendContext();
        mOrdersAdapter = new OrdersAdapter(orderList, context);
        mPresenter.onAdapterReady();
    }

    public void setInteractorContext(Context context) {
        this.context = context;
    }

    public OrdersAdapter sendOrdersAdapter() {
        return mOrdersAdapter;
    }

}
