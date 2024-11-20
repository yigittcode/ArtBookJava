package com.yigit.artbookjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.yigit.artbookjava.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    ArrayList <Art> artArrayList;
    ArtAdaptor artAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mainBinding.getRoot();
        setContentView(view);
        artArrayList = new ArrayList<>();
        mainBinding.recylerView.setLayoutManager(new LinearLayoutManager(this));
        artAdaptor = new ArtAdaptor(artArrayList);
        mainBinding.recylerView.setAdapter(artAdaptor);

        getData();
    }
    private  void getData (){
        try {
            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("Arts", MODE_PRIVATE ,null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM arts", null);
            int nameIndex = cursor.getColumnIndex("artname");
            int IdIndex = cursor.getColumnIndex("id");
            while (cursor.moveToNext()){
                String name = cursor.getString(nameIndex);
                int id = cursor.getInt(IdIndex);
                Art art = new Art(name , id);
                artArrayList.add(art);


            }

            artAdaptor.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.art_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_art) {
            Intent intent = new Intent(MainActivity.this, ArtActivity.class);
            intent.putExtra("info", "new");
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}