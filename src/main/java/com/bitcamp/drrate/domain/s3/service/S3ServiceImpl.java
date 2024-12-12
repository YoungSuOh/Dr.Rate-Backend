package com.bitcamp.drrate.domain.s3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bitcamp.drrate.domain.s3.dto.request.FileRequestDTO;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.S3ServiceExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${cloud.aws.s3.bucket-directory}")
    private String bucketDirectoryPath;

    @Value("${cloud.aws.s3.endpoint}")
    private String endpoint;

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        File file = convertMultipartFileToFile(multipartFile);

        String fileName = UUID.randomUUID().toString(); // 고유 파일 이름 생성

        try (FileInputStream fileIn = new FileInputStream(file)) {
            // 메타데이터 설정
            ObjectMetadata objectMetadata = new ObjectMetadata();
            Path path = Paths.get(file.getAbsolutePath());
            String contentType;

            try {
                contentType = Files.probeContentType(path);
            } catch (IOException e) {
                throw new S3ServiceExceptionHandler(ErrorStatus.FILE_METADATA_ERROR);
            }

            objectMetadata.setContentType(contentType);
            objectMetadata.setContentLength(file.length());

            // S3에 저장할 파일 경로 생성
            String key = bucketDirectoryPath.endsWith("/") ? bucketDirectoryPath + fileName : bucketDirectoryPath + "/" + fileName;

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    key,
                    fileIn,
                    objectMetadata
            ).withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3.putObject(putObjectRequest); // S3에 파일 업로드

            return endpoint + "/" + bucketName + "/" + key;  // Object Storage 파일 url 반환함

        } catch (FileNotFoundException e) {
            throw new S3ServiceExceptionHandler(ErrorStatus.FILE_NOT_FOUND);
        } catch (AmazonServiceException e) {
            throw new S3ServiceExceptionHandler(ErrorStatus.S3_UPLOAD_ERROR);
        } catch (IOException e) {
            throw new S3ServiceExceptionHandler(ErrorStatus.FILE_PROCESSING_ERROR);
        }
    }

    @Override
    public File convertMultipartFileToFile(MultipartFile multipartFile) {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        try {
            multipartFile.transferTo(convFile);
        } catch (IOException e) {
            throw new S3ServiceExceptionHandler(ErrorStatus.FILE_CONVERSION_ERROR);
        }
        return convFile;
    }

    @Override
    public void deleteFile(FileRequestDTO.FileDeleteRequest fileUrl) {
        try {
            String fileKey = "";

            System.out.println("fileUrl : "+fileUrl.getFileUrl());

            // fileUrl이 전체 URL인지, 파일 이름만 포함하는지 확인
            if (fileUrl.getFileUrl().startsWith(endpoint + "/" + bucketName + "/")) {
                fileKey = fileUrl.getFileUrl().replace(endpoint + "/" + bucketName + "/", "");
            } else {
                System.out.println("hi");
                throw new S3ServiceExceptionHandler(ErrorStatus.FILE_UNVAILD_URL);
            }
            // S3 파일 삭제
            amazonS3.deleteObject(bucketName, fileKey);
        } catch (AmazonServiceException e) {
            throw new S3ServiceExceptionHandler(ErrorStatus.FILE_DELETE_ERROR);
        } catch (Exception e) {
            throw new S3ServiceExceptionHandler(ErrorStatus.FILE_DELETE_UNKNOWN_ERROR);
        }
    }
}
