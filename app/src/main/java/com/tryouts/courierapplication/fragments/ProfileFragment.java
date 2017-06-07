package com.tryouts.courierapplication.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tryouts.courierapplication.presenters.ProfilePresenter;
import com.tryouts.courierapplication.R;

public class ProfileFragment extends Fragment {

    private TextView mName;
    private TextView mPhone;
    private TextView mMail;
    private ProgressDialog mProgressDialog;
    private ProfilePresenter mPresenter;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mName = (TextView) view.findViewById(R.id.name_text_view);
        mPhone = (TextView) view.findViewById(R.id.telephone_text_view);
        mMail = (TextView) view.findViewById(R.id.mail_text_view);

        mPresenter = new ProfilePresenter(this);
        mPresenter.onUserDetailsRequired();
    }

    public void setTexts(String name, String phone, String mail) {
        mName.setText(name);
        mPhone.setText(phone);
        mMail.setText(mail);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
