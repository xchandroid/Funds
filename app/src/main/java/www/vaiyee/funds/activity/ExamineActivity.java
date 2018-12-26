package www.vaiyee.funds.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import www.vaiyee.funds.ClientThread;
import www.vaiyee.funds.R;
import www.vaiyee.funds.adapter.ExamineAdpater;
import www.vaiyee.funds.bean.Student;

public class ExamineActivity extends AppCompatActivity {
    ListView listView;
    ClientThread clientThread;
    List<Student> studentList = new ArrayList<>();
    ExamineAdpater adpater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examine);
        listView = findViewById(R.id.examine_list);
        adpater = new ExamineAdpater(this,studentList);
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
                    break;
            }
        }
    };
}
