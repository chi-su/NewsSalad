/*
* (C) Copyright 2014-2015 Kurento (http://kurento.org/)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

/*
* 영상을 보내고 받는 클라이언트 사이에서의 서버 코드.
* webRTC 연결을 위해 kurento 서버와 클라이언트가 sdp와 ice candidates를 교환할 필요가 있다.
* sdp(session discription protocol), 스트리밍 미디어 세션의 인자를 기록한 문서.
* sdp offer/answer : sender와 receiver 간에 미디어 스트릠, 코덱등을 ip와 port를 통해 공유하는 것.
* ICE(Interactive Connectivity Establishment), Nat traversal을 사용하는데 도움 되는 기술.
* ice는 스턴 프로토콜과 그 확장인 턴을 사용.ice는 SDP offer/answer 모델을 사용하는 모든 프로그램에서 사용 가능.
* ice candidate는 ip,port등의 정보가 담긴 네트워크 정보의 집합체이다.
* 양 측이 통신하기 적합한 ice candidate를 찾을 때까지 지속적으로 ice 후보자들을 교환한다.
* 클라이언트와 서버 소통에는 웹 소켓이 사용된다. 쿠렌토 미디어 서버의 미디어 능력을 컨트롤하기 위해,
* node 앱 서버 안의 kurentoclient 객체가 필요하다. kurentoclient가 갖추어지면 ,서버와 통신할 수 있다.
* */

/*
작동 순서
1. 서버를 켠다.
2. 서버를 켜면 wss가 지속적으로 들어오는 연결을 처리한다.
3.
*/

//유저세션들을 관리할 유저 레지스트리 클래스.
function UserRegistry() {
    //유저의 세션 아이디 프로퍼티
    this.usersById = {};
    this.usersByName = {};
}

//실제로 사용할 유저 레지스트리 객체 생성.
 var userRegistry = new UserRegistry();

//유저가 접속할 때마다 생겨나는 유저 세션 클래스.
function UserSession(id, ws, type, roomId) {
    this.id = id;
    this.name = null;
    this.ws = ws;
    this.type = type;
    this.roomId = roomId;
}

//유저 레지스트리의 레지스터 메소드 정의. 유저의 아이디와 이름을 등록한다.
UserRegistry.prototype.register = function (user) {
    this.usersById[user.id] = user;
    this.usersByName[user.name] = user;
}

UserRegistry.prototype.unregister = function (id) {
    var user = this.getById(id);
    //유저가 있다면 해당 유저 제거
    if (user) delete this.usersById[id]
    //유저의 아이디와 이름 모두 제거
    if (user && this.getByName(user.name)) delete this.usersByName[user.name];
}
//고유한 아이디를 통해 유저를 가져오는 함수
UserRegistry.prototype.getById = function (id) {
    return this.usersById[id];
}

UserRegistry.prototype.getByName = function (name) {
    return this.usersByName[name];
}
//유저가 나갈 때 유저 리스트에서 나간 유저를 제거하는 함수.
UserRegistry.prototype.removeById = function (id) {
    var userSession = this.usersById[id];
    if (!userSession) return;
    delete this.usersById[id];
    delete this.usersByName[userSession.name];
}

var util = require('util');
var pathModule = require('path');
var path = require('path');
var url = require('url');
//node js의 서버 프레임워크 express
var express = require('express');
//파싱 모듈
var minimist = require('minimist');
//웹 소켓 모듈. wss는 web socket secure.
var ws = require('ws');
//쿠렌토 클라이언트 모듈
var kurento = require('kurento-client');
//nodejs 파일시스템 모듈
var fs = require('fs');

var https = require('https');
var binding = process.binding('fs');

var pp = process.cwd() + '/node_modules/@ffmpeg-installer/ffmpeg';

//이 라이브러리들을 쓰려면 컴퓨터에 ffmpeg가 설치되어 있어야 한다.
//나는 apt-get으로 ffmpeg를 설치했다.
var ffmpeg0 = require('ffmpeg');
var ffmpeg = require('fluent-ffmpeg');

//서버 주소 uri 변수
var argv = minimist(process.argv.slice(2), {
    default: {
        as_uri: 'https://localhost:6008/',
        ws_uri: 'ws://localhost:8888/kurento',
  //      file_uri: "file:///var/www/html/record/ddddd.webm"
    }
});

//파일시스템에 필요한 인증
var options =
    {
        key: fs.readFileSync('keys/server.key'),
        cert: fs.readFileSync('keys/server.crt')
    };

//서버 변수에 서버 할당
var app = express();
//방 모임. 단 하나
var presenters = {};

//프레젠터 클래스. 이 안에 뷰어들이 들어간다. 또한 프레젠터스 배열의 원소로 들어가기도 한다.
function presenter(sessionId, roomId) {
    this.id = sessionId,
        this.roomId = roomId,
        this.pipeline = null,
        this.webRtcEndpoint = null,
        this.recorderEndpoint = null,
        this.viewers = {};
}

function viewer(sessionId, roomId, ws) {
    this.id = sessionId,
        this.roomId = roomId,
        this.webRtcEndpoint = null,
        this.ws = ws
}

/*
* Definition of global variables.
*/
//세션아이디(프레젠터와 뷰어 모두, 즉 연결 그 자체)에 적용되는 카운터
var idCounter = 0;
var candidatesQueue = {};
//멀티 방을 위해서 쿠렌토 클라이언트는 여러 개가 필요
var kurentoClient = null;
var noPresenterMessage = 'No active presenter. Try again later...';

/*
* Server startup
*/
var asUrl = url.parse(argv.as_uri);
var port = asUrl.port;
var server = https.createServer(options, app).listen(port, function () {
    console.log('Kurento Tutorial started');
    console.log('Open ' + url.format(asUrl) + ' with a WebRTC capable browser');
    // webmToHls();
});

var wss = new ws.Server({
    server: server,
    path: '/one2many'
});

//유저가 서버에 연결 시, 해당 세션에 id를 붙임. 서로 다른 세션을 구별하기위해 ++1 처리.
function nextUniqueId() {
    idCounter++;
    return idCounter.toString();
}

function webmToHls(roomName) {

    new ffmpeg('/var/www/html/record/ab.webm')
        .size('480x?')
        .addOption('-profile:v', 'baseline')
        .addOption('-level', 3)
        .addOption('-hls_time', 10)
        //코덱 및 crf 수준. 18~28이 일반적으로 설정하는 수준이라고 하는데
        //여러 개를 테스트해 봤을 때 나는 26이 그나마 나은 거같다.
        .addOption('-vcodec', 'libx264')
        .addOption('-crf', 26)
        .on('error', function (err) {
            console.log('An error occurred: ' + err.message);
        })
        .on('end', function () {
            console.log('Processing finished!');
        })
        .save('/var/www/html/hlsrecording/'+ roomName +'.m3u8');
}

//webm에서 스크린샷 따기
function makeVodThumbnail(roomName) {

    ffmpeg('/var/www/html/record/ab.webm')
        .on('end', function() {
            console.log('Screenshots taken');
        })
        .screenshots({
            // Will take screens at 20%, 40%, 60% and 80% of the video
            timestamps: ['20%'],
            filename: roomName + ".png",
            folder: '/var/www/html/vodthumbnails/',
            size: '480x?'
        });
}


/*
* Management of WebSocket messages
* 유저가 연결될 때마다 발생하는 메소드?
*/
wss.on('connection', function (ws) {
    //연결이 들어올 때마다 연결에 해당하는 세션을 만든다.

    //유저를 구별하는 세션 아이디. 0부터 시작해서 연결될 때마다 송출자와 시청자를 가리지 않고 1씩 차례로 증가.
    var sessionId = nextUniqueId();
    //유저 세션의 객체를 생성한다.

    console.log('Connection received with sessionId ' + sessionId);

    //에러가 발생할 경우
    ws.on('error', function (error) {
        console.log('Connection ' + sessionId + ' error');
        stop(sessionId);
    });

    ws.on('close', function () {
        console.log('Connection ' + sessionId + ' closed');
        stop(sessionId);
    });

    //메세지가 올 경우(대부분)
    ws.on('message', function (_message) {

        //받은 메세지는 json으로 파싱해 새 변수에 넣기.
        var message = JSON.parse(_message);
        console.log('Connection ' + sessionId + ' received message ', message.roomId);

        //메세지의 아이디에 따라
        switch (message.id) {
            //프레젠터가 서버에 접속하는 경우
            case 'presenter':
                console.log("presenter room id is : " + message.roomId);

                //startPresenter 함수 실행
                startPresenter(sessionId, ws, message.sdpOffer, message.roomId, function (error, sdpAnswer) {
                    if (error) {
                        return ws.send(JSON.stringify({
                            id: 'presenterResponse',
                            response: 'rejected',
                            message: error
                        }));
                    }
                    ws.send(JSON.stringify({
                        id: 'presenterResponse',
                        response: 'accepted',
                        sdpAnswer: sdpAnswer
                    }));
                });
                break;
            //메세지가 시청자일 경우
            case 'viewer':
                console.log("viewer room id is : " + message.roomId);
                startViewer(sessionId, ws, message.sdpOffer, message.roomId, function (error, sdpAnswer) {
                    if (error) {
                        return ws.send(JSON.stringify({
                            id: 'viewerResponse',
                            response: 'rejected',
                            message: error
                        }));
                    }
                    ws.send(JSON.stringify({
                        id: 'viewerResponse',
                        response: 'accepted',
                        sdpAnswer: sdpAnswer
                    }));
                });
                break;

            case 'stop':
                console.log("got message.stop...");
                stop(sessionId);
                break;

            //ICE Candidate 는 Peer들 간에 교환해야 하는 네트워크 커넥션에 대한 정보
            case 'onIceCandidate':
                console.log("oncandidate room id is : " + message.roomId);
                onIceCandidate(message.roomId, sessionId, message.candidate);
                break;

            default:
                ws.send(JSON.stringify({
                    id: 'error',
                    message: 'Invalid message ' + message
                }));
                break;
        }
    });
});

// Definition of functions
// Recover kurentoClient for the first time.
// kurentoclient를 생성하는 함수. startPresenter메소드 내부에서 발동
function getKurentoClient(callback) {

    //만약 쿠렌토 클라이언트가 이미 있다면 리턴한다.
    if (kurentoClient !== null) {
        return callback(null, kurentoClient);
    }

    kurento(argv.ws_uri, function (error, _kurentoClient) {
        if (error) {
            console.log("Could not find media server at address " + argv.ws_uri);
            return callback("Could not find media server at address" + argv.ws_uri
                + ". Exiting with error " + error);
        }
        kurentoClient = _kurentoClient;
        callback(null, kurentoClient);
    });
}

//startPresenter시마다 presenter객체를 생성해 준다.
function startPresenter(sessionId, ws, sdpOffer, roomId, callback) {

    var userSession = new UserSession(sessionId, ws, "presenter", roomId);
    userRegistry.register(userSession);

    //프레젠터스 객체에 키 값형태로 프레젠터를 넣는다. 뷰어는 프레젠터 객체 안에 들어간다.
    var newPresenter = new presenter(sessionId, roomId);
    //프레젠터스의 룸아이디 키 - 프레젠터 값. 형태로 넣어준다.
    presenters[roomId] = newPresenter;

    //candidatequeue는 일단 그냥 둘까? 일단 그냥 둬 본다.
    clearCandidatesQueue(sessionId);

    //쿠렌토 클라이언트를 생성한다. 이 함수는 스타트 프레젠터 안에 있다.
    getKurentoClient(function (error, kurentoClient) {
        if (error) {
            stop(sessionId);
            return callback(error);
        }

        kurentoClient.create('MediaPipeline', function (error, pipeline) {
            if (error) {
                stop(sessionId);
                return callback(error);
            }
            newPresenter.pipeline = pipeline;

            createMediaElements(pipeline, ws, function (error, webRtcEndpoint, recorderEndpoint) {
                newPresenter.webRtcEndpoint = webRtcEndpoint;
                newPresenter.recorderEndpoint = recorderEndpoint;
                recorderEndpoint.record();
                console.log("recording started ...");

                if (error) {
                    pipeline.release();
                    return callback(error);
                }
                console.log("TIOWTJHIPWTHG " + typeof webRtcEndpoint.addIceCandidate);

                if (candidatesQueue[sessionId]) {
                    while (candidatesQueue[sessionId].length) {
                        var candidate = candidatesQueue[sessionId].shift();
                        newPresenter.webRtcEndpoint.addIceCandidate(candidate);
                    }
                }

                connectMediaElements(webRtcEndpoint, recorderEndpoint, function (error) {
                    if (error) {
                        pipeline.release();
                        return callback(error);
                    }
                    newPresenter.webRtcEndpoint.on('OnIceCandidate', function (event) {
                        //ice candidate 생성 및 전송
                        var candidate = kurento.getComplexType('IceCandidate')(event.candidate);
                        console.log('onIcecandidate in presenter');

                        ws.send(JSON.stringify({
                            id: 'iceCandidate',
                            candidate: candidate
                        }));
                    });

                    newPresenter.webRtcEndpoint.processOffer(sdpOffer, function (error, sdpAnswer) {
                        if (error) {
                            stop(sessionId);
                            return callback(error);
                        }
                        return callback(null, sdpAnswer);
                    });

                    newPresenter.webRtcEndpoint.gatherCandidates(function (error) {
                        if (error) {
                            console.log('Connection ' + sessionId + ' closed');
                            stop(sessionId);
                            return callback(error);
                        }
                        console.log('gatherCandidates in presenter ');
                    });
                });
            });
        });
    });
    // });
    console.log("방을 만들었습니다. 세션 아이디는 : " + sessionId + " 룸 아이디는 : " + roomId);
}

//엔드포인트 생성 메소드
//샤오미 폰 마이크 권한 설정 잊지 말것
//녹화 가능한 형식 : WEBM|MP4|WEBM_VIDEO_ONLY|WEBM_AUDIO_ONLY|MP4_VIDEO_ONLY|MP4_AUDIO_ONLY|JPEG_VIDEO_ONLY|KURENTO_SPLIT_RECORDER
function createMediaElements(pipeline, ws, callback) {
    pipeline.create('RecorderEndpoint', {uri: "file:///var/www/html/record/ab.webm", mediaProfile: 'WEBM'},
        function (error, recorderEndpoint) {
            if (error) {
                console.log("ERROR CREATING PIPELINE ELEMENTS");
                return callback(error);
            }
            // setInterval(makeVodThumbnail, 5000);

            pipeline.create('WebRtcEndpoint', function (error, webRtcEndpoint) {
                webRtcEndpoint.connect(recorderEndpoint, 'AUDIO', function (err) {
                })
                if (error) {
                    console.log("ERROR CREATING PIPELINE ELEMENTS");
                    return callback(error);
                }
                return callback(null, webRtcEndpoint, recorderEndpoint);
            });
        });
}

//생성한 엔드포인트 연결 메소드.
function connectMediaElements(webRtcEndpoint, recorderEndpoint, callback) {
    webRtcEndpoint.connect(recorderEndpoint, function (error) {
        if (error) {
            return callback(error);
        }
        recorderEndpoint.connect(webRtcEndpoint, function (error) {
            if (error) {
                return callback(error);
            }
            return callback(null);
        });
    });
}


//뷰어 처리 작업
function startViewer(sessionId, ws, sdpOffer, roomId, callback) {

    var userSession = new UserSession(sessionId, ws, "viewer", roomId);
    userRegistry.register(userSession);

    clearCandidatesQueue(sessionId);

    var newViewer = new viewer(sessionId, roomId, ws);

    console.log("newViewer's room id : " + roomId);

    //뷰어는 선택한 프레젠터의 안으로 들어간다.
    presenters[roomId].viewers[sessionId] = newViewer;

    //프레젠터가 없을 때 스톱하는 조건문
    if (presenters[roomId] === null) {
        stop(sessionId);
        console.log("some error!!!");
        return callback(noPresenterMessage);
    }

    // console.log(sessionId);
    //선택된 특정 프레젠터의 파이프라인에서 엔드포인트를 생성
    presenters[roomId].pipeline.create('WebRtcEndpoint', function (error, webRtcEndpoint) {
        if (error) {
            stop(sessionId);
            console.log("some error in startviewer in pipeline create!!");
            return callback(error);
        }
        //생성한 엔드포인트를 뷰어의 그것에 대입
        newViewer.webRtcEndpoint = webRtcEndpoint;

        //방에 들어왓는데 해당 방에 프레젠터가 없으면 스톱.
        if (presenters[roomId] === null) {
            stop(sessionId);
            console.log("some error in startviewer in pipeline create!!!");
            return callback(noPresenterMessage);
        }

        if (candidatesQueue[sessionId]) {
            while (candidatesQueue[sessionId].length) {
                var candidate = candidatesQueue[sessionId].shift();
                webRtcEndpoint.addIceCandidate(candidate);
                console.log("addIceCandidate in viewer ");
            }
        }
        //Onicecandidate
        newViewer.webRtcEndpoint.on('OnIceCandidate', function (event) {
            var candidate = kurento.getComplexType('IceCandidate')(event.candidate);
            console.log("OnIceCandidate in viewer ");
            ws.send(JSON.stringify({
                id: 'iceCandidate',
                candidate: candidate
            }));
        });

        newViewer.webRtcEndpoint.processOffer(sdpOffer, function (error, sdpAnswer) {
            if (error) {
                console.log("erro01 ");
                console.log(error.toString());
                stop(sessionId);
                return callback(error);
            }
            if (presenters[roomId] === null) {
                console.log("erro02 ");
                stop(sessionId);
                return callback(noPresenterMessage);
            }

            //별 에러가 없을 경우 presenter의 엔드포인트를 뷰어의 엔드포인트에 연결한다.
            presenters[roomId].webRtcEndpoint.connect(webRtcEndpoint, function (error) {
                if (error) {
                    console.log("erro03 " + error.toString());
                    stop(sessionId);
                    return callback(error);
                }
                if (presenters[roomId] === null) {
                    console.log("erro04 ");
                    stop(sessionId);
                    return callback(noPresenterMessage);
                }

                callback(null, sdpAnswer);
                newViewer.webRtcEndpoint.gatherCandidates(function (error) {
                    console.log("viewer gathering candidates");
                    if (error) {
                        console.log("erro05 ");
                        stop(sessionId);
                        return callback(error);
                    }
                });
            });
        });
    });
}

//정보 큐를 클리어하는 함수.
function clearCandidatesQueue(sessionId) {
    if (candidatesQueue[sessionId]) {
        delete candidatesQueue[sessionId];
    }
}

//스탑을 할 때 룸 아이디도 넘겨야 할 거 같은데?
function stop(sessionId) {

    //유저레지스트리에서 세션아이디에 해당하는 유저를 가져온다.
    var theUser = userRegistry.getById(sessionId);
    console.log(' the users id : ' + theUser.id + " roomId : " + theUser.roomId + '');

    if (theUser.type == "presenter") {

            presenters[theUser.roomId].recorderEndpoint.stop();
            //스크린샷을 찍고 나서 hls화 한다.
            makeVodThumbnail(theUser.roomId);

            webmToHls(theUser.roomId);
            console.log("recording stop... ");

        for (var i in presenters[theUser.roomId].viewers) {
            var viewer = presenters[theUser.roomId].viewers[i];
            if (viewer.ws) {

                viewer.webRtcEndpoint.release();
                delete viewer;
                viewer.ws.send(JSON.stringify({
                    //뷰어들에게 끊으라고 메세지를 보낸다.
                    id: 'stopCommunication'
                }));
            }
        }

    } else if (theUser.type == "viewer") {
        var theRoomId = theUser.roomId;
        presenters[theRoomId].viewers[sessionId].webRtcEndpoint.release();
        delete presenters[theRoomId].viewers[sessionId];
    }

    console.log('Connection ' + sessionId + ' closed...');
    var theRoomId = theUser.roomId;

    clearCandidatesQueue(sessionId);

    //객체의 길이를 재는 변수.
    var obj_length = Object.keys(presenters[theRoomId].viewers).length;

    //방의 뷰어들의 숫자가 1이하이고 프레젠터가 없고 쿠렌토클라이언트가 널이 아니라면
    //즉 방의 실질적인 기능을 상실했음에도 클라이언트가 있다면 메모리 절약을 위해 없애야 하므로
    //이게 발동을 하려면 어떻게 해야 할까
    // if (obj_length < 1 && presenters[theRoomId] && kurentoClient !== null) {
    if (!presenters[theRoomId] && kurentoClient !== null) {

        presenters[theUser.roomId].pipeline.release();
        presenters[theUser.roomId].viewers = [];
        presenters[theUser.roomId] = null;

        console.log('Closing kurento client');
        kurentoClient.close();
        kurentoClient = null;
    }
}

//ICE Candidate 는 Peer들 간에 교환해야 하는 네트워크 커넥션에 대한 정보
function onIceCandidate(roomId, sessionId, _candidate) {

    console.info('onIcecandidta');

    var theUser = userRegistry.getById(sessionId);
    var theSessionId = theUser.id;
    // console.log("");

    var candidate = kurento.getComplexType('IceCandidate')(_candidate);
    //프레젠터스 객체가 있고 세션아이디가 해당 프레젠터의 아이디와 같으며 해당 프레젠터의 엔드포인트가 있다면
    if (presenters && presenters[roomId].id === theSessionId && presenters[roomId].webRtcEndpoint) {
        console.info('Sending presenter candidate');
        //프레젠터의 ice 정보를 보내고 있다.
        presenters[roomId].webRtcEndpoint.addIceCandidate(candidate);
    }
    //세션아이디 번째
    else if (presenters[roomId].viewers[theSessionId] && presenters[roomId].viewers[theSessionId].webRtcEndpoint) {
        console.info('Sending viewer candidate');
        //뷰어의 ice 정보를 보내고 있다.
        presenters[roomId].viewers[theSessionId].webRtcEndpoint.addIceCandidate(candidate);
    }
    else {
        console.info('Queueing candidate');
        if (!candidatesQueue[theSessionId]) {
            //ice 정보 큐를 초기화한다.
            candidatesQueue[theSessionId] = [];
        }
        //그리고 새로 들어온 정보를 큐에 넣는다.
        candidatesQueue[theSessionId].push(candidate);
    }
}

app.use(express.static(path.join(__dirname, 'static')));
