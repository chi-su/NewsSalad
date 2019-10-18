package com.example.chisu.newssalad.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chisu.newssalad.R;

//리사이클러뷰의 어댑터는 뷰홀더가 필요하다.
//뷰홀더가 있으면 리소스를 더 경제적으로 사용할 수 있다.
public class CatAdapter extends RecyclerView.Adapter<CatViewHolder> {

    private static final int[]CAT_IMAGE_IDS = new int[]{
            R.drawable.cat_1,
            R.drawable.cat_2,
            R.drawable.cat_3,
            R.drawable.cat_4,
            R.drawable.cat_5,
            R.drawable.cat_6,
            R.drawable.cat_7,
            R.drawable.cat_8,
            R.drawable.cat_9,
            R.drawable.cat_10,
            R.drawable.cat_11,
            R.drawable.cat_12,
            R.drawable.cat_13,
            R.drawable.cat_14,
            R.drawable.cat_15,
            R.drawable.cat_16,
            R.drawable.cat_17,
            R.drawable.cat_18,
            R.drawable.cat_19
    };

    private Context mContext;

    CatAdapter(Context context){
        mContext = context;
    }

    //캣뷰홀더를 리턴하는 메소드.
    @Override
    public CatViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        //xml 레이아웃파일을 인플레이트해서 방금 생성한 뷰 객체에 넣어준다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cat, parent, false);
        //xml을 view로 변환한 것을 리턴.
        return new CatViewHolder(view);
    }

    //뷰홀더에 데이터를 넣는 메소드.
    //스크롤을 내리면서 데이터가 표시되어야 할 때마다 지속적으로 실행된다.
    @Override
    public void onBindViewHolder(CatViewHolder holder, int position){
       //이 예제에서는 실제 바인드 작업을 어댑터에서 하지 않고 뷰홀더에서 처리한다.
       //구현하기 나름이긴 하지만...

        //배열의 인덱스는 0부터 시작하므로 0~18까지, 총 19개가 있다.
        //또한 배열의 길이는 1부터 시작하므로 1~19까지, 즉 길이는 19이다.
        //19개의 길이를 가진 배열에서 19번째 인덱스를 찾으려고 하면 에러가 난다.
        //위의 포지션은 1부터 시작하므로 필연적으로 에러가 날 수밖에 없으므로,
        //해당 포지션을 배열의 길이로 나눈 결과를 새로운 변수에 넣어준다.
        //그렇게 되면 pos는 0~18의 범위를 가져 에러가 나지 않게 된다.

        int pos = position % CAT_IMAGE_IDS.length;
//        String s = String.valueOf(pos);
//        Log.e("newsSalad : pos is : ", s);

        //리소스를 가져와서 drawable 변수에 넣는다.
        Drawable drawable = ResourcesCompat.getDrawable(mContext.getResources(), CAT_IMAGE_IDS[pos], null);
        //그 drawable을 이미지뷰에 적용시키기
        holder.bindTo(drawable);
    }

    //보여줘야 할 아이템 카운트 리턴.
    //여기서는 배열의 4번이므로 결과적으로 총 4번 반복해서 보여주게 된다.
    @Override
    public int getItemCount(){
        return CAT_IMAGE_IDS.length * 4;
    }

}
