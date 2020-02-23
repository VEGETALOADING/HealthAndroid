package com.tyut.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.tyut.R;
import com.tyut.utils.EmojiUtil;
import com.tyut.view.EmojiIndicatorView;
import com.tyut.vo.Emoji;

import java.util.ArrayList;
import java.util.List;

public class FaceFragment extends Fragment implements View.OnClickListener {

    public static FaceFragment Instance() {
        FaceFragment instance = new FaceFragment();
        Bundle bundle = new Bundle();
        instance.setArguments(bundle);
        return instance;
    }

    ViewPager faceViewPager;
    EmojiIndicatorView faceIndicator;


    List<View> ViewPagerItems = new ArrayList<>();
    ArrayList<Emoji> emojiList;
    private int columns = 7; //每一行的表情数量
    private int rows = 3;  //设置总共有几行

    private OnEmojiClickListener listener;

    public void setListener(OnEmojiClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof OnEmojiClickListener) {
            this.listener = (OnEmojiClickListener) activity;
        }
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        emojiList = EmojiUtil.getEmojiList();
        /*try {
            //用SharedPerference来保存我们最近使用的表情
            if (recentManager.getCollection(RecentEmojiManager.PREFERENCE_NAME) != null) {
                recentlyEmojiList = (ArrayList<Emoji>) recentManager.getCollection(RecentEmojiManager.PREFERENCE_NAME);
            } else {
                recentlyEmojiList = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face, container, false);
        faceViewPager = (ViewPager) view.findViewById(R.id.face_viewPager);
        faceIndicator = (EmojiIndicatorView) view.findViewById(R.id.face_indicator);
        initViews();
        return view;
    }

    private void initViews() {
        initViewPager(emojiList);
    }

    private void initViewPager(ArrayList<Emoji> list) {
        intiIndicator(list);
        ViewPagerItems.clear();
        for (int i = 0; i < getPagerCount(list); i++) {
            ViewPagerItems.add(getViewPagerItem(i, list));
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(ViewPagerItems);
        faceViewPager.setAdapter(mVpAdapter);
        faceViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int oldPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                faceIndicator.playBy(oldPosition, position);
                oldPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void intiIndicator(ArrayList<Emoji> list) {
        faceIndicator.init(getPagerCount(list));
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 根据表情数量以及GridView设置的行数和列数计算Pager数量
     *
     * @return
     */
    private int getPagerCount(ArrayList<Emoji> list) {
        int count = list.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }

    private View getViewPagerItem(int position, ArrayList<Emoji> list) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.layout_face_grid, null);//表情布局
        GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);
        /**
         * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
         * */
        final List<Emoji> subList = new ArrayList<>();
        subList.addAll(list.subList(position * (columns * rows - 1),
                (columns * rows - 1) * (position + 1) > list
                        .size() ? list.size() : (columns
                        * rows - 1)
                        * (position + 1)));
        /**
         * 末尾添加删除图标
         * */
        if (subList.size() < (columns * rows - 1)) {
            for (int i = subList.size(); i < (columns * rows - 1); i++) {
                subList.add(null);
            }
        }
        Emoji deleteEmoji = new Emoji();
        deleteEmoji.setImageUri(R.mipmap.face_delete);
        subList.add(deleteEmoji);
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, getActivity());
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == columns * rows - 1) {
                    if(listener != null){
                        listener.onEmojiDelete();
                    }
                    return;
                }
                if(listener != null){
                    listener.onEmojiClick(subList.get(position));
                }
            }
        });

        return gridview;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    class FaceGVAdapter extends BaseAdapter {
        private List<Emoji> list;
        private Context mContext;

        public FaceGVAdapter(List<Emoji> list, Context mContext) {
            super();
            this.list = list;
            this.mContext = mContext;
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_face, null);
                holder.iv = (ImageView) convertView.findViewById(R.id.face_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (list.get(position) != null) {
                holder.iv.setImageBitmap(EmojiUtil.decodeSampledBitmapFromResource(getActivity().getResources(), list.get(position).getImageUri(),
                        EmojiUtil.dip2px(getActivity(), 32), EmojiUtil.dip2px(getActivity(), 32)));
            }
            return convertView;
        }

        class ViewHolder {
            ImageView iv;
        }
    }

    class FaceVPAdapter extends PagerAdapter {
        // 界面列表
        private List<View> views;

        public FaceVPAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) (arg2));
        }

        @Override
        public int getCount() {
            return views.size();
        }

        // 初始化arg1位置的界面
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(views.get(arg1));
            return views.get(arg1);
        }

        // 判断是否由对象生成界
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }
    }

    //让你的Activity实现(Fragment中set接口)这个接口来处理emoji的点击事件
    public interface OnEmojiClickListener {
        void onEmojiDelete();

        void onEmojiClick(Emoji emoji);
    }
}
