package com.pikit.shared.client;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class S3Client {
    private AmazonS3 s3Client;
    private ObjectMapper objectMapper;

    public S3Client(AmazonS3 s3Client, ObjectMapper objectMapper) {
        this.s3Client = s3Client;
        this.objectMapper = objectMapper;
    }

    public void writeObjectToS3(String bucketName, String key, Object object, boolean zipFile) throws IOException, AmazonS3Exception {
        byte[] bytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(object);

        if (zipFile) {
            bytes = zipBytes(bytes);
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(bytes.length);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(bytes), objectMetadata);

        s3Client.putObject(putObjectRequest);
    }

    //TODO: Implement support for zipped files and S3 Select
    public <T> T getTypeReferenceFromS3(String bucketName, String key, TypeReference<T> typeReference) throws IOException, AmazonS3Exception {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);

        S3Object object = s3Client.getObject(getObjectRequest);

        S3ObjectInputStream inputStream = object.getObjectContent();

        return objectMapper.readValue(inputStream, typeReference);
    }

    //TODO: Implement support for zipped files and S3 Select
    public <T> T getObjectFromS3(String bucketName, String key, Class<T> clazz) throws IOException, AmazonS3Exception {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);

        S3Object object = s3Client.getObject(getObjectRequest);

        S3ObjectInputStream inputStream = object.getObjectContent();

        return objectMapper.readValue(inputStream, clazz);
    }

    public void deleteObjectFromS3(String bucketName, String key) throws AmazonS3Exception {
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
    }



    private byte[] zipBytes(byte[] bytesToZip) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        gzipOutputStream.write(bytesToZip, 0, bytesToZip.length);
        gzipOutputStream.finish();
        gzipOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }
}
