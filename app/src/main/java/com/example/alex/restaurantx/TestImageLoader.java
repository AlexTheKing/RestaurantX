package com.example.alex.restaurantx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.alex.restaurantx.adapter.ImageAdapter;
import com.example.alex.restaurantx.callbacks.IClickCallback;

public class TestImageLoader extends AppCompatActivity {

    private static final String[] IMAGE_URLS =
            {
                    "https://images-na.ssl-images-amazon.com/images/G/01/img15/pet-products/small-tiles/30423_pets-products_january-site-flip_3-cathealth_short-tile_592x304._CB286975940_.jpg",
                    "https://s-media-cache-ak0.pinimg.com/236x/8a/1b/7c/8a1b7c35091025bf2417ce2d9a6b058d.jpg",
                    "https://cnet4.cbsistatic.com/hub/i/2011/10/27/a66dfbb7-fdc7-11e2-8c7c-d4ae52e62bcc/android-wallpaper5_2560x1600_1.jpg",
                    "https://www.android.com/static/img/home/more-from-2.png",
                    "http://www.howtablet.ru/wp-content/uploads/2016/04/%D0%9E%D0%B1%D0%BD%D0%BE%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5-Android-6.0.1-Marshmallow.jpg",
                    "http://keddr.com/wp-content/uploads/2015/12/iOS-vs-Android.jpg",
                    "https://www.android.com/static/img/history/features/feature_icecream_3.png",
                    "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcRfZ5OiAt7GIz57jyvjK8ca82pIvgd7pvD-3JyPG73ppN8FbqpbUA",
                    "http://androidwallpape.rs/content/02-wallpapers/131-night-sky/wallpaper-2707591.jpg"
            };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
        RecyclerView recyclerView = (RecyclerView) findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new ImageAdapter(IMAGE_URLS, new IClickCallback() {
            @Override
            public void onClick(int pPosition) {
                Toast.makeText(TestImageLoader.this, (pPosition + ""), Toast.LENGTH_SHORT).show();
            }
        }));
    }
}
