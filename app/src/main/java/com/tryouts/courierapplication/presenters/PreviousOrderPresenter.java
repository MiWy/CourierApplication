package com.tryouts.courierapplication.presenters;

import com.tryouts.courierapplication.fragments.PreviousOrdersFragment;
import com.tryouts.courierapplication.interactors.PreviousOrderInteractor;


public class PreviousOrderPresenter {

    private PreviousOrdersFragment mView;
    private PreviousOrderInteractor mInteractor;

    public PreviousOrderPresenter(PreviousOrdersFragment fragment) {
        this.mView = fragment;
        this.mInteractor = new PreviousOrderInteractor(this);
    }

    public void initialize() {
        mInteractor.fetchPreviousOrdersData();
    }

    public void sendContext() {
        mInteractor.setInteractorContext(mView.getFragmentContext());
    }

    public void onAdapterReady() {
        mView.setListViewWithAdapter(mInteractor.sendOrdersAdapter());
    }

    public void detachView() {
        mView = null;
    }

}
