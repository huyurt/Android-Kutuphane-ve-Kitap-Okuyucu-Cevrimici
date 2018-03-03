package kutuphane.com.kutuphane;

import java.security.MessageDigest;

public class Encryption {

    public String md5(final String toEncrypt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            messageDigest.update(toEncrypt.getBytes());
            final byte[] bytes = messageDigest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return toEncrypt;
        }
    }
}