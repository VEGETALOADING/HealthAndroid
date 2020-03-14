package com.tyut.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tyut.R;

import org.w3c.dom.Text;

public class RgGoalFragment extends Fragment implements View.OnClickListener {

    Button registerSuccess_btn;
    ImageView loseFat_iv;
    TextView loseFat_tv;
    ImageView keep_iv;
    TextView keep_tv;
    ImageView addFat_iv;
    TextView addFat_tv;
    RelativeLayout loseFat_rl;
    RelativeLayout keep_rl;
    RelativeLayout addFat_rl;

    private Integer goal = 0;
    private FragmentListener listener;
    public interface FragmentListener{
        void getGoal(Integer str);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rggoal, container, false);

        registerSuccess_btn = view.findViewById(R.id.nextStep_btn);
        loseFat_iv = view.findViewById(R.id.loseFat_iv);
        loseFat_tv = view.findViewById(R.id.loseFat_tv);
        addFat_iv = view.findViewById(R.id.addFat_iv);
        addFat_tv = view.findViewById(R.id.addFat_tv);
        keep_iv = view.findViewById(R.id.keep_iv);
        keep_tv = view.findViewById(R.id.keep_tv);
        loseFat_rl = view.findViewById(R.id.loseFat_rl);
        keep_rl = view.findViewById(R.id.keep_rl);
        addFat_rl = view.findViewById(R.id.addFat_rl);

        registerSuccess_btn.setOnClickListener(this);
        loseFat_rl.setOnClickListener(this);
        addFat_rl.setOnClickListener(this);
        keep_rl.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nextStep_btn:
                listener.getGoal(goal);
                break;
            case R.id.loseFat_rl:
                goal = 0;
                loseFat_iv.setImageResource(R.mipmap.ic_goal0_selected);
                loseFat_tv.setTextColor(getResources().getColor(R.color.green_light));
                keep_iv.setImageResource(R.mipmap.ic_goal1_unselected);
                keep_tv.setTextColor(getResources().getColor(R.color.nav_text_default));
                addFat_iv.setImageResource(R.mipmap.ic_goal2_unselected);
                addFat_tv.setTextColor(getResources().getColor(R.color.nav_text_default));
                break;
            case R.id.keep_rl:
                goal = 1;
                loseFat_iv.setImageResource(R.mipmap.ic_goal0_unselected);
                loseFat_tv.setTextColor(getResources().getColor(R.color.nav_text_default));
                keep_iv.setImageResource(R.mipmap.ic_goal1_selected);
                keep_tv.setTextColor(getResources().getColor(R.color.green_light));
                addFat_iv.setImageResource(R.mipmap.ic_goal2_unselected);
                addFat_tv.setTextColor(getResources().getColor(R.color.nav_text_default));
                break;
            case R.id.addFat_rl:
                goal = 2;
                loseFat_iv.setImageResource(R.mipmap.ic_goal0_unselected);
                loseFat_tv.setTextColor(getResources().getColor(R.color.nav_text_default));
                keep_iv.setImageResource(R.mipmap.ic_goal1_unselected);
                keep_tv.setTextColor(getResources().getColor(R.color.nav_text_default));
                addFat_iv.setImageResource(R.mipmap.ic_goal2_selected);
                addFat_tv.setTextColor(getResources().getColor(R.color.green_light));
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FragmentListener) {
            listener = (FragmentListener)context;
        } else{
            throw new IllegalArgumentException("activity must implements FragmentListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}

