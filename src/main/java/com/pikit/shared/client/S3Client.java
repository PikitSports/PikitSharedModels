package com.pikit.shared.client;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
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

    //TODO: Implement support for S3 Select
    public <T> T getTypeReferenceFromS3(String bucketName, String key, TypeReference<T> typeReference, boolean isZipped) throws IOException, AmazonS3Exception {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);

        S3Object object = s3Client.getObject(getObjectRequest);

        S3ObjectInputStream inputStream = object.getObjectContent();

        if (isZipped) {
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            return objectMapper.readValue(gzipInputStream, typeReference);
        } else {
            return objectMapper.readValue(inputStream, typeReference);
        }
    }

    //TODO: Implement support for S3 Select
    public <T> T getObjectFromS3(String bucketName, String key, Class<T> clazz, boolean isZipped) throws IOException, AmazonS3Exception {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);

        S3Object object = s3Client.getObject(getObjectRequest);

        S3ObjectInputStream inputStream = object.getObjectContent();

        if (isZipped) {
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            return objectMapper.readValue(gzipInputStream, clazz);
        } else {
            return objectMapper.readValue(inputStream, clazz);
        }
    }

    public <T> List<T> getListOfObjectsFromS3(String bucketName, String path, Class<T> clazz, boolean isZipped) throws AmazonS3Exception {
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(path);

        ListObjectsV2Result listObjectsResult = s3Client.listObjectsV2(listObjectsV2Request);

        return listObjectsResult.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .map(key -> {
                    try {
                        return getObjectFromS3(bucketName, key, clazz, isZipped);
                    } catch (IOException e) {
                        log.error("Failed to get object from S3 {}", key, e);
                        throw new RuntimeException("Failed to get object from S3");
                    }
                })
                .collect(Collectors.toList());
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
