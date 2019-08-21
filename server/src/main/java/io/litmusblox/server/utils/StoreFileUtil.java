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
import java.util.HashMap;
import java.util.Map;

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
            if(Util.isNull(filePath)){
                StringBuffer info = new StringBuffer(multipartFile.getName()).append(" FilePath is null ");
                log.info(info.toString());
                Map<String, String> breadCrumb = new HashMap<>();
                breadCrumb.put("Candidate Id",candidateId.toString());
                breadCrumb.put("filePath", filePath);
                SentryUtil.logWithStaticAPI(null, info.toString(), breadCrumb);
                throw new WebException(IErrorMessages.INVALID_SETTINGS, HttpStatus.EXPECTATION_FAILED);
            }
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
            StringBuffer filePath=new StringBuffer();
            String staticRepoPath = null;
            if (Util.isNull(repoLocation)) {
                StringBuffer info = new StringBuffer(fileName).append(" repoLocation is null ");
                log.info(info.toString());
                Map<String, String> breadCrumb = new HashMap<>();
                breadCrumb.put("Candidate Id",candidateId.toString());
                breadCrumb.put("repoLocation", repoLocation);
                SentryUtil.logWithStaticAPI(null, info.toString(), breadCrumb);
                throw new WebException(IErrorMessages.INVALID_SETTINGS, HttpStatus.EXPECTATION_FAILED);
            }
            staticRepoPath = repoLocation;

            //String time = Calendar.getInstance().getTimeInMillis() + "";
            filePath.append(uploadType).append(File.separator).append(id);
            File file = new File(staticRepoPath + File.separator + filePath);
            if (!file.exists()) {
                file.mkdirs();
            }

            if(null!=candidateId)
                filePath.append(File.separator).append(candidateId).append(".").append(Util.getFileExtension(fileName));
            else
                filePath.append(File.separator).append(fileName.substring(0,fileName.indexOf('.'))).append("_").append(Util.formatDate(new Date(), IConstant.DATE_FORMAT_yyyymmdd_hhmm)).append(".").append(Util.getFileExtension(fileName));

            log.info("Saved file: "+filePath);
            return filePath.toString();
        }
        catch (Exception e) {
            log.error(e.getMessage());
            throw new WebException(IErrorMessages.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

}
