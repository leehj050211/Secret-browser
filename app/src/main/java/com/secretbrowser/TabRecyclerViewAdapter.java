package com.secretbrowser;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TabRecyclerViewAdapter extends RecyclerView.Adapter<TabRecyclerViewAdapter.ViewHolder> {

    private Activity activity;
    private List<Tabdata> Tabdata;

    public TabRecyclerViewAdapter(Activity activity, List<Tabdata> Tabdata) {
        this.activity = activity;
        this.Tabdata = Tabdata;
    }

    @Override
    public int getItemCount() {
        return Tabdata.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RelativeLayout tab;
        ImageButton btn_del;
        public ViewHolder(View itemView) {
            super(itemView);
            MainActivity mainActivity = (MainActivity) MainActivity.main_activity;
            tab = itemView.findViewById(R.id.tab);
            title = (TextView) itemView.findViewById(R.id.tab_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    TabActivity tabActivity = (TabActivity) TabActivity.tab_activity;
                    MainActivity mainActivity = (MainActivity) MainActivity.main_activity;
                    if (mainActivity.gettab() == index) {
                        tabActivity.finish();
                    } else {
                        mainActivity.settab(index);
                        mainActivity.web_load();
                        tabActivity.finish();
                    }
                }
            });
            btn_del = (ImageButton) itemView.findViewById(R.id.tab_btn_del);
            btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    TabActivity tabActivity = (TabActivity) TabActivity.tab_activity;
                    MainActivity mainActivity = (MainActivity) MainActivity.main_activity;
                    mainActivity.removetab_list(index);
                    if (mainActivity.gettab() < index) {
                        mainActivity.settab(mainActivity.gettab());
                    } else {
                        if (mainActivity.gettab() == 0) {
                            mainActivity.settab(0);
                        } else {
                            mainActivity.settab(mainActivity.gettab() - 1);
                            mainActivity.web_load();
                        }
                    }
                    tabActivity.reloadtab();
                    removeItemView(index);
                }
            });
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // 재활용 되는 View가 호출, Adapter가 해당 position에 해당하는 데이터를 결합
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Tabdata data = Tabdata.get(position);
        MainActivity mainActivity = (MainActivity) MainActivity.main_activity;

        // 데이터 결합
        holder.title.setText(data.getTabTitle());
        if (mainActivity.gettab() == position) {
            holder.tab.setBackgroundResource(R.drawable.tab_focus);
        } else {
            holder.tab.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        }
    }

    private void removeItemView(int position) {
        Tabdata.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, Tabdata.size()); // 지워진 만큼 다시 채워넣기.
    }
}