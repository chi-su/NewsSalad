package com.example.chisu.newssalad.activity;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.chisu.newssalad.R;
import com.google.android.flexbox.FlexboxLayoutManager;

public class CatViewHolder extends RecyclerView.ViewHolder {


    private ImageView mImageView;

    CatViewHolder(View itemView){
        super(itemView);

        mImageView = (ImageView) itemView.findViewById(R.id.catImageVIew);

    }

    //이미지뷰에 이미지를 넣는 메소드.
    void bindTo(Drawable drawable){

        //이미지뷰에 이미지를 설치하고
        mImageView.setImageDrawable(drawable);
        //이 이미지뷰의 인자들을 뷰그룹의 인자로 넘겨준다
        //만약에 내 걸로 바꾸고 싶으면... 제일 큰, 감싸는 레이아웃을 넣어주면 될 것이다.
        //뷰홀더 원본을 어떻게 만드는지가 중요할듯.
        //클릭 이벤트도 제일큰거에 달아주면 된다.
        ViewGroup.LayoutParams lp = mImageView.getLayoutParams();
        //만약 이 lp가 flexbox의 layoutparams라면
        if (lp instanceof FlexboxLayoutManager.LayoutParams){
            //다시 여기에 넣어주고.
            FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) lp;

            //이게 뭔지 잘 모르겠당.
            flexboxLp.setFlexGrow(1.0f);
        }
    }
}
