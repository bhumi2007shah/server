/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.error.ValidationException;
import io.litmusblox.server.model.Candidate;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for holding Utility methods to be used across the application
 *
 * @author : Shital Raval
 * Date : 12/7/19
 * Time : 10:00 AM
 * Class Name : Util
 * Project Name : server
 */
@Configuration
@PropertySource("classpath:appConfig.properties")
@Log4j2
public class Util {

    private static Pattern INDIAN_MOBILE_PATTERN = Pattern.compile(IConstant.INDIAN_MOBILE_PATTERN);

    private static Pattern JUNK_MOBILE_PATTERN = Pattern.compile(IConstant.JUNK_MOBILE_PATTERN);

    /**
     * Utility method to convert only relevant information into json
     *
     * @param responseBean the response bean to be converted to json
     * @param serializeMap map with key = filterclassname and value as a list of all bean properties required to be serialized
     * @param serializeExceptMap map with key = filterclassname and value as a list of bean properties that shouldn't be serialized
     * @return
     */
    public static String stripExtraInfoFromResponseBean(Object responseBean, Map<String, List<String>> serializeMap, Map<String, List<String>> serializeExceptMap) {

        ObjectMapper mapper = new ObjectMapper();

        String json="";
        try {

            SimpleFilterProvider filter = new SimpleFilterProvider();
            if (null != serializeMap)
                serializeMap.forEach((key, value) ->
                        filter.addFilter(key, SimpleBeanPropertyFilter.filterOutAllExcept(new HashSet<String>(value)))
                );

            if(null != serializeExceptMap)
                serializeExceptMap.forEach((key, value) ->
                        filter.addFilter(key, SimpleBeanPropertyFilter.serializeAllExcept(new HashSet<String>(value)))
                );

            json = mapper.writer(filter).writeValueAsString(responseBean);

        } catch (JsonGenerationException e) {
            log.error("error generating JSON string from response object: " + e.getMessage());
            e.printStackTrace();
        } catch (JsonMappingException e) {
            log.error("error generating JSON string from response object: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("error generating JSON string from response object: " + e.getMessage());
            e.printStackTrace();
        }

        return json;
    }

    public static boolean isNull(String s) {
        return (null == s || s.trim().length() == 0) ? true : false;
    }

    public static boolean isNotNull(String s) {
        return (null == s || s.trim().length() == 0) ? false : true;
    }

    public static boolean validateName(String name) throws ValidationException {
        if(isNull(name) || name.trim().length() == 0)
            throw new ValidationException(IErrorMessages.NAME_NULL_OR_BLANK + " - " + name, HttpStatus.BAD_REQUEST);
        if(name.length() > IConstant.CANDIDATE_NAME_MAX_LENGTH)
            throw new  ValidationException(IErrorMessages.NAME_FIELD_TOO_LONG + " - " + name, HttpStatus.BAD_REQUEST);
        if(!name.matches(IConstant.REGEX_FOR_NAME_VALIDATION)) {
            return false;
        }
        //name is valid
        return true;
    }

    public static boolean validateEmail(String email) throws ValidationException {
        if(Util.isNull(email) || email.trim().length() == 0)
            throw new ValidationException(IErrorMessages.EMAIL_NULL_OR_BLANK + " - " + email, HttpStatus.BAD_REQUEST);
        if(email.length() > IConstant.CANDIDATE_EMAIL_MAX_LENGTH)
            throw new  ValidationException(IErrorMessages.EMAIL_TOO_LONG + " - " + email, HttpStatus.BAD_REQUEST);

        //check domain name has at least one dot
        String domainName = email.substring(email.indexOf('@')+1);
        if(domainName.indexOf('.') == -1)
            throw new ValidationException(IErrorMessages.INVALID_EMAIL + " - " + email, HttpStatus.BAD_REQUEST);

        if(!email.matches(IConstant.REGEX_FOR_EMAIL_VALIDATION)) {
            return false;
        }
        //email address is valid
        return true;
    }

    public static boolean validateMobile(String mobile, String countryCode) throws ValidationException  {
        if(Util.isNull(mobile) || mobile.trim().length() == 0)
            throw new ValidationException(IErrorMessages.MOBILE_NULL_OR_BLANK + " - " + mobile, HttpStatus.BAD_REQUEST);

        if(!mobile.matches(IConstant.REGEX_FOR_MOBILE_VALIDATION))
            return false; //the caller should check for status, if it is false, due to regex failure, call again after cleaning up the mobile number

        if(countryCode.equals(IConstant.INDIA_CODE)) {
            Matcher m = INDIAN_MOBILE_PATTERN.matcher(mobile);
            if(!(m.find() && m.group().equals(mobile))) //did not pass the Indian mobile number pattern
                throw new ValidationException(IErrorMessages.INVALID_INDIAN_MOBILE_NUMBER + " - " + mobile, HttpStatus.BAD_REQUEST);
        }

        //check if the number is junk, like all the same digits
        if(JUNK_MOBILE_PATTERN.matcher(mobile).matches())
            throw new ValidationException(IErrorMessages.JUNK_MOBILE_NUMBER + " - " + mobile, HttpStatus.BAD_REQUEST);
        //mobile is valid
        return true;
    }

    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public static boolean validateUploadFileType(String fileName) {
        String extension = getFileExtension(fileName);
        if(!Arrays.asList(IConstant.supportedExtensions).contains(extension)) {
            throw new ValidationException(IErrorMessages.UNSUPPORTED_FILE_TYPE + " - " + extension, HttpStatus.BAD_REQUEST);
        }
        return true;
    }

    /*public static File storeFile(InputStream is, String filePath, String repoLocation) throws IOException {
        File targetFile =  null;
        try {
            //String staticRepoPath = null;

            if(isNull(filePath))
                throw new WebException(IErrorMessages.INVALID_SETTINGS);


            targetFile = new File(repoLocation + File.separator + filePath);
            Files.copy(is, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            new WebException(e.getMessage());
        }
        return targetFile;
    }*/

    public static String formatDate(Date date, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(date);
    }

    public static void modifyTextFileInZip(String zipPath) throws IOException {
        Path zipFilePath = Paths.get(zipPath);
        try (FileSystem fs = FileSystems.newFileSystem(zipFilePath, null)) {
            Path source = fs.getPath("[Content_Types].xml");
            Path temp = fs.getPath("/[Content_Types]_new.xml");
            if (Files.exists(temp)) {
                throw new IOException("Invalid excel format");
            }
            Files.move(source, temp);
            streamCopy(temp, source);
            Files.delete(temp);
        }
        System.out.println("returning from zip modification method");
    }

    public static void streamCopy(Path src, Path dst) throws IOException {
        System.out.println("In the method to copy the missing information in xlsx file");
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Files.newInputStream(src)));
             BufferedWriter bw = new BufferedWriter(
                     new OutputStreamWriter(Files.newOutputStream(dst)))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("<Override PartName=\"/_rels/.rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>", "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n" +
                        "<Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/_rels/.rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>");
                bw.write(line);
                bw.newLine();
            }
        }
        System.out.println("Modified the file.....");
    }

    public static void handleCandidateName(Candidate candidate, String candidateName) {
        if (candidateName.indexOf('.') != -1) { //a dot (.) is found in the name, process accordingly
            String[] name = candidateName.split(IConstant.REGEX_FOR_DOT_IN_NAME);
            if (name.length > 1) {
                candidate.setLastName(name[0]);
                candidate.setFirstName(name[1]);
                return;
            }
            else if (name.length > 0) {
                candidate.setFirstName(name[0]);
                return;
            }
        }

        String[] name = candidateName.split("\\s+");
        candidate.setFirstName(name[0]);
        StringBuffer lastName = null;
        if (name.length > 1) {
            lastName = new StringBuffer();
            for (int i = 1; i < name.length; i++) {
                if (i > 1)
                    lastName.append(" ");
                lastName.append(name[i]);
            }
        }
        if (null != lastName)
            candidate.setLastName(lastName.toString());

    }

    public static String indianMobileConvertor(String mobileNo) {
        //remove all occurences of '
        mobileNo = mobileNo.replaceAll("\'","");

        //check if number contains any prefix like 0 or +
        //strip all occurences of 0 and +
        while(mobileNo.charAt(0) == '0' || mobileNo.charAt(0) == '+') {
            mobileNo = mobileNo.substring(1);
        }

        //strip all white spaces
        mobileNo = mobileNo.replaceAll("\\s+", "");

        //if mobile number is greater than 10 digits, and prefix is 91, remove 91
        if(mobileNo.length() > 10 && mobileNo.startsWith("91"))
            mobileNo = mobileNo.substring(2);

        return mobileNo;
    }
}
