package com.d2d.controller;

import com.d2d.config.MinioFileUploader;
import com.d2d.exception.CustomValidationException;
import io.minio.StatObjectResponse;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class fileController {
    /* File uploader service class
    @class fileUploader
     */
    private final MinioFileUploader fileUploader;

    /* FileUploaderController constructor

    @class fileUploader
   */
    @Autowired
    public fileController(MinioFileUploader fileUploader) {
        this.fileUploader = fileUploader;
    }

    /*

    @Method uploadFile
    @Param file
    @Description Upload file to minio server.

    */
    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String,String>> uploadFile(@RequestPart("file") MultipartFile file) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException, CustomValidationException {
        return new ResponseEntity<>(fileUploader.uploadFile(file), HttpStatus.OK);
    }

    /*

    @Method downloadFile
    @Param fileName
    @Description download file from minio server by filename.

    */
    @GetMapping(value = "/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String fileName) throws IOException, CustomValidationException {
        return new ResponseEntity<>(fileUploader.downloadFile(fileName).readAllBytes(), HttpStatus.OK);
    }

    /*

    @Method getFileMetadata
    @Param fileName
    @Description metadata details by filename.

    */
    @GetMapping(value = "/metadata")
    public ResponseEntity<StatObjectResponse> getFileMetadata(@RequestParam String fileName) throws CustomValidationException {
        return new ResponseEntity<>(fileUploader.getFileMetadata(fileName), HttpStatus.OK);
    }

    /*

    @Method deleteFile
    @Param fileName
    @Description delete a file from minio server by filename.

    */
    @GetMapping(value = "/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String fileName) throws CustomValidationException {
        fileUploader.deleteFile(fileName);
        return new ResponseEntity<>("File deleted successfully", HttpStatus.OK);
    }

    /*

    @Method createBucket
    @Param bucketName
    @Description create a bucket to minio server.

    */
    @GetMapping(value = "/create-bucket")
    public ResponseEntity<String> createBucket(@RequestParam String bucketName) throws CustomValidationException {
        return new ResponseEntity<>(fileUploader.createBucket(bucketName), HttpStatus.OK);
    }

    /*

    @Method deleteBucket
    @Param bucketName
    @Description delete a bucket to minio server by bucket name.

    */
    @GetMapping(value = "/delete-bucket")
    public ResponseEntity<String> deleteBucket(@RequestParam String bucketName) throws CustomValidationException {
        return new ResponseEntity<>(fileUploader.deleteBucket(bucketName), HttpStatus.OK);
    }

}
