package sync.slamtalk.common.s3bucket;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.common.s3bucket.repository.AwsS3RepositoryImpl;

import java.util.List;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {
/*
   Controller Sample
   컨트롤러는 각 도메인마다 각각 작성하는게 자연스러울 거 같음

*/

    @Autowired
    private final AwsS3RepositoryImpl awsS3Service;

    @PostMapping("/upload")
    @Operation(
            summary = "개별 파일 업로드",
            description = "이 기능은 S3 Bucket 에 파일을 업로드 하는 기능입니다.",
            tags = {"S3Bucket"}
    )
    public ApiResponse<String> uploadFile(@RequestParam("file")MultipartFile multipartFile){
        String loadFile= awsS3Service.uploadFile(multipartFile);
        log.debug("====> capacity:{}",multipartFile.getSize());
        return ApiResponse.ok(loadFile,"업로드 완료");
    }

    @PostMapping("/uploads")
    @Operation(
            summary = "여러개 파일 업로드",
            description = "이 기능은 S3 Bucket 에 여러개의 파일을 업로드 하는 기능입니다.",
            tags = {"S3Bucket"}
    )
    public ApiResponse<List<String>> uploadFiles(@RequestParam("files")List<MultipartFile> multipartFileList){
        List<String> loadFiles = awsS3Service.uploadFiles(multipartFileList);
        return ApiResponse.ok(loadFiles,"업로드 완료");
    }

    @PostMapping("/delete")
    @Operation(
            summary = "파일 삭제",
            description = "이 기능은 S3 Bucket 에서 파일을 삭제하는 기능입니다.",
            tags = {"S3Bucket"}
    )
    public ApiResponse<String> deleteFiles(@RequestParam("file")String fileName){
        String result = awsS3Service.deleteFile(fileName);
        return ApiResponse.ok(result);
    }


}
