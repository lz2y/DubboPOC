package top.lz2y.test;


import top.lz2y.tools.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * description: ByteTest
 * date: 2021/7/22 9:42
 * author: lz2y
 * version: 1.0
 */
public class ByteTest {
    public static void main(String[] args) throws Exception{
//        System.out.println("Test");
        byte[] payload = FileUtil.getBytesByFile("src\\main\\java\\top\\lz2y\\1.ser");
        assert payload != null;
        ObjectInputStream ois = new ObjectInputStream(new
                ByteArrayInputStream(payload));
        ois.readObject();
    }
}
