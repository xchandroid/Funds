package www.vaiyee.funds.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import www.vaiyee.funds.R;
import www.vaiyee.funds.bean.Funds;

/**
 * Created by Administrator on 2018/12/3.
 */

public class FundsAdapter extends BaseAdapter {
    Context context;
    List<Funds> fundsList;

    public FundsAdapter(Context context,List<Funds> fundsList)
    {
        this.context = context;
        this.fundsList = fundsList;
    }
    @Override
    public int getCount() {
        return fundsList.size();
    }

    @Override
    public Object getItem(int i) {
        return fundsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Funds funds = fundsList.get(i);
        ViewHolder viewHolder;
        if (view ==null)
        {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.funds_detail_item,viewGroup,false);
            viewHolder.type = view.findViewById(R.id.name);
            viewHolder.remainder = view.findViewById(R.id.remainder);
            viewHolder.time = view.findViewById(R.id.time);
            viewHolder.detail = view.findViewById(R.id.detail);
            view.setTag(viewHolder);
        }
        {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.type.setText(funds.getType());
        viewHolder.remainder.setText(funds.getRemainder());
        viewHolder.time.setText(funds.getTime());
        viewHolder.detail.setText(funds.getDetail());
        return view;
    }

    class ViewHolder
    {
        TextView type,remainder,time,detail;
    }
}
