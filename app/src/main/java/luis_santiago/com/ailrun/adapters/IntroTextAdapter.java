package luis_santiago.com.ailrun.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import luis_santiago.com.ailrun.R;

/**
 * Created by Luis Santiago on 9/9/18.
 */
public class IntroTextAdapter extends RecyclerView.Adapter<IntroTextAdapter.IntroTextViewHolder>{

    private ArrayList<String> mPhrases;

    public IntroTextAdapter(ArrayList<String> phrases){
        this.mPhrases = phrases;
    }

    @NonNull
    @Override
    public IntroTextViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_intro_text , viewGroup , false);
        return new IntroTextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntroTextViewHolder introTextViewHolder, int i) {
        String currentItem = mPhrases.get(i);
        introTextViewHolder.bindText(currentItem);
    }

    @Override
    public int getItemCount() {
        return mPhrases.size();
    }

    public class IntroTextViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        public IntroTextViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.main_text);
        }

        public void bindText(String text){
            textView.setText(text);
        }
    }
}
