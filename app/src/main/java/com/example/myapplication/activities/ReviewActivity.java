package com.example.myapplication.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.ExampleReview;
import com.example.myapplication.R;
import com.example.myapplication.ReviewAdapter;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;


public class ReviewActivity extends AppCompatActivity implements com.example.myapplication.ReviewActivity {

    private RecyclerView mRecyclerView;
    ArrayList<ExampleReview> mExampleList;

    //bridge between data n recyler view.
    private ReviewAdapter mAdapter;

    ArrayList<ExampleReview> reviewList;

    TextInputLayout getStrTitle,getStrReviewDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_activity);

        getStrTitle = findViewById(R.id.edittext_line_1);
        getStrReviewDesc = findViewById(R.id.edittext_line_2);

        setTitle(R.string.review_title);


        reviewList = new ArrayList<>();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ReviewAdapter(reviewList,ReviewActivity.this);



        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button




        Button buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(v -> {
            saveData();
            Toast.makeText(ReviewActivity.this,"Review has been saved.", Toast.LENGTH_LONG).show();


        });
        loadData();
        setInsertButton();
    }





    public void updateRecyclerView(ArrayList<ExampleReview> newList){
        mAdapter = new ReviewAdapter(newList,ReviewActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }



    private void saveData() {

        SharedPreferences sp = getSharedPreferences("shared pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        ExampleReview exampleReview = new ExampleReview(Objects.requireNonNull(getStrTitle.getEditText()).getText().toString(), Objects.requireNonNull(getStrReviewDesc.getEditText()).getText().toString());
        mExampleList.add(exampleReview);


        Gson gson = new Gson();
        String json = gson.toJson(mExampleList);
        editor.putString("review list", json);
        editor.apply();

    }



    private void loadData() {
        SharedPreferences sp = getSharedPreferences("shared pref", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("review list", null);
        Type type = new TypeToken<ArrayList<ExampleReview>>() {
        }.getType();
        mExampleList = gson.fromJson(json, type);
        reviewList = gson.fromJson(json, type);

        if(reviewList!=null) {
            mAdapter = new ReviewAdapter(reviewList,ReviewActivity.this);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
        if (mExampleList == null) {
            mExampleList = new ArrayList<>();

        }

    }

    private void setInsertButton() {
        Button buttonInsert = findViewById(R.id.button_insert);
        buttonInsert.setOnClickListener(v -> {

            insertItem(Objects.requireNonNull(getStrTitle.getEditText()).getText().toString(), Objects.requireNonNull(getStrReviewDesc.getEditText()).getText().toString());
            Toast.makeText(ReviewActivity.this,"Review has been added, remember to save.", Toast.LENGTH_LONG).show();
        });
    }





    private void insertItem(String getStrTitle, String getStrReviewDesc) {
        reviewList.add(new ExampleReview(getStrTitle, getStrReviewDesc));
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onLongClick(int postition) {
        reviewList.remove(postition);
        mAdapter.notifyItemRemoved(postition);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.review_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.act_search);


        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean onQueryTextSubmit(String query) {
                return false;
            }



            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()) {
                    mAdapter.getFilter().filter(newText);
                }else{
                    mAdapter = new ReviewAdapter(reviewList,ReviewActivity.this);//This is null
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        return true;
    }


}

