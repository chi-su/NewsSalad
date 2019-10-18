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

$tid;

//PaymentReady 파일에서 저장해 둔 tid를 가져온다.
$sql = "SELECT tid FROM tidTable WHERE id=1";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
//    echo $row["tid"];
    $tid = $row["tid"];
    }
} else {
//    echo "0 results";
}

$url = 'https://kapi.kakao.com/v1/payment/approve';

$header_data = [];
$header_data[] = 'Authorization: KakaoAK ae495bace443f18a141dd784a391e892';

$ch = curl_init(); //curl 사용 전 초기화 필수(curl handle)

//URL 지정하기
curl_setopt($ch, CURLOPT_URL, $url);

//헤더 지정하기
curl_setopt($ch, CURLOPT_HTTPHEADER, $header_data);

//결과 값을 리턴하는 옵션?
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

//0이 default 값이며 POST 통신을 위해 1로 설정해야 함
curl_setopt($ch, CURLOPT_POST, 1);

curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query(array(
    'cid' => "TC0ONETIME",
    'tid' => $tid,
    'partner_order_id' => 'partner_order_id',
    'partner_user_id' => 'partner_user_id',
    //결제승인 요청을 인증하는 토큰.
    //사용자가 결제수단 선택 완료시 approval_url로 redirection해줄 때 pg_token을 query string으로 넘겨줌
    //쿼리 스트링은 GET 방식으로 넘어온다. POST로는 안됨.
    'pg_token' => $_GET['pg_token']
)));

$result = curl_exec($ch);
curl_close($ch);

?>
