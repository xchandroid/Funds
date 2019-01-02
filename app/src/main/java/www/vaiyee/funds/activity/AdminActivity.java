package www.vaiyee.funds.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import www.vaiyee.funds.ClientThread;
import www.vaiyee.funds.R;
import www.vaiyee.funds.adapter.ClassList_Adapter;
import www.vaiyee.funds.bean.Class;

public class AdminActivity extends AppCompatActivity {

    String details = "";
    ClientThread clientThread;
    EditText name,remainder,detail;
    List<Class> classList = new ArrayList<>();
    ClassList_Adapter adapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        listView = findViewById(R.id.class_list);
        adapter = new ClassList_Adapter(this,classList);
        listView.setAdapter(adapter);
        TextView button = findViewById(R.id.examine);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this,ExamineActivity.class);
                startActivity(intent);
            }
        });
        clientThread = new ClientThread();
        clientThread.sendMessage("getclassdata***获取服务器上的班级信息");
    }

    @Override
    protected void onResume() {
        super.onResume();
        clientThread.setHandler(handler);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void viewOnclick(View view)
    {
        switch (view.getId())
        {
            case R.id.add_class:
               showPopuwindow();
                break;
        }
    }

    PopupWindow popupWindow;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showPopuwindow() {
        if (popupWindow==null)
        {
            View contentView = LayoutInflater.from(this).inflate(R.layout.add_class,null);
            name = contentView.findViewById(R.id.class_name);
            remainder = contentView.findViewById(R.id.remainder);
            detail = contentView.findViewById(R.id.detail);
            Button comfirm = contentView.findViewById(R.id.comfirm_add);
            comfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(name.getText().toString()))
                    {
                        Toast.makeText(AdminActivity.this,"班级名称不能为空！",Toast.LENGTH_LONG).show();
                    }
                    if (TextUtils.isEmpty(remainder.getText().toString()))
                    {
                        Toast.makeText(AdminActivity.this,"余额不能为空！",Toast.LENGTH_LONG).show();
                    }
                    if (TextUtils.isEmpty(detail.getText().toString()))
                    {
                        details = "暂无";
                    }
                    else
                    {
                        details = detail.getText().toString();
                    }
                    clientThread.sendMessage("addClass***"+name.getText().toString()+"***"+remainder.getText().toString()+"***"+details);
                }
            });
            popupWindow = new PopupWindow(contentView,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            Button cancel = contentView.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                }
            });
        }

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    if (msg.getData().getString("response").equals("添加成功!****##end"))
                    {
                        Toast.makeText(AdminActivity.this,"班级添加成功！",Toast.LENGTH_LONG).show();
                        name.setText("");
                        remainder.setText("");
                        detail.setText("");
                        clientThread.sendMessage("getclassdata***获取服务器上的班级信息");
                    }
                    else
                    {
                        String []classlist = msg.getData().getString("response").split("\\*\\*\\*\\*");
                        classList.clear();//清空列表，防止数据重复
                        for (int i=0;i<classlist.length-1;i++)
                        {
                            String []data = classlist[i].split("\\*\\*\\*");
                            Class c = new Class();
                            c.setId(Integer.parseInt(data[0]));
                            c.setName(data[1]);
                            c.setRemainder(data[2]);
                            c.setDetail(data[3]);
                            classList.add(c);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };
}
