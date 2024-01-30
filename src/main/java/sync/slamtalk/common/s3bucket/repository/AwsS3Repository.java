package sync.slamtalk.common.s3bucket.repository;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AwsS3Repository {
    // 개별 파일 루트 경로로 업로드
    public String uploadFile(MultipartFile multipartFile);

    // 개별 파일 특정 폴더에 업로드
    public String uploadFileToFolder(MultipartFile multipartFile, String folder);

    // 여러 개의 파일을 루트경로로 동시에 업로드
    public List<String> uploadFiles(List<MultipartFile> multipartFiles);

    // 여러 개의 파일을 특정 폴더에 업로드
    public List<String> uploadFilesToFoler(List<MultipartFile> multipartFiles, String folder);

    // 저장된 파일 지우기
    public String deleteFile(String fileName);

}
