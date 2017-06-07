package com.tryouts.courierapplication.presenters;

import com.tryouts.courierapplication.fragments.NewOrderFragment;
import com.tryouts.courierapplication.interactors.NewOrderInteractor;

public class NewOrderPresenter {

    private NewOrderInteractor mModel;
    private NewOrderFragment view;
    private static final int RESULT_OK = -1;
    private static final int RESULT_CANCELED = 0;
    public static final int RESULT_ERROR = 2;
    private static final int MODE_FULLSCREEN = 1;
    private static final int MODE_OVERLAY = 2;

    public NewOrderPresenter(NewOrderFragment view) {
        this.mModel = new NewOrderInteractor(this);
        this.view = view;
    }

    public void createListenerForCall() {
        if(mModel.isOrderReadyToSubmit()) {
            mModel.createAndSendOrderToDb();
        } else {
            // We dont have all details needed for order's submission.
            view.makeToast(mModel.getNotReadyString());
        }
    }

    /**
     * Data received from Google Places
     *
     */
    public void onActivityResultCalled(int requestCode, int resultCode) {
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                mModel.onActivityResultSuccesful(1, view.getOnActivityResultIntent());
            } else if(resultCode == RESULT_ERROR) {
                view.makeToast(mModel.getStringFromXml(RESULT_ERROR));
            } else if(resultCode == RESULT_CANCELED) {
                // The user cancelled the operation.
            }
        } else if(requestCode == 2) {
            if(resultCode == RESULT_OK) {
                mModel.onActivityResultSuccesful(2, view.getOnActivityResultIntent());
            } else if(resultCode == RESULT_ERROR) {
                view.makeToast(mModel.getStringFromXml(RESULT_ERROR));
            } else if(resultCode == RESULT_CANCELED) {
                // The user cancelled the operation.
            }
        }
    }

    public void onReadyFromText(String addressFrom) {
        view.setFromText(addressFrom);
    }

    public void onReadyToText(String addressTo) {
        view.setToText(addressTo);
    }
    /**
     * Methods for Google Maps and related views management
     */

    public void setMapView() {
        view.setMapClear();
        if(!mModel.areFromToReceived()) {
            view.moveCameraTo(mModel.getDefaultLatitude(), mModel.getDefaultLongitude());
        } else {
            mModel.prepareMapVariables();
        }
    }

    public void onMapPolylineReady() {
        view.onMapPolylineAdded(mModel.getPolylineOptions());
    }

    public void onCameraUpdateReady() {
        view.animateCamera(mModel.getCameraUpdate());
    }

    public void setGMapsUrl(String gMapsKey) {
        mModel.setGMapKey(gMapsKey);
    }

    public void onMapAsyncCall() {
        view.mapAsyncCall();
    }

    public void onMapUpdatedShowDistance() {
        view.setDistanceView(mModel.getDistanceString());
    }

    /**
     * Methods for listeners management
     */

    public void createListenerForCalculate() {
        onMapAsyncCall();
    }

    public void createListenerForAdditionalServices() {
        view.showAlertDialog(mModel.sendAdditionalAlertDialog());
    }

    public void createListenerForPlaceAutocompleteCall(int requestCode) {
        mModel.onPlaceAutocompleteCall(requestCode);
    }

    public void sendFragmentActivityToInteractor() {
        mModel.setFragmentActivity(view.getParentActivity());
    }

    public void onReadyStartActivityForResult(int requestCode) {
        view.startActivityForResult(mModel.getPlacesAutocompleteIntent(), requestCode);
    }

    public void detachView() {
        view = null;
    }



}
