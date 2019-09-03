/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.uploadProcessor;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.model.*;
import io.litmusblox.server.repository.CvParsingDetailsRepository;
import io.litmusblox.server.repository.JobCandidateMappingRepository;
import io.litmusblox.server.repository.JobRepository;
import io.litmusblox.server.repository.UserRepository;
import io.litmusblox.server.service.IJobControllerMappingService;
import io.litmusblox.server.service.MasterDataBean;

import io.litmusblox.server.utils.RestClient;
import io.litmusblox.server.utils.StoreFileUtil;
import io.litmusblox.server.utils.Util;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service class to process the CV uploaded against RChilli application
 * @author : shital
 * Date : 21/8/19
 * Time : 1:06 PM
 * Class Name : DragAndDropCvProcessor
 * Project Name : server
 */
@Log4j2
@Service
public class RChilliCvProcessor {

    @Autowired
    Environment environment;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    IUploadDataProcessService uploadDataProcessService;

    @Autowired
    JobCandidateMappingRepository jobCandidateMappingRepository;

    @Autowired
    IJobControllerMappingService jobControllerMappingService;

    @Autowired
    CvParsingDetailsRepository cvParsingDetailsRepository;


    /**
     * Service method to process the CV uploaded against RChilli application
     * @param filePath
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void processFile(String filePath) {
        // TODO:
        // 1. call the RChilli api to parse the candidate via RestClient
        // 2. from the name of file (<userId>_<jobId>_actualFileName), retrieve user Id and job id, to be used
        // 3. add jcm, and jcm communication details records
        // 4. increment the number of candidates processed by the user
        // 5. add a record in the new table cv_parsing_details with required details
        // 6. move the file to the job folder using the candidate id generated
        // In case of error from RChilli
        // 1. add record in cv_parsing_details <repolocation>/error_files/job_id

        String fileName=filePath.split("/")[6];
        String[] s=fileName.split("_");
        long userId=Long.parseLong(s[0]);
        long jobId=Long.parseLong(s[1]);
        User user= userRepository.getOne(userId);
        Job job = jobRepository.getOne(jobId);
        Candidate candidate=null;
        String rchilliFormattedJson=null;
        ResumeParserDataRchilliBean bean=null;
        long rchilliResponseTime= 0L;
        Boolean isCandidateFailedToProcess=false;
        Date createdOn=Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        int candidateProcessed=jobCandidateMappingRepository.getUploadedCandidateCount(createdOn,user);

        RestClient rest=RestClient.getInstance();
        String jsonString = "{\"url\":\"" + environment.getProperty(IConstant.FILE_STORAGE_URL)+filePath + "\",\"userkey\":\"" + environment.getProperty(IConstant.USER_KEY) + "\",\"version\":\"" + environment.getProperty(IConstant.VERSION)
                + "\",\"subuserid\":\"" + environment.getProperty(IConstant.SUB_USER_ID) + "\"}";
        try{
            long startTime = System.currentTimeMillis();
            //String rchilliJsonResponse=rest.consumeRestApi(jsonString, environment.getProperty(IConstant.RCHILLI_API_URL), HttpMethod.POST,null);
             rchilliResponseTime=System.currentTimeMillis()-startTime;
            log.info("Completed adding candidate from plugin in " + rchilliResponseTime + "ms.");
          String rchilliJsonResponse= "{\n" +
                  "  \"ResumeParserData\" : {\n" +
                  "    \"ResumeFileName\" : \"HX_Amol_Kale_4_2_Infosys_Pune.pdf\",\n" +
                  "    \"ResumeLanguage\" : \"English\",\n" +
                  "    \"ParsingDate\" : \"19/08/2019 11:37:13\",\n" +
                  "    \"FullName\" : \"Madra K\",\n" +
                  "    \"TitleName\" : \"\",\n" +
                  "    \"FirstName\" : \"sdfghj\",\n" +
                  "    \"Middlename\" : \"\",\n" +
                  "    \"LastName\" : \"dsdfgh\",\n" +
                  "    \"DateOfBirth\" : \"\",\n" +
                  "    \"Gender\" : \"\",\n" +
                  "    \"FatherName\" : \"\",\n" +
                  "    \"MotherName\" : \"\",\n" +
                  "    \"MaritalStatus\" : \"\",\n" +
                  "    \"Nationality\" : \"\",\n" +
                  "    \"LanguageKnown\" : \"\",\n" +
                  "    \"UniqueID\" : \"\",\n" +
                  "    \"LicenseNo\" : \"\",\n" +
                  "    \"PassportNo\" : \"\",\n" +
                  "    \"PanNo\" : \"\",\n" +
                  "    \"VisaStatus\" : \"\",\n" +
                  "    \"Email\" : \"sdfgh22@gmail.com\",\n" +
                  "    \"AlternateEmail\" : \"\",\n" +
                  "    \"Phone\" : \"+917700449900\",\n" +
                  "    \"FormattedPhone\" : \"+1 770-044-9900\",\n" +
                  "    \"Mobile\" : \"\",\n" +
                  "    \"FormattedMobile\" : \"\",\n" +
                  "    \"FaxNo\" : \"\",\n" +
                  "    \"WebSites\" : {\n" +
                  "      \"WebSite\" : [ {\n" +
                  "        \"Type\" : \"\",\n" +
                  "        \"Url\" : \"\"\n" +
                  "      } ]\n" +
                  "    },\n" +
                  "    \"Address\" : \"\",\n" +
                  "    \"City\" : \"\",\n" +
                  "    \"State\" : \"\",\n" +
                  "    \"Country\" : \"\",\n" +
                  "    \"ZipCode\" : \"\",\n" +
                  "    \"FormattedAddress\" : \"\",\n" +
                  "    \"PermanentAddress\" : \"\",\n" +
                  "    \"PermanentCity\" : \"\",\n" +
                  "    \"PermanentState\" : \"\",\n" +
                  "    \"PermanentCountry\" : \"\",\n" +
                  "    \"PermanentZipCode\" : \"\",\n" +
                  "    \"FormattedPermanentAddress\" : \"\",\n" +
                  "    \"Category\" : \"Software/IT\",\n" +
                  "    \"SubCategory\" : \"Software Engineer\",\n" +
                  "    \"CurrentSalary\" : \"\",\n" +
                  "    \"ExpectedSalary\" : \"\",\n" +
                  "    \"Qualification\" : \"M. C. A. \\n  \\n B. C. A. \\n  \\n H. S. C. \\n  \\n S. S. C. \\n  \\n Sinhgad Institutes , Lonavala with 65.00% \\n  \\n College of Computer Science and Information Technology , Latur with 68.00% \\n  \\n Annandmuni Vidhayalay Kelgaon with 66.50% \\n Annandmuni Vidhayalay Kelgaon with 49.60%\",\n" +
                  "    \"SegregatedQualification\" : {\n" +
                  "      \"EducationSplit\" : [ {\n" +
                  "        \"Institution\" : {\n" +
                  "          \"Name\" : \"\",\n" +
                  "          \"Type\" : \"\",\n" +
                  "          \"City\" : \"\",\n" +
                  "          \"State\" : \"\",\n" +
                  "          \"Country\" : \"\"\n" +
                  "        },\n" +
                  "        \"Degree\" : \"M. C. A\",\n" +
                  "        \"StartDate\" : \"\",\n" +
                  "        \"EndDate\" : \"31/12/2014\",\n" +
                  "        \"Aggregate\" : {\n" +
                  "          \"Value\" : \"\",\n" +
                  "          \"MeasureType\" : \"\"\n" +
                  "        }\n" +
                  "      }, {\n" +
                  "        \"Institution\" : {\n" +
                  "          \"Name\" : \"\",\n" +
                  "          \"Type\" : \"\",\n" +
                  "          \"City\" : \"\",\n" +
                  "          \"State\" : \"\",\n" +
                  "          \"Country\" : \"\"\n" +
                  "        },\n" +
                  "        \"Degree\" : \"B. C. A\",\n" +
                  "        \"StartDate\" : \"\",\n" +
                  "        \"EndDate\" : \"31/12/2014\",\n" +
                  "        \"Aggregate\" : {\n" +
                  "          \"Value\" : \"\",\n" +
                  "          \"MeasureType\" : \"\"\n" +
                  "        }\n" +
                  "      }, {\n" +
                  "        \"Institution\" : {\n" +
                  "          \"Name\" : \"\",\n" +
                  "          \"Type\" : \"\",\n" +
                  "          \"City\" : \"\",\n" +
                  "          \"State\" : \"\",\n" +
                  "          \"Country\" : \"\"\n" +
                  "        },\n" +
                  "        \"Degree\" : \"H. S. C.\",\n" +
                  "        \"StartDate\" : \"\",\n" +
                  "        \"EndDate\" : \"31/12/2014\",\n" +
                  "        \"Aggregate\" : {\n" +
                  "          \"Value\" : \"\",\n" +
                  "          \"MeasureType\" : \"\"\n" +
                  "        }\n" +
                  "      }, {\n" +
                  "        \"Institution\" : {\n" +
                  "          \"Name\" : \"Sinhgad Institute s\",\n" +
                  "          \"Type\" : \"Institute\",\n" +
                  "          \"City\" : \"Lonavala\",\n" +
                  "          \"State\" : \"\",\n" +
                  "          \"Country\" : \"\"\n" +
                  "        },\n" +
                  "        \"Degree\" : \"S. S. C.\",\n" +
                  "        \"StartDate\" : \"\",\n" +
                  "        \"EndDate\" : \"\",\n" +
                  "        \"Aggregate\" : {\n" +
                  "          \"Value\" : \"65.00\",\n" +
                  "          \"MeasureType\" : \"Percentage\"\n" +
                  "        }\n" +
                  "      }, {\n" +
                  "        \"Institution\" : {\n" +
                  "          \"Name\" : \"College of Computer Science and Information Technology\",\n" +
                  "          \"Type\" : \"College\",\n" +
                  "          \"City\" : \"Latur\",\n" +
                  "          \"State\" : \"\",\n" +
                  "          \"Country\" : \"\"\n" +
                  "        },\n" +
                  "        \"Degree\" : \"\",\n" +
                  "        \"StartDate\" : \"\",\n" +
                  "        \"EndDate\" : \"\",\n" +
                  "        \"Aggregate\" : {\n" +
                  "          \"Value\" : \"68.00\",\n" +
                  "          \"MeasureType\" : \"Percentage\"\n" +
                  "        }\n" +
                  "      } ]\n" +
                  "    },\n" +
                  "    \"Skills\" : \"PROFILE SUMMARY   \\n JAVA/J2EE Development \\n  \\n E-Commerce / Travel/Real Estate \\n  \\n Product Design & Development \\n  \\n Team Management \\n  \\n  Client Servicing \\n \\t Working as Senior Software Engineer with Infosys India Pvt. \\n \\t  Ltd. all aspects of software development including implementation \\n \\t  Of Enterprise, Scalable web applications. \\n  \\n \\t Experience in OOP, Software development and business \\n \\t  Modeling in Web applications. \\n  \\n \\t Strong Web development skills and Experience in Client-Server based \\n \\t  Internet technology, portal design / development. Web based data \\n \\t  reporting system, Framework development for Internet application. \\n  \\n \\t Thorough knowledge with J2EE application platform configuration, \\n \\t  application deployment automation and unit testing. \\n  \\n \\t Skilled in \\n \\t   \\n \\t  Excellent in JAVA/J2EE Development including , Spring \\n \\t  framework, Spring boot, Multithreading, Hibernate, Web Services \\n \\t and Maven \\n \\t   \\n \\t  Good Hands on databases like MySQL, Oracle and PostgreSQL \\n \\t   \\n \\t  Basic knowledge of JavaScript, jQuery, HTML, CSS \\n  \\n \\t   \\n Knows frameworks like Velocity, Tapestry.\\n\\n\\n\\n\\n\\n\\n\\nJAVA Frameworks \\n  \\n Front End Technologies \\n  \\n Databases \\n  \\n Web / Application server \\n  \\n Build Tool / Repository \\n  \\n JAVA, J2EE, Multithreading \\n Struts,Spring MVC, Spring Boot, Hibernate, Rest and SOAP Web Services \\n  \\n JavaScript, jQuery,HTML5 ,CSS3 ,Bootstrap \\n  \\n MySQL, Oracle \\n  \\n Apache Tomcat \\n  \\n Maven, GIT \\n    \\n I Infosys India Pvt Ltd. \\n  \\n Project Name : Bank of America. \\n Team Size : 25 \\n Duration : 12 Months \\n  \\n Technologies / Languages :  \\n  \\n Struts 2.x Spring 4.1 Framework , IBM WEB server Agile methodology Development model \\n Databases : Oracle \\n Operating System : Windows 7 ,10\",\n" +
                  "    \"SkillKeywords\" : {\n" +
                  "      \"SkillSet\" : [ {\n" +
                  "        \"Skill\" : \"Implementation\",\n" +
                  "        \"Type\" : \"SoftSkill\",\n" +
                  "        \"Alias\" : \"Implementator\",\n" +
                  "        \"FormattedName\" : \"Implementation\",\n" +
                  "        \"Evidence\" : \"SkillSection\",\n" +
                  "        \"LastUsed\" : \"\",\n" +
                  "        \"ExperienceInMonths\" : \"0\"\n" +
                  "      }, {\n" +
                  "        \"Skill\" : \"Application Deployment Automation\",\n" +
                  "        \"Type\" : \"OperationalSkill\",\n" +
                  "        \"Alias\" : \"\",\n" +
                  "        \"FormattedName\" : \"\",\n" +
                  "        \"Evidence\" : \"SkillSection\",\n" +
                  "        \"LastUsed\" : \"\",\n" +
                  "        \"ExperienceInMonths\" : \"0\"\n" +
                  "      }, {\n" +
                  "        \"Skill\" : \"WEB Server Agile Methodology\",\n" +
                  "        \"Type\" : \"OperationalSkill\",\n" +
                  "        \"Alias\" : \"\",\n" +
                  "        \"FormattedName\" : \"\",\n" +
                  "        \"Evidence\" : \"SkillSection\",\n" +
                  "        \"LastUsed\" : \"\",\n" +
                  "        \"ExperienceInMonths\" : \"0\"\n" +
                  "      }, {\n" +
                  "        \"Skill\" : \"Hibernate\",\n" +
                  "        \"Type\" : \"OperationalSkill\",\n" +
                  "        \"Alias\" : \"Hibernate, Hibernate 3.1\",\n" +
                  "        \"FormattedName\" : \"Hibernate\",\n" +
                  "        \"Evidence\" : \"SkillSection\",\n" +
                  "        \"LastUsed\" : \"\",\n" +
                  "        \"ExperienceInMonths\" : \"0\"\n" +
                  "      }, {\n" +
                  "        \"Skill\" : \"Reporting\",\n" +
                  "        \"Type\" : \"OperationalSkill\",\n" +
                  "        \"Alias\" : \"\",\n" +
                  "        \"FormattedName\" : \"\",\n" +
                  "        \"Evidence\" : \"SkillSection\",\n" +
                  "        \"LastUsed\" : \"\",\n" +
                  "        \"ExperienceInMonths\" : \"0\"\n" +
                  "      }, {\n" +
                  "        \"Skill\" : \"JAVA/J2EE\",\n" +
                  "        \"Type\" : \"OperationalSkill\",\n" +
                  "        \"Alias\" : \"Java/J2ee, Java/J2ee Spring\",\n" +
                  "        \"FormattedName\" : \"JAVA/J2EE\",\n" +
                  "        \"Evidence\" : \"SkillSection\",\n" +
                  "        \"LastUsed\" : \"\",\n" +
                  "        \"ExperienceInMonths\" : \"0\"\n" +
                  "      }, {\n" +
                  "        \"Skill\" : \"Tapestry\",\n" +
                  "        \"Type\" : \"OperationalSkill\",\n" +
                  "        \"Alias\" : \"\",\n" +
                  "        \"FormattedName\" : \"\",\n" +
                  "        \"Evidence\" : \"SkillSection\",\n" +
                  "        \"LastUsed\" : \"\",\n" +
                  "        \"ExperienceInMonths\" : \"0\"\n" +
                  "      }, {\n" +
                  "        \"Skill\" : \"HTML\",\n" +
                  "        \"Type\" : \"OperationalSkill\",\n" +
                  "        \"Alias\" : \"Basic Html, Html 3.2/4.0, Html 4.0.1, Html 4.01, Html4.0, Hypertext Markup Language\",\n" +
                  "        \"FormattedName\" : \"HTML\",\n" +
                  "        \"Evidence\" : \"SkillSection\",\n" +
                  "        \"LastUsed\" : \"\",\n" +
                  "        \"ExperienceInMonths\" : \"0\"\n" +
                  "      } ]\n" +
                  "    },\n" +
                  "    \"Experience\" : \"PROJECT\\nThis application is used to distribute the product orders based on catalog to different types of fulfilment  \\n \\t  vendors. It keeps tracks of master catalog data and solving issue product order up to its end to end users.\",\n" +
                  "    \"SegregatedExperience\" : {\n" +
                  "      \"WorkHistory\" : [ ]\n" +
                  "    },\n" +
                  "    \"CurrentEmployer\" : \"\",\n" +
                  "    \"JobProfile\" : \"\",\n" +
                  "    \"WorkedPeriod\" : {\n" +
                  "      \"TotalExperienceInMonths\" : \"\",\n" +
                  "      \"TotalExperienceInYear\" : \"\",\n" +
                  "      \"TotalExperienceRange\" : \"\"\n" +
                  "    },\n" +
                  "    \"GapPeriod\" : \"\",\n" +
                  "    \"AverageStay\" : \"\",\n" +
                  "    \"LongestStay\" : \"\",\n" +
                  "    \"Summary\" : \"\",\n" +
                  "    \"ExecutiveSummary\" : \"\",\n" +
                  "    \"ManagementSummary\" : \"\",\n" +
                  "    \"Coverletter\" : \"\",\n" +
                  "    \"Certification\" : \"\",\n" +
                  "    \"Publication\" : \"\",\n" +
                  "    \"CurrentLocation\" : \"Pune\",\n" +
                  "    \"PreferredLocation\" : \"\",\n" +
                  "    \"Availability\" : \"\",\n" +
                  "    \"Hobbies\" : \"\",\n" +
                  "    \"Objectives\" : \"Aim to work in a challenging work environment where I can utilize my expertise in technical skills, towards the development and implementation of the new ideas and contributing to growth of the organization.\",\n" +
                  "    \"Achievements\" : \"\",\n" +
                  "    \"References\" : \"\",\n" +
                  "    \"CustomFields\" : \"Parsing Time : 1845ms;\",\n" +
                  "    \"EmailInfo\" : {\n" +
                  "      \"EmailTo\" : \" \",\n" +
                  "      \"EmailBody\" : \" \",\n" +
                  "      \"EmailReplyTo\" : \" \",\n" +
                  "      \"EmailSignature\" : \" \",\n" +
                  "      \"EmailFrom\" : \" \",\n" +
                  "      \"EmailSubject\" : \" \",\n" +
                  "      \"EmailCC\" : \" \"\n" +
                  "    },\n" +
                  "    \"Recommendations\" : {\n" +
                  "      \"Recommendation\" : [ {\n" +
                  "        \"PersonName\" : \"\",\n" +
                  "        \"PositionTitle\" : \"\",\n" +
                  "        \"CompanyName\" : \"\",\n" +
                  "        \"Relation\" : \"\",\n" +
                  "        \"Description\" : \"\"\n" +
                  "      } ]\n" +
                  "    },\n" +
                  "    \"DetailResume\" : \"\",\n" +
                  "    \"HtmlResume\" : \"\",\n" +
                  "    \"CandidateImage\" : {\n" +
                  "      \"CandidateImageData\" : \"\",\n" +
                  "      \"CandidateImageFormat\" : \"\"\n" +
                  "    },\n" +
                  "    \"TemplateOutput\" : {},\n" +
                  "    \"Platform\" : \"\"\n" +
                  "  }\n" +
                  "}";

            rchilliJsonResponse= rchilliJsonResponse.replace("{\n" +
                    "  \"ResumeParserData\" : ","");

            rchilliFormattedJson=rchilliJsonResponse.substring(0, rchilliJsonResponse.indexOf(",\n" +
                    "    \"DetailResume\""))+"\n"+"}";
            //log.info("RchilliJsonResponse  : "+rchilliJsonResponse);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            bean = mapper.readValue(rchilliJsonResponse, ResumeParserDataRchilliBean.class);
            //log.info("ResumeParserDataRchilliBean :"+resumeParserDataRchilliBean);
            candidate=setCandidateModel(bean,user);


            if(candidateProcessed >= MasterDataBean.getInstance().getConfigSettings().getCandidatesPerFileLimit()) {
                log.error(IErrorMessages.MAX_CANDIDATE_PER_FILE_EXCEEDED + " : user id : " +  user.getId());
            }
            //check for daily limit per user
            if (candidateProcessed >= MasterDataBean.getInstance().getConfigSettings().getDailyCandidateUploadPerUserLimit()) {
                log.error(IErrorMessages.MAX_CANDIDATES_PER_USER_PER_DAY_EXCEEDED  + " : user id : " +  user.getId());
            }
            candidate=uploadDataProcessService.validateDataAndSaveJcmAndJcmCommModel(null,candidate,user,!candidate.getMobile().isEmpty(),job);
            jobControllerMappingService.saveCandidateSupportiveInfo(candidate,user);
        }catch(Exception e) {
            log.error("Error while processing candidate in drag and drop : " + ((null!=candidate) ? candidate.getEmail() :user.getEmail()) + " : " + e.getMessage(), HttpStatus.BAD_REQUEST);
            isCandidateFailedToProcess=true;
        }

        try {
            File file=new File(filePath);
            DiskFileItem fileItem = new DiskFileItem("file", "text/plain", false, file.getName(), (int) file.length() , file.getParentFile());
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            int ret = input.read();
            while ( ret != -1 )
            {
                os.write(ret);
                ret = input.read();
            }
            os.flush();
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
            if(isCandidateFailedToProcess)
                StoreFileUtil.storeFile(multipartFile, jobId, environment.getProperty(IConstant.REPO_LOCATION), IConstant.ERROR_FILES,candidate.getId());
            else
                StoreFileUtil.storeFile(multipartFile, jobId, environment.getProperty(IConstant.REPO_LOCATION), IConstant.UPLOAD_TYPE.CandidateCv.toString(),candidate.getId());

            //file.delete();
        } catch (Exception ex) {
            log.error("Error while save candidate resume in drag and drop : " + fileName + " : " + ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            //Add cv_parsing_details
            CvParsingDetails cvParsingDetails = new CvParsingDetails();
            cvParsingDetails.setCvFileName(fileName);
            cvParsingDetails.setProcessedOn(new Date());
            cvParsingDetails.setProcessingTime(rchilliResponseTime);
            if(isCandidateFailedToProcess)
                cvParsingDetails.setProcessingStatus(IConstant.UPLOAD_STATUS.Failure.toString());
            else
                cvParsingDetails.setProcessingStatus(IConstant.UPLOAD_STATUS.Success.toString());

            cvParsingDetails.setParsingResponseHtml(bean.getHtmlResume());
            cvParsingDetails.setParsingResponseText(bean.getDetailResume());
            cvParsingDetails.setParsingResponseJson(rchilliFormattedJson);
            cvParsingDetailsRepository.save(cvParsingDetails);
        } catch (Exception e) {
            log.info("Save CvParsingDetails");
            e.printStackTrace();
        }
    }

    private Candidate setCandidateModel(ResumeParserDataRchilliBean bean, User user) {
        String mobile=bean.getMobile().isEmpty() ? bean.getPhone() : bean.getMobile();
        //Format mobile no
        mobile=Util.indianMobileConvertor(mobile);

        Candidate candidate=new Candidate(bean.getFirstName(),bean.getLastName(),bean.getEmail(), mobile,null,new Date(),null);
        candidate.setCandidateName(bean.getFullName());
        candidate.setCandidateSource(IConstant.CandidateSource.DragDropCv.toString());
        candidate.setCountryCode(user.getCountryId().getCountryCode());

        CandidateDetails candidateDetails=new CandidateDetails();
        candidateDetails.setDateOfBirth(Util.convertStringToDate(bean.getDateOfBirth()));
        candidateDetails.setGender(bean.getGender());

        if(bean.getSkills().length()>255)
            candidateDetails.setKeySkills(bean.getSkills());
        else
            candidateDetails.setKeySkills(bean.getSkills().substring(0,255));

        candidateDetails.setMaritalStatus(bean.getMaritalStatus());
        if(!bean.getFormattedAddress().isEmpty())
            candidateDetails.setCurrentAddress(bean.getFormattedAddress());
        else
            candidateDetails.setCurrentAddress(bean.getFormattedAddress());

        candidate.setCandidateDetails(candidateDetails);

        List<CandidateEducationDetails> candidateEducationDetailsList=new ArrayList<>();
        bean.getSegregatedQualification().getEducationSplit().forEach(educationSplit -> {
            CandidateEducationDetails candidateEducationDetails= new CandidateEducationDetails();

            if(educationSplit.getInstitution().getName().length()>75)
                candidateEducationDetails.setInstituteName(educationSplit.getInstitution().getName().substring(0,75));
            else
                candidateEducationDetails.setInstituteName(educationSplit.getInstitution().getName());

            candidateEducationDetails.setDegree(educationSplit.getDegree());
            if(!educationSplit.getEndDate().isEmpty()){
                candidateEducationDetails.setYearOfPassing(Util.getYearFromStringDate(educationSplit.getEndDate()));
            }
            candidateEducationDetailsList.add(candidateEducationDetails);
        });
        candidate.setCandidateEducationDetails(candidateEducationDetailsList);

        List<CandidateCompanyDetails> candidateCompanyDetailsList = new ArrayList<>();
        List<CandidateProjectDetails> candidateProjectDetailsList = new ArrayList<>();
        bean.getSegregatedExperience().getWorkHistory().forEach(workHistory -> {
            CandidateCompanyDetails candidateCompanyDetails = new CandidateCompanyDetails();
            candidateCompanyDetails.setCompanyName(workHistory.getEmployer());
            candidateCompanyDetails.setDesignation(workHistory.getJobProfile().getTitle());
            candidateCompanyDetails.setLocation(workHistory.getJobLocation().getEmployerCity());
            candidateCompanyDetails.setStartDate(Util.convertStringToDate(workHistory.getStartDate()));
            candidateCompanyDetails.setEndDate(Util.convertStringToDate(workHistory.getEndDate()));
            candidateCompanyDetailsList.add(candidateCompanyDetails);

            workHistory.getProjects().forEach(projects -> {
                CandidateProjectDetails candidateProjectDetails = new CandidateProjectDetails();
                candidateProjectDetails.setCompanyName(workHistory.getEmployer());
                candidateProjectDetails.setSkillsUsed(projects.getUsedSkills());
                candidateProjectDetailsList.add(candidateProjectDetails);
            });
        });
        candidate.setCandidateCompanyDetails(candidateCompanyDetailsList);
        candidate.setCandidateProjectDetails(candidateProjectDetailsList);

        List<CandidateOnlineProfile> candidateOnlineProfileList= new ArrayList<>();
        bean.getWebSites().getWebsite().forEach(webSite -> {
            CandidateOnlineProfile candidateOnlineProfile = new CandidateOnlineProfile();
            candidateOnlineProfile.setUrl(webSite.getUrl());
            candidateOnlineProfile.setProfileType(webSite.getType());
            candidateOnlineProfileList.add(candidateOnlineProfile);
        });
        candidate.setCandidateOnlineProfiles(candidateOnlineProfileList);

        if(!bean.getLanguageKnown().isEmpty()){
            List<CandidateLanguageProficiency> candidateLanguageProficiencyList = new ArrayList<>();
            for (String language : bean.getLanguageKnown().split(",")) {
                CandidateLanguageProficiency candidateLanguageProficiency=new CandidateLanguageProficiency();
                candidateLanguageProficiency.setLanguage(language);
                candidateLanguageProficiencyList.add(candidateLanguageProficiency);
            }
            candidate.setCandidateLanguageProficiencies(candidateLanguageProficiencyList);
        }

        List<CandidateSkillDetails> candidateSkillDetailsList = new ArrayList<>();
        bean.getSkillKeywords().getSkillSet().forEach(skillSet -> {
            CandidateSkillDetails candidateSkillDetails=new CandidateSkillDetails();
            candidateSkillDetails.setSkill(skillSet.getSkill());
            candidateSkillDetails.setLastUsed(Util.convertStringToDate(skillSet.getLastUsed()));
            candidateSkillDetailsList.add(candidateSkillDetails);
        });
        candidate.setCandidateSkillDetails(candidateSkillDetailsList);
        return candidate;
    }
}
