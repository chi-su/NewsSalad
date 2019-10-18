package com.example.chisu.newssalad.general.fragments;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.chisu.newssalad.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    private ArrayList<VodListItem> recyclerItemList = new ArrayList<>();

    ImageView imageView;
    Handler handler = new Handler();  // 외부쓰레드 에서 메인 UI화면을 그릴때 사용


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        imageView = findViewById(R.id.imageView0);
//        new DownloadImageTask( findViewById(R.id.imageView0))
//                .execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");

//        RecyclerView recyclerView = findViewById(R.id.testRecyclerView);
//
//        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getApplicationContext());
//        layoutManager.setFlexWrap(FlexWrap.WRAP);
//        Log.e("newsSalad", "onCreate(");
//
//        //가로로 쌓임
//        layoutManager.setFlexDirection(FlexDirection.ROW);
//        layoutManager.setAlignItems(AlignItems.STRETCH);
//        recyclerView.setLayoutManager(layoutManager);
//        RecyclerView.Adapter adapter = new FlexAdapter(recyclerItemList, this);
//        recyclerView.setAdapter(adapter);
//        Log.e("newsSalad", "onCreate(");
        Picasso.get().load("http://13.125.67.216/vodthumbnails/0.png").placeholder(R.drawable.cat_1).into(imageView);

//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {    // 오래 거릴 작업을 구현한다
//                // TODO Auto-generated method stub
//                try{
//                    // 걍 외우는게 좋다 -_-;
//                    final ImageView iv = (ImageView)findViewById(R.id.imageView0);
//                    URL url = new URL("http://developer.android.com/assets/images/android_logo.png");
//                    InputStream is = url.openStream();
//                    final Bitmap bm = BitmapFactory.decodeStream(is);
//                    handler.post(new Runnable() {
//
//                        @Override
//                        public void run() {  // 화면에 그려줄 작업
//                            iv.setImageBitmap(bm);
//                        }
//                    });
//                    iv.setImageBitmap(bm); //비트맵 객체로 보여주기
//                } catch(Exception e){
//
//                }
//
//            }
//        });
//
//        t.start();

    }

//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
//    }


}
