package com.jungle.chalnaServer.infra.file.controller;

import com.jungle.chalnaServer.infra.file.service.FileService;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.infra.file.domain.dto.FileRequest;
import com.jungle.chalnaServer.infra.file.domain.dto.FileResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/file")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public CommonResponse<FileResponse.UPLOAD> getUploadPreSignedUrl(@AuthUserId final Long id,
                                                        @RequestBody FileRequest.UPLOAD fileDto
                                                        ) {
        return CommonResponse.ok(fileService.uploadFile(id, fileDto));
    }

    @GetMapping("/download/{fileId}")
    public CommonResponse<FileResponse.DOWNLOAD> getDownloadPreSignedUrl(@PathVariable(value = "fileId") final Long fileId) {
        return CommonResponse.ok(fileService.downloadFile(fileId));
    }

    @DeleteMapping("/remove/{fileId}")
    public CommonResponse<?> deleteFile(@PathVariable(value = "fileId") final Long fileId) {
        fileService.deleteFile(fileId);
        return CommonResponse.ok("파일 삭제 완료");
    }
}
