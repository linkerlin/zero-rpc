import org.bson.BSONObject;
import org.bson.BasicBSONObject;

/**
 * fireflyc@icloud.com
 */
public class Test {
    public void sayHello() {
        System.out.println("hello");
    }

    public BSONObject sayHello(String name) {
        BSONObject r = new BasicBSONObject();
        r.put("name", name);
        return r;
    }

    public BSONObject getBook() {
        BSONObject r = new BasicBSONObject();
        r.put("name", "getBook");
        return r;
    }
}