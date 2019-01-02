package www.vaiyee.funds.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import www.vaiyee.funds.ClientThread;
import www.vaiyee.funds.ExcelUtil;
import www.vaiyee.funds.R;
import www.vaiyee.funds.adapter.FundsAdapter;
import www.vaiyee.funds.bean.Funds;

public class StudentActivity extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    List<Funds> fundsList = new ArrayList<>();
    FundsAdapter adapter;
    ClientThread clientThread;
    String id="";
    TextView out;
    String s_id="";
    Button apply;
    String class_name="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        id = getIntent().getStringExtra("id");
        s_id = getIntent().getStringExtra("s_id");
        class_name = getIntent().getStringExtra("name");
        listView = findViewById(R.id.funds_list);
        TextView info = findViewById(R.id.info);
        out = findViewById(R.id.output);
        out.setOnClickListener(this);
        info.setText(class_name+"的班费信息");
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
                    if (s[0].length()<10)
                    {
                      return;
                    }
                    for (int i=0;i<s.length-2;i++)
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

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.output)
        {
            if (isGrantExternalRW(this)) {  //获取读写权限
                //内置sd卡路径
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                File file = new File(path);
                //文件夹是否已经存在
                if (!file.exists()) {
                    file.mkdirs();
                }

                String[] title = {"收支类型", "详情", "日期", "余额"};
                String fileName = file.toString() + "/" + class_name + "的班费.xls";
                ExcelUtil.initExcel(fileName, title);
                ExcelUtil.writeObjListToExcel(fundsList, fileName, this);
                System.out.println("点击了导出");
            }
            else
            {
                Toast.makeText(this,"拒绝内存权限将无法导出数据",Toast.LENGTH_LONG).show();
            }
        }
    }
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }
        return true;
    }
}
