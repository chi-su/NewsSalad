<?php

$servername = "localhost";
$username = "root";
$password = "wltn27";
$database = "newsSalad";

$conn = new mysqli($servername, $username, $password, $database);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} else {
//    echo "hi";
}

//하나의 php파일과 2번 통신할 수는 없나?

//$tokenamount = "";
$tokenamount = $_POST['tokenAmount'];
//$tokenamount = $_GET['tokenAmount'];
//echo $tokenamount;
$intTokenAmount = (int)$tokenamount;

//결제 준비 php 파일. 안드로이드에서 결제하기 버튼을 누르면 매개변수 정보와 함께
//이 페이지를 호출하게 되고, 여기서 카카오페이 api를 호출해서 결과값을 json으로 받는다.
//받은 json 값은 array로 바꿔주고, 그 중에 연결해 줄 url을 골라서 다시 클라이언트로 보내준다.
//그럼 클라이언트는 그 url을 웹뷰로 접속하면 된다.

$kakaourl = 'https://kapi.kakao.com/v1/payment/ready';

$header_data = [];
$header_data[] = 'Authorization: KakaoAK ae495bace443f18a141dd784a391e892';

$ch = curl_init(); //curl 사용 전 초기화 필수(curl handle)

//URL 지정하기
curl_setopt($ch, CURLOPT_URL, $kakaourl);

//결과 값을 리턴하는 옵션?
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

//헤더 지정하기
curl_setopt($ch, CURLOPT_HTTPHEADER, $header_data);

//0이 default 값이며 POST 통신을 위해 1로 설정해야 함
curl_setopt($ch, CURLOPT_POST, 1);

curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query(array(
    'cid' => "TC0ONETIME",
    'partner_order_id' => 'partner_order_id',
    'partner_user_id' => 'partner_user_id',
    'item_name' => 'TomaToken',
    'quantity' => 1,
    'total_amount' => $intTokenAmount,
    'tax_free_amount' => 0,
    'approval_url' => 'http://13.125.67.216/PaymentSuccessApi.php',
    'fail_url' => 'http://13.125.67.216/fail.php',
    'cancel_url' => 'http://13.125.67.216/cancel.php'
)));

$result = curl_exec($ch);
curl_close($ch);

//json을 array로 바꿔주기.
$result_array = json_decode($result, true);

//tid 따내기.
$gottid =  $result_array["tid"];

//tid를 success 페이지에서 써먹기 위해 db에 임시로 저장해 둔다.
$sql = "UPDATE tidTable SET tid = '".$gottid."' WHERE id = 1;";
if ($conn->query($sql) === TRUE) {
//    echo "Record updated successfully";
} else {
//    echo "Error updating record: " . $conn->error;
}
mysqli_close($conn);

//클라이언트로 보낼 주소값을 따내기. 모바일 전용. 이 주소값은 안드로이드 웹뷰에서 사용됨.
//결제 성공 화면 url 보내주기.
$successurl = $result_array["next_redirect_mobile_url"];
echo $successurl;

?>
