package luis_santiago.com.ailrun.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import luis_santiago.com.ailrun.POJOS.Run;
import luis_santiago.com.ailrun.R;

import static luis_santiago.com.ailrun.Constants.EXTRA_MS_LAPSE;

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

        private TextView mts_ran;
        private TextView kca_burned;
        private TextView time;
        private TextView dateTextView;

        public RunHolder(@NonNull View v) {
            super(v);
            mts_ran = v.findViewById(R.id.mts_ran);
            kca_burned = v.findViewById(R.id.kca_burned);
            time =  v.findViewById(R.id.time);
            dateTextView = v.findViewById(R.id.date);
        }

        public void bindRun(Run currentItem) {

            mts_ran.setText(currentItem.getKmRan() + "mts");
            kca_burned.setText(currentItem.getKcaBurned() + "kca");
            time.setText(String.valueOf(currentItem.getTimeElapsedMs()));
            Date date;
            try {
                date = new Date(currentItem.getDate());
            } catch (Exception e) {
                date = new Date();
            }

            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
            dateTextView.setText(dateFormat.format(date));
        }
    }
}
