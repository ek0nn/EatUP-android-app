package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.activities.ReviewActivity;

import java.util.ArrayList;
import java.util.List;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ExampleViewHolder> implements Filterable {
    private ArrayList<ExampleReview> mExampleList;
    private ArrayList<ExampleReview> mFilteredList;
    //to filter
    String MY_PREFERNCE_NAME = "shared pref";
    String SP_TOTAL_KEY = "sp_key";
    private ArrayList<ExampleReview> mExampleListFull;
    private ReviewActivity reviewActivity;

    public static void removeDataFromPref(ReviewActivity reviewActivity) {
    }

    Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<ExampleReview> tempList=new ArrayList<ExampleReview>();
            // filter code
            if(constraint != null && mExampleList!=null) {
                int length= mExampleList.size();
                int i=0;
                while(i<length){
                    ExampleReview item= mExampleList.get(i);

                    if(item.getStrTitle().contains(constraint.toString()))  {  // fill the tempList

                        tempList.add(item);
                    }

                    i++;
                }

                //publish result can only take FilterResults users
                filterResults.values = tempList;
                filterResults.count = tempList.size();
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            mFilteredList = (ArrayList<ExampleReview>) results.values;
            reviewActivity.updateRecyclerView(mFilteredList);
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                //notifyDataSetInvalidated();
            }
        }
    };
    @Override
    public Filter getFilter() {
        return myFilter;
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public TextView textView1;
        public TextView textView2;
        public ImageView imageView;
        private ImageView deleteButton;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.titleTextView);
            textView2 = itemView.findViewById(R.id.reviewDescTextView);



            deleteButton = itemView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Remove the item from the data source
                    int position = getAbsoluteAdapterPosition();
                    mExampleList.remove(position);

                    // Update the RecyclerView

                    notifyItemRemoved(position);
                }
            });


        }



    }


    public ReviewAdapter(ArrayList<ExampleReview> exampleReview,
                         ReviewActivity _reviewActivity) {

        this.reviewActivity = _reviewActivity;
        mExampleList = exampleReview;
        //filter

        mExampleListFull = new ArrayList<>(mExampleList);
        mFilteredList = new ArrayList<>(exampleReview);

    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_review, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;

    }


    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        ExampleReview currentReview = mExampleList.get(position);

        holder.textView1.setText(currentReview.getStrTitle());
        holder.textView2.setText(currentReview.getStrReviewDesc());


    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }


}
