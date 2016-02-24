package com.example.alerther.alerther;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MarkerActivity extends AppCompatActivity {
    Intent intent;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        intent = getIntent();
        latLng = (LatLng)intent.getExtras().getParcelable("latlong");
        TextView incidentDate = (TextView)findViewById(R.id.incidentdate);
        TextView incidentTime = (TextView)findViewById(R.id.incidenttime);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat tf = new SimpleDateFormat("hh:mm a");
        String formattedDate = df.format(c.getTime());
        String formattedTime = tf.format(new Date());
        incidentDate.setText(formattedDate);
        incidentTime.setText(formattedTime);
    }

    public void incidentdateClick(View v)
    {
        final View dialogView = View.inflate(this, R.layout.fragment_date, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        dialogView.findViewById(R.id.date_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth());
                TextView incidentDate = (TextView)findViewById(R.id.incidentdate);
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(calendar.getTime());
                incidentDate.setText(formattedDate);
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    public void incidenttimeClick(View v)
    {
        final View dialogView = View.inflate(this, R.layout.fragment_time, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        dialogView.findViewById(R.id.time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
                TextView incidentTime = (TextView)findViewById(R.id.incidenttime);
                SimpleDateFormat tf = new SimpleDateFormat("hh:mm a");
                Date today = new Date();
                String formattedTime = tf.format(new Date(today.getYear(), today
                .getMonth(), today.getDay(), timePicker.getHour(), timePicker.getMinute(), 0));
                incidentTime.setText(formattedTime);
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    public void onSubmitClick(View v){
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;
        EditText incidentView = (EditText)findViewById(R.id.incident);
        Editable incident = incidentView.getText();

        EditText tipsView = (EditText)findViewById(R.id.tips);
        Editable tips = tipsView.getText();

        TextView incidentDate = (TextView)findViewById(R.id.incidentdate);
        TextView incidentTime = (TextView)findViewById(R.id.incidenttime);

        final IncidentItem incidentItem = new IncidentItem();
        incidentItem.mLatitude = latitude;
        incidentItem.mLongitude = longitude;
//        incidentItem.mCreatedDate = new java.util.Date();
        incidentItem.mDescription = incident.toString();
        incidentItem.mTips = tips.toString();
//        incidentItem.mReportedTime = new java.util.Date();
        incidentItem.mReportedTime = "2016-02-24T16:16:28.978Z";
        incidentItem.mCreatedDate = "2016-02-24T16:16:28.978Z";
        incidentItem.mUserName = "android";
        incidentItem.mid = java.util.UUID.randomUUID().toString();

        try {
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        AlertHerMobileServiceClient client = new AlertHerMobileServiceClient();
                        client.PostIncident(incidentItem);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MarkerActivity.this
                                        , MapsActivity.class);
                                startActivity(intent);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            runAsyncTask(task);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }
}
