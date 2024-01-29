package sync.slamtalk.common.s3bucket.config;

import org.springframework.stereotype.Component;

/*
   파일 이름 설정
 */
@Component
public class CommonUtils {
    public static final String FILE_EXTENSION_SEPARATOR = ".";

    // 파일이름 가져오기
    public static String getFileName(String originalFileName){
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        return originalFileName.substring(0,fileExtensionIndex); // 파일 이름
    }



    // 파일명
    public static String buildFileName(String originalFileName){
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR); // 파일 확장자 구분선
        String fileExtension = originalFileName.substring(fileExtensionIndex); // 파일 확장자
        String fileName = originalFileName.substring(0,fileExtensionIndex); // 파일 이름
        String now = String.valueOf(System.currentTimeMillis()); // 파일 업로드 시간

       return fileName + "_" + now + fileExtension;
    }



    // 파일명, 폴더명
    public static String buildFileName(String originalFileName,String folderName){
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR); // 파일 확장자 구분선
        String fileExtension = originalFileName.substring(fileExtensionIndex); // 파일 확장자
        String fileName = originalFileName.substring(0,fileExtensionIndex); // 파일 이름
        String now = String.valueOf(System.currentTimeMillis()); // 파일 업로드 시간

        return folderName + "/" + fileName + "_" + now + fileExtension;
    }
}
