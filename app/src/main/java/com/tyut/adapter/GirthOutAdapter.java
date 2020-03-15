package com.tyut.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tyut.HomeActivity;
import com.tyut.R;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.vo.GirthVO;
import com.tyut.widget.GirthPopUpWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GirthOutAdapter extends RecyclerView.Adapter<GirthOutAdapter.LinearViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;

    private Map<String, List<GirthVO>> girthMap;
    private List<String> keys = new ArrayList<>();
    public GirthOutAdapter(Context context, Map<String, List<GirthVO>> girthMap, OnItemClickListener listener){
        this.mContext = context;
        this.mListener = listener;
        this.girthMap = girthMap;
        Set<String> set = girthMap.keySet();
        for (String key : set) {
            keys.add(key);
        }
    }
    @NonNull
    @Override
    public GirthOutAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //传入Item布局
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_girthout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull GirthOutAdapter.LinearViewHolder holder, final int position) {

        if(keys.size() > position){

            holder.girthDate.setText(keys.get(position));

            final List<GirthVO> vos = girthMap.get(keys.get(position));
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            holder.recyclerView.addItemDecoration(new RecycleViewDivider(mContext,  LinearLayoutManager.VERTICAL));
            holder.recyclerView.setAdapter(new GirthInAdapter(mContext, vos, new GirthInAdapter.OnItemClickListener() {
                @Override
                public void onClick(final int position) {
                    final GirthPopUpWindow girthPopUpWindow = new GirthPopUpWindow(mContext,
                            vos.get(position).getType(),
                            Float.parseFloat(vos.get(position).getValue()),
                            vos.get(position).getCreateTime());
                    girthPopUpWindow.setCancel(new GirthPopUpWindow.IOnCancelListener() {
                        @Override
                        public void onCancel(GirthPopUpWindow dialog) {
                            girthPopUpWindow.getGirthPopUpWindow().dismiss();
                        }
                    }).setConfirm( new GirthPopUpWindow.IOnConfirmListener() {
                        @Override
                        public void onConfirm(GirthPopUpWindow dialog) {
                            Intent intent = new Intent(mContext, mContext.getClass());
                            intent.putExtra("src", 1);
                            mContext.startActivity(intent);
                        }
                    }).showFoodPopWindow();

                }
            }));

           /* holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(position);
                }
            });*/

        }



    }

    @Override
    public int getItemCount() {
       return girthMap.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder{

        private TextView girthDate;
        private RecyclerView recyclerView;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            girthDate = itemView.findViewById(R.id.girthItem_title);
            recyclerView = itemView.findViewById(R.id.girthInRv_main);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }

}
