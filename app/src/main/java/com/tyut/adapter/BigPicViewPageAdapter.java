package com.tyut.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.tyut.BigPicActivity;

import java.util.List;

public class BigPicViewPageAdapter extends PagerAdapter {

     private List<View> viewList;
     private Context context;
     private CloseActivity closeActivity;
     private SavePicInterface savePicInterface;

    public void setSavePicInterface(SavePicInterface savePicInterface) {
        this.savePicInterface = savePicInterface;
    }

    public BigPicViewPageAdapter(final Context context, final List<View> viewList, final CloseActivity closeActivity) {
        this.viewList = viewList;
        this.context = context;
        this.closeActivity = closeActivity;
        for (final View view : viewList) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(savePicInterface != null){
                        savePicInterface.savePic();
                    }
                    return true;
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(closeActivity != null){
                        closeActivity.close();
                    }
                }
            });
        }
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = viewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        View view = viewList.get(position);
        container.removeView(view);
        //super.destroyItem(container, position, object);
    }

    public interface CloseActivity{
        void close();
    }
    public interface SavePicInterface{
        void savePic();
    }
}
