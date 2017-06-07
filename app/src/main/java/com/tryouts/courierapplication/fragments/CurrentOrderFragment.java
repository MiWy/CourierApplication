package com.tryouts.courierapplication.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.tryouts.courierapplication.presenters.CurrentOrderPresenter;
import com.tryouts.courierapplication.R;


public class CurrentOrderFragment extends Fragment {
    private static final String PREFERENCES_NAME = "SharePref";
    private static final String PREFERENCES_TEXT_FIELD = "orderTimeStamp";
    private static final String PREFERENCES_EMPTY = "empty";
    private String orderTimeStamp;
    private SharedPreferences mSharedPreferences;
    private TextView mDateTextView;
    private TextView mFromTextView;
    private TextView mToTextView;
    private TextView mDistanceTextView;
    private TextView mPhoneTextView;
    private CheckedTextView mFirstAdditionalServiceTextView;
    private CheckedTextView mSecondAdditionalServiceTextView;
    private CheckedTextView mThirdAdditionalServiceTextView;
    private ProgressDialog mProgressDialog;
    private CurrentOrderPresenter mPresenter;

    public CurrentOrderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSharedPreferences = this.getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        orderTimeStamp = mSharedPreferences.getString(PREFERENCES_TEXT_FIELD, PREFERENCES_EMPTY);

        return inflater.inflate(R.layout.fragment_current_order, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mDateTextView = (TextView) view.findViewById(R.id.progress_body_date);
        mFromTextView = (TextView) view.findViewById(R.id.progress_body_from_txt);
        mToTextView = (TextView) view.findViewById(R.id.progress_body_to_txt);
        mDistanceTextView = (TextView) view.findViewById(R.id.progress_body_distance);
        mPhoneTextView = (TextView) view.findViewById(R.id.progress_body_phone_txt);
        mFirstAdditionalServiceTextView = (CheckedTextView) view.findViewById(R.id.progress_check_1);
        mSecondAdditionalServiceTextView = (CheckedTextView) view.findViewById(R.id.progress_check_2);
        mThirdAdditionalServiceTextView = (CheckedTextView) view.findViewById(R.id.progress_check_3);

        mPresenter = new CurrentOrderPresenter(this);
        mPresenter.initialize();
    }

    /**
     * Sets views' text with provided strings about the current order.
     * @param from
     * @param to
     * @param distance
     * @param phone
     * @param date
     * @param additionalOne
     * @param additionalTwo
     * @param additionalThree
     */
    public void setViewsWithData(String from, String to, String distance, String phone,
                                 String date, boolean additionalOne, boolean additionalTwo,
                                 boolean additionalThree) {
        mDateTextView.setText(date);
        mFromTextView.setText(from);
        mToTextView.setText(to);
        mDistanceTextView.setText(distance);
        mPhoneTextView.setText(phone);
        mFirstAdditionalServiceTextView.setChecked(additionalOne);
        mSecondAdditionalServiceTextView.setChecked(additionalTwo);
        mThirdAdditionalServiceTextView.setChecked(additionalThree);
    }

    /**
     * Method launches only if the user is a courier. Inflates the courier buttons.
     */
    public void onInflateCourierButtonsCall() {
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.current_order_courier_buttons, null);

        Button buttonAccept = (Button) v.findViewById(R.id.progress_accept_button);
        Button buttonFinish = (Button) v.findViewById(R.id.progress_success_button);
        Button buttonCancel = (Button) v.findViewById(R.id.progress_cancel_button);


        ViewGroup insertPoint = (ViewGroup) getActivity().findViewById(R.id.progress_buttons_insert_point);
        insertPoint.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        setButtonsListeners(buttonAccept, buttonFinish, buttonCancel);
    }

    /**
     * ClickListeners for courier's buttons.
     * @param accept
     * @param finish
     * @param cancel
     */
    private void setButtonsListeners(Button accept, Button finish, Button cancel) {
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCourierButtonClick(1);
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCourierButtonClick(2);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCourierButtonClick(3);
            }
        });
    }

    /**
     * Displays alertDialog provided to the view.
     * @param alertDialog
     */
    public void showDialog(AlertDialog alertDialog) {
        alertDialog.show();
    }

    /**
     * Sets sharedpreferences current order string value to 'empty'.
     */
    public void removeOrderFromSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PREFERENCES_TEXT_FIELD, PREFERENCES_EMPTY);
        editor.commit();
    }

    /**
     * Shows toast to the user.
     */
    public void makeToast(String whichType) {
        Toast toast = Toast.makeText(getContext(), whichType, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * Prepares and shows ProgressDialog upon call (while the data is loading).
     */
    public void showProgressDialog() {
        if(mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.progress_dialog_loading));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        mProgressDialog.hide();
    }

    public String getOrderTimeStamp() {
        return orderTimeStamp;
    }

    public Context getFragmentContext() {
        return getContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
