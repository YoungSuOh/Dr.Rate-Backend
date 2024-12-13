package com.bitcamp.drrate.domain.s3.controller;

import com.bitcamp.drrate.domain.s3.dto.request.FileRequestDTO;
import com.bitcamp.drrate.domain.s3.service.S3Service;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3Controller {
    private final S3Service s3Service;

    @PostMapping("/upload")
    public ApiResponse<List<Users>> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        try {
            List<Users> list = null;
            s3Service.uploadFile(multipartFile);
            return ApiResponse.onSuccess(list, SuccessStatus.FILE_UPLOAD_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.FILE_UPLOAD_FAILED.getCode(), ErrorStatus.FILE_UPLOAD_FAILED.getMessage(), null);
        }
    }

    @DeleteMapping("/delete")
    public ApiResponse<HttpStatus> delete(@RequestBody FileRequestDTO.FileDeleteRequest fileUrl) {
        try {
            s3Service.deleteFile(fileUrl);
            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.FILE_DELETE_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.FILE_DELETE_FAILED.getCode(), ErrorStatus.FILE_DELETE_FAILED.getMessage(), null);
        }
    }

}
