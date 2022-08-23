//package com.xiaopeng.server.app.bean.utils;
//
//import org.apache.commons.codec.binary.Base64;
//import org.springframework.util.StringUtils;
//import sun.misc.BASE64Encoder;
//
//import javax.crypto.Cipher;
//import java.io.ByteArrayOutputStream;
//import java.security.*;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.security.spec.X509EncodedKeySpec;
//
///**
// * @Auto:BUGPeng
// * @Date:2022/6/29 11:33
// * @ClassName:RSAUtil
// * @Remark:
// */
//public class RSAUtil {
//
//    /**
//     * 密钥长度(bit)
//     */
//    public static final int KEY_LENGTH = 1024;
//
//    public static final int MAX_ENCRYPT_BLOCK = 117;
//    /**
//     * <p>
//     * 单次解密最大密文长度，这里仅仅指1024bit 长度密钥
//     * </p>
//     *
//     * @see #MAX_ENCRYPT_BLOCK
//     */
//    public static final int MAX_DECRYPT_BLOCK = 128;
//
//    /**
//     * 加密
//     */
//    // public static final String SIGN_TYPE_RSA = "RSA";
//
//    /**
//     * 加密算法
//     */
//    public static final String ALGORITHM_RSA = "RSA";
//
//    /**
//     * 算法/模式/填充
//     */
//    public static final String CIPHER_TRANSFORMATION_RSA = "RSA/ECB/PKCS1Padding";
//
//    /**
//     * 签名算法
//     */
//    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
//
//    /**
//     * UTF-8字符集
//     **/
//    public static final String CHARSET_UTF8 = "UTF-8";
//
//    /**
//     * GBK字符集
//     **/
//    public static final String CHARSET_GBK = "GBK";
//
//    public static final String CHARSET = CHARSET_UTF8;
//
//    /**
//     * 得到公钥
//     *
//     * @param key     密钥字符串（经过base64编码）
//     * @param charset
//     * @throws Exception
//     */
//    public static PublicKey getPublicKey(String key, String charset)
//            throws Exception {
//        byte[] keyBytes = Base64.decodeBase64(key.getBytes(charset));
//
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//        PublicKey publicKey = keyFactory.generatePublic(keySpec);
//        return publicKey;
//    }
//
//    /**
//     * 得到私钥
//     *
//     * @param key     密钥字符串（经过base64编码）
//     * @param charset
//     * @throws Exception
//     */
//    public static PrivateKey getPrivateKey(String key, String charset)
//            throws Exception {
//        byte[] keyBytes;
//        keyBytes = Base64.decodeBase64(key.getBytes(charset));
//
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
//        return privateKey;
//    }
//
//    /**
//     * 得到密钥字符串（经过base64编码）
//     *
//     * @return
//     */
//    public static String getKeyString(Key key) throws Exception {
//        byte[] keyBytes = key.getEncoded();
//        String s = new String(Base64.encodeBase64(keyBytes), CHARSET);
//        return s;
//    }
//
//    /**
//     * 公钥加密
//     *
//     * @param content   待加密内容
//     * @param publicKey 公钥
//     * @param charset   字符集，如UTF-8, GBK, GB2312
//     * @return 密文内容
//     * @throws Exception
//     */
//    public static String rsaEncrypt(String content, String publicKey,
//                                    String charset) throws Exception {
//        try {
//            PublicKey pubKey = getPublicKey(publicKey, charset);
//            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_RSA);
//            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
//            byte[] data = StringUtils.isEmpty(charset) ? content.getBytes()
//                    : content.getBytes(charset);
//            int inputLen = data.length;
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            int offSet = 0;
//            byte[] cache;
//            int i = 0;
//            // 对数据分段加密
//            while (inputLen - offSet > 0) {
//                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
//                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
//                } else {
//                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
//                }
//                out.write(cache, 0, cache.length);
//                i++;
//                offSet = i * MAX_ENCRYPT_BLOCK;
//            }
//            byte[] encryptedData = Base64.encodeBase64(out.toByteArray());
//            out.close();
//
//            return StringUtils.isEmpty(charset) ? new String(encryptedData)
//                    : new String(encryptedData, charset);
//        } catch (Exception e) {
//            throw new Exception(
//                    "error occured in rsaEncrypt: EncryptContent = " + content
//                            + ",charset = " + charset, e);
//        }
//    }
//
//    /**
//     * 公钥加密
//     *
//     * @param content   待加密内容
//     * @param publicKey 公钥
//     * @return 密文内容
//     * @throws Exception
//     */
//    public static String rsaEncrypt(String content, String publicKey)
//            throws Exception {
//        return rsaEncrypt(content, publicKey, CHARSET);
//    }
//
//    /**
//     * 私钥解密
//     *
//     * @param content    待解密内容
//     * @param privateKey 私钥
//     * @param charset    字符集，如UTF-8, GBK, GB2312
//     * @return 明文内容
//     * @throws Exception
//     */
//    public static String rsaDecrypt(String content, String privateKey,
//                                    String charset) throws Exception {
//        try {
//            PrivateKey priKey = getPrivateKey(privateKey, charset);
//            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_RSA);
//            cipher.init(Cipher.DECRYPT_MODE, priKey);
//            byte[] encryptedData = StringUtils.isEmpty(charset) ? Base64
//                    .decodeBase64(content.getBytes()) : Base64
//                    .decodeBase64(content.getBytes(charset));
//            int inputLen = encryptedData.length;
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            int offSet = 0;
//            byte[] cache;
//            int i = 0;
//            // 对数据分段解密
//            while (inputLen - offSet > 0) {
//                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
//                    cache = cipher.doFinal(encryptedData, offSet,
//                            MAX_DECRYPT_BLOCK);
//                } else {
//                    cache = cipher.doFinal(encryptedData, offSet, inputLen
//                            - offSet);
//                }
//                out.write(cache, 0, cache.length);
//                i++;
//                offSet = i * MAX_DECRYPT_BLOCK;
//            }
//            byte[] decryptedData = out.toByteArray();
//            out.close();
//
//            return StringUtils.isEmpty(charset) ? new String(decryptedData)
//                    : new String(decryptedData, charset);
//        } catch (Exception e) {
//            throw new Exception("error occured in rsaDecrypt: EncodeContent = "
//                    + content + ",charset = " + charset, e);
//        }
//    }
//
//    /**
//     * 私钥解密
//     *
//     * @param content    待解密内容
//     * @param privateKey 私钥
//     * @return 明文内容
//     * @throws Exception
//     */
//    public static String rsaDecrypt(String content, String privateKey)
//            throws Exception {
//        return rsaDecrypt(content, privateKey, CHARSET);
//    }
//
//    /**
//     * 获得密钥对
//     *
//     * @return
//     * @throws NoSuchAlgorithmException KeyPair
//     * @Title creatKeyPair
//     */
//    public static KeyPair creatKeyPair() throws NoSuchAlgorithmException {
//        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);
//        // 密钥位数
//        keyPairGen.initialize(KEY_LENGTH);
//        // 密钥对
//        KeyPair keyPair = keyPairGen.generateKeyPair();
//        return keyPair;
//    }
//
//    /**
//     * 签名
//     *
//     * @param data       待签名数据
//     * @param privateKey 私钥
//     * @return 签名
//     */
//    public static String sign(String data, PrivateKey privateKey) throws Exception {
//        byte[] keyBytes = privateKey.getEncoded();
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//        PrivateKey key = keyFactory.generatePrivate(keySpec);
//        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
//        signature.initSign(key);
//        signature.update(data.getBytes());
//        return new String(Base64.encodeBase64(signature.sign()));
//    }
//
//    /**
//     * 验签
//     *
//     * @param srcData   原始字符串
//     * @param publicKey 公钥
//     * @param sign      签名
//     * @return 是否验签通过
//     */
//    public static boolean verify(String srcData, PublicKey publicKey, String sign) throws Exception {
//        byte[] keyBytes = publicKey.getEncoded();
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//        PublicKey key = keyFactory.generatePublic(keySpec);
//        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
//        signature.initVerify(key);
//        signature.update(srcData.getBytes());
//        return signature.verify(Base64.decodeBase64(sign.getBytes()));
//    }
//
//    public static void main(String[] args) throws Exception {
//        KeyPair keyPair = creatKeyPair();
//        System.out.println("生成公钥");
//        PublicKey publicKey = getPublicKey(new BASE64Encoder().encode(keyPair.getPublic().getEncoded()), "utf-8");
//        String publicKeyStr = new BASE64Encoder().encode(publicKey.getEncoded());
//        System.out.println(publicKeyStr);
//        System.out.println("=============");
//        System.out.println("生成私钥");
//        PrivateKey privateKey = getPrivateKey(new BASE64Encoder().encode(keyPair.getPrivate().getEncoded()), "utf-8");
//        String privateKeyStr = new BASE64Encoder().encode(privateKey.getEncoded());
//
//        System.out.println(privateKeyStr);
//        String password = "root@2020";
//        System.out.println("明文===" + password);
//        String strEncrpt = rsaEncrypt(password, publicKeyStr, "utf-8");
//        System.out.println("密文===" + strEncrpt);
//        String content = rsaDecrypt(strEncrpt, privateKeyStr, "utf-8");
//        System.out.println("解密===" + content);
//
//        // RSA签名
//        String sign = sign(password, getPrivateKey(privateKeyStr, "utf-8"));
//        // RSA验签
//        boolean result = verify(password, getPublicKey(publicKeyStr, "utf-8"), sign);
//        System.out.print("验签结果:" + result);
//
//    }
//
//}
