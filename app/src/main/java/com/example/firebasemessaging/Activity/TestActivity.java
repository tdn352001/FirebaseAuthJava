package com.example.firebasemessaging.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import com.example.firebasemessaging.Adapter.TestAdapter;
import com.example.firebasemessaging.R;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    ArrayList<String> test;
    TestAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initTestList();
        initRV();
    }

    private void initRV() {
        recyclerView = findViewById(R.id.test_recyclerview);
        adapter = new TestAdapter(test, position -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(TestActivity.this);
            dialog.setTitle("Hello");
            dialog.setMessage(test.get(position));
            dialog.setNegativeButton("OK", null);
            dialog.show();
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(TestActivity.this, LinearLayoutManager.VERTICAL, false));
    }

    private void initTestList() {
        test = new ArrayList<>();
        test.add("Học bài lần 1");
        test.add("Học bài lần 2");
        test.add("Học bài lần 3");
        test.add("Học bài lần 4");
        test.add("Học bài lần 5");
        test.add("Học bài lần 6");
    }
}