import com.cbuffer.zerorpc.client.NettyConnection;
import com.cbuffer.zerorpc.client.RpcClientInitializer;
import com.cbuffer.zerorpc.client.packet.collector.PacketCollectorManager;
import com.cbuffer.zerorpc.client.packet.filter.PacketTypeFilter;
import com.cbuffer.zerorpc.protobuf.Rpc;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * User: xingsen
 * Date: 14-3-5
 * Time: 下午3:27
 */
public class MainClient {
    public static void main(String args[]) throws InterruptedException, IOException {
        PacketCollectorManager packetCollectorManager = new PacketCollectorManager();
        NettyConnection connection = new NettyConnection("localhost", 3000,
                new RpcClientInitializer(packetCollectorManager), packetCollectorManager);
        connection.connection();

        Rpc.Login payload = Rpc.Login.newBuilder()
                .setUsername("xingsen")
                .setPassword("12312")
                .build();
        try {
            Rpc.LoginResponse loginResponse =
                    (Rpc.LoginResponse) connection.sendPacket(payload, new PacketTypeFilter(Rpc.LoginResponse.class));
            String token = loginResponse.getToken();

            Rpc.RpcRequest request = Rpc.RpcRequest.newBuilder()
                    .setToken(token)
                    .setRequestId(System.currentTimeMillis())
                    .setAsync(false)
                    .setServiceName(1)
                    .setParameter(ByteString.copyFromUtf8("参数"))
                    .setMethod(1)
                    .build();
            connection.sendPacket(request);

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        }

        Thread.sleep(1000000);
    }
}
