package com.example.chisu.newssalad.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllegalFormatCodePointException;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//내가 원하는 로그를 json 형식으로 로컬에 파일 형태로 저장하는 클래스.
//싱글톤으로 사용하기 위해 따로 클래스를 만듦.
public class LogManager {
    Context context;

    //필요할 때 이미 생성되어 있는 로그4j매니저를 가져오는 메소드.
    //new 가 최초 한번만 실행되면 되기 때문에 메모리에 이점이 있다.
    public static synchronized LogManager getLogManagerInstance(Context context1) {

        if (logManagerInstance == null) {
            logManagerInstance = new LogManager(context1);
        }
        return logManagerInstance;
    }


    //이 매니저는 모든 액티비티에서 onResume마다 쓰인다.
    public static LogManager logManagerInstance;

    public LogManager(Context context1) {
        //로그매니저의 컨텍스트를 그때그때 바꿔준다. 액티비티 로그를 제대로 찍기 위해서.
        context = null;
        context = context1;


    }

    //로그파일이 전송될 폴더

    //로그에 적을 시간을 구하는 메소드
    public String getTime() {
        //현재 시간 가져오기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        String getTime = sdf.format(date);

        return getTime;
    }

    //로그파일의 크기를 확인하는 메소드
    public long getFileSize(String filepath) {
        //여기서 size의 단위는 byte
        long lFileSize;
        File mFile = new File(filepath);
        if (mFile.exists()) {
            lFileSize = mFile.length();
            return lFileSize;
        } else {
            Log.e("newsSalad", "file is not exist");
            return 0;
        }
    }

    //로그를 쓰는 메소드
    public void appendLog(String activityName) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LOG/";
        File logFile = new File(dirPath + "log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // 적절한 예외처리를 해주면됩니다.
                e.printStackTrace();
            }
        }
        try {
            //파일의 크기가 일정 수준 이상 넘어가면 그 파일을 서버로 보내고, 파일을 지운다.

            //퍼포먼스를 위해 BufferedWriter를 썼고 FileWriter의 true는 파일을 이어쓸 수 있게 하기 위해서 해줬습니다.
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            //로그용 랜덤 로그. 들어가는 정보는 나이와 시간, 성별과 액티비티.

            Random randomage = new Random();
            //0은 남자, 1은 여자
            String sex;
            if (randomage.nextInt(2) == 0) {
                sex = "man";
            } else {
                sex = "woman";
            }

            String logTest = getTime() + "|" + (randomage.nextInt(70) + 5) + "|" + sex + "|" + activityName;
            buf.append(logTest);
            //2kb를 넘는지 체크해서 넘으면 서버로 보내고 삭제. 열고 있는 파일도 되는거 같다.
            //보내기 직전에 개행문자를 넣어서 보내면 null 데이터가 생기므로
            //보내기 직전에는 newLine을 해주지 않는다.
            if (getFileSize(logFile.getPath()) > 200) {
                uploadLogFile();
            } else {
                buf.newLine();
            }
            buf.close();
        } catch (IOException e) {
            // 적절한 예외처리를 해주면됩니다.
            e.printStackTrace();
        }
    }

    //쌓아놨던 로그를 php 서버로 푸쉬하는 메소드...
    public void uploadLogFile() {

        //로그파일을 보내는 스레드는 여기서 만들어놓고 나중에 시작시킨다.
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LOG/";
                File file = new File(dirPath + "log.txt");

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.getName(),
                                RequestBody.create(MediaType.parse("text/csv"), file))
                        .build();

                okhttp3.Request request = new Request.Builder()
                        .url(URLs.LOG_UPLOAD_URL)
                        .post(requestBody)
                        .build();
                Log.e("newsSalad", " upload sended");

                //Check the response
                //이 부분은 스레드로 처리해야 함.
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Log.e("newsSalad", " uploadOK");
                    Log.e("newsSalad", response.toString());
                    file.delete();
                    Log.e("newsSalad", "로그 파일 삭제 완료");

                } catch (Exception e) {
                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                    Log.e("newsSalad error : ", errors.toString());
                }
            }
        });

        thread.start();
    }
}
