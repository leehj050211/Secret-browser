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
                        value.setText("?????? ??? " + settingdata.getSetting(index));
                    } else if (index == 1) {
                        new AlertDialog.Builder(activity, R.style.DialogTheme)
                                .setTitle("??? ????????? ???????????? ?????? ??????")
                                .setMessage("\n??? ?????? ???????????? ?????? ????????? ?????????????????? ???:2")
                                .setView(alertinput)
                                .setPositiveButton("?????? ??????", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        settingdata.setSetting(index, alertinput.getText().toString());
                                        value.setText("?????? ??? " + settingdata.getSetting(index));
                                    }
                                })
                                .show();
                    } else if (index == 2) {
                        new AlertDialog.Builder(activity, R.style.DialogTheme)
                                .setTitle("??? ?????? ????????? ?????? ??????")
                                .setMessage("\n?????? ????????? ????????? ????????? ?????????????????? 0?????? ???????????? ???????????? ????????? ????????? ms ?????? 1000ms??? 1s ????????? ???:100(0.1???)")
                                .setView(alertinput)
                                .setPositiveButton("?????? ??????", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        settingdata.setSetting(index, alertinput.getText().toString());
                                        settingdata.setplz_restart(true);
                                        value.setText("?????? ??? " + settingdata.getSetting(index));
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
                        value.setText("?????? ??? " + settingdata.getSetting(index));
                    } else if (index == 4) {
                    if (Boolean.parseBoolean(settingdata.getSetting(4)) == false) {
                        settingdata.setSetting(index, "true");
                    } else {
                        settingdata.setSetting(index, "false");
                    }
                    settingdata.setplz_restart(true);
                    value.setText("?????? ??? " + settingdata.getSetting(index));
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

    // ????????? ?????? View??? ??????, Adapter??? ?????? position??? ???????????? ???????????? ??????
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SettingTitle title = Settingtitle.get(position);

        // ????????? ??????
        holder.title.setText(title.getSettingTitle());
        holder.subtitle.setText(title.getSettingSubTitle());
        if (title.getSettingType() == "int") {
            holder.type.setText(title.getSettingType() + "(?????????)");
        }else if (title.getSettingType() == "double") {
            holder.type.setText(title.getSettingType() + "(?????????)");
        }else if (title.getSettingType() == "boolean") {
            holder.type.setText(title.getSettingType() + "(?????????)");
        }else if (title.getSettingType() == "string") {
            holder.type.setText(title.getSettingType() + "(?????????)");
        }
        holder.value.setText("?????? ??? " + title.getSettingValue());
    }

    private void removeItemView(int position) {
        Settingtitle.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, Settingtitle.size()); // ????????? ?????? ?????? ????????????.
    }
}