package com.example.mapfitapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class Information extends AppCompatDialogFragment {

    ActivityInformation activityInformation;
    ActivityInformation currentUser;
    GoogleMap map;
    LatLng coordinates;

    public Information (ActivityInformation a, ActivityInformation c, GoogleMap m, LatLng coord)
    {
        activityInformation = a;
        currentUser = c;
        map = m;
        coordinates = coord;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(activityInformation.getEmail()).setMessage(activityInformation.getWorkoutType() + " Workout").setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                map.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 16));
            }
        }).setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String recipient = activityInformation.getEmail();
                String subject = "MapFit - Join Request";
                String message = "Hey, " + currentUser.getEmail() + " would like to join your " + activityInformation.getWorkoutType() + " workout!";

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ recipient});
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, message);

                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Choose an email client"));

            }
        });
        Log.d("SUCCESS", "CREATED");
        return alert.create();
    }
}
