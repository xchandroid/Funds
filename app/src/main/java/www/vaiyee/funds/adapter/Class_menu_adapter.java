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
 * Created by Administrator on 2018/12/1.
 */

public class Class_menu_adapter extends BaseAdapter {
    private List<Class> classList;
    private Context context;
    public Class_menu_adapter(Context context,List<Class>classList)
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
        return classList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Class c = classList.get(i);
        ViewHolder viewHolder;
        if (view==null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.class_menu_item,viewGroup,false);
            viewHolder = new ViewHolder();
            viewHolder.name = view.findViewById(R.id.class_name);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(c.getName());
        return view;
    }

    class ViewHolder
    {
        TextView name;
    }
}
