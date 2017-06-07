package com.tryouts.courierapplication.interactors;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import com.tryouts.courierapplication.helperclasses.FirebaseOpsHelper;
import com.tryouts.courierapplication.items.OrderReceived;
import com.tryouts.courierapplication.presenters.CurrentOrderPresenter;
import com.tryouts.courierapplication.R;


public class CurrentOrderInteractor {

    private CurrentOrderPresenter mPresenter;
    private AlertDialog mAlertDialog;
    private String orderUserTimeStamp;
    private static final String PREFERENCES_EMPTY = "empty";
    private Context mContext;

    public CurrentOrderInteractor(CurrentOrderPresenter presenter) {
        this.mPresenter = presenter;
    }

    /**
     * Given order timestamp string, request order data from the firebase db with FirebaseOpsHelper.
     * In case of string being empty, inform the presenter that the view should show a toast to the user.
     * @param orderTimestamp
     */
    public void getOrderData(String orderTimestamp) {
        if(!orderTimestamp.equals(PREFERENCES_EMPTY)) {
            FirebaseOpsHelper firebaseOpsHelper = new FirebaseOpsHelper(this);
            firebaseOpsHelper.getOrderFromDbWithTimestamp(orderTimestamp);
        } else {
            mPresenter.sendToast(getInteractorContext().getString(R.string.toast_no_current_order));
            mPresenter.hideProgressDialog();
        }
    }

    /**
     * Upon receiving order data sends it to preparing data for view method.
     * @param orderReceived
     */
    public void onReceivedOrderData(OrderReceived orderReceived) {
        prepareOrderDataForTextViews(orderReceived);
    }

    /**
     * Prepares and sends given order data to presenter, which displays it to the user.
     * @param orderReceived
     */
    private void prepareOrderDataForTextViews(OrderReceived orderReceived) {
        String from = orderReceived.getFrom();
        String to = orderReceived.getTo();
        String distance = orderReceived.getDistance();
        String phone = orderReceived.getPhoneNumber();
        String date = orderReceived.getDate();
        String additionalOneS = orderReceived.getIsExpress();
        String additionalTwoS = orderReceived.getIsSuperExpress();
        String additionalThreeS = orderReceived.getIsCarExpress();
        boolean additionalOne = false;
        boolean additionalTwo = false;
        boolean additionalThree = false;
        if(additionalOneS.equals("yes")) {
            additionalOne = true;
        }
        if(additionalTwoS.equals("yes")) {
            additionalTwo = true;
        }
        if(additionalThreeS.equals("yes")) {
            additionalThree = true;
        }
        mPresenter.onOrderStringsReceived(from, to, distance, phone, date, additionalOne,
                additionalTwo, additionalThree);
    }

    /**
     * Receives a call from the presenter.
     * Sends a request to FirebaseOpsHelper to check whether logged in user is a courier.
     */
    public void checkWhetherLoggedUserIsCourier() {
        FirebaseOpsHelper fbHelper = new FirebaseOpsHelper(this);
        fbHelper.onCheckWhetherUserIsACourier();
    }

    /**
     * Receives a call from the FirebaseOpsHelper.
     * If @param is true, then the user is a courier. In that case it informs the presenter
     * that the view should inflate courier buttons.
     * @param isItCourier
     */
    public void onUserRoleReceived(boolean isItCourier) {
        if(isItCourier) {
            mPresenter.inflateCourierButtons();
        }
    }

    /**
     * Receives a call from the presenter. Prepares an alertDialog that will be showed to the user.
     * If user clicks positive button, then sends the request to the db to change its status.
     */
    public void checkIfSureToChangeOrderStatus(
            Context context, final String orderTimestamp, final int whichStatus) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.dialog_sure_body))
                .setTitle(context.getString(R.string.dialog_sure_title))
                .setPositiveButton(context.getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setOrderStatus(whichStatus);
                    }
                })
                .setNegativeButton(context.getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                });
        mAlertDialog = builder.create();
        mPresenter.onCreatedAlertDialog();
    }

    /**
     * Receives a call upon clicking positive button. Makes call to the FirebaseOpsHelper
     * in order to change the order status in firebase db. Type of status depends on the @param.
     * @param whichStatus
     */
    private void setOrderStatus(int whichStatus) {
        FirebaseOpsHelper fbHelper = new FirebaseOpsHelper(this);
        fbHelper.checkAndChangeOrderStatus(orderUserTimeStamp, whichStatus);
    }

    /**
     * Receives a call from the presenter, returns a prepared alertdialog.
     */
    public AlertDialog getAlertDialog() {
        return mAlertDialog;
    }

    /**
     * Sets userTimestamp String (name of order's node in firebase db)
     * @param userTimeStamp
     */
    public void setOrderUserTimeStamp(String userTimeStamp) {
        this.orderUserTimeStamp = userTimeStamp;
    }

    /**
     * Sets context for interactor
     */
    public void setInteractorContext(Context context) {
        this.mContext = context;
    }

    public Context getInteractorContext() {
        return mContext;
    }

    /**
     * Receives a call from the FirebaseOpsHelper.
     * Informs the presenter that the view should remove order's timestamp from
     * sharedpreferences.
     */
    public void removeOrderFromSharedPreferences() {
        mPresenter.removeOrderFromSharedPreferences();
    }

    /**
     * Receives a call from the FirebaseOpsHelper.
     * Informs the presenter that it should show toast with message that the
     * order is @param.
     */
    public void sendToastCall(String whichType) {
        mPresenter.sendToast(whichType);
    }

    /**
     * Sends request to presenter to hide the progressDialog.
     */
    public void hideProgressDialog() {
        mPresenter.hideProgressDialog();
    }
}
