package com.example.cctvapp;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Spinner dataSpinner;
    private ArrayAdapter<String> spinnerAdapter;
    private VideoView videoView;
    private List<String> spinnerData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSpinner = findViewById(R.id.dataSpinner);
        videoView = findViewById(R.id.videoView);

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerData);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataSpinner.setAdapter(spinnerAdapter);

        dataSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = spinnerData.get(position);
                if (selectedItem.startsWith("Video URL: ")) {
                    String videoUrl = selectedItem.replace("Video URL: ", "");
                    playVideo(videoUrl);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무 작업을 하지 않음
            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM Log", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM Log", "Current token: " + token);
                });
    }
    public void loadData(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataRef = database.getReference("Detected"); // Firebase 데이터베이스 경로

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                spinnerData.clear(); // 스피너 데이터 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String timestamp_webcam = snapshot.child("timestamp_webcam").getValue(String.class);
                    String videoUrl = snapshot.child("video_url").getValue(String.class);

                    spinnerData.add("Timestamp: " + timestamp_webcam);
                    spinnerData.add("Video URL: " + videoUrl);
                }

                spinnerAdapter.notifyDataSetChanged(); // 스피너 업데이트
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "데이터를 불러오는 중 오류 발생: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void playVideo(String videoUrl) {
        Uri videoUri = Uri.parse(videoUrl);
        videoView.setVideoURI(videoUri);
        videoView.start();
    }
}

