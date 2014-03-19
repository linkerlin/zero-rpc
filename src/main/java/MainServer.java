import com.cbuffer.zerorpc.handler.RequestHandler;
import com.cbuffer.zerorpc.protobuf.Rpc;
import com.cbuffer.zerorpc.server.NettyServer;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: xingsen
 * Date: 14-3-5
 * Time: 下午1:46
 */
public class MainServer {
    private Logger logger = LoggerFactory.getLogger(MainServer.class);

    public static void main(String args[]) throws InterruptedException {
        NettyServer server = new NettyServer(3000, new RequestHandler() {

            @Override
            protected Rpc.LoginResponse onLogin(Rpc.Login payload) {
                return Rpc.LoginResponse.newBuilder().setSuccess(true).setToken("1bcd").build();
            }

            @Override
            protected Rpc.RpcResponse onCall(Rpc.RpcRequest payload) {
                return Rpc.RpcResponse.newBuilder()
                        .setSuccess(true)
                        .setRequestId(payload.getRequestId())
                        .build();
            }
        });
        server.run();
    }
}
