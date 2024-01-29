package sync.slamtalk.common.s3bucket.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.common.ErrorResponseCode;
import sync.slamtalk.common.s3bucket.config.CommonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3RepositoryImpl implements AwsS3Repository{

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;



    @Override
    // 개별 파일 업로드
    public String uploadFile(MultipartFile multipartFile){
        if(multipartFile.isEmpty()){
            log.debug("image is null");
            return "";
        }
        String fileName = getFileName(multipartFile);

        try{
            // multipartFile로부터 데이터를 읽은 후 PutObjectRequest 객체를 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName) // 저장할 버킷명
                    .contentType(multipartFile.getContentType()) // 파일 객체의 메타 데이터 설정
                    .contentLength(multipartFile.getSize()) // 사이즈 설정
                    .key(fileName)
                    .build();

            // 파일 업로드 요청
            RequestBody requestBody = RequestBody.fromBytes(multipartFile.getBytes()); // 파일 데이터를 바이트 배열로 변환하여 RequestBody 객체를 생성
            s3Client.putObject(putObjectRequest,requestBody);
        }catch (IOException e){
            log.error("cannot upload image",e); // 이미지 업로드 실패
            throw new BaseException(ErrorResponseCode.S3_BUCKET_CANNOT_UPLOAD);
        }

        // 업로드된 파일 URL 반환
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        // GetUrlRequest로부터 파일 URL을 가져오고, 이를 문자열로 반환
        return s3Client.utilities().getUrl(getUrlRequest).toString();
    }



    // 개별 파일 특정 폴더에 업로드
    @Override
    public String uploadFileToFolder(MultipartFile multipartFile, String folder) {
        if(multipartFile.isEmpty()){
            log.debug("image is null");
            return "";
        }
        // 특정
        String fileName = getFileName(multipartFile,folder);
        try{
            // multipartFile로부터 데이터를 읽은 후 PutObjectRequest 객체를 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName) // 저장할 버킷명
                    .contentType(multipartFile.getContentType()) // 파일 객체의 메타 데이터 설정
                    .contentLength(multipartFile.getSize()) // 사이즈 설정
                    .key(fileName)
                    .build();

            // 파일 업로드 요청
            RequestBody requestBody = RequestBody.fromBytes(multipartFile.getBytes()); // 파일 데이터를 바이트 배열로 변환하여 RequestBody 객체를 생성
            s3Client.putObject(putObjectRequest,requestBody);
        }catch (IOException e){
            log.error("cannot upload image",e); // 이미지 업로드 실패
            throw new BaseException(ErrorResponseCode.S3_BUCKET_CANNOT_UPLOAD);
        }

        // 업로드된 파일 URL 반환
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        // GetUrlRequest로부터 파일 URL을 가져오고, 이를 문자열로 반환
        return s3Client.utilities().getUrl(getUrlRequest).toString();
    }


    @Override
    // 여러 개의 파일을 동시에 업로드
    public List<String> uploadFiles(List<MultipartFile> multipartFiles){
        List<String> urlList = new ArrayList<>();

        multipartFiles.forEach(multipartFile -> {
            String uploadFile = uploadFile(multipartFile);
            urlList.add(uploadFile);
        });
        return urlList;
    }


    // 여러 개의 파일을 특정 폴더에 업로드
    @Override
    public List<String> uploadFilesToFoler(List<MultipartFile> multipartFiles, String folder) {
        List<String> urlList = new ArrayList<>();

        multipartFiles.forEach(multipartFile -> {
            String uploadFile = uploadFileToFolder(multipartFile,folder);
            urlList.add(uploadFile);
        });
        return urlList;
    }


    @Override
    // 저장된 파일 지우기
    // TODO 권한 확인 후 수정
    public String deleteFile(String fileName) {
        try {
            // 파일 삭제 요청 생성
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            // S3 클라이언트를 사용하여 파일 삭제 요청 실행
            s3Client.deleteObject(deleteObjectRequest);
            return "File deleted successfully";
        } catch (S3Exception e) {
            log.error("Error occurred while deleting file from S3 bucket", e);
            throw new BaseException(ErrorResponseCode.S3_BUCKET_CANNOT_DELETE);
        }
    }



    // 파일명 생성 메소드
    private String getFileName(MultipartFile multipartFile){
        if(multipartFile.isEmpty()){
            return "";
        }
        return CommonUtils.buildFileName(multipartFile.getOriginalFilename());
    }



    // 폴더 및 파일 생성 메소드
    private String getFileName(MultipartFile multipartFile,String folderName){
        if(multipartFile.isEmpty()){
            return "";
        }
        return CommonUtils.buildFileName(multipartFile.getOriginalFilename(),folderName);
    }
}
