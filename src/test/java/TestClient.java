import com.cbuffer.zerorpc.client.ResponseWrapper;
import com.cbuffer.zerorpc.client.ZeroRpc;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * fireflyc@icloud.com
 */
public class TestClient {
    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
        ZeroRpc zeroRpc = new ZeroRpc("localhost", 3000);
        try {
            zeroRpc.connection("fireflyc", "123");
            ResponseWrapper rw = zeroRpc.syncExecute("Test", "sayHello");
            System.out.println(String.format("msg: %s data: %s", rw.getErrorMsg(), rw.getData()));

            rw = zeroRpc.syncExecuteArgs("Test", "sayHello", "xingsen");
            System.out.println(String.format("msg: %s data: %s", rw.getErrorMsg(), rw.getData()));

            rw = zeroRpc.syncExecute("Test", "sayHi");
            System.out.println(String.format("msg: %s data: %s", rw.getErrorMsg(), rw.getData()));

            rw = zeroRpc.syncExecute("Test", "getBook");
            System.out.println(String.format("msg: %s data: %s", rw.getErrorMsg(), rw.getData()));
        } finally {
            zeroRpc.close();
        }
    }
}
