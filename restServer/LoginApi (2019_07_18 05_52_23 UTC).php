<?php
header('Content-type=application/json; charset=utf-8');
$servername = "localhost";
$username = "root";
$password = "wltn27";
$database = "newsSalad";

$conn = new mysqli($servername, $username, $password, $database);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
//접속을 시도한 유저가 맞는지 확인하는 방법
/*
 * 1. 우선 이메일이 DB에 있는지부터 확인한다.
 * 2. 있으면 해당 행의 pw와 일치하는지 확인한다.
 * 3. 일치하면 로그인 시키고, 이 위에서부터 뭔가 문제가 있으면 돌려보낸다.
 */

//클라이언트에서 받은 요청을 처리해 돌려보내는 데 사용할 리스폰스 배열
$response = array();

$email = $_POST['userEmail'];
$pw = $_POST['uwerPw'];

$test = $_GET['test'];
$testpw = $_GET['testpw'];

//아이디가 테이블에 존재할 경우 비밀번호 일치 여부를 체크하기 위한 변수
$pwForCheck;
$userId;
$userName;
$userImage;
$userCoin;
$userWalletFile;

//SQL 인젝션을 방지하기 위해 쿼리를 쓸 때 ?로 하고 나중에 바인딩한다.
$stmt = $conn->prepare("SELECT userId, userPassword, userName ,userImage, userCoin, userWalletFile FROM userTable WHERE userEmail = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$stmt->store_result();

//id가 있다면 비밀번호 확인 작업 실행
if($stmt->num_rows > 0) {

    //가져온 패스워드를 변수에 넣어준다.
    $stmt->bind_result($userId,$pwForCheck, $userName, $userImage, $userCoin, $userWalletFile);
    $stmt->fetch();

    //넘어온 비밀번호와 DB에서 뽑은 비밀번호 비교하는 함수.
    //일치하면 0(true), 일치하지 않으면 1(false)반환
    $check = strcmp($pw, $pwForCheck);

    //넘어온 비밀번호와 디비 상의 비밀번호 비교. if 문 안이 다를 때, else 문 안이 비밀번호가 일치할 때.
    if ($check == false){

        $response['error'] = false;
        $response['message'] = '아이디나 비밀번호가 맞지 않습니다!';

        echo json_encode($response);
    } else {
        //로그인 조건이 완성되었으므로 유저 정보를 클라이언트에 보내준다.
        //이 보내진 유저정보는 클라이언트의 sharedpreference에 저장되어
        //로그아웃할때까지 유지될 것이다.
        $user = array(
            'userId'=>$userId,
            'userName'=>$userName,
            'userEmail'=>$email,
            'userImage'=>$userImage,
            'userWallet'=>$userCoin,
            'userWalletFile'=>$userWalletFile
        );
        $response['error'] = false;
        $response['message'] = '로그인 되었습니다.';
        $response['user'] = $user;

        echo json_encode($response);
    }
}
else { //유저가 디비 상에 없다면

    $response['error'] = false;
    $response['message'] = '아이디나 비밀번호가 맞지 않습니다...';

    echo json_encode($response);
}

?>