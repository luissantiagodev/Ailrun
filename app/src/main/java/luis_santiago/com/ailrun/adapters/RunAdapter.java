package luis_santiago.com.ailrun.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import luis_santiago.com.ailrun.POJOS.Run;
import luis_santiago.com.ailrun.R;

/**
 * Created by Luis Santiago on 10/26/18.
 */
public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunHolder> {

    private ArrayList<Run> mList;
    private Context mContext;

    public RunAdapter(ArrayList<Run> list , Context mContext) {
        this.mList = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RunHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_last_run , viewGroup , false);
        return new RunHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RunHolder runHolder, int i) {
        Run currentItem = mList.get(i);
        runHolder.bindRun(currentItem);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class RunHolder extends RecyclerView.ViewHolder {
        public RunHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindRun(Run currentItem) {

        }
    }
}
