package org.esec.mcg.bleinsight.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.esec.mcg.bleinsight.R;

import java.util.ArrayList;

/**
 * Created by yz on 2015/10/20.
 */
public class LogViewRecyclerAdapter extends RecyclerView.Adapter<LogViewRecyclerAdapter.LogViewRecyclerViewHolder> {
    private ArrayList<String> list;
    private LayoutInflater inflater;
    private static LogViewRecyclerAdapter instance = null;

    public static synchronized LogViewRecyclerAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new LogViewRecyclerAdapter(context);
        }

        return instance;
    }

    private LogViewRecyclerAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        list = new ArrayList<>();
        list.add("Yang Zhou");
        list.add("Liu Yue");
    }

    /**
     * 插入一条log
     * @param logString
     */
    public void insertLogItem(String logString) {
        list.add(logString);
        notifyItemInserted(list.size() - 1);
    }

    @Override
    public LogViewRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = inflater.inflate(R.layout.log_view_row, parent, false);
        LogViewRecyclerViewHolder holder = new LogViewRecyclerViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(LogViewRecyclerViewHolder holder, int position) {
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class LogViewRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public LogViewRecyclerViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.log_text);
        }
    }
}

