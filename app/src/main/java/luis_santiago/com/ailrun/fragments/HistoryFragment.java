package luis_santiago.com.ailrun.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;

import luis_santiago.com.ailrun.POJOS.Run;
import luis_santiago.com.ailrun.R;
import luis_santiago.com.ailrun.adapters.RunAdapter;
import luis_santiago.com.ailrun.helpers.FirebaseHelper;
import luis_santiago.com.ailrun.interfaces.OnRunsAvailable;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayout container;
    private RunAdapter runAdapter;
    private ArrayList<Run> mList;
    private ProgressBar progress_bar;
    private SwipeRefreshLayout swiperefresh;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        init(view);
        mList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        requestData();
        checkToShowEmptyBox();
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
                checkToShowEmptyBox();
            }
        });
        return view;
    }

    private void checkToShowEmptyBox() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mList.size() == 0) {
                    container.setVisibility(View.VISIBLE);
                    progress_bar.setVisibility(View.GONE);
                }
            }
        };

        new Handler().postDelayed(runnable , 2000);

    }

    private void requestData() {
        FirebaseHelper.getInstance().getListOfRuns(new OnRunsAvailable() {
            @Override
            public void onDataLoaded(ArrayList<Run> list) {
                mList = list;
                runAdapter = new RunAdapter(list , getActivity().getApplicationContext());
                mRecyclerView.setAdapter(runAdapter);
                if (list.size() == 0) {
                    container.setVisibility(View.VISIBLE);
                }
                progress_bar.setVisibility(View.GONE);
            }
        });

        stopRefreshing();
    }

    private void init(View view) {
        container = view.findViewById(R.id.container);
        mRecyclerView = view.findViewById(R.id.list);
        progress_bar = view.findViewById(R.id.progress_bar);
        swiperefresh = view.findViewById(R.id.swiperefresh);
    }

    private void stopRefreshing() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swiperefresh.setRefreshing(false);
            }
        }, 1000);
    }

}
