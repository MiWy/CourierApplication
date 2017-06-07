package com.tryouts.courierapplication.interactors;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.widget.ListView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tryouts.courierapplication.helperclasses.DirectionsJSONParser;
import com.tryouts.courierapplication.helperclasses.FirebaseOpsHelper;
import com.tryouts.courierapplication.items.NewOrder;
import com.tryouts.courierapplication.presenters.NewOrderPresenter;
import com.tryouts.courierapplication.R;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NewOrderInteractor {

    private NewOrderPresenter mPresenter;
    private LatLng mFromAddressLatLng;
    private LatLng mToAddressLatLng;
    private String addressFrom;
    private String addressTo;
    private PolylineOptions polylineOptions;
    private String distance;
    private String gMapKey;
    private CameraUpdate cu;
    private FragmentActivity mFragmentActivity;
    private Intent mPlacesAutocompleteIntent;
    private boolean[] additionalServices = {false, false, false};



    public NewOrderInteractor(NewOrderPresenter presenter) {
        this.mPresenter = presenter;
    }

    public boolean isOrderReadyToSubmit() {
        return mFromAddressLatLng != null && mToAddressLatLng != null && addressFrom != null
                && addressTo != null && distance != null;
    }

    public void createAndSendOrderToDb() {
        FirebaseOpsHelper fbHelper = new FirebaseOpsHelper();
        fbHelper.addDbDataToOrderAndSend(prepareNewOrderFromExistingData(createNewOrder()));
    }

    private NewOrder createNewOrder() {
        return new NewOrder();
    }

    private NewOrder prepareNewOrderFromExistingData(NewOrder newOrder) {
        newOrder.setDate(generateDateString());
        newOrder.setUserTimeStamp(generateTimestampString());
        newOrder.setDistance(getDistanceString());
        newOrder.setFromPlaceLat(String.valueOf(getFromAddressLatLng().latitude));
        newOrder.setFromPlaceLng(String.valueOf(getFromAddressLatLng().longitude));
        newOrder.setToPlaceLat(String.valueOf(getToAddressLatLng().latitude));
        newOrder.setToPlaceLng(String.valueOf(getToAddressLatLng().longitude));
        newOrder.setFrom(getAddressFrom());
        newOrder.setTo(getAddressTo());
        if(additionalServices[0]) {
            newOrder.setIsExpress("yes");
        }
        if(additionalServices[1]) {
            newOrder.setIsSuperExpress("yes");
        }
        if(additionalServices[2]) {
            newOrder.setIsCarExpress("yes");
        }
        return newOrder;
    }


    public AlertDialog.Builder sendAdditionalAlertDialog() {
        return createAdditionalAlertDialog();
    }

    private AlertDialog.Builder createAdditionalAlertDialog() {
        CharSequence[] list = new CharSequence[3];
        list[0]=mFragmentActivity.getString(R.string.neworder_express_checkbox);
        list[1]=mFragmentActivity.getString(R.string.neworder_superexpress_checkbox);
        list[2]=mFragmentActivity.getString(R.string.neworder_carexpress_checkbox);
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragmentActivity);
        builder.setTitle(mFragmentActivity.getString(R.string.neworder_alert_additional));
        builder.setMultiChoiceItems(list, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            }
        });
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView listview = ((AlertDialog) dialog).getListView();
                        additionalServices[0] = listview.isItemChecked(0);
                        additionalServices[1] = listview.isItemChecked(1);
                        additionalServices[2] = listview.isItemChecked(2);
                    }
                }
        );
        return builder;
    }

    public String getStringFromXml(int stringId) {
        if(stringId == NewOrderPresenter.RESULT_ERROR) {
            return mFragmentActivity.getString(R.string.neworder_error);
        } else {
            return mFragmentActivity.getString(R.string.neworder_placed_success);
        }
    }

    public void onPlaceAutocompleteCall(int requestCode) {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            mPlacesAutocompleteIntent = new PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .setBoundsBias(getDefaultBounds())
                    .build(mFragmentActivity);
            mPresenter.onReadyStartActivityForResult(requestCode);
        } catch (GooglePlayServicesNotAvailableException e) {
            // Handle the error.
        } catch (GooglePlayServicesRepairableException e) {
            // Handle the error.
        }
    }

    public Intent getPlacesAutocompleteIntent() {
        return mPlacesAutocompleteIntent;
    }

    public void setFragmentActivity(FragmentActivity fragmentActivity) {
        this.mFragmentActivity = fragmentActivity;
    }

    public PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public CameraUpdate getCameraUpdate() {
        return cu;
    }

    public String getDistanceString() {
        return distance;
    }

    public void onActivityResultSuccesful(int requestCode, Intent data) {
        if(requestCode == 1) {
            onAddressFromReceived(String.valueOf(PlaceAutocomplete.getPlace(mFragmentActivity, data).getAddress()));
            onFromLatLngReceived(PlaceAutocomplete.getPlace(mFragmentActivity, data).getLatLng().latitude,
                    PlaceAutocomplete.getPlace(mFragmentActivity, data).getLatLng().longitude);
            sendFromAddressString();
        } else if(requestCode == 2) {
            onAddressToReceived(String.valueOf(PlaceAutocomplete.getPlace(mFragmentActivity, data).getAddress()));
            onToLatLngReceived(PlaceAutocomplete.getPlace(mFragmentActivity, data).getLatLng().latitude,
                    PlaceAutocomplete.getPlace(mFragmentActivity, data).getLatLng().longitude);
            sendToAddressString();
        }
    }

    private void sendFromAddressString() {
        mPresenter.onReadyFromText(getAddressFromString());
    }

    private void sendToAddressString() {
        mPresenter.onReadyToText(getAddressToString());
    }

    private String getAddressFromString() {
        return addressFrom;
    }

    private String getAddressToString() {
        return addressTo;
    }

    private void onAddressFromReceived(String addressFrom) {
        this.addressFrom = addressFrom;
    }

    private void onAddressToReceived(String addressTo) {
        this.addressTo = addressTo;
    }

    private void onFromLatLngReceived(double latitude, double longitude) {
        this.mFromAddressLatLng = new LatLng(latitude, longitude);
    }

    private void onToLatLngReceived(double latitude, double longitude) {
        this.mToAddressLatLng = new LatLng(latitude, longitude);
    }

    public boolean areFromToReceived() {
        return mFromAddressLatLng != null && mToAddressLatLng != null;
    }

    /**
     * Returns default coordinates (city of Poznań)
     * Called from NewOrderPresenter.
     * @return double
     */
    public double getDefaultLatitude() {
        return 52.4166667;
    }

    public double getDefaultLongitude() {
        return 16.9666667;
    }

    /**
     * Returns default LatLngBounds (area around Poznań city)
     * Called from NewOrderPresenter.
     * @return LatLngBounds
     */
    private LatLngBounds getDefaultBounds() {
        return new LatLngBounds(new LatLng(52.22843, 17.27617), new LatLng(52.61201, 16.57794));
    }


    /**
     * GENERATORS
     */

    private String generateDateString() {
        return new SimpleDateFormat(
                "yyyy-MM-dd HH-mm", Locale.US).
                format(Calendar.getInstance().
                        getTime());
    }

    private String generateTimestampString() {
        return new SimpleDateFormat(
                "yyyyMMddHHmmssSSS", Locale.US)
                .format(Calendar.getInstance()
                        .getTime());
    }

    /**
     * SETTERS AND GETTERS
     *
     */

    public String getNotReadyString() {
        return getFragmentActivity().getString(R.string.neworder_placed_unsuccesful);
    }


    private LatLng getFromAddressLatLng() {
        return mFromAddressLatLng;
    }

    private LatLng getToAddressLatLng() {
        return mToAddressLatLng;
    }

    private String getAddressFrom() {
        return addressFrom;
    }

    private String getAddressTo() {
        return addressTo;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    private FragmentActivity getFragmentActivity() {
        return mFragmentActivity;
    }

    //Method to download JSON data from url


    public void setGMapKey(String gMapKey) {
        this.gMapKey = gMapKey;
    }

    private String getMapsApiDirectionsUrl() {
        String origin = "origin=" + mFromAddressLatLng.latitude + "," + mFromAddressLatLng.longitude;
        String destination = "destination=" + mToAddressLatLng.latitude + "," + mToAddressLatLng.longitude;
        String sensor = "sensor=false";
        String travelingMode = "mode=bicycling";
        String output = "json";
        String key = "key=" + gMapKey;
        //String params = origin + "&" + destination + "&" + travelingMode + "&" + sensor + "&" + key;
        String params = origin + "&" + destination + "&" + sensor + "&" + travelingMode;
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }


    public void prepareMapVariables() {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(getMapsApiDirectionsUrl());
    }

    // Method to download JSON data from url
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){

        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
            }
            return data;
        }
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            polylineOptions = null;
            distance = "";
            MarkerOptions markerOptions = new MarkerOptions();
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<>();
                polylineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    if(j==0) {
                        distance = point.get("distance");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                polylineOptions.addAll(points);
                polylineOptions.width(2);
                polylineOptions.color(Color.RED);
            }
            if(result.size()<1){
                return;
            }

            // Drawing polyline in the Google Map for the i-th route
            mPresenter.onMapPolylineReady();
            // Set bounds according to the path in polyline.
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(LatLng point : points) {
                builder.include(point);
            }
            LatLngBounds bounds = builder.build();
            int padding = 20;
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mPresenter.onCameraUpdateReady();
            mPresenter.onMapUpdatedShowDistance();
        }
    }

}
