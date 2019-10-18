import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

//채트서버 - 채트서버이니셜라이저 - 채트 서버핸들러 순으로 실행됨.

/*
* 네티 채팅 서버 클래스. 여기서 서버 동작이 시작됨.
* 채트서버 - 채트서버이니셜라이저 - 채트 서버핸들러 구조.
* 대부분의 실제 작업은 핸들러에서 이루어짐.
*
* */

public class ChatServer {
    private final int port;

    //생성자
    public ChatServer(int port) {
        super();
        this.port = port;
    }

    //현재 호스트에서 5001 포트로 채팅 서버 시작
    public static void main(String[] args) throws Exception {
        new ChatServer(5001).run();
    }

    public void run() throws Exception {

        System.out.println("서버가 켜졌습니다!!!");

        // SslContext를 사용할 경우
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                .build();

        //보스와 워커 스레드그룹 생성. 이벤트루프그룹은 i/o작업을 처리하는 멀티스레드의 이벤트루프.
        //보스그룹은 이벤트 받기, 워커그룹은 처리하는 역할.

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //부트스트랩은 서버를 설정하는 헬퍼 클래스
            //부트스트랩의 그룹, 채널, 핸들러 설정
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //정의한 Chatserverinitializer사용
                    .childHandler(new ChatServerInitializer(sslCtx));

            //부트스트랩을 포트와 연동.
            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

     static class ChatServerHandler extends ChannelInboundHandlerAdapter {

        //네티에서는 채널그룹이 방, 채널이 사용자라고 생각하면 된다.
        //채널그룹스는 방들의 모임이라고 생각하면 된다.
        //채널그룹스는 단 하나만 필요하므로 스태틱으로 지정.
        public static final HashMap<String, ChannelGroup> channelGroups = new HashMap<>();

        //핸들러가 추가됐을 때 발동. 기본적으로 아무것도 안함.
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("handlerAdded of [SERVER]");
        }

        //핸들러애디드 다음에 발동하는 이벤트.
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // 사용자가 접속했을 때 서버에 표시.
            System.out.println("User Access!!!");
//        Channel incoming = ctx.channel();
//        channelCounter();

        }

        //사용자가 나갔을 때 기존 사용자에게 알림
        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            System.out.println("handlerRemoved of [SERVER]");

//        Channel incoming = ctx.channel();
//        ctx.close();

//        for (Channel channel : channelGroup) {
//            //사용자가 나갔을 때 다른 클라이언트들에게 알림
//            channel.write("[SERVER] - " + incoming.remoteAddress() + "has left!\n");
//        }
//        channelGroup.remove(incoming);
        }


        //read하고 나서 데이터를 다 읽어 없을 때 발생하는 이벤트.
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        //여기서 서버의 대부분의 작업이 일어난다.
        //여기서 스트링을 넘겨받고, 사용할 땐 json으로 변환해 사용한다.
        //클라이언트가 메세지를 보낼 때 받는 메소드. ctx가 연결, msg가  메시지.
        //클라이언트는 stringObject에 방번호 키/값 + 명령어 키/값 + (채팅 내용 키/값)을 보낸다.
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("무언가 메시지가 왔습니다.");

            //넘어온 String오브젝트를 받을 String 객체 생성 및 할당
            String stringObject = (String) msg;

            JSONParser parser = new JSONParser();
            //넘어온 스트링 오브젝트를 다시 제이슨으로 변경.
            JSONObject jsonObject = (JSONObject) parser.parse(stringObject);

            Connection con = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            Class.forName("org.mariadb.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/newsSalad",
                    "root",
                    "wltn27");

            //        String roomName = null;

            //서버가 무슨 일을 할지 알려주는 커맨드 변수
            //넘어온 키에 따라 반응이 달라지게 해야 한다.
            String command = null;

            command = jsonObject.get("command").toString();
            System.out.println("커맨드는 : " + command);

            switch (command){
                //방을 만들라는 커맨드일 경우에 채널그룹을 생성하고 그 안에 쏙 들어간다.
                case "make a room" :
                    String roomName = null;
                    String roomGenre = null;
                    String userName = null;
                    //방 이름 넘겨받기
                    roomName = jsonObject.get("room name").toString();
                    roomGenre = jsonObject.get("genre").toString();
                    userName = jsonObject.get("user name").toString();
                    //방 역할을 하는 채널 그룹을 생성한다.
                    ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                    //채널그룹스에 키-값 형태로 방 이름과 채널을 넣어준다.
                    channelGroups.put(roomName, channelGroup);

                    //연결에 쓸 채널을 생성한다.
                    Channel channel = ctx.channel();
                    //방 이름에 해당하는 채널그룹에 채널을 넣어준다.
                    channelGroups.get(roomName).add(channel);

                    try {
                        pstmt = con.prepareStatement("insert into newsTable(newsTitle, newsGenre, newsAuthor, newsStartTime) values (\""+roomName +"\",\""+roomGenre+"\",\""+userName+"\",now())");

                        rs = pstmt.executeQuery();
                        while(rs.next()) {
                            //.
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            if(rs != null) {
                                rs.close(); // 선택 사항
                            }

                            if(pstmt != null) {
                                pstmt.close(); // 선택사항이지만 호출 추천
                            }

                            if(con != null) {
                                con.close(); // 필수 사항
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println("방을 만들었습니다. 만세~" + roomName);
                    break;

                //방에 입장하는 커맨드
                case "enter the room" :
                {
                    roomName = jsonObject.get("room name").toString();
                    System.out.println("한 유저가 방에 입장했습니다. 방의 이름은 : " + roomName);

                    //이미 있는 방에 채널만 추가하면 되므로 채널만 생성한다.
//                channel = ctx.channel();
                    try{
                        //채널 그룹스의 방들 중에 이름이 맞는 채널그룹으로 들어가서 채널을 추가한다.
                        channelGroups.get(roomName).add(ctx.channel());

                    }catch (Exception e){
                        System.out.println("에러 발생 : " + e.getMessage());
                    }
                    break;
                }
                //메시지를 보내는 커맨드. 송출자 및 시청자에게 똑같이 적용된다.
                case "send a message" :

                    roomName = jsonObject.get("room name").toString();
                    String message = jsonObject.get("message").toString();
                    String userName1 = jsonObject.get("user name").toString();

                    channelGroups.get(roomName).writeAndFlush(message);
                    try{
//                        pstmt = con.prepareStatement("INSERT INTO chatTable (chatDescription, chatRoom, chatTime) VALUES (\""+message+"\",\""+roomName+ "\",now())");

                        pstmt = con.prepareStatement("INSERT INTO chatTable (chatDescription, chatAuthor, chatRoom, chatTime) VALUES (\""+message+"\",\""+userName1+"\",\""+roomName+ "\",now())");
                        rs = pstmt.executeQuery();
                        while(rs.next()) {
                            //.
                        }

                    }catch (Exception e){
                        e.printStackTrace();

                    }finally {
                        try{
                            if(rs != null) {
                                rs.close(); // 선택 사항
                            }

                            if(pstmt != null) {
                                pstmt.close(); // 선택사항이지만 호출 추천
                            }

                            if(con != null) {
                                con.close(); // 필수 사항
                            }
                        }catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }

                    System.out.println("서버에서 메시지를 받아서 다시 반사했습니다.");
                    break;

                //방송하던 사람이 나가면 전부 나간다.
                case "broadcaster exit the room" :
                    roomName = jsonObject.get("room name").toString();
                    for (Channel channel2 : channelGroups.get(roomName)){
                        channelGroups.get(roomName).writeAndFlush("closed by presenter");
                        channelGroups.get(roomName).close();
                    }

                    try {
                        pstmt = con.prepareStatement("UPDATE newsTable SET newsIsLive =0, newsThumbnail = \""+roomName+".png\" WHERE newsTitle like \"" +roomName + "\";");
                        rs = pstmt.executeQuery();
                        while(rs.next()) {
                            //.
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            if(rs != null) {
                                rs.close(); // 선택 사항
                            }

                            if(pstmt != null) {
                                pstmt.close(); // 선택사항이지만 호출 추천
                            }

                            if(con != null) {
                                con.close(); // 필수 사항
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("방의 송출자가 나가서 나머지 시청자도 다 나갔습니다.");

                    break;

                //시청자가 나갈 때는 걔만 나간다.
                case "watcher exit the room" :
                    roomName = jsonObject.get("room name").toString();
                    ctx.close();
                    System.out.println("방의 시청자가 나갔습니다.");

                    break;
            }
        }
    }

     class ChatServerInitializer extends ChannelInitializer<SocketChannel> {

        private final SslContext sslCtx;
        //생성자
        public ChatServerInitializer(SslContext sslCtx) {
            this.sslCtx = sslCtx;
        }

        //클라이언트 소켓 채널이 생성될 때 호출 됨.
        @Override
        protected void initChannel(SocketChannel arg0) throws Exception {
            //파이프라인 생성. 채널 파이프라인 : 네트워크 상의 흐름을 가로채거나 해서 처리한다.
            //채널에 채널파이프라인을 붙여야 한다. 일단 한번 붙이면 채널과 파이프라인은 영구적으로 결합된다.
            //채널은 현재 붙은 채널파이프라인을 떼내지 않으면 다른것을 붙일 수 없다.
            //채널파이프라인은 채널에서 발생한 이벤트가 이동하는 통로이다.
            //네티의 이벤트 메소드는 데이터가 수신되면 자동으로 호출된다.
            ChannelPipeline pipeline = arg0.pipeline();

            //문자열을 주고받기 위해 필요한 것들.
            pipeline.addLast(new ByteToMessageDecoder() {
                @Override
                public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                    out.add(in.readBytes(in.readableBytes()));
                }
            });
            pipeline.addLast(new StringDecoder());
            pipeline.addLast(new StringEncoder());
            pipeline.addLast(new ChatServerHandler());

        }

    }

}
