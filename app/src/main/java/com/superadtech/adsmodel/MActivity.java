package com.superadtech.adsmodel;

import static com.superadtech.modids.MACT.CheckInternet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.superadtech.modids.GetLoadAsds;

public class MActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m);

        CheckInternet(this, "7CAB452364AEDE88F168481C95DCF68D4B9EB84AEA401F2DF05445CF7F5CBE5239063AAE1755236B643FAEB3E545B084A34C3FE276B4BF0586798FC36F0FF1C0", new GetLoadAsds.MySpalshCallback() {
            @Override
            public void onSuccessSplashMethod() {
                    startActivity(new Intent(MActivity.this,MainActivity2.class));
            }
        });
    }
}