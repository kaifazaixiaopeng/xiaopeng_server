package com.xiaopeng.server.vx.utils;


import io.minio.*;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppMinioUtil {

    @Autowired
    private MinioClient minioClient;



    public static final String MINIO = "minio:";
    private static final String BUCKET_NAME="baiiinfo-app-service";

    /**
     * 创建一个桶
     */
    public void createBucket(String bucket) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }



    /**
     * 上传一个文件
     */
    public void uploadFile(InputStream stream, String objectName) throws Exception {
        //是否存在名为“bucket”的桶
        createBucket(BUCKET_NAME);
        minioClient.putObject(PutObjectArgs.builder().bucket(BUCKET_NAME).object(objectName)
                .stream(stream, -1, 10485760).build());
    }

    /**
     * 列出所有的桶
     */
    public List<String> listBuckets() throws Exception {
        List<Bucket> list = minioClient.listBuckets();
        List<String> names = new ArrayList<>();
        list.forEach(b -> {
            names.add(b.name());
        });
        return names;
    }

    /**
     * 下载一个文件
     */
    public InputStream download(String bucket, String objectName) throws Exception {
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).object(objectName).build());
        return stream;
    }

    /**
     * 文件下载
     * @param fileName 文件名称
     * @param res response
     * @return Boolean
     */
    public void downloads(String fileName, HttpServletResponse res) {
        downloads(fileName,BUCKET_NAME,res);
    }

    public void downloads(String fileName,String bucketName, HttpServletResponse res){
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName)
                //因为数据库存了"/"+minioConfig.getBucketName()
                .object(fileName.replace("/"+bucketName,"")).build();
        try (GetObjectResponse response = minioClient.getObject(objectArgs)){
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()){
                while ((len=response.read(buf))!=-1){
                    os.write(buf,0,len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                res.setCharacterEncoding("utf-8");
                // 设置强制下载不打开
                // res.setContentType("application/force-download");
                res.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                try (ServletOutputStream stream = res.getOutputStream()){
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 返回文件路径
     */
    public String getBucketName()  {
        return  BUCKET_NAME+ "/" ;
    }

    public boolean containsMinio(String s)  {
        return  s.contains(MINIO);
    }
}
