package www.vaiyee.funds.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import www.vaiyee.funds.ClientThread;
import www.vaiyee.funds.R;

/**
 * 登录界面
 */
public class LoginActivity extends AppCompatActivity {
    Button login;
    EditText accout,pwd;
    ClientThread clientThread;
    RadioGroup radioGroup;
    String loginType="student";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accout = findViewById(R.id.accout);
        pwd = findViewById(R.id.pwd);
        radioGroup = findViewById(R.id.rg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i)
                {
                    case R.id.student:
                        loginType = "student";
                        break;
                    case R.id.admin:
                        loginType = "admin";
                        break;
                    case R.id.super_admin:
                        loginType = "super_admin";
                        break;
                }

            }
        });
        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(accout.getText().toString()))
                {
                    Toast.makeText(LoginActivity.this,"账号不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(pwd.getText().toString()))
                {
                    Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                switch (loginType)
                {
                    case "student":
                        clientThread.sendMessage("select***select * from student where s_id=" + accout.getText().toString() + " and pwd='" + pwd.getText().toString() + "'***获取服务器数据验证学生登录");
                        break;
                    case "admin":
                        clientThread.sendMessage("validateAdmin***select * from student where s_id=" + accout.getText().toString() + " and pwd='" + pwd.getText().toString() + "'***获取服务器数据验证班级管理员登录");
                        break;
                    case "super_admin":
                        clientThread.sendMessage("validateSuperAdmin***select * from admin where id ="+accout.getText().toString()+" and pwd ='"+pwd.getText().toString()+"'***获取服务器数据验证超级管理员登录");
                        break;
                }
            }
        });

        clientThread = new ClientThread();
        clientThread.setHandler(handler);
        clientThread.start();  //打开socket连接
    }

    @Override
    protected void onResume() {
        super.onResume();
        clientThread.setHandler(handler); //解决在注册界面切换回来后handler接收数据的问题
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    System.out.println(msg.getData().getString("response"));
                    String [] ss = msg.getData().getString("response").split("\\*\\*\\*\\*");
                    if (ss[0].equals("学生登录成功")) {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, StudentActivity.class);
                        intent.putExtra("id",ss[1]); //把服务器返回的班级ID传给学生界面
                        intent.putExtra("name",ss[2]);
                        intent.putExtra("s_id",accout.getText().toString());
                        startActivity(intent);
                        finish();
                    }
                    else if(ss[0].equals("班级管理员登录成功"))
                    {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, StudentAdminActivity.class);
                        intent.putExtra("id",ss[1]); //把服务器返回的班级ID传给学生界面
                        intent.putExtra("name",ss[2]);
                        startActivity(intent);
                        finish();
                    }
                    else if (msg.getData().getString("response").equals("超级管理员登录成功****##end"))
                    {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this,AdminActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "账号或密码错误！", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };
}
