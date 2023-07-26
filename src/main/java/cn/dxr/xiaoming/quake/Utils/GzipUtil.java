package cn.dxr.xiaoming.quake.Utils;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class GzipUtil {

    /**
     * 解压GZip
     *
     * @return String
     */
    public static String unGZip(String input) {
        byte[] bytes;
        String out = input;
        GZIPInputStream gzip = null;
        ByteArrayInputStream bis;
        ByteArrayOutputStream bos = null;
        try {
            bis = new ByteArrayInputStream(Base64.decodeBase64(input));
            gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[1024];
            int num;
            bos = new ByteArrayOutputStream();
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                bos.write(buf, 0, num);
            }
            bytes = bos.toByteArray();
            out = new String(bytes, StandardCharsets.UTF_8);
            gzip.close();
            bis.close();
            bos.flush();
            bos.close();
        } catch (Exception e) {
            System.out.println("解压出错：" + e);
        } finally {
            try {
                if (gzip != null)
                    gzip.close();
                if (bos != null)
                    bos.close();
            } catch (final IOException ioe) {
                System.out.println("解压出错：" + ioe);
            }
        }
        return out;
    }
}
