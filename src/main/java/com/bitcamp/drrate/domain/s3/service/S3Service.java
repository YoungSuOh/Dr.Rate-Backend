package com.bitcamp.drrate.domain.s3.service;

import com.bitcamp.drrate.domain.s3.dto.request.FileRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface S3Service {
    public String uploadFile(MultipartFile multipartFile);
    public File convertMultipartFileToFile(MultipartFile multipartFile);
    public void deleteFile(FileRequestDTO.FileDeleteRequest fileUrl);
}
