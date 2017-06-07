package com.tryouts.courierapplication.interactors;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tryouts.courierapplication.adapters.BasePriceAdapterByGenre;
import com.tryouts.courierapplication.items.PriceItem;
import com.tryouts.courierapplication.items.PriceList;
import com.tryouts.courierapplication.presenters.PricelistPresenter;

import java.util.ArrayList;
import java.util.List;


public class PricelistInteractor {

    private Context mContext;

    public PricelistInteractor(PricelistPresenter presenter) {
        PricelistPresenter mPresenter = presenter;
    }

    /**
     * Prepares List with priceitems in correct order.
     * Dummy list within PriceList.class.
     * @return
     */
    private List<PriceItem> preparePrices() {
        PriceList priceList = PriceList.get();
        return priceList.getPrices();
    }

    public void onPrepareRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(prepareAdapter());
    }

    /**
     * Called from this interactor.
     * Creates and returns an adapter (calls getPriceItemList to prepare data)
     * @return
     */
    private BasePriceAdapterByGenre prepareAdapter() {
        return new BasePriceAdapterByGenre(getPriceItemList());
    }

    /**
     * Called from this presenter.
     * Retrieves priceitems list from PricelistInteractor.
     * @return
     */
    private List<PriceItem> getPriceItemList() {
        return preparePrices();
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    private Context getContext() {
        return mContext;
    }


}
