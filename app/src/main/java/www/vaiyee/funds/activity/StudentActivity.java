package www.vaiyee.funds.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import www.vaiyee.funds.ClientThread;
import www.vaiyee.funds.R;
import www.vaiyee.funds.adapter.FundsAdapter;
import www.vaiyee.funds.bean.Funds;

public class StudentActivity extends AppCompatActivity {

    ListView listView;
    List<Funds> fundsList = new ArrayList<>();
    FundsAdapter adapter;
    ClientThread clientThread;
    String id="";
    String s_id="";
    Button apply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        id = getIntent().getStringExtra("id");
        s_id = getIntent().getStringExtra("s_id");
        listView = findViewById(R.id.funds_list);
        TextView info = findViewById(R.id.info);
        info.setText(getIntent().getStringExtra("name")+"的班费信息");
        adapter = new FundsAdapter(this,fundsList);
        listView.setAdapter(adapter);
        clientThread = new ClientThread();
        clientThread.setHandler(handler);
        clientThread.sendMessage("getFunds***"+id+"***获取服务器上的班级经费信息");
        apply = findViewById(R.id.apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clientThread.sendMessage("apply***"+s_id);
                Toast.makeText(StudentActivity.this,"申请已发出，等待超级管理员审核中...",Toast.LENGTH_LONG).show();
            }
        });
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    String response =msg.getData().getString("response");
                    String response2 = response.substring(0,response.indexOf("****##end"));
                    String []s = response2.split("\\+\\+\\+\\+"); //先把每条记录切开
                    for (int i=0;i<s.length-1;i++)
                    {
                        String []ss =s[i].split("\\+\\+\\+");//解析每条记录
                        Funds funds = new Funds();
                        funds.setType(ss[0]);
                        funds.setRemainder(ss[1]);
                        funds.setTime(ss[2]);
                        funds.setDetail(ss[3]);
                        fundsList.add(funds);
                    }
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

}
