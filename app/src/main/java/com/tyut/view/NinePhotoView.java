package com.tyut.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.tyut.adapter.NinePhotoViewAdapter;
import com.tyut.adapter.NinePhotoViewHolder;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Idtk on 2017/3/8.
 * Blog : http://www.idtkm.com
 * GitHub : https://github.com/Idtk
 * des : 九宫格View
 */

public class NinePhotoView extends FrameLayout implements Observer{

    private NinePhotoViewAdapter adapter;
    private int border = 5;
    private int childSize;
    private ArrayList<NinePhotoViewHolder> mRecyclerList = new ArrayList<>();


    public NinePhotoView(@NonNull Context context) {
        super(context);
    }

    public NinePhotoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NinePhotoView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NinePhotoView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        generateDefaultLayoutParams();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ninePhotoMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        childLayout(left, top, right, bottom);
    }

    private void ninePhotoCreateView(){
        removeAllViews();
        for (int i = 0; i < adapter.getItemCount(); i++) {
//            removeView(getChildAt(i));
            addView(generateViewHolder(i).getItemView(),generateDefaultLayoutParams());
        }
    }

    private void ninePhotoMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec)-getPaddingLeft()-getPaddingRight();
        int height;
	
	if (adapter == null) return;

        if (adapter.getItemCount() < 0 || adapter.getItemCount() > 9) {
            throw new IllegalStateException("itemCount may not be more than 9 or less than 0");
        }

        if (adapter.getItemCount() == 0) {
            setMeasuredDimension(0, 0);
            return;
        }

        if (adapter.getItemCount() > 1) {
            childSize = (width - border * 2) / 3;
            height = (int) (childSize * (int) Math.ceil(adapter.getItemCount() / 3.0) + border * (int) Math.ceil(adapter.getItemCount() / 3.0 - 1));
            if (adapter.getItemCount() == 4 || adapter.getItemCount() == 2) {
                int currentWidth = childSize*2 + border;
                setMeasuredDimension(currentWidth + getPaddingLeft() + getPaddingRight(), height + getPaddingTop() + getPaddingBottom());
            }else {
                int currentWidth = childSize*3 + border*2;
                setMeasuredDimension(currentWidth + getPaddingLeft() + getPaddingRight(), height + getPaddingTop() + getPaddingBottom());
            }
        } else {
            childSize = width/3;
            height = width/3;
            setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(), height + getPaddingTop() + getPaddingBottom());
        }
		ninePhotoCreateView();
    }

    private void childLayout(int left, int top, int right, int bottom) {
	    
	if (adapter == null) return;

        if (adapter.getItemCount() < 0 || adapter.getItemCount() > 9) {
            throw new IllegalStateException("itemCount may not be more than 9 or less than 0");
        }

        int count = adapter.getItemCount();
        int colNum = 3;
        if (count == 4){
            colNum = 2;
        }

        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);

            if (childView == null){
                return;
            }

            if (adapter != null && mRecyclerList.get(i) != null &&!mRecyclerList.get(i).getFlag()) {
                adapter.displayView(generateViewHolder(i), i);
                mRecyclerList.get(i).setFlag(true);
            }

            int rows = i / colNum;
            int cols = i % colNum;

            int childLeft = getPaddingLeft() + (childSize + border) * (cols);
            int childTop = getPaddingTop() + (childSize + border) * (rows);
            int childRight = childLeft + childSize;
            int childBottom = childTop + childSize;
            childView.layout(childLeft, childTop, childRight, childBottom);
        }
    }



    public void setAdapter(NinePhotoViewAdapter adapter){
        this.adapter = adapter;
        adapter.addObserver(this);
//        mRecyclerList.clear();
        for(NinePhotoViewHolder holder: mRecyclerList){
            holder.setFlag(false);
        }
    }

    public void setBorder(int border){
        this.border = border;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof NinePhotoViewAdapter){
            this.adapter = (NinePhotoViewAdapter) o;
            adapter.addObserver(this);
//            mRecyclerList.clear();
            for(NinePhotoViewHolder holder: mRecyclerList){
                holder.setFlag(false);
            }
            requestLayout();
            invalidate();
        }
    }

    private NinePhotoViewHolder generateViewHolder(int position){
        if (position < mRecyclerList.size()) {
            return mRecyclerList.get(position);
        } else {
            if (adapter != null){
                NinePhotoViewHolder holder = adapter.createView(NinePhotoView.this);
                if (holder == null){
                    return null;
                }
                mRecyclerList.add(holder);
                return holder;
            } else
                return null;
        }
    }

    public void clearRecycler(){
        mRecyclerList.clear();
    }
}
