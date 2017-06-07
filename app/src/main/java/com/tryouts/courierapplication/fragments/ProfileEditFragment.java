package com.tryouts.courierapplication.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.tryouts.courierapplication.presenters.ProfileEditPresenter;
import com.tryouts.courierapplication.R;


public class ProfileEditFragment extends Fragment {

    private ProfileEditPresenter mPresenter;
    private EditText mNameEdit;
    private EditText mPhoneEdit;
    private EditText mMailEdit;
    private Button mAcceptButton;
    private ProgressDialog mProgressDialog;

    public ProfileEditFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mNameEdit = (EditText) view.findViewById(R.id.profileedit_name_edit_text);
        mPhoneEdit = (EditText) view.findViewById(R.id.profileedit_telephone_edit_text);
        mMailEdit = (EditText) view.findViewById(R.id.profileedit_mail_edit_text);
        mAcceptButton = (Button) view.findViewById(R.id.profileedit_accept_button);

        mPresenter = new ProfileEditPresenter(this);
        mPresenter.initialize();
    }

    public void setListeners() {
        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.setListenerForAccept(mNameEdit.getText().toString(),
                        mPhoneEdit.getText().toString(),
                        mMailEdit.getText().toString());
            }
        });
        mNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.checkIfEditTextValid(getString(R.string.profile_error_name), mNameEdit.getText().toString(), 1);
            }
        });
        mPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.checkIfEditTextValid(getString(R.string.profile_error_phone), mPhoneEdit.getText().toString(), 2);
            }
        });
        mMailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.checkIfEditTextValid(getString(R.string.profile_error_mail), mMailEdit.getText().toString(), 3);
            }
        });
    }

    /**
     * Method for producing toasts when event of a numberOfMessage type happened.
     * @param numberOfMessage: 1 = error in submission, 2 = successful submission
     */
    public void makeToast(int numberOfMessage) {
        String text;
        if(numberOfMessage == 1) {
            text = getString(R.string.profile_error_submit);
        } else {
            text = getString(R.string.profile_toast_update);
        }
        Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    public void setTexts(String name, String phone, String mail) {
        mNameEdit.setText(name);
        mPhoneEdit.setText(phone);
        mMailEdit.setText(mail);
    }

    public void setEditTextErrors(String errorMessage, int dataNumber) {
        if(dataNumber == 1) {
            mNameEdit.setError(errorMessage);
        } else if(dataNumber == 2) {
            mPhoneEdit.setError(errorMessage);
        } else if(dataNumber == 3) {
            mMailEdit.setError(errorMessage);
        }
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
