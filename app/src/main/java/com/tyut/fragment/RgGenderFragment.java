package com.tyut.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tyut.R;
import com.tyut.utils.StringUtil;
import com.tyut.view.HeightRulerView;
import com.tyut.view.MyHorizontalScrollView;


public class RgGenderFragment extends Fragment implements View.OnClickListener {

    private ImageView boy_iv;
    private TextView boy_tv;
    private ImageView girl_iv;
    private TextView girl_tv;
    private Button nextStep_btn;

    private TextView height_tv;

    private HeightRulerView rulerView;
    private MyHorizontalScrollView horizontalScrollView;

    private String height = 160+"";
    private Integer gender = 1;
    private FragmentListener listener;
    public interface FragmentListener{
        void getGender(Integer gender, String height);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rggender, container, false);

        boy_iv = view.findViewById(R.id.boy_iv);
        boy_tv = view.findViewById(R.id.boy_tv);
        girl_iv = view.findViewById(R.id.girl_iv);
        girl_tv = view.findViewById(R.id.girl_tv);
        nextStep_btn = view.findViewById(R.id.nextStep_btn);
        height_tv = view.findViewById(R.id.height_tv);
        rulerView = view.findViewById(R.id.height_rv);
        horizontalScrollView = view.findViewById(R.id.height_sv);

        height_tv.setText(160+"");

        boy_iv.setOnClickListener(this);
        girl_iv.setOnClickListener(this);
        nextStep_btn.setOnClickListener(this);
        initRuler(rulerView, horizontalScrollView);

        return view;
    }


    private void initRuler(final HeightRulerView rulerView, MyHorizontalScrollView horizontalScrollView){

        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);// 去掉超出滑动后出现的阴影效果

        // 设置水平滑动
        rulerView.setHorizontalScrollView(horizontalScrollView);
        rulerView.setDefaultScaleValue(160-100);


        // 当滑动尺子的时候
        horizontalScrollView.setOnScrollListener(new MyHorizontalScrollView.OnScrollListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

                rulerView.setScrollerChanaged(l, t, oldl, oldt);
            }
        });


        rulerView.onChangedListener(new HeightRulerView.onChangedListener(){
            @Override
            public void onSlide(float number) {

                height = StringUtil.keepDecimal(number, 1)+100+"";
                height_tv.setText(height);
            }
        });

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.boy_iv:
                gender = 1;
                boy_iv.setImageResource(R.mipmap.pic_boy_selected);
                boy_tv.setTextColor(getResources().getColor(R.color.green_light));
                girl_iv.setImageResource(R.mipmap.pic_girl_unselected);
                boy_tv.setTextColor(getResources().getColor(R.color.line_grey));
                break;
            case R.id.girl_iv:
                gender = 0;
                boy_iv.setImageResource(R.mipmap.pic_boy_unselected);
                boy_tv.setTextColor(getResources().getColor(R.color.line_grey));
                girl_iv.setImageResource(R.mipmap.pic_girl_selected);
                boy_tv.setTextColor(getResources().getColor(R.color.green_light));
                break;
            case R.id.nextStep_btn:
                listener.getGender(gender, height);
                break;
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof RgBirthdayFragment.FragmentListener) {
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
