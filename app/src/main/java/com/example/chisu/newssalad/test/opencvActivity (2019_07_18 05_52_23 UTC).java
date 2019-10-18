package com.example.chisu.newssalad.test;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.chisu.newssalad.R;
import com.example.chisu.newssalad.streaming.viewer.ViewerActivity;
import com.example.chisu.newssalad.utils.LogManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class opencvActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }
    private static final String TAG = "opencv";

    //카메라와 openCV 라이브러리 사이의 상호작용을 가능하게 하는 클래스.
    private CameraBridgeViewBase mOpenCvCameraView;

    //Mat는 행렬. opencv 라이브러리에 들어있는 클래스임.
    private Mat matInput;
    private Mat matResult;

    //네이티브 함수들은 C++ 함수를 가져온 것임
    public static native long loadCascade(String cascadeFileName);

    //얼굴인식 함수.
    public static native void detect(long cascadeClassifier_face, long cascadeClassifier_eye, long matAddrInput, long matAddrResult);

    //얼굴 구분자 변수
    public long cascadeClassifier_face = 0;
    //눈 구분자 변수
    public long cascadeClassifier_eye = 0;


    //미리 애셋 폴더에 넣어둔 xml 파일을 가져오기 위한 메소드
    private void copyFile(String filename) {
        //파일의 경로값을 구한다.
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        //경로값에 인자로 넘어온 파일명을 더해서 완전한 경로값을 만든다.
        //separator는 / 인듯.
        String pathDir = baseDir + File.separator + filename;

        //AssetManager 클래스를 사용하면, 앱 실행 시에 에셋 폴더에 포함된 파일을 읽을 수 있다.
        AssetManager assetManager = this.getAssets();

        //외부데이터를 받고 전송할 input/outputStream들.
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d(TAG, "copyFile :: 다음 경로로 파일복사 " + pathDir);
            //애셋 파일을 열어서 인풋 스트림에 넣어준다.
            inputStream = assetManager.open(filename);
            //앞전에 만들어 둔 경로값을 이용해 아웃풋스트림을 생성하고 할당해준다.
            outputStream = new FileOutputStream(pathDir);

            //1024개의 원소를 가진 바이트 배열을 생성한다.
            byte[] buffer = new byte[1024];
            int read;

            //inputstream의 데이터를 outputstream에 넣어준다.
            while ((read = inputStream.read(buffer)) != -1) {
                //inputStream의 read 함수는 스트림으로부터 바이트 하나를 읽어서 결과를 숫자로 반환한다.
                //만약 읽은 데이터가 없으면 -1을 반환한다.
                outputStream.write(buffer, 0, read);
            }

            //다 끝나면 종료하고 메모리 해제.
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 " + e.toString());
        }
    }

    //얼굴인식 학습 데이터 가져오기
    //onCreate나 권한을 받고 나서 한번만 실행된다.
    private void read_cascade_file() {
        copyFile("haarcascade_frontalface_alt.xml");
        copyFile("haarcascade_eye_tree_eyeglasses.xml");

        Log.d(TAG, "read_cascade_file:");

        cascadeClassifier_face = loadCascade("haarcascade_frontalface_alt.xml");
        Log.d(TAG, "read_cascade_file:");

        cascadeClassifier_eye = loadCascade("haarcascade_eye_tree_eyeglasses.xml");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //상태 바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_opencv);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else read_cascade_file(); //추가
        } else read_cascade_file(); //추가


        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
//        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        mOpenCvCameraView.setCameraIndex(1); // front-camera(1),  back-camera(0)

        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();

        //로그 매니저
        LogManager logManager;
        //최초로 생성된 로그 매니저를 가져오기.
        logManager = LogManager.getLogManagerInstance(opencvActivity.this);
        String log = getClass().getSimpleName().trim();
        logManager.appendLog(log);

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }


    //카메라가 찍는 동안 지속적으로 발동.
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //카메라에서 데이터를 얻어 행렬에 저장한다.
        matInput = inputFrame.rgba();

        //비어있지 않으면 비워주기
        if ( matResult != null ) matResult.release();

        //MatResult가 없을 경우 생성해 두기.
        if (matResult == null){
            matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());
        }
        //카메라로부터 영상을 읽어올 때  전면 카메라의 경우 영상이 뒤집혀서 읽히기 때문에  180도 회전 시켜줘야 한다
        Core.flip(matInput, matInput, 1);

        //C++에서 detect 함수를 실행한다.
        detect(cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(),
                matResult.getNativeObjAddr());

        return matResult;
    }

    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;

    //    String[] PERMISSIONS  = {"android.permission.CAMERA"};
    String[] PERMISSIONS = {"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};


    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED) {
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

//                    if (!cameraPermissionAccepted)
//                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                    boolean writePermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted || !writePermissionAccepted) {
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                        return;
                    } else {
                        read_cascade_file();
                    }

                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(opencvActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
}
