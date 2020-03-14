package com.tyut.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tyut.R;
import com.tyut.utils.StringUtil;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.WeightRulerView;

public class RgWeightFragment extends Fragment implements View.OnClickListener {

    private Button nextStep_btn;
    private TextView weight_tv;
    private WeightRulerView rulerView;
    private MyHorizontalScrollView horizontalScrollView;

    private String weight = "67";
    private FragmentListener listener;
    public interface FragmentListener{
        void getWeight(String str);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rgweight, container, false);

        nextStep_btn = view.findViewById(R.id.nextStep_btn);
        weight_tv = view.findViewById(R.id.weight_tv);
        rulerView = view.findViewById(R.id.weight_rv);
        horizontalScrollView = view.findViewById(R.id.weight_sv);
        nextStep_btn.setOnClickListener(this);

        initRuler(rulerView, horizontalScrollView);

        return view;
    }

    private void initRuler(final WeightRulerView rulerView, MyHorizontalScrollView horizontalScrollView){

        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);// 去掉超出滑动后出现的阴影效果

        // 设置水平滑动
        rulerView.setHorizontalScrollView(horizontalScrollView);


        rulerView.setDefaultScaleValue(70.0f);
        rulerView.setMaxScaleValue(200.0f);


        // 当滑动尺子的时候
        horizontalScrollView.setOnScrollListener(new MyHorizontalScrollView.OnScrollListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

                rulerView.setScrollerChanaged(l, t, oldl, oldt);
            }
        });


        rulerView.onChangedListener(new WeightRulerView.onChangedListener(){
            @Override
            public void onSlide(float number) {

                weight = StringUtil.keepDecimal(number, 1)+"";
                weight_tv.setText(weight);
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nextStep_btn:
                listener.getWeight(weight);
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
