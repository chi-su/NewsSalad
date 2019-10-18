package com.example.chisu.newssalad.general;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chisu.newssalad.test.opencvActivity;
import com.example.chisu.newssalad.token.BuyTokenActivity;
import com.example.chisu.newssalad.R;
import com.example.chisu.newssalad.general.fragments.TestPagerAdapter;
import com.example.chisu.newssalad.streaming.broadcaster.ReadyBroadcastActivity;
import com.example.chisu.newssalad.token.MakeWalletActivity;
import com.example.chisu.newssalad.token.T11;
import com.example.chisu.newssalad.utils.SharedPreferenceManager;
import com.example.chisu.newssalad.utils.URLs;
import com.example.chisu.newssalad.utils.User;
import com.heinrichreimersoftware.materialdrawer.DrawerActivity;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;

/**
 * 사용자가 입장하면 처음으로 보게 되는 액티비티.(로그인됐을 때)
 * 선택에 따라 방송 준비 액티비티, VOD 액티비티로 갈 수 있다.
 * 드로어에서 사용자의 토큰 갯수를 체크할 수 있다.
 */

//드로어 라이브러리를 사용하기 위해 드로어액티비티를 상속받음
public class HomeActivity extends DrawerActivity {

    //스태틱으로 선언하는 이유. 여러 군데에서 공유하기 때문.
    public static final BigInteger CUSTOM_GAS_PRICE = Convert.toWei("8", Convert.Unit.GWEI).toBigInteger();
    static final BigInteger CUSTOM_GAS_LIMIT = BigInteger.valueOf(60000);

    Toolbar toolbar;

    Web3j web3j;

    Credentials credentials;
    User user;
    Handler handler;
    BigInteger tokenAmount;
    String walletPath;

    //TokenItem을 여러 곳에서 사용하기 위해 전역 변수로 설정.
    DrawerItem drawerTokenItem;

    //뒤로가기 버튼이 클릭된 시간
    private long lastTimeBackPressed;
    //채팅 및 스트리밍 방에서 뒤로가기 버튼을 눌렀을 때
    //뒤로가기 버튼을 2초 이내에 2번 연속으로 눌러야 종료되도록 했다.

    private int[] tabIcons = {
            R.drawable.ic_presentation_play_white_24dp,
            R.drawable.ic_badminton_white_24dp,
            R.drawable.ic_flask_outline_white_24dp,
            R.drawable.ic_guitar_electric_white_24dp,
            R.drawable.ic_currency_usd_white_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        TestPagerAdapter testPagerAdapter = new TestPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.homeViewPager);
        viewPager.setAdapter(testPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setSelectedTabIndicatorColor(Color.RED);
        tabLayout.setTabTextColors(Color.WHITE, Color.RED);

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);

        //라이브러리로 프로필 및 드로어 아이템 추가.
        addProfile(
                new DrawerProfile()
                        .setRoundedAvatar((BitmapDrawable) getResources().getDrawable(R.drawable.cat_1))
                        .setBackground(getResources().getDrawable(R.drawable.cat_2))
                        .setName("이름")
                        .setDescription("안녕하세요~")
                        .setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
                            @Override
                            public void onClick(DrawerProfile drawerProfile, long id) {
                            }
                        })
        );

        addItem(
                drawerTokenItem = new DrawerItem()
                        .setImage(getResources().getDrawable(R.drawable.cat_1))
                        .setTextPrimary("토큰 갯수")
                        .setTextSecondary("로딩 중입니다...")
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, long id, int position) {
                            }
                        })
        );
//        addDivider();
        addItem(
                new DrawerItem()
                        .setImage(getResources().getDrawable(R.drawable.cat_2))
                        .setTextPrimary("토큰 구매하기")
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, long id, int position) {
                                showTokenBuyDialog();
                            }
                        })
        );
        addItem(
                new DrawerItem()
                        .setImage(getResources().getDrawable(R.drawable.cat_2))
                        .setTextPrimary("방송하기")
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, long id, int position) {
                                Intent intent = new Intent(getApplicationContext(), ReadyBroadcastActivity.class);
                                startActivity(intent);
                            }
                        })
        );

        addItem(
                new DrawerItem()
                        .setImage(getResources().getDrawable(R.drawable.cat_2))
                        .setTextPrimary("지갑 만들기")
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, long id, int position) {
                                Intent intent = new Intent(getApplicationContext(), MakeWalletActivity.class);
                                startActivity(intent);
                            }
                        })
        );
        addItem(
                new DrawerItem()
                        .setImage(getResources().getDrawable(R.drawable.cat_2))
                        .setTextPrimary("openCV")
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, long id, int position) {
                                Intent intent = new Intent(getApplicationContext(), opencvActivity.class);
                                startActivity(intent);
                            }
                        })
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //sp에 저장된 지갑 관련 정보를 가져옴(지갑 주소, 지갑 파일 경로)
                user = SharedPreferenceManager.getInstance(getApplicationContext()).getUser();
                walletPath = user.getUserWalletFile();

                //이더리움 테스트넷에 연결하기 위해 web3j 객체 생성.
                web3j = Web3jFactory.build(new HttpService(URLs.INFURA_ADDRESS));

                try {
                    //지갑에서 인증서 가져오기
                    credentials = WalletUtils.loadCredentials("wltn7994!", walletPath);

                    //web3j 클라이언트 연결 여부 및 버전 확인
                    Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
                    Log.e("newsSalad!!", web3ClientVersion.getWeb3ClientVersion());

                    //컨트랙트와 통신하기 위해 컨트랙트를 불러오기
                    T11 contract = T11.load(URLs.CONTRACT_ADDRESS, web3j, credentials, CUSTOM_GAS_PRICE, CUSTOM_GAS_LIMIT);

                    //토큰 갯수 확인해서 저장.
                    tokenAmount = contract.balanceOf(credentials.getAddress()).sendAsync().get();

                    Log.e("real address", credentials.getAddress());

                } catch (Exception ex) {
                    //printstacktrace가 에러 로그를 가장 자세히 보여주는데
                    //이거는 스트링 형태가 아니라서 좀 다른 방식으로 출력한다.
                    //printstacktrace가 좋은 방법이 아니라는데 그에 대해서는 나중에 찾아볼 것.
                    //사실 안할거같다.
                    StringWriter errors = new StringWriter();
                    ex.printStackTrace(new PrintWriter(errors));
                    Log.e("newsSalad error : ", errors.toString());
                }
                //ui 작업 부분
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //onCreate에서 만든 토큰아이템의 토큰 갯수를 업데이트한다.
                        drawerTokenItem.setTextSecondary(tokenAmount.toString());
                    }
                });
            }
        });
        thread.start();
    }

    void showTokenBuyDialog() {
        final EditText edittext = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("토큰 구매하기");
        builder.setMessage("구매할 토큰의 갯수를 입력하세요.");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String tokenAmount = edittext.getText().toString();
                        Intent intent = new Intent(getApplicationContext(), BuyTokenActivity.class);
                        intent.putExtra("tokenAmount", tokenAmount);
                        startActivity(intent);
                }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }


    @Override
    public void onBackPressed() {
        //2초 이내에 뒤로가기 버튼을 재 클릭 시 앱 종료
        if (System.currentTimeMillis() - lastTimeBackPressed < 2000) {
            finish();
            return;
        }
        //'뒤로' 버튼 한번 클릭 시 메시지
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        //lastTimeBackPressed에 '뒤로'버튼이 눌린 시간을 기록
        lastTimeBackPressed = System.currentTimeMillis();
    }
}
