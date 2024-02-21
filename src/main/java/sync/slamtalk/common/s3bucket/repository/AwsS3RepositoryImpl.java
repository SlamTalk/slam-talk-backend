package sync.slamtalk.common.s3bucket.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
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

        @Value("${s3.bucket.base.url}")
        private String s3BucketBaseUrl;
        private static final long MAX_FILE_SIZE = 1 * 1024 * 1024;


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

            // jpeg,png 확장자인지 검증
            extensionValidator(multipartFile);

            // 용량 초과 하지 않는지 검증
            if(!capacityValidator(multipartFile)){
                throw new BaseException(ErrorResponseCode.S3_BUCKET_EXCEEDED_CAPACITY);
            }

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

            // jpeg,png 확장자인지 검증
            extensionValidator(multipartFile);

            // 용량 초과 하지 않는지 검증
            if(!capacityValidator(multipartFile)){
                throw new BaseException(ErrorResponseCode.S3_BUCKET_EXCEEDED_CAPACITY);
            }

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

            // jpeg,png 확장자인지 검증
            extensionValidator(multipartFile);

            // 용량 초과 하지 않는지 검증
            if(!capacityValidator(multipartFile)){
                throw new BaseException(ErrorResponseCode.S3_BUCKET_EXCEEDED_CAPACITY);
            }

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

            // jpeg,png 확장자인지 검증
            extensionValidator(multipartFile);

            // 용량 초과 하지 않는지 검증
            if(!capacityValidator(multipartFile)){
                throw new BaseException(ErrorResponseCode.S3_BUCKET_EXCEEDED_CAPACITY);
            }

            urlList.add(uploadFile);
        });
        return urlList;
    }


    @Override
    // 저장된 파일 지우기
    public String deleteFile(String fileName) {

        // key값만 추출
        String targetKey = extractObjectKey(fileName);

        try {
            // 파일 삭제 요청 생성
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(targetKey)
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

    // 올바른 파일 확장자인지 검증
    // jpeg, png 이미지 파일 확장자만 가능
    private void extensionValidator(MultipartFile multipartFile){
        String contentType = multipartFile.getContentType();

        // 확장자가 jpeg,png,jpg 인 파일들만 받아서 처리
        if(ObjectUtils.isEmpty(contentType) || (!contentType.contains("image/jpeg") && !contentType.contains("image/png")) && !contentType.contains("image/jpg")){
            throw new BaseException(ErrorResponseCode.S3_BUCKET_INVALID_EXTENSION);
        }
    }

    // 1MB 가 초과하지 않는 파일인지 검증
    private boolean capacityValidator(MultipartFile multipartFile){
        if(multipartFile.getSize()<=MAX_FILE_SIZE){
            return true;
        }
        return  false;
    }


    // 객체 key 값 추출
    // 파라미터로 이미지 url
    private String extractObjectKey(String s3objectUrl){
        if(s3objectUrl!=null && s3objectUrl.startsWith(s3BucketBaseUrl)){
            log.debug("extractkey:{}",s3objectUrl.substring(s3BucketBaseUrl.length()));
            return s3objectUrl.substring(s3BucketBaseUrl.length());
        }
        return null;
    }




}
