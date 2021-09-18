package com.secretbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    public static Activity setting_activity;

    int settings, numberOfColumns = 0;
    Button btn_reset;

    RecyclerView recyclerView;
    private GridLayoutManager GridLayoutManager;
    SettingRecyclerViewAdapter settingRecyclerViewAdapter;
    List<SettingTitle> settingtitle = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final MainActivity mainActivity = new MainActivity();

        recyclerView = findViewById(R.id.setting_recyclerView);
        numberOfColumns = 1;
        GridLayoutManager = new GridLayoutManager(this, numberOfColumns);

        recyclerView.addItemDecoration(new DividerItemDecoration(this,GridLayoutManager.getOrientation()));
        recyclerView.setLayoutManager(GridLayoutManager);
        settings = mainActivity.getsetting_title_list_length();

        // reset 버튼을 눌렀을때
        btn_reset = findViewById(R.id.setting_btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reset
            }
        });
        setting();
    }

    public void setting() {
        MainActivity mainActivity = new MainActivity();
        SettingData settingdata = new SettingData();
        for (int i = 0; i < settings; i++) {
            settingtitle.add(new SettingTitle(mainActivity.getsetting_title_list(i), mainActivity.getsetting_subtitle_list(i), mainActivity.getsetting_type_list(i), settingdata.getSetting(i), i + 1));
        }
        reloadsetting();
    }

    public void reloadsetting() {
        settingRecyclerViewAdapter = new SettingRecyclerViewAdapter(this, settingtitle);
        recyclerView.setAdapter(settingRecyclerViewAdapter);
    }

    @Override
    public void onBackPressed() {
        final SettingData settingdata = new SettingData();
        if (settingdata.getplz_restart() == false) {
            finish();
        } else {
            new AlertDialog.Builder(SettingActivity.this, R.style.DialogTheme)
                    .setTitle("설정한 내용은 앱을 재시작해야 적용됩니다\n재시작 하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlg, int sumthin) {
                            settingdata.setplz_restart(false);
                            ActivityCompat.finishAffinity(SettingActivity.this);
                        }
                    })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlg, int sumthin) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SettingData settingdata = new SettingData();
        settingdata.save_setting(SettingActivity.this);
    }
}
