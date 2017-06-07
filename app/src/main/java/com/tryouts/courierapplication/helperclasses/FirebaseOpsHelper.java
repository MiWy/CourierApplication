package com.tryouts.courierapplication.helperclasses;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tryouts.courierapplication.interactors.CurrentOrderInteractor;
import com.tryouts.courierapplication.interactors.PreviousOrderInteractor;
import com.tryouts.courierapplication.items.NewOrder;
import com.tryouts.courierapplication.items.OrderReceived;
import com.tryouts.courierapplication.R;
import java.util.ArrayList;


public class FirebaseOpsHelper {

    private FirebaseAuth mAuth;
    private NewOrder mNewOrder;
    private CurrentOrderInteractor mCurrentOrderInteractor;
    private ArrayList<OrderReceived> mOrdersList;
    private OrderReceived mOrderReceived;
    private static final String ORDER_TAKEN = "taken";
    private static final String ORDER_FINISH = "finished";
    private static final String ORDER_CANCEL = "canceled";


    public FirebaseOpsHelper() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseOpsHelper(CurrentOrderInteractor interactor) {
        mAuth = FirebaseAuth.getInstance();
        this.mCurrentOrderInteractor = interactor;
    }

    private FirebaseUser getCurrentUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        return auth.getCurrentUser();
    }

    private String getCurrentUserUid() {
        return getCurrentUser().getUid();
    }

    private String getCurrentUserToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    private void sendNewOrderToDbWithTimestampAndPhone() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("users").child(getCurrentUserUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    mNewOrder.setPhoneNumber(String.valueOf(dataSnapshot.child("phone").getValue()));
                    mNewOrder.setTimeStamp(ServerValue.TIMESTAMP);
                    sendNewOrderToDb(dbRef);
                } else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addDbDataToOrderAndSend(NewOrder newOrder) {
        this.mNewOrder = newOrder;
        mNewOrder.setUserToken(getCurrentUserToken());
        mNewOrder.setCustomersUid(getCurrentUserUid());
        sendNewOrderToDbWithTimestampAndPhone();
    }

    private void sendNewOrderToDb(DatabaseReference dbRef) {
        dbRef.child("orders").child(mNewOrder.getUserTimeStamp()).setValue(mNewOrder);
    }

    /**
     * METHODS FOR CURRENTORDERFRAGMENT
     */

    /**
     * Looks for order in db based on given timestamp, then
     * gets it and sends it to the next method.
     * @param orderTimestamp
     */
    public void getOrderFromDbWithTimestamp(String orderTimestamp) {
        mOrderReceived = new OrderReceived();
        long orderTimestampLong = Long.parseLong(orderTimestamp);
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("orders").orderByChild("timeStamp").equalTo(orderTimestampLong)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot order : dataSnapshot.getChildren()) {
                        mOrderReceived = order.getValue(OrderReceived.class);
                        mCurrentOrderInteractor.setOrderUserTimeStamp(mOrderReceived.getUserTimeStamp());
                    }
                    checkIfPhoneNumberAndSend(mOrderReceived);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Send order back to fragment's main interactor.
     * @param order
     */
    private void onCurrentOrderReceived(OrderReceived order) {
        mCurrentOrderInteractor.onReceivedOrderData(order);
    }

    /**
     * If order's status is ,,taken'' or further, set courier's
     * phone number as order's phone number and send order data to the onCurrentOrderReceived().
     * @param orderReceived
     */
    private void checkIfPhoneNumberAndSend(OrderReceived orderReceived) {
        mOrderReceived = orderReceived;
        if(!mOrderReceived.getType().equals("new")) {
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("users").orderByChild("uid").equalTo(mOrderReceived.getWhoDelivers())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                for(DataSnapshot order : dataSnapshot.getChildren()) {
                                    mOrderReceived.setPhoneNumber(String.valueOf(
                                            order.child("phone").getValue()));
                                }
                                onCurrentOrderReceived(mOrderReceived);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        } else {
            onCurrentOrderReceived(mOrderReceived);
        }
    }

    public void onCheckWhetherUserIsACourier() {
        getUserRole(getCurrentUser());
    }

    private void getUserRole(FirebaseUser user) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("users").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            String role = String.valueOf(dataSnapshot.child("role").getValue());
                            if(role.equals("courier")) {
                                mCurrentOrderInteractor.onUserRoleReceived(true);
                            } else {
                                mCurrentOrderInteractor.onUserRoleReceived(false);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    /**
     * Changes the order status in the database, according to the int value
     * 1 = taken, 2 = finish, 3 = cancel
     * First checks whether order is already taken by someone else.
     * If not, sends it to the method that performs the actual db update.
     */

    public void checkAndChangeOrderStatus(final String userOrderTimestamp, final int whatStatus) {
        DatabaseReference checkingReference = FirebaseDatabase.getInstance().getReference();
        checkingReference.child("orders").child(userOrderTimestamp).child("whoDelivers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            String courierUidInDb = (String) dataSnapshot.getValue();
                            if(!courierUidInDb.equals(getCurrentUserUid()) && !courierUidInDb.equals("")) {
                                mCurrentOrderInteractor.sendToastCall(mCurrentOrderInteractor.getInteractorContext().getString(R.string.toast_order_other_courier));
                                mCurrentOrderInteractor.hideProgressDialog();
                            } else {
                                changeOrderStatus(userOrderTimestamp, whatStatus, courierUidInDb);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void changeOrderStatus(String userOrderTimestamp, int whatStatus, String courierUidInDb) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        if(whatStatus == 1) {
            changeOrderToTaken(dbRef, userOrderTimestamp);
        } else if(whatStatus == 2) {
            if(courierUidInDb.equals(getCurrentUserUid())) {
                dbRef.child("orders").child(userOrderTimestamp).child("type").setValue(ORDER_FINISH);
                mCurrentOrderInteractor.removeOrderFromSharedPreferences();
                mCurrentOrderInteractor.sendToastCall(mCurrentOrderInteractor.getInteractorContext().getString(R.string.toast_order_finished));
            } else {
                mCurrentOrderInteractor.sendToastCall(mCurrentOrderInteractor.getInteractorContext().getString(R.string.toast_first_take_order));
            }
        } else if(whatStatus == 3) {
            if (courierUidInDb.equals(getCurrentUserUid())) {
                dbRef.child("orders").child(userOrderTimestamp).child("type").setValue(ORDER_CANCEL);
                mCurrentOrderInteractor.removeOrderFromSharedPreferences();
                mCurrentOrderInteractor.sendToastCall(mCurrentOrderInteractor.getInteractorContext().getString(R.string.toast_order_canceled));
            } else {
                mCurrentOrderInteractor.sendToastCall(mCurrentOrderInteractor.getInteractorContext().getString(R.string.toast_first_take_order));
            }
        }
    }

    private void changeOrderToTaken(final DatabaseReference dbRef, final String userOrderTimestamp) {
        dbRef.child("users").child(getCurrentUserUid()).child("name").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            dbRef.child("orders").child(userOrderTimestamp).child("type").setValue(ORDER_TAKEN);
                            dbRef.child("orders").child(userOrderTimestamp).child("whoDelivers").setValue(getCurrentUserUid());
                            dbRef.child("orders").child(userOrderTimestamp).child("whoDeliversName").setValue(String.valueOf(dataSnapshot.getValue()));
                            mCurrentOrderInteractor.sendToastCall(mCurrentOrderInteractor.getInteractorContext().getString(R.string.toast_order_taken));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    public void onPreviousOrdersCall(final PreviousOrderInteractor interactor) {
        mOrdersList = new ArrayList<>();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("orders").orderByChild("customersUid").equalTo(getCurrentUserUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            for(DataSnapshot order : dataSnapshot.getChildren()) {
                                OrderReceived orderFromDb = order.getValue(OrderReceived.class);
                                mOrdersList.add(orderFromDb);
                                interactor.onReceivedPreviousOrdersData(mOrdersList);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
