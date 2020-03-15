package com.tyut;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.tyut.adapter.BigPicViewPageAdapter;
import com.tyut.widget.SavePicPUW;

import java.util.ArrayList;
import java.util.List;

public class BigPicActivity extends AppCompatActivity {

    ViewPager viewPager;
    TextView pageInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bigpic);
        viewPager = findViewById(R.id.viewPager);
        pageInfo = findViewById(R.id.pageInfo);
        final List<View> viewList = new ArrayList<>();
        ArrayList<String> picList = getIntent().getStringArrayListExtra("picList");
        for (String s : picList) {
            ImageView imageView = new ImageView(this);
            Glide
                    .with(this)
                    .load("http://"+this.getString(R.string.url)+":8080/activitypic/" + s)
                    .into(imageView);
            viewList.add(imageView);
        }
        int defaultPosition = getIntent().getIntExtra("defaultposition", 0);
        pageInfo.setText((defaultPosition+1) + "/"+viewList.size());
        BigPicViewPageAdapter adapter = new BigPicViewPageAdapter(this, viewList, new BigPicViewPageAdapter.CloseActivity() {
            @Override
            public void close() {
                BigPicActivity.this.finish();
                overridePendingTransition(R.anim.activityshow, R.anim.activityhidden);
            }
        });
        adapter.setSavePicInterface(new BigPicViewPageAdapter.SavePicInterface() {
            @Override
            public void savePic() {
                Bitmap bitmap = ((BitmapDrawable) (((ImageView) viewList.get(viewPager.getCurrentItem())).getDrawable())).getBitmap();
                new SavePicPUW(BigPicActivity.this, bitmap).showFoodPopWindow();
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(defaultPosition);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageInfo.setText((position+1)+"/"+ viewList.size());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activityshow, R.anim.activityhidden);
    }
}

