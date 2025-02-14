package vn.flast.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.flast.models.DataMedia;
import vn.flast.models.Media;
import vn.flast.repositories.MediaRepository;
import vn.flast.utils.Common;
import vn.flast.utils.GlobalUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;


    public static String UPLOAD_PATH =  "/uploads/product-media/";

    public static String PRODUCT  = "Product";

    public static String folderUpload() {
        String fd = System.getProperty("user.dir") + UPLOAD_PATH + GlobalUtil.getFolderUpload(GlobalUtil.dateToInt())  + "/";
        return Common.makeFolder(fd);
    }

    public void uploadFileMediaProduct(List<MultipartFile> files, Integer productId) throws IOException, NoSuchAlgorithmException {
        var folderUpload = folderUpload();
        List<Media> medias = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            assert fileName != null : "File name not extract .!";
            String fileMd5 = GlobalUtil.setFileName(fileName + GlobalUtil.dateToInt())  + "." +  GlobalUtil.pathNameFile(fileName);
            String filePath = folderUpload + fileMd5;
            InputStream fileStream = file.getInputStream();
            File targetFile = new File(filePath);
            FileUtils.copyInputStreamToFile(fileStream, targetFile);
            Media model = new Media();
            model.setObjectId(productId);
            model.setObject(PRODUCT);
            model.setFileName(filePath.replace(System.getProperty("user.dir"), ""));
            model.setStatus(Media.ACTIVE);
            medias.add(model);
        }
        mediaRepository.saveAll(medias);
    }

    public List<Media> list(Integer objectId, String objetc){
        var data = mediaRepository.listByObjectId(objectId, objetc);
        return data;
    }

    public void removeFileProduct(String file, Integer productId){
        var media = mediaRepository.findFileName(file, productId).orElseThrow(
                () -> new RuntimeException("Không tồn tại bản ghi này")
        );
        media.setStatus(Media.NOT_ACTIVE);
        mediaRepository.save(media);
    }
}
