package com.tyut;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.adapter.ChooseReportReasonAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.StringUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.SportVO;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView confirm_tv;
    private LinearLayout return_ll;
    private RecyclerView rvMain;
    private Integer objectId;
    private Integer category;
    private Integer reason = 0;
    private Integer userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        confirm_tv = findViewById(R.id.confirm_tv);
        return_ll = findViewById(R.id.return_ll);
        rvMain = findViewById(R.id.reasonRv_main);

        rvMain.setLayoutManager(new LinearLayoutManager(this));
        rvMain.addItemDecoration(new RecycleViewDivider(this,  LinearLayoutManager.VERTICAL));
        rvMain.setAdapter(new ChooseReportReasonAdapter(this).setListener(new ChooseReportReasonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                reason = position;
            }
        }));

        confirm_tv.setOnClickListener(this);
        return_ll.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userId = SPSingleton.get(this, SPSingleton.USERINFO).readInt("userid");
        category = getIntent().getIntExtra("category", 0);
        objectId = getIntent().getIntExtra("objectid", 0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_ll:
                finish();
                break;
            case R.id.confirm_tv:
                String createTime = null;
                try {
                    createTime = URLEncoder.encode(StringUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080//portal/report/add.do?userid="
                                +userId
                                +"&objectid="+objectId
                                +"&category="+category
                                +"&reason="+reason
                                +"&createtime="+ createTime,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse serverResponse = gson.fromJson(msg, ServerResponse.class);

                                ReportActivity.this.finish();
                                Looper.prepare();
                                Toast.makeText(ReportActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                Looper.loop();

                            }
                        }
                );
                break;
        }
    }
}

