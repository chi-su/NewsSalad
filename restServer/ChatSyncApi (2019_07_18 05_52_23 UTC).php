<?php
$servername = "localhost";
$username = "root";
$password = "wltn27";
$database = "newsSalad";

//creating a new connection object using mysqli
$conn = new mysqli($servername, $username, $password, $database);


//연결 중 에러가 있을 경우 해당 에러를 표시하라.
//if there is some error connecting to the database
//with die we will stop the further execution by displaying a message causing the error
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} else {
//    echo "hi";
}

$response = array();

$roomName = $_POST['roomName'];

$stmt = $conn->prepare("SELECT chatAuthor, chatDescription, chatTime FROM chatTable WHERE chatRoom = '".$roomName."';");

$stmt->execute();
//binding results to the query
$stmt->bind_result($vodChatAuthor, $vodChatDescription, $vodChatTime);

//배열 생성해서 저장.
$products = array();

//배열의 원소에 하나씩 이름을 맞추어서 집어넣는다.
//traversing through all the result
while($stmt->fetch()){

    $temp = array();
    $temp['vodChatAuthor'] = $vodChatAuthor;
    $temp['vodChatDescription'] = $vodChatDescription;
    $temp['vodChatTime'] = $vodChatTime;

    //array_push는 뒤의 값을 앞의 배열에 추가하는 것이다. 배열에 배열을 추가할 수도 있다.
    array_push($products, $temp);
}
//products 배열을 json 형태로 바꿔서 보낸다.
echo json_encode($products);


?>