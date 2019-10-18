package com.example.chisu.newssalad.token;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chisu.newssalad.R;
import com.example.chisu.newssalad.streaming.viewer.ViewerActivity;
import com.example.chisu.newssalad.utils.LogManager;
import com.example.chisu.newssalad.utils.SharedPreferenceManager;
import com.example.chisu.newssalad.utils.URLs;
import com.example.chisu.newssalad.utils.User;

import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.example.chisu.newssalad.general.HomeActivity.CUSTOM_GAS_PRICE;
import static org.web3j.tx.Contract.GAS_LIMIT;

public class BuyTokenActivity extends AppCompatActivity {
    //카카오페이 페이지를 나타낼 웹뷰
    private WebView mWebView;

    private WebSettings mWebSettings;

    //구매할 토큰의 갯수
    String tokenAmount;

    //이더리움 지갑 관련 변수들
    //지갑 안의 인증서 변수
    org.web3j.crypto.Credentials credentials;
    //web3j 클라이언트
    Web3j web3j;
    //지갑 주소를 가져오기 위한 유저 객체 선언
    User user;
    //지갑 주소를 담을 스트링 객체
    String walletPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_token);

        mWebView = findViewById(R.id.kakaoPayWebView);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

        //발리로 구매 제품 관련 정보를 보내야 한다.
        Intent intent = getIntent();
        tokenAmount = intent.getStringExtra("tokenAmount");
        readyForPayment(tokenAmount);
    }

    @Override
    protected void onResume(){
        super.onResume();

        user = SharedPreferenceManager.getInstance(getApplicationContext()).getUser();
        //로그 매니저
        LogManager logManager;
        //최초로 생성된 로그 매니저를 가져오기.
        logManager = LogManager.getLogManagerInstance(BuyTokenActivity.this);
        String log = getClass().getSimpleName().trim();
        logManager.appendLog(log);
    }

    //결제준비 화면으로 넘어가는 메소드.
    public void readyForPayment(String tokenAmount) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.PAYMENT_READY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("newsSalad url", response);
                        mWebView.loadUrl(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("newsSalad", error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tokenAmount", tokenAmount);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    //클라이언트 설정을 위해 따로 클래스를 만들어 주었다.
    //나중에 세팅할때도 이 클라이언트를 사용한다.
    class MyWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //결제 완료된 후 처리하기 위해 3초간 기다렸다가 종료시켜준다.
            if (url.startsWith("http://52.79.75.232/PaymentSuccessApi.php?")){

                //결제를 완료했으므로 토큰을 사용자 지갑에 전송하는 작업을 시작한다.
                TokenBuyTask tokenBuyTask = new TokenBuyTask();
                tokenBuyTask.execute();
            }

            //url이 intent로 시작하면
            if (url != null && url.startsWith("intent://")) {
                try {
                    // http://”, “ftp://”, “market://”과 같은 문자열을 url scheme이라 부릅니다.
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    //특정 앱을 실행하기 위해,
                    //즉 특정 앱으로 가는 인텐트를 얻기 위해 패키지매니저의 함수를 실행한다.
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());

                    if (existPackage != null){
                        //제대로 실행되면 그대로 액티비티 열기.
                        startActivity(intent);
                    } else {
                        //그렇지 않으면 uri를 다시 설정해서 열기.
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse("market://details?id="+intent.getPackage()));
                        startActivity(marketIntent);
                    }
                    return true;
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //url이 market으로 시작할 경우
            else if (url != null && url.startsWith("market://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent != null) {
                        startActivity(intent);
                    }
                    return true;
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            view.loadUrl(url);
            return false;
        }
    }

    //프로그래스다이얼로그를 틀기 위해 어싱크태스크를 사용했다.
    private class TokenBuyTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress("토큰 구매 진행 중입니다...");
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            hideProgress();
            Toast.makeText(getApplicationContext(), "결제가 완료되었습니다.", Toast.LENGTH_LONG).show();
            finish();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            //스트링을 long으로 바꿔주기
            long tokenLong = Long.valueOf(tokenAmount);
            //롱을 빅 인티저로 바꿔주기
            BigInteger sendingTokenAmount = BigInteger.valueOf(tokenLong);

            //web3j 클라이언트 생성
            web3j = Web3jFactory.build(new HttpService(URLs.INFURA_ADDRESS));
            //유저의 지값주소값 알아내기
            walletPath = user.getUserWalletFile();

            try {
                //유저 지갑 주소에서 인증서 로드
                credentials = WalletUtils.loadCredentials("wltn7994!", walletPath);
            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.e("newsSalad error : ", errors.toString());
            }

            //계약서 로드
            T11 contract = T11.load(URLs.CONTRACT_ADDRESS, web3j, credentials, CUSTOM_GAS_PRICE, GAS_LIMIT);

            try {
                //트랜잭션 영수증은 나중에 거래 기록을 조회하는 데 사용할 수 있다.
                //getBlockHash 메소드를 사용하면 됨.
                //토큰 구매해서 그 기록을 영수증에 적기.
                TransactionReceipt transactionReceipt1 = contract.buy(sendingTokenAmount).send();
                transactionReceipt1.getBlockHash();

                Log.e("newsSalad", "토큰이 구매되었습니다");
                Log.e("newsSalad", transactionReceipt1.getBlockHash());

            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.e("newsSalad error : ", errors.toString());
            }
            return null;
        }
    }

    // ================================================================================================================================================================================================================================
    //다이얼로그 관련 클래스, 변수, 메소드들.
    private ProgressDialog pd;
    //프로그래스바를 보여주는 메소드
    public void showProgress(String msg) {
        if( pd == null ) {
            pd = new ProgressDialog(this);
            pd.setCancelable(false);
        }
        pd.setMessage(msg);
        // 원하는 메시지를 세팅한다.
        pd.show();
        // 화면에 띄워라
    }
    // 프로그레스 다이얼로그 숨기기
    public void hideProgress() {
        if( pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

}
