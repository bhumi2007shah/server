/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.utils;

import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.error.WebException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

/**
 * @author : Sumit
 * Date : 01/08/19
 * Time : 10:57 AM
 * Class Name : StoreFileUtil
 * Project Name : server
 */
@Log4j2
public class StoreFileUtil {

    /**
     * storeFile method to save MultipartFile
     *
     * @param multipartFile which file we upload
     * @param id it is like userId or CompanyId
     * @param repoLocation location for save the file
     * @param uploadType which type of file we save
     * @return it return filepath string
     * @throws Exception
     */

    public static String storeFile(MultipartFile multipartFile, long id, String repoLocation, String uploadType, Long candidateId) throws Exception {
        File targetFile =  null;
        try {
            InputStream is = multipartFile.getInputStream();
            String filePath = getFileName(multipartFile.getOriginalFilename(), id, repoLocation, uploadType, candidateId);
            //Util.storeFile(is, filePath,repoLocation);
            if(Util.isNull(filePath))
                throw new WebException(IErrorMessages.INVALID_SETTINGS);

            targetFile = new File(repoLocation + File.separator + filePath);
            Files.copy(is, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return filePath;
        }
        catch (WebException e) {
            throw e;
        }
        catch (Exception e) {
            throw new WebException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,e);
        }
    }

    private static String getFileName(String fileName, long id, String repoLocation, String uploadType, Long candidateId) throws Exception {

        try {
            String filePath = null;
            String staticRepoPath = null;
            if (Util.isNull(repoLocation)) {
                throw new WebException(IErrorMessages.INVALID_SETTINGS);
            }
            staticRepoPath = repoLocation;

            //String time = Calendar.getInstance().getTimeInMillis() + "";
            filePath = uploadType + File.separator + id;
            File file = new File(staticRepoPath + File.separator + filePath);
            if (!file.exists()) {
                file.mkdirs();
            }

            if(null!=candidateId)
                filePath = filePath + File.separator + candidateId+"_"+fileName.substring(0,fileName.indexOf('.')) + "_" + Util.formatDate(new Date(), IConstant.DATE_FORMAT_yyyymmdd_hhmm) + "." + Util.getFileExtension(fileName);
            else
                filePath = filePath + File.separator + fileName.substring(0,fileName.indexOf('.')) + "_" + Util.formatDate(new Date(), IConstant.DATE_FORMAT_yyyymmdd_hhmm) + "." + Util.getFileExtension(fileName);

            log.info("Saved file: "+filePath);
            return filePath;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            throw new WebException(IErrorMessages.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

}
