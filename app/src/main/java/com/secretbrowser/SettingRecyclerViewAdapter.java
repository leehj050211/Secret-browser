package com.secretbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SettingRecyclerViewAdapter extends RecyclerView.Adapter<SettingRecyclerViewAdapter.ViewHolder> {

    private Activity activity;
    private List<SettingTitle> Settingtitle;

    public SettingRecyclerViewAdapter(Activity activity, List<SettingTitle> Settingtitle) {
        this.activity = activity;
        this.Settingtitle = Settingtitle;
    }

    @Override
    public int getItemCount() {
        return Settingtitle.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, type, value;
        Button btn_mode;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.setting_title);
            subtitle = (TextView) itemView.findViewById(R.id.setting_subtitle);
            type = (TextView) itemView.findViewById(R.id.setting_type);
            value = (TextView) itemView.findViewById(R.id.setting_value);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                }
            });
            btn_mode = (Button) itemView.findViewById(R.id.setting_btn_mode);
            btn_mode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int index = getAdapterPosition();
                    SettingActivity settingActivity = new SettingActivity();
                    final SettingData settingdata = new SettingData();
                    final EditText alertinput = new EditText(activity);
                    if (index == 0) {
                        if (Boolean.parseBoolean(settingdata.getSetting(0)) == false) {
                            settingdata.setSetting(index, "true");
                        } else {
                            settingdata.setSetting(index, "false");
                        }
                        settingdata.setplz_restart(true);
                        value.setText("현재 값 " + settingdata.getSetting(index));
                    } else if (index == 1) {
                        new AlertDialog.Builder(activity, R.style.DialogTheme)
                                .setTitle("탭 그리드 레이아웃 표시 개수")
                                .setMessage("\n한 줄당 표시되는 탭의 개수를 입력해주세요 예:2")
                                .setView(alertinput)
                                .setPositiveButton("입력 완료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        settingdata.setSetting(index, alertinput.getText().toString());
                                        value.setText("현재 값 " + settingdata.getSetting(index));
                                    }
                                })
                                .show();
                    } else if (index == 2) {
                        new AlertDialog.Builder(activity, R.style.DialogTheme)
                                .setTitle("웹 에러 발생시 반복 연결")
                                .setMessage("\n다시 연결을 시도할 속도를 입력해주세요 0으로 설정하면 비활성화 됩니다 단위는 ms 이며 1000ms는 1s 입니다 예:100(0.1초)")
                                .setView(alertinput)
                                .setPositiveButton("입력 완료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        settingdata.setSetting(index, alertinput.getText().toString());
                                        settingdata.setplz_restart(true);
                                        value.setText("현재 값 " + settingdata.getSetting(index));
                                    }
                                })
                                .show();
                    } else if (index == 3) {
                        if (Boolean.parseBoolean(settingdata.getSetting(3)) == false) {
                            settingdata.setSetting(index, "true");
                        } else {
                            settingdata.setSetting(index, "false");
                        }
                        settingdata.setplz_restart(true);
                        value.setText("현재 값 " + settingdata.getSetting(index));
                    } else if (index == 4) {
                    if (Boolean.parseBoolean(settingdata.getSetting(4)) == false) {
                        settingdata.setSetting(index, "true");
                    } else {
                        settingdata.setSetting(index, "false");
                    }
                    settingdata.setplz_restart(true);
                    value.setText("현재 값 " + settingdata.getSetting(index));
                }
                }
            });
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // 재활용 되는 View가 호출, Adapter가 해당 position에 해당하는 데이터를 결합
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SettingTitle title = Settingtitle.get(position);

        // 데이터 결합
        holder.title.setText(title.getSettingTitle());
        holder.subtitle.setText(title.getSettingSubTitle());
        if (title.getSettingType() == "int") {
            holder.type.setText(title.getSettingType() + "(정수형)");
        }else if (title.getSettingType() == "double") {
            holder.type.setText(title.getSettingType() + "(실수형)");
        }else if (title.getSettingType() == "boolean") {
            holder.type.setText(title.getSettingType() + "(논리형)");
        }else if (title.getSettingType() == "string") {
            holder.type.setText(title.getSettingType() + "(문자형)");
        }
        holder.value.setText("현재 값 " + title.getSettingValue());
    }

    private void removeItemView(int position) {
        Settingtitle.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, Settingtitle.size()); // 지워진 만큼 다시 채워넣기.
    }
}