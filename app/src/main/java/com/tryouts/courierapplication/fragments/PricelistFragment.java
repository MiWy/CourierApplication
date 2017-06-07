package com.tryouts.courierapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tryouts.courierapplication.presenters.PricelistPresenter;
import com.tryouts.courierapplication.R;

public class PricelistFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private PricelistPresenter mPresenter;

    public PricelistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pricelist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.pricelist_recyclerview);

        mPresenter = new PricelistPresenter(this);
        mPresenter.initialize();
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public Context getContextFromFragment() {
        return getContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

}
