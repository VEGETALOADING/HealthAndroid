package com.tyut.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tyut.R;
import com.tyut.SchemaDetailActivity;
import com.tyut.utils.SPSingleton;
import com.tyut.view.GlideRoundTransform;
import com.tyut.vo.MSchema;
import com.tyut.vo.UserVO;

import java.util.List;

public class SchemaListAdapter extends RecyclerView.Adapter<SchemaListAdapter.LinearViewHolder> {

    private Context mContext;

    private List<MSchema> mList;
    public SchemaListAdapter(Context context, List<MSchema> list){
        this.mContext = context;
        this.mList = list;
    }


    @NonNull
    @Override
    public SchemaListAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //传入Item布局
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_schema, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final SchemaListAdapter.LinearViewHolder holder, final int position) {
        UserVO userVO = (UserVO) SPSingleton.get(mContext, SPSingleton.USERINFO).readObject("user", UserVO.class);
        if(mList.size() > position){
            holder.intro_tv.setText(mList.get(position).getIntro());
            Glide.with(mContext)
                    .load("http://"+mContext.getString(R.string.url)+":8080/schemapic/" + mList.get(position).getJrpic())
                    .transform(new GlideRoundTransform(mContext, 25))
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.pic_iv);
            if(mList.get(position).getId() == userVO.getSchemaid()){
                holder.ifChose_ll.setVisibility(View.VISIBLE);
            }else{
                holder.ifChose_ll.setVisibility(View.GONE);
            }
            holder.name_tv.setText(mList.get(position).getName());
            holder.health_tv.setText(mList.get(position).getHealthIndex());
            holder.execute_tv.setText(mList.get(position).getExecuteIndex());
            holder.cost_tv.setText(mList.get(position).getCostIndex());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SchemaDetailActivity.class);
                    intent.putExtra("schema", mList.get(position));
                    mContext.startActivity(intent);
                }
            });

        }



    }

    @Override
    public int getItemCount() {
       return mList.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder{

        private TextView intro_tv;
        private TextView name_tv;
        private TextView health_tv;
        private TextView execute_tv;
        private TextView cost_tv;
        private ImageView pic_iv;
        private LinearLayout ifChose_ll;
        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            intro_tv = itemView.findViewById(R.id.schemaIntro_tv);
            name_tv = itemView.findViewById(R.id.schemaName_tv);
            health_tv = itemView.findViewById(R.id.healthIndex_tv);
            execute_tv = itemView.findViewById(R.id.executeIndex_tv);
            cost_tv = itemView.findViewById(R.id.costIndex_tv);
            ifChose_ll = itemView.findViewById(R.id.ifChose_ll);
            pic_iv = itemView.findViewById(R.id.schemaJrPic_iv);
        }
    }

}
