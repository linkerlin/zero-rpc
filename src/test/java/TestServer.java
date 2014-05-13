import com.cbuffer.zerorpc.server.NettyServer;
import com.cbuffer.zerorpc.server.NettyServerConfig;
import com.cbuffer.zerorpc.server.RemoteExecutorService;
import com.cbuffer.zerorpc.server.ServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestServer {
    private static Logger logger = LoggerFactory.getLogger(TestServer.class);

    public static void main(String[] args) {
        try {
            RemoteExecutorService rpcService = new RemoteExecutorService();
            Test test = new Test();
            rpcService.addRpcInvoker(rpcService.createInvoker("sayHello", Test.class, test));
            rpcService.addRpcInvoker(rpcService.createInvoker("sayHi", Test.class, test));
            rpcService.addRpcInvoker(rpcService.createInvoker("getBook", Test.class, test));

            ServerHandler handler = new ServerHandler(rpcService);
            NettyServerConfig config = NettyServerConfig.builder()
                    .handler(handler)
                    .serverThreads(3)
                    .executeThread(1024)
                    .listenPort(3000)
                    .build();
            NettyServer server = new NettyServer(config);
            server.run();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
