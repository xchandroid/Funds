package www.vaiyee.funds.adapter;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import www.vaiyee.funds.ClientThread;
import www.vaiyee.funds.R;
import www.vaiyee.funds.bean.Student;

/**
 * Created by Administrator on 2019/1/2.
 */

public class DeleteAdminAdapter extends BaseAdapter implements View.OnClickListener{
    private Context context;
    private List<Student> studentList;
    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
    private static int position;
    public DeleteAdminAdapter(Context context,List<Student> studentList)
    {
        this.context = context;
        this.studentList =studentList;
    }
    @Override
    public int getCount() {
        return studentList.size();
    }

    @Override
    public Object getItem(int i) {
        return studentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Student student = studentList.get(i);
        ViewHolder viewHolder;
        if (view==null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.delete_admin,viewGroup,false);
            viewHolder = new ViewHolder();
            viewHolder.name = view.findViewById(R.id.textView);
            viewHolder.class_name = view.findViewById(R.id.textView2);
            viewHolder.confirm = view.findViewById(R.id.button);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (sparseBooleanArray.get(i)==true)
        {
            viewHolder.confirm.setText("已删除");
            viewHolder.confirm.setBackgroundResource(R.color.hui);
        }
        viewHolder.name.setText(student.getName());
        viewHolder.class_name.setText(student.getClass_name());
        viewHolder.confirm.setTag(i);
        viewHolder.confirm.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (sparseBooleanArray.get((int)view.getTag())==false) {
            position = (int)view.getTag(); //保存当前点击的位置
            sendCMD(studentList.get((int)view.getTag()).getClass_id(),studentList.get((int)view.getTag()).getS_id());
        }
    }

    ClientThread clientThread;
    private void sendCMD(String class_id,String s_id)
    {
        if (clientThread==null)
        {
            clientThread = new ClientThread();
        }
        clientThread.setHandler(handler);
        clientThread.sendMessage("deleteAdmin***"+class_id+"***"+s_id);
    }

    public void resetSparseBooleanArray()
    {
        sparseBooleanArray.clear();
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    String response = msg.getData().getString("response");
                    if (response.equals("管理员删除成功****##end"))
                    {
                        sparseBooleanArray.put(position, true);
                        notifyDataSetChanged();
                        Toast.makeText(context,"删除成功！",Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    class ViewHolder
    {
        TextView name,class_name;
        Button confirm;
    }
}
