package com.example.mapfitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FitnessActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ArrayList<ActivityInformation> activeUsers;
    String email;
    Spinner spinner;
    ArrayList<String> list;
    TextView workoutText;
    String user;
    DatabaseReference workoutLog;
    String workout;
    boolean created;
    ListView workoutList;
    ArrayList<ActivityInformation> workoutInformation;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d("workoutval", workout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user = bundle.getString("USER");
            workout = bundle.getString("WORKOUT");
            created = bundle.getBoolean("CREATED");
        }
        else {
            created = false;
        }
        setContentView(R.layout.activity_fitness);
        workoutText = findViewById(R.id.workoutText);
        spinner = findViewById(R.id.spinner);
        workoutList = findViewById(R.id.workoutList);
        list = new ArrayList<>();
        list.add("");
        list.add("Walking");
        list.add("Jogging");
        list.add("Running");
        list.add("Biking");
        list.add("Basketball");
        list.add("Soccer");
        list.add("Football");
        list.add("Volleyball");
        list.add("Swimming");
        list.add("Yoga");
        list.add("HIIT");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("activeusers");
        activeUsers = new ArrayList<>();
        workoutInformation = new ArrayList<>();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser.getEmail() != null)
            email = firebaseUser.getEmail();
        Log.d(email, "CURRENTEMAIL");


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        spinner.setAdapter(arrayAdapter);
        if (workout != null) {
            int pos = 0;
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i).equals(workout)) {
                    pos = i;
                    Log.d("YOPOSITION", String.valueOf(pos));
                }
            }
            spinner.setSelection(pos);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                workout = list.get(position);
                if (workout.length() > 0) {
                    databaseReference.child(user).child("workoutType").setValue(workout);

                    if (!created) {
                        workoutLog = FirebaseDatabase.getInstance().getReference("workouts");
                        String workoutUser = workoutLog.push().getKey();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy   hh:mm aa", Locale.getDefault());
                        String currentTimestamp = sdf.format(new Date());
                        ActivityInformation activityInformation = new ActivityInformation(email, 0, 0, user, null, workout, currentTimestamp);
                        workoutLog.child(workoutUser).setValue(activityInformation);
                        created = true;
                    }
                }
                if (workoutLog != null) {
                    workoutLog.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            workoutInformation.clear();
                            for (DataSnapshot d : snapshot.getChildren()) {
                                ActivityInformation activityInformation = d.getValue(ActivityInformation.class);

                                if (activityInformation.getEmail() != null) {
                                    if (activityInformation.getEmail().equals(email))
                                        workoutInformation.add(activityInformation);
                                }
                            }
                            ListAdapter listAdapter = new ListAdapter(FitnessActivity.this, R.layout.adapter_list, workoutInformation);
                            workoutList.setAdapter(listAdapter);

                            workoutList.setOnItemClickListener(new AdapterView.OnItemClickListener() { //onClickListener for ListView
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Alert i = new Alert(workoutInformation.get(position));
                                    i.show(getSupportFragmentManager(), "popup");
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.map_nav) {
            Intent mapActivity = new Intent(FitnessActivity.this, MapActivity.class);
            mapActivity.putExtra("WORKOUT", workout);
            mapActivity.putExtra("CREATED", created);
            startActivity(mapActivity);
        }
        if (id == R.id.signout_nav)
        {
            mAuth.getCurrentUser().delete();
            mAuth.signOut();
            Intent signOut = new Intent(FitnessActivity.this, MainActivity.class);
            startActivity(signOut);
        }
        return super.onOptionsItemSelected(item);
    }

    public class ListAdapter extends ArrayAdapter<ActivityInformation> { //custom ArrayAdapter for ListView

        Context mainContext;
        int xml;
        List<ActivityInformation> workouts;

        public ListAdapter(@NonNull Context context, int resource, @NonNull List<ActivityInformation> objects) {
            super(context, resource, objects);
            mainContext = context;
            xml = resource;
            workouts = objects;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) mainContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterView = layoutInflater.inflate(xml, null);

            TextView timeText = adapterView.findViewById(R.id.id_adapter_timestamp);
            TextView workText = adapterView.findViewById(R.id.id_adapter_workout);
            workText.setText(workouts.get(position).getWorkoutType());
            timeText.setText(workouts.get(position).getTimestamp());
            return adapterView;

        }


    }

    public class Alert extends AppCompatDialogFragment { //custom object class for AlertDialog

        ActivityInformation activityInformation;


        public Alert(ActivityInformation a) {
            activityInformation = a;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(activityInformation.getEmail()).setMessage(activityInformation.getWorkoutType() + " Workout").setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            Log.d("SUCCESS", "CREATED");
            return alert.create();
        }
    }
}