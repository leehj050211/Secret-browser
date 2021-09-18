package com.secretbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabActivity extends AppCompatActivity {

    public static Activity tab_activity;

    int tabs, numberOfColumns = 0;
    ImageButton btn_addtab;
    Button btn_tabcopy, btn_paste;

    RecyclerView recyclerView;
    private GridLayoutManager GridLayoutManager;
    TabRecyclerViewAdapter tabRecyclerViewAdapter;
    List<Tabdata> tabdata = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        tab_activity = TabActivity.this;
        final MainActivity mainActivity = new MainActivity();
        SettingData settingdata = new SettingData();

        numberOfColumns = Integer.parseInt(settingdata.getSetting(1));
        recyclerView = findViewById(R.id.tab_recyclerView);
        GridLayoutManager = new GridLayoutManager(this, numberOfColumns);

        recyclerView.addItemDecoration(new DividerItemDecoration(this,GridLayoutManager.getOrientation()));
        recyclerView.setLayoutManager(GridLayoutManager);

        final TextView tab_num = findViewById(R.id.tab_num);
        tabs = mainActivity.sizetab_list();
        tab_num.setText(tabs + " 탭");

        // addtab 버튼을 눌렀을때
        btn_addtab = findViewById(R.id.btn_addtab);
        btn_addtab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtab();
            }
        });
        // tabcopy 버튼을 눌렀을때
        btn_tabcopy = findViewById(R.id.btn_tabcopy);
        btn_tabcopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", mainActivity.tablist().toString().substring(1, mainActivity.tablist().toString().length()-1));
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getApplication(), "탭 리스트가 복사되었습니다.",Toast.LENGTH_LONG).show();
            }
        });
        // addpaste 버튼을 눌렀을때
        btn_paste = findViewById(R.id.btn_tabpaste);
        btn_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText alertinput = new EditText(TabActivity.this);
                new AlertDialog.Builder(TabActivity.this, R.style.DialogTheme)
                        .setTitle("탭 리스트 불러오기")
                        .setMessage("\n불러올 탭 리스트를 입력해 주세요.")
                        .setView(alertinput)
                        .setPositiveButton("입력 완료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String s = alertinput.getText().toString();
                                        String[] sArr = s.split(",");
                                        mainActivity.set_tablist(new ArrayList<>(Arrays.asList(sArr)));
                                        android.widget.Toast.makeText(getApplicationContext(), "탭 리스트 불러오기 완료", android.widget.Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(TabActivity.this, TabActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                        .show();
            }
        });
        tab();
    }

    public void tab() {
        MainActivity mainActivity = new MainActivity();
        for (int i = 0; i < tabs; i++) {
            tabdata.add(new Tabdata(mainActivity.gettab_list(i), i + 1));
        }
        tabRecyclerViewAdapter = new TabRecyclerViewAdapter(this, tabdata);
        recyclerView.setAdapter(tabRecyclerViewAdapter);
    }

    public void addtab() {
        MainActivity mainActivity = new MainActivity();
        mainActivity.addtab_list("https://google.com");
        tabs = mainActivity.sizetab_list();
        tabdata.add(new Tabdata("https://google.com", tabs));
        reloadtab();
    }

    public void reloadtab() {
        MainActivity mainActivity = new MainActivity();
        final TextView tab_num = findViewById(R.id.tab_num);
        tabs = mainActivity.sizetab_list();
        tab_num.setText(tabs + " 탭");
        tabRecyclerViewAdapter.notifyDataSetChanged();
        ArrayList tab_list = mainActivity.tablist();
        setStringArrayPref("tablist" ,tab_list);
    }

    public void setStringArrayPref(String key, ArrayList<String> values) {
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        preferences = getSharedPreferences("SettingFile", MODE_PRIVATE);
        editor = preferences.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainActivity mainActivity = new MainActivity();
        ArrayList tab_list = mainActivity.tablist();
        setStringArrayPref("tablist" ,tab_list);
    }
}
