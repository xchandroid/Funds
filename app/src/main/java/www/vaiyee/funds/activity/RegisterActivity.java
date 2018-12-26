package www.vaiyee.funds.activity;
/**
 * 注册界面
 */

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import www.vaiyee.funds.ClientThread;
import www.vaiyee.funds.R;
import www.vaiyee.funds.adapter.Class_menu_adapter;
import www.vaiyee.funds.bean.Class;

public class RegisterActivity extends AppCompatActivity {
    private EditText s_id,pwd,pwds,name,qq,phonenumber;
    private TextView class_id;
    private static ClientThread clientThread;
    private List<Class> classList = new ArrayList<>();
    private static int c_id;
    private RadioGroup radioGroup;
    private static String sex="男";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        s_id = findViewById(R.id.s_id);
        pwd = findViewById(R.id.pwd);
        pwds = findViewById(R.id.pwds);
        name = findViewById(R.id.name);
        qq = findViewById(R.id.qq);
        phonenumber= findViewById(R.id.phonenumber);
        class_id = findViewById(R.id.class_id);
        radioGroup = findViewById(R.id.rg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton chocis = findViewById(i);
                sex = chocis.getText().toString();
            }
        });
        clientThread = new ClientThread();
        clientThread.setHandler(handler);
    }
    public void ViewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back:
                finish();
                break;
            case R.id.zhuce:
                 if (TextUtils.isEmpty(s_id.getText().toString()))
                 {
                     Toast.makeText(this,"学号不能为空",Toast.LENGTH_LONG).show();
                     return;
                 }
                if (TextUtils.isEmpty(pwd.getText().toString()))
                {
                    Toast.makeText(this,"密码不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!pwds.getText().toString().equals(pwd.getText().toString()))
                {
                    Toast.makeText(this,"两次输入的密码不一致",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(name.getText().toString()))
                {
                    Toast.makeText(this,"姓名不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(qq.getText().toString()))
                {
                    Toast.makeText(this,"QQ号不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(phonenumber.getText().toString()))
                {
                    Toast.makeText(this,"手机号不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(s_id.getText().toString()))
                {
                    Toast.makeText(this,"请选择班级",Toast.LENGTH_LONG).show();
                    return;
                }
                 String sql = "insert***"+s_id.getText().toString()+"***"+pwd.getText().toString()+"***"+name.getText().toString()+"***"+sex+"***"+qq.getText().toString()+"***"+phonenumber.getText().toString()+"***"+c_id+"***"+class_id.getText().toString();
                 clientThread.sendMessage(sql);
                break;
            case R.id.class_id:
                clientThread.sendMessage("getclassdata***获取班级数据");//先获取服务器上的班级数据再显示弹窗
                break;
        }
    }
    private static PopupWindow popupWindow;
    //显示班级选择菜单
    private void showSelectMenu()
    {
        View contentview = LayoutInflater.from(getBaseContext()).inflate(R.layout.class_menu,null);
        initContentview(contentview);
       if (popupWindow==null)
       {
           popupWindow = new PopupWindow(contentview,class_id.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
       }
       popupWindow.setOutsideTouchable(true);
       popupWindow.setFocusable(true);
       popupWindow.showAsDropDown(class_id);
    }

    private void initContentview(View view)
    {
        ListView listView = view.findViewById(R.id.class_list);
        listView.setAdapter(new Class_menu_adapter(this,classList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Class aClass = classList.get(i);
                class_id.setText(aClass.getName());
                c_id  =aClass.getId();
                popupWindow.dismiss();
            }
        });
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    if (msg.getData().getString("response").equals("注册成功****##end")) {
                        Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_LONG).show();
                    }
                    else if (msg.getData().getString("response").equals("此学号已注册过了****##end"))
                    {
                        Toast.makeText(RegisterActivity.this, "此学号已注册过了", Toast.LENGTH_LONG).show();
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
                            classList.add(c);
                        }
                        showSelectMenu(); //获取数据成功后再显示数据
                    }

                    break;

            }
        }
    };
}
