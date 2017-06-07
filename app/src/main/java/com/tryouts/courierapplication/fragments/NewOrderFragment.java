package com.tryouts.courierapplication.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tryouts.courierapplication.presenters.NewOrderPresenter;
import com.tryouts.courierapplication.R;



public class NewOrderFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private NewOrderPresenter mPresenter;
    private GoogleMap mGoogleMap;
    private TextView mDistanceView;
    private Button mCalculateButton;
    private Button mCallButton;
    private Button mAdditionalButton;
    private TextView placesFrom;
    private TextView placesTo;
    private Intent mOnActivityResultIntent;


    public NewOrderFragment() {
    }

    public static Fragment newInstance() {
        return new NewOrderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_neworder, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mPresenter = new NewOrderPresenter(this);
        mPresenter.setGMapsUrl(getString(R.string.google_maps_key));
        mPresenter.sendFragmentActivityToInteractor();

        mDistanceView = (TextView) view.findViewById(R.id.neworder_textview_summary);
        mCalculateButton = (Button) view.findViewById(R.id.neworder_button_calculate);
        mAdditionalButton = (Button) view.findViewById(R.id.neworder_button_additional);
        mCallButton = (Button) view.findViewById(R.id.neworder_button_call);
        placesFrom = (TextView) view.findViewById(R.id.place_autocomplete_from);
        placesTo = (TextView) view.findViewById(R.id.place_autocomplete_to);
        mMapView = (MapView) view.findViewById(R.id.neworder_map);
        mMapView.onCreate(savedInstanceState);

        mPresenter.onMapAsyncCall();
        setListeners();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mOnActivityResultIntent = data;
        mPresenter.onActivityResultCalled(requestCode, resultCode);
    }

    public Intent getOnActivityResultIntent() {
        return mOnActivityResultIntent;
    }

    public void setDistanceView(String distance) {
        mDistanceView.setText(getString(R.string.neworder_summary_start) + " " + distance +
        getString(R.string.neworder_summary_mid));
    }

    public void setFromText(String address) {
        placesFrom.setText(address);
    }

    public void setToText(String address) {
        placesTo.setText(address);
    }

    public void moveCameraTo(double lat, double lng) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(10)
                .build()));
    }

    // STARTING AGAIN FFS

    private void setListeners() {
        placesFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForPlaceAutocompleteCall(1);
            }
        });
        placesTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForPlaceAutocompleteCall(2);
            }
        });
        mAdditionalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForAdditionalServices();
            }
        });
        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForCalculate();
            }
        });
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForCall();
            }
        });
    }

    public FragmentActivity getParentActivity() {
        return getActivity();
    }

    public void mapAsyncCall() {
        mMapView.getMapAsync(this);
    }

    public void setMapClear() {
        mGoogleMap.clear();
    }

    public void onMapPolylineAdded(PolylineOptions polylineOptions) {
        mGoogleMap.addPolyline(polylineOptions);
    }

    public void animateCamera(CameraUpdate cu) {
        mGoogleMap.moveCamera(cu);
        mGoogleMap.animateCamera(cu, 1000, null);
    }

    public void makeToast(String message) {
        Toast toast = Toast.makeText(getContext(),
                message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void showAlertDialog(AlertDialog.Builder builder) {
        builder.show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        mPresenter.setMapView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public final void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public final void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public final void onPause() {
        super.onPause();
        mMapView.onPause();
    }

}
