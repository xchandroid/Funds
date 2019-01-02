package www.vaiyee.funds.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import www.vaiyee.funds.ClientThread;
import www.vaiyee.funds.ExcelUtil;
import www.vaiyee.funds.R;
import www.vaiyee.funds.adapter.FundsAdapter;
import www.vaiyee.funds.bean.Funds;

public class StudentAdminActivity extends AppCompatActivity implements View.OnClickListener{
    ListView listView;
    List<Funds> fundsList = new ArrayList<>();
    FundsAdapter adapter;
    ClientThread clientThread;
    String id="";
    String details="";
    String class_name ="";
    EditText name,remainder,detail;
    static Funds funds=null;
    static int position=0;
    TextView out;
    public static double  Remainder=0,Remainder2;
    RadioGroup rg;
    RadioButton shouru,zhichu;
    String type="";//收支类型
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_admin);
        id = getIntent().getStringExtra("id");
        listView = findViewById(R.id.funds_list);
        out = findViewById(R.id.output);
        out.setOnClickListener(this);
        TextView info = findViewById(R.id.info);
        class_name = getIntent().getStringExtra("name");
        info.setText("管理"+class_name+"的班费信息");
        adapter = new FundsAdapter(this,fundsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              funds = fundsList.get(i);
              position = i;
              showPopuwindow();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;
                showDiolog();
                return true;
            }
        });
        clientThread = new ClientThread();
        clientThread.setHandler(handler);
        clientThread.sendMessage("getFunds***"+id+"***获取服务器上的班级经费信息");
    }



    PopupWindow popupWindow;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showPopuwindow() {
        System.out.println("点击的位置是"+position);
        System.out.println("点击的位置的Funds对象的时间是"+funds.getTime());
        View contentView= LayoutInflater.from(this).inflate(R.layout.updata_funds_menu, null);;
            name = contentView.findViewById(R.id.class_name);
            remainder = contentView.findViewById(R.id.remainder);
            detail = contentView.findViewById(R.id.detail);
            popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Button cancel = contentView.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                }
            });


            name.setText(funds.getType());
            remainder.setText(funds.getRemainder());
            detail.setText(funds.getDetail());
            Button comfirm = contentView.findViewById(R.id.comfirm_add);
            comfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(name.getText().toString()))
                    {
                        Toast.makeText(StudentAdminActivity.this,"收支详情不能为空！",Toast.LENGTH_LONG).show();
                    }
                    if (TextUtils.isEmpty(remainder.getText().toString()))
                    {
                        Toast.makeText(StudentAdminActivity.this,"余额不能为空！",Toast.LENGTH_LONG).show();
                    }
                    if (TextUtils.isEmpty(detail.getText().toString()))
                    {
                        Toast.makeText(StudentAdminActivity.this,"请输入经费详情",Toast.LENGTH_LONG).show();
                    }
                    Funds funds1 = new Funds();
                    funds1.setType(name.getText().toString());
                    funds1.setRemainder(remainder.getText().toString());
                    funds1.setDetail(detail.getText().toString());
                    funds1.setTime(funds.getTime());
                    fundsList.set(position,funds1);//修改相应的记录
                    String result ="";
                    for (int i=0;i<fundsList.size();i++)
                    {
                        Funds funds2 = fundsList.get(i);
                        result += funds2.getType()+"+++"+funds2.getRemainder()+"+++"+funds2.getTime()+"+++"+funds2.getDetail()+"++++";
                    }
                    result = result.substring(0,result.lastIndexOf("++++"));
                    System.out.println(result);
                    clientThread.sendMessage("updateFunds***"+result+"***"+id);
                    popupWindow.dismiss();
                }
            });

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

   public void add_info(View view)
   {
       switch (view.getId())
       {
           case R.id.add_info:
               final View contentView= LayoutInflater.from(this).inflate(R.layout.add_funds_info, null);
               name = contentView.findViewById(R.id.class_name);
               remainder = contentView.findViewById(R.id.remainder);
               remainder.setText("余额:"+String.valueOf(Remainder));
               detail = contentView.findViewById(R.id.detail);
               rg = contentView.findViewById(R.id.rg);
               rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(RadioGroup radioGroup, int i) {
                       RadioButton rb = contentView.findViewById(i);
                       type = rb.getText().toString();
                       if (name.getText().toString().isEmpty())
                       {
                           Toast.makeText(StudentAdminActivity.this,"请输入额度！",Toast.LENGTH_LONG).show();
                           rb.setChecked(false);
                           return;
                       }
                       if (type.equals("支出"))
                       {
                           double d = Double.parseDouble(name.getText().toString());
                           remainder.setText(("余额:"+String.valueOf(Remainder2-d)));
                           Remainder =Remainder2-d;
                       }
                       else if (type.equals("收入"))
                       {
                           double d = Double.parseDouble(name.getText().toString());
                           remainder.setText(("余额:"+String.valueOf(Remainder2+d)));
                           Remainder =Remainder2+d;
                       }
                   }
               });
               popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
               Button cancel = contentView.findViewById(R.id.cancel);
               cancel.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Remainder =Remainder2;
                       popupWindow.dismiss();
                   }
               });
               Button comfirm = contentView.findViewById(R.id.comfirm_add);
               comfirm.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       if (TextUtils.isEmpty(name.getText().toString()))
                       {
                           Toast.makeText(StudentAdminActivity.this,"收支额度不能为空！",Toast.LENGTH_LONG).show();
                           return;
                       }
                       if (TextUtils.isEmpty(detail.getText().toString()))
                       {
                           Toast.makeText(StudentAdminActivity.this,"请输入经费详情",Toast.LENGTH_LONG).show();
                           return;
                       }
                       if (TextUtils.isEmpty(type))
                       {
                           Toast.makeText(StudentAdminActivity.this,"请选择收支类型",Toast.LENGTH_LONG).show();
                           return;
                       }
                       String result ="";
                       for (int i=0;i<fundsList.size();i++)
                       {
                           Funds funds2 = fundsList.get(i);
                           result += funds2.getType()+"+++"+funds2.getRemainder()+"+++"+funds2.getTime()+"+++"+funds2.getDetail()+"++++";
                       }
                       result +=type+name.getText().toString()+"+++"+remainder.getText().toString()+"+++"+getTime()+"+++"+detail.getText().toString();
                       System.out.println(result);
                       clientThread.sendMessage("addFunds***"+result+"***"+Remainder+"***"+id);
                       popupWindow.dismiss();
                   }
               });

               popupWindow.setOutsideTouchable(true);
               popupWindow.setFocusable(true);
               popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
               break;
       }
   }
    private void showDiolog()
    {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("确定要删除此条记录吗？")//设置对话框的标题
                .setMessage("删除后不可恢复！")//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fundsList.remove(position);
                        String result ="";
                        for (int i=0;i<fundsList.size();i++)
                        {
                            Funds funds2 = fundsList.get(i);
                            result += funds2.getType()+"+++"+funds2.getRemainder()+"+++"+funds2.getTime()+"+++"+funds2.getDetail()+"++++";
                        }
                        result = result.substring(0,result.lastIndexOf("++++"));
                        System.out.println(result);
                        String yue = fundsList.get(fundsList.size()-1).getRemainder().substring(3);
                        clientThread.sendMessage("deleteFunds***"+result+"***"+yue+"***"+id);
                        dialog.dismiss();
                    }
                }).create();
                 dialog.show();
    }
    public static String getTime()
    {
        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month+1;
        int date = t.monthDay;
        int hour = t.hour; // 0-23
        int minute = t.minute;
        //int second = t.second;
        return String.valueOf(year)+"/"+String.valueOf(month)+"/"+String.valueOf(date)+"/ "+String.valueOf(hour)+":"+String.valueOf(minute);
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    String response = msg.getData().getString("response");
                    details = response.substring(0, response.indexOf("****##end"));
                    String[] s = details.split("\\+\\+\\+\\+"); //先把每条记录切开
                    Remainder = Double.parseDouble(s[s.length-2]);//保存余额
                    Remainder2 = Double.parseDouble(s[s.length-2]);//这个是不变的，点取消按钮时用回这个值
                    if (s[0].length()<10)
                    {
                        return; //防止没有经费记录时崩溃
                    }
                    if (s[s.length-1].equals("经费信息更新成功"))
                    {
                        Toast.makeText(StudentAdminActivity.this,"经费信息更新成功！",Toast.LENGTH_LONG).show();
                    }
                    else if (s[s.length-1].equals("经费信息添加成功"))
                    {
                        Toast.makeText(StudentAdminActivity.this,"经费信息添加成功！",Toast.LENGTH_LONG).show();
                    }
                    else if (s[s.length-1].equals("经费信息删除成功"))
                    {
                        Toast.makeText(StudentAdminActivity.this,"经费信息删除成功！",Toast.LENGTH_LONG).show();
                    }
                    fundsList.clear();
                        for (int i = 0; i < s.length-2; i++) {
                            String[] ss = s[i].split("\\+\\+\\+");//解析每条记录
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
        if (view.getId()==R.id.output) {
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
