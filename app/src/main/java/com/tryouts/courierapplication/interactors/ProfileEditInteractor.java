package com.tryouts.courierapplication.interactors;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tryouts.courierapplication.items.User;
import com.tryouts.courierapplication.presenters.ProfileEditPresenter;
import com.tryouts.courierapplication.presenters.ProfilePresenter;

public class ProfileEditInteractor {

    private ProfileEditPresenter mPresenterEdit;
    private DatabaseReference mDatabaseReference;
    private User mUser;

    public ProfileEditInteractor(ProfileEditPresenter mPresenterEdit) {
        this.mPresenterEdit = mPresenterEdit;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        this.mUser = new User();
    }

    /**
     * Get logged user data from DB, called from the profilePresenter which is called
     * from the ProfileFragment onViewCreated.
     * Returns user data to the presenter.
     */
    public void getUserDataFromDb() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();
        mDatabaseReference.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUser.setName(String.valueOf(dataSnapshot.child("name").getValue()));
                        mUser.setPhone(String.valueOf(dataSnapshot.child("phone").getValue()));
                        mUser.setEmail(String.valueOf(dataSnapshot.child("email").getValue()));
                        mUser.setUid(uid);
                        mUser.setRole(String.valueOf(dataSnapshot.child("role").getValue()));
                        checkAndSetCurrentUserToken(uid);
                        mPresenterEdit.onReceivedUserDataFromDb(mUser);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }


    // Precaution, in case user's token get changed or removed
    private String checkAndSetCurrentUserToken(String userUid) {
        String instanceId = FirebaseInstanceId.getInstance().getToken();
        if(instanceId != null) {
            DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mDatabaseReference.child("users")
                    .child(userUid)
                    .child("instanceId")
                    .setValue(instanceId);
        }
        return instanceId;
    }

    public void setUserDetail(int dataNumber, String value) {
        if(dataNumber == 3) {
            mUser.setEmail(value);
        } else if(dataNumber == 2) {
            mUser.setPhone(value);
        } else if(dataNumber == 1) {
            mUser.setName(value);
        }
    }

    /**
     * Receives a call from editPresenter upon user's button click.
     * Updates user data in db.
     */
    public void updateUserDb() {
        final String userUid = mUser.getUid();
        mDatabaseReference.child("users").child(userUid).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //DATA ALREADY EXISTS
                        String oldMail = String.valueOf(dataSnapshot.child("email").getValue());
                        if(!oldMail.equals(mUser.getEmail())) {
                            resetUserAuthMail(mUser.getEmail());
                        }
                        mDatabaseReference.child("users").child(userUid).setValue(mUser);
                        mPresenterEdit.sendToastRequestToView(2);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("warning", "oncancelled");
                    }
                });
    }

    private void resetUserAuthMail(String newmail) {
        FirebaseUser loggedUser = FirebaseAuth.getInstance().getCurrentUser();
        loggedUser.updateEmail(newmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("MAILRESET", "Succesful");
                        }
                    }
                });
    }

    public static boolean isValidEmailAddress(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
