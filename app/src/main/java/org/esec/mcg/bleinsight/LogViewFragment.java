package org.esec.mcg.bleinsight;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.esec.mcg.bleinsight.adapter.LogViewRecyclerAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LogViewRecyclerAdapter mLogViewRecyclerAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LogViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogViewFragment newInstance() {
        LogViewFragment fragment = new LogViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public LogViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mLogViewRecyclerAdapter = LogViewRecyclerAdapter.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(getActivity());
        recyclerView.setAdapter(mLogViewRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}

/**
 * 这是一个私有(private)类，并不是一个public类，因此只能在此文件中被访问
 */
//class LogViewRecyclerAdapter extends RecyclerView.Adapter<LogViewRecyclerAdapter.LogViewRecyclerViewHolder> {
//    private ArrayList<String> list;
//    private LayoutInflater inflater;
//    private static LogViewRecyclerAdapter instance = null;
//
//    public static synchronized LogViewRecyclerAdapter getInstance(Context context) {
//        if (instance == null) {
//            instance = new LogViewRecyclerAdapter(context);
//        }
//
//        return instance;
//    }
//
//    private LogViewRecyclerAdapter(Context context) {
//        inflater = LayoutInflater.from(context);
//        list = new ArrayList<>();
//        list.add("Yang Zhou");
//        list.add("Liu Yue");
//    }
//
//    /**
//     * 插入一条log
//     * @param logString
//     */
//    public void insertLogItem(String logString) {
//        list.add(logString);
//        notifyItemInserted(list.size() - 1);
//    }
//
//    @Override
//    public LogViewRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View root = inflater.inflate(R.layout.log_view_row, parent, false);
//        LogViewRecyclerViewHolder holder = new LogViewRecyclerViewHolder(root);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(LogViewRecyclerViewHolder holder, int position) {
//        holder.textView.setText(list.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    class LogViewRecyclerViewHolder extends RecyclerView.ViewHolder {
//        TextView textView;
//
//        public LogViewRecyclerViewHolder(View itemView) {
//            super(itemView);
//            textView = (TextView) itemView.findViewById(R.id.log_text);
//        }
//    }
//}
