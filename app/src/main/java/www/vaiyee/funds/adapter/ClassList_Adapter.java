package www.vaiyee.funds.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import www.vaiyee.funds.R;
import www.vaiyee.funds.bean.Class;

/**
 * Created by Administrator on 2018/12/3.
 */

public class ClassList_Adapter extends BaseAdapter {
    private Context context;
    private List<Class> classList;
    public ClassList_Adapter(Context context,List<Class> classList)
    {
        this.context = context;
        this.classList = classList;
    }
    @Override
    public int getCount() {
        return classList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        Class c = classList.get(i);
        if (view ==null)
        {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.class_list,viewGroup,false);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.remainder = view.findViewById(R.id.remainder);
            viewHolder.detail = view.findViewById(R.id.detail);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
        }
            viewHolder.name.setText(c.getName());
        viewHolder.remainder.setText("余额:"+c.getRemainder());
        viewHolder.detail.setText(c.getDetail());
        return view;
    }
    class ViewHolder
    {
        TextView name,remainder,detail;
    }
}
