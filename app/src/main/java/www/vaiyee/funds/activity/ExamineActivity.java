package www.vaiyee.funds.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import www.vaiyee.funds.ClientThread;
import www.vaiyee.funds.R;
import www.vaiyee.funds.adapter.DeleteAdminAdapter;
import www.vaiyee.funds.adapter.ExamineAdpater;
import www.vaiyee.funds.bean.Student;

public class ExamineActivity extends AppCompatActivity implements View.OnClickListener{
    ListView listView;
    ClientThread clientThread;
    List<Student> studentList = new ArrayList<>();
    ExamineAdpater adpater;
    DeleteAdminAdapter deleteAdminAdapter;
    TextView yanzheng,shanchu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examine);
        listView = findViewById(R.id.examine_list);
        yanzheng = findViewById(R.id.yanzheng);
        yanzheng.setOnClickListener(this);
        shanchu = findViewById(R.id.shanchu);
        shanchu.setOnClickListener(this);
        adpater = new ExamineAdpater(this,studentList);
        deleteAdminAdapter = new DeleteAdminAdapter(this,studentList);
        listView.setAdapter(adpater);
        clientThread = new ClientThread();
        clientThread.setHandler(handler);
        clientThread.sendMessage("exmaine***获取申请成为班级管理员的学生名单");
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    String response = msg.getData().getString("response");
                    String[]s = response.split("\\*\\*\\*\\*");
                    studentList.clear();
                    for (int i=0;i<s.length-1;i++)
                    {
                        String[] ss = s[i].split("\\*\\*\\*");
                        Student student = new Student();
                        student.setS_id(ss[0]);
                        student.setName(ss[1]);
                        student.setClass_id(ss[2]);
                        student.setClass_name(ss[3]);
                        studentList.add(student);
                    }
                    adpater.notifyDataSetChanged();
                    deleteAdminAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.yanzheng:
                yanzheng.setTextColor(getResources().getColor(R.color.lv));
                shanchu.setTextColor(getResources().getColor(R.color.black));
                clientThread.sendMessage("exmaine***获取申请成为班级管理员的学生名单");
                listView.setAdapter(adpater);
                adpater.resetSparseBooleanArray();
                clientThread.setHandler(handler);
                break;
            case R.id.shanchu:
                yanzheng.setTextColor(getResources().getColor(R.color.black));
                shanchu.setTextColor(getResources().getColor(R.color.red));
                clientThread.sendMessage("getAdmin***获取班级管理员的学生名单");
                listView.setAdapter(deleteAdminAdapter);
                deleteAdminAdapter.resetSparseBooleanArray();
                clientThread.setHandler(handler);
                break;
        }
    }
}
