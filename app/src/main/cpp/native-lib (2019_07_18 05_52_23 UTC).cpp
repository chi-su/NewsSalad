#include <jni.h>
//#include <string>
#include <opencv2/opencv.hpp>
#include <android/log.h>

using namespace cv;
using namespace std;

int DELAY_CAPTION = 1500;
int DELAY_BLUR = 100;
int MAX_KERNEL_LENGTH = 31;

Mat src;
Mat dst;
char window_name[] = "Filter Demo 1";

/// Function headers
int display_caption(char *caption);

int display_dst(int delay);

//extern "C" JNIEXPORT jstring JNICALL
//Java_com_example_chisu_newssalad_MainActivity_stringFromJNI(
//        JNIEnv *env,
//        jobject /* this */) {
//    std::string hello = "Hello from C++";
//    return env->NewStringUTF(hello.c_str());
//}



//extern "C"
//JNIEXPORT void JNICALL
//Java_com_example_chisu_newssalad_test_opencvActivity_ConvertRGBtoGray(JNIEnv *env, jobject instance,
//                                                                      jlong matAddrInput,
//                                                                      jlong matAddrResult) {
//
//    // TODO
//    // 입력 RGBA 이미지를 GRAY 이미지로 변환
//
//    Mat &matInput = *(Mat *)matAddrInput;
//    Mat &matResult = *(Mat *)matAddrResult;
//
//    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);
//}

float resize(Mat img_src, Mat &img_resize, int resize_width) {

    float scale = resize_width / (float) img_src.cols;
    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    } else {
        img_resize = img_src;
    }
    return scale;
}


//종속된 애셋 파일을 읽어오는 c 함수
//2개의 애셋 파일을 핸드폰의 폴더에 넣어준다.
extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_chisu_newssalad_test_opencvActivity_loadCascade(JNIEnv *env, jobject instance,
                                                                 jstring cascadeFileName_) {
//    const char *cascadeFileName = env->GetStringUTFChars(cascadeFileName_, 0);

    // TODO
    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName_, 0);
    //기본 경로값 얻기
    string baseDir("/storage/emulated/0/");
    //기본 경로값에 파일명 더해서 완성시키기
    baseDir.append(nativeFileNameString);

    //c_str함수 : string 을 char* 형으로 변환해주어
    //문자열 관련 함수들인 strcmp(), strcat() 등을 사용할 수 있게 해준다.
    const char *pathDir = baseDir.c_str();

    //long인듯
    jlong ret = 0;
    //파일 주소를 기반으로 구분자 생성
    ret = (jlong) new CascadeClassifier(pathDir);

    if (((CascadeClassifier *) ret)->empty()) {

        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",

                            "CascadeClassifier로 로딩 실패  %s", nativeFileNameString);

    } else

        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",

                            "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);

    env->ReleaseStringUTFChars(cascadeFileName_, nativeFileNameString);

    return ret;
//    env->ReleaseStringUTFChars(cascadeFileName_, cascadeFileName);
}

void salt(Mat image, int n) {

    int i, j;
    for (int k = 0; k < n; k++) {

        i = rand() % image.cols; //rand() : 렌덤함수입니다.
        j = rand() % image.rows;

        if (image.type() == CV_8UC1) {//그레이 영상에 노이즈 추가
            image.at<uchar>(j, i) = 255; //흰색 노이즈 추가
        } else if (image.type() == CV_8UC3) {//컬러 영상에 노이즈 추가
            image.at<Vec3b>(j, i)[0] = 0;
            image.at<Vec3b>(j, i)[1] = 0;
            image.at<Vec3b>(j, i)[2] = 0;
        }
    }
}


//얼굴 인식 함수
extern "C"{
JNIEXPORT void  JNICALL
Java_com_example_chisu_newssalad_test_opencvActivity_detect(JNIEnv *env, jobject instance,
                                                            jlong cascadeClassifier_face,
                                                            jlong cascadeClassifier_eye,
                                                            jlong matAddrInput,
                                                            jlong matAddrResult) {

    // TODO
    //이미지를 담을 행렬 변수
    //카메라에서 들어오는 input 행렬
    Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;

    //리절트에 인풋을 복사해서 넣어줌.
    img_result = img_input.clone();

    //얼굴들. 얼굴이 여러개여도 적용된다.
    std::vector<Rect> faces;

    Mat img_gray;

    //컬러를 바꾸는 함수.
    //인자는 각각 컬러를 바꿀 이미지, 바꾸고 난 이미지를 저장할 이미지, 바꿀 색깔
//    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);

    //히스토그램 평활화(Histogram Equalization)는,
    //영상의 히스토그램을 조절하여 명암 분포가 빈약한 영상을 균일하게 만들어주는 기법을 의미합니다.
    //인풋과 아웃풋을 같은 파일을 사용해도 된다.
//    equalizeHist(img_gray, img_gray);

    Mat img_resize;

    //img_gray를 640으로 리사이즈해서 img_resize에 넣어준다.
    float resizeRatio = resize(img_input, img_resize, 640);
    __android_log_print(ANDROID_LOG_ERROR, (char *) "native-lib :: ",
                        (char *) "resizeRatio %f found ", resizeRatio);

    //-- Detect faces
    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale(img_resize, faces, 1.1, 2,
                                                                     0 | CASCADE_SCALE_IMAGE,
                                                                     Size(30, 30));

    //얼굴이 몇개 찍혔는 지 알려주는 로그
    __android_log_print(ANDROID_LOG_ERROR, (char *) "native-lib :: ",
                        (char *) "face %d found ", faces.size());

    //얼굴 갯수만큼 돈다.
    for (int i = 0; i < faces.size(); i++) {

        //얼굴 시작 지점
        double real_facesize_x = faces[i].x / resizeRatio;
        double real_facesize_y = faces[i].y / resizeRatio;
        //얼굴 폭
        double real_facesize_width = faces[i].width / resizeRatio;
        //얼굴 길이
        double real_facesize_height = faces[i].height / resizeRatio;

        //Point는 2차원 위치를 나타내는 클래스이다.
        //객체간 덧셈 뺼셈이 가능하고, 비교 연산이 가능하다.
        //곱셈과 나눗셈은 스칼라연산과 가능하다.
        //타원을 그릴 때 사용할 가운데 포인트.
//        Point center(real_facesize_x + real_facesize_width / 2,
//                     real_facesize_y + real_facesize_height / 2);

        //타원을 그릴때 사용하는 함수. 인자들은 타원을 그릴 때 필요한 재료들.
        //이게 없으면 얼굴을 표시하는 자주색 원이 뜨지 않는다.

        //매개변수 정보 : 원 이미지, 중심 좌표, 타원의 크기(x축 반지름, y축 반지름), 타원의 각도, 호의 시작 각도 호의 종료 각도, 선의 색상
        //뒤의 것들은 그림을 그리는 변수들
//        ellipse(img_result, center, Size(real_facesize_width / 2, real_facesize_height / 2), 0, 0,
//                360, Scalar(255, 0, 255), 30, 8, 0);


        Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width, real_facesize_height);

        __android_log_print(ANDROID_LOG_ERROR, (char *) "native-lib :: ",
                            (char *) "real_facesize_x, %d found!!! ", real_facesize_x);
        __android_log_print(ANDROID_LOG_ERROR, (char *) "native-lib :: ",
                            (char *) "real_facesize_y, %d found!!! ", real_facesize_y);
        __android_log_print(ANDROID_LOG_ERROR, (char *) "native-lib :: ",
                            (char *) "real_facesize_width/2, %d found!!! ", real_facesize_width);
        __android_log_print(ANDROID_LOG_ERROR, (char *) "native-lib :: ",
                            (char *) "real_facesize_height/2, %d found!!! ", real_facesize_height);
        //이거 하면 전체 다 블러처리 됨.
        //GaussianBlur(img_result, img_result, Size(55, 55), 55);

        //가우시안 블러의 커널 사이즈는 반드시 ODD(홀수)여야만 한다. 짝수면 에러남
        //kernel은 행렬을 의미하는데 kernel의 크기가 크면 이미지 전체가 blur처리가 많이 됩니다.
        GaussianBlur(img_result(face_area), img_result(face_area), Size(111, 111), 111);
            return;

        //ROI : region of interest. 즉 얼굴 구역. 이 부분을 블러처리하면 될 거 같다.
        //faceROI는 face_area만큼의 매트릭스이다.

        //일단 눈 인식은 주석처리 해둠.
//        std::vector<Rect> eyes;
//
//
//        //-- In each face, detect eyes
//
//        ((CascadeClassifier *) cascadeClassifier_eye)->detectMultiScale(faceROI, eyes, 1.1, 2,
//                                                                        0 | CASCADE_SCALE_IMAGE,
//                                                                        Size(30, 30));
//
//        for (size_t j = 0; j < eyes.size(); j++) {
//            Point eye_center(real_facesize_x + eyes[j].x + eyes[j].width / 2,
//                             real_facesize_y + eyes[j].y + eyes[j].height / 2);
//
//            int radius = cvRound((eyes[j].width + eyes[j].height) * 0.25);
//
//            circle(img_result, eye_center, radius, Scalar(255, 0, 0), 30, 8, 0);
//
//        }

    }
}

}
