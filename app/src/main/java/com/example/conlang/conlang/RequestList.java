package com.example.conlang.conlang;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* ths code implements registration request list
it been derived from the following url
https://3alam.pro/khadijah-salim/series/recyclerview/lessons/part1
and it first edit by Asmaa Mostafa
*/
public class RequestList extends AppCompatActivity implements RequestListAdapter.ListItemClickListener {

    private TextView mTextMessage;

    private FirebaseFirestore db ;
    private FirebaseAuth mFirebaseAuth;

    private ProgressDialog progressDialog;

    private List<String> mIdList;
    private List<Map<String, Object>> mRequest;

    private static View emptyView;
    private RequestListAdapter mAdapter;
    private RecyclerView mRequestList;
    private LinearLayoutManager layoutManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
 /* for the navigation bar  */
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(RequestList.this,RequestList.class));
                    return true;
                case R.id.navigation_dashboard:
                    startActivity(new Intent(RequestList.this,ManageAccount.class));
                    return true;
                case R.id.navigation_notifications:
                    startActivity(new Intent(RequestList.this,Report.class));
                    return true;
            }
            return false;
        }
    };

/* the first method to be executed */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);
        initElement();
        viewRecyclerView();

    }
    /*IN THIS METHOD WE INITIATE ALL ELEMENT TO MAKE CODE MORE READABLE*/
    private void initElement() {
        // for the element inside the list
        FirebaseApp.initializeApp(this);
        mTextMessage = (TextView) findViewById(R.id.message);
        mRequest=new ArrayList<>();
        mIdList = new ArrayList<>();
        emptyView = findViewById(R.id.empty_view);
        mRequestList = findViewById(R.id.rv);// recycler view
        // for the Firebase
        //mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // for semi notification
        progressDialog = new ProgressDialog(this);
        // for the navigating bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
/**/
   private void viewRecyclerView(){
       layoutManager = new LinearLayoutManager(this);
       getDataFromDatabase();
       mRequestList.setHasFixedSize(true);
       mRequestList.setLayoutManager(layoutManager);
       mRequestList.setAdapter(mAdapter);
   }
   /**/
    private void getDataFromDatabase() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();


        db.collection("Registration Request").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.getResult() != null){
                    getDate(task);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RequestList.this,"Error",Toast.LENGTH_LONG);
                }
            }
        });
    }
    /**/
    private void getDate(Task<QuerySnapshot> task) {
        boolean isEmpty = true;
        for (DocumentSnapshot document : task.getResult()) {
            Map<String, Object> Request = document.getData();
                    mIdList.add(document.getId());
                    mRequest.add(Request);
                    if (Request.size() != 0)
                        isEmpty = false;
            }

        displayListView(isEmpty);
    }

    /**/
    private void displayListView(boolean isEmpty) {
        if(!isEmpty ){
            //emptyView.setVisibility(View.GONE);
            initAdapter();
        }else {
            progressDialog.dismiss();
            emptyView.setVisibility(View.VISIBLE);
        }
    }
    /**/
    private void initAdapter() {
        mAdapter = new RequestListAdapter(mRequest,this);
        mRequestList.setAdapter(mAdapter);
        progressDialog.dismiss();
    }

    @Override
    public void onListItemClick(int clickedItemIndex, RequestListAdapter.ListItemClickListener listener) {
        HashMap<String, Object> request =(HashMap) mRequest.get(clickedItemIndex);
        Context context = RequestList.this;
        Class RequestClass= RequestDetail.class;
        Intent intent = new Intent(context,RequestClass);
        intent.putExtra("Request", request);
        startActivity(intent);
    }

    public static View getEmptyView(){
        return emptyView;
    }

}
