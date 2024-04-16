package com.d2d.config;

import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ErrorCode;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MinioFileUploader {
    private final MinioClient minioClient;

    /*

    @Param bucketName
    @Description Constant data read from application.yml file.

    */
    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${media.protocol}")
    private String protocol;
    @Value("${media.host}")
    private String host;
    @Value("${media.port}")
    private String port;

    @Autowired
    public MinioFileUploader(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /*

    @Method uploadFile
    @Param file
    @Description Upload file to minio server.

    */
    public Map<String,String> uploadFile(MultipartFile file) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException, CustomValidationException {

        /*
        @Description Generate a unique filename.
        */
        String fileName = UUID.randomUUID().toString();

        /*
        @Description Set the object name (path within the bucket).
        */
        String generatedFileName = fileName + "-" + file.getOriginalFilename();

        /*
        @Description Set the content type of the file.
        */
        String contentType = file.getContentType();

        try {
            /*
            @Description  Upload the file to the MinIO server.
            @Param bucketName
            @Param generatedFileName
            @Param file
            @Param contentType
            */
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(generatedFileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException | IllegalArgumentException e) {
            throw new CustomValidationException(ErrorCode.valueOf(e.getMessage()));
        }
        Map<String,String> map=new HashMap<>();
        map.put("originalFileName",file.getOriginalFilename());
        map.put("generatedFileName",generatedFileName);

        /*
        @Description Return success message.
        */
        return map;
    }

    /*

    @Method downloadFile
    @Param fileName
    @Description download file from minio server by filename.

    */
    public InputStream downloadFile(String fileName) throws CustomValidationException {
        try {
            /*
            @Description  Download file from minio server by bucket name and filename.
            @Param bucketName
            @Param fileName
            */
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(ErrorCode.valueOf("Failed to download the file."));
        }
    }

    /*

    @Method getFileMetadata
    @Param fileName
    @Description metadata details by filename.

    */
    public StatObjectResponse getFileMetadata(String fileName) throws CustomValidationException {
        try {
            /*
            @Description  Get metadata details for particular file by using filename.
            @Param bucketName
            @Param fileName
            */
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(ErrorCode.valueOf("Failed to retrieve file metadata."));
        }
    }


    /*

    @Method deleteFile
    @Param fileName
    @Description delete a file from minio server by filename.

    */
    public void deleteFile(String fileName) throws CustomValidationException {
        try {
            /*
            @Description  Delete file from minio server by filename.
            @Param bucketName
            @Param fileName
            */
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(ErrorCode.valueOf("Failed to delete the file."));
        }
    }


    /*

    @Method createBucket
    @Param bucketName
    @Description create a bucket to minio server.

    */
    public String createBucket(String bucketName) throws CustomValidationException {
        try {
            /*
            @Description  Create a bucket from minio server.
            @Param bucketName
            */
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            return "Bucket created successfully";
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(ErrorCode.valueOf("Failed to create the bucket"));
        }
    }


    /*

    @Method deleteBucket
    @Param bucketName
    @Description delete a bucket to minio server by bucket name.

    */
    public String deleteBucket(String bucketName) throws CustomValidationException {
        try {
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket("").build())) {

                /*
                @Description  delete a bucket from minio server.
                @Param bucketName
                */
                minioClient.removeBucket(RemoveBucketArgs.
                        builder().
                        bucket(bucketName).
                        build());
                return "Bucket deleted successfully";
            } else {
                throw new CustomValidationException(ErrorCode.valueOf("Bucket not found"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(ErrorCode.valueOf("Failed to delete the bucket"));
        }
    }


    public String getDownloadUrl(String fileName) {
        return protocol+"://"+host+":"+port+"/api/v1/media/download/"+fileName;
    }
}
