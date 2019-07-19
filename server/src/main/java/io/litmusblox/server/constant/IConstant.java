/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.constant;

/**
 * @author : Sumit
 * Date : 4/7/19
 * Time : 11:35 PM
 * Class Name : IConstant
 * Project Name : server
 */
public interface IConstant {


     // Regex
     String REGEX_FOR_JOB_TITLE ="^[\\-\\.\\,a-zA-Z0-9\\s\\t]+$";
     String REGEX_FOR_COMPANY_JOB_ID = "^[a-zA-Z0-9]|$+$";

     String INDIA_CODE = "+91";
     String INDIAN_MOBILE_PATTERN = "(0/91)?[6-9][0-9]{9}";
     String JUNK_MOBILE_PATTERN = "([0-9])\\1{8,}";
     String REGEX_FOR_EMAIL_VALIDATION = "^[a-z0-9A-Z]+[\\w.]+@[a-zA-Z]+[a-zA-Z0-9.-]+[a-zA-Z]$";
     String REGEX_FOR_MOBILE_VALIDATION = "[\\d]+";
     String REGEX_TO_CLEAR_SPECIAL_CHARACTERS_FOR_EMAIL = "[^\\d\\w@.-]";
     String REGEX_TO_CLEAR_SPECIAL_CHARACTERS_FOR_MOBILE = "[^\\d]";
     String REGEX_FOR_NAME_VALIDATION = "[a-zA-Z\\-][a-zA-Z.\\-\\s]*";
     String REGEX_TO_CLEAR_SPECIAL_CHARACTERS_FOR_NAME = "[^\\w\\s\\-]*";
     String REGEX_FOR_DOT_IN_NAME = "([A-Z][\\.]?\\s)+";


     // lengths
     Integer TITLE_MAX_LENGTH=100;
     int JOB_ID_MAX_LENGTH=10;
     Integer SCREENING_QUESTIONS_LIST_MAX_SIZE=10;
     Integer CANDIDATE_NAME_MAX_LENGTH = 45;
     Integer CANDIDATE_EMAIL_MAX_LENGTH = 50;

     String MAX_CANDIDATES_PER_FILE = "maxCandidatesPerFile";
     String MAX_CANDIDATES_PER_USER_PER_DAY = "maxCandidatesPerUserPerDay";
     String REPO_LOCATION = "repoLocation";
     String DATE_FORMAT_yyyymmdd_hhmm = "yyyyMMdd_HHmm";
     String STR_INDIA = "India";



/*     enum AddJobPages {
         OVERVIEW("overview"),SCREENING_QUESTIONS("screeningQuestions"),KEY_SKILLS("keySkills"),CAPABILITIES("capabilities");

        private String value;

        AddJobPages(String val) {this.value = val;}
        public String getValue() {return this.value;}
     }*/

    enum AddJobPages {
        overview,screeningQuestions,keySkills,capabilities,jobDetail,hiringTeam;
    }

     enum JobStatus {
         DRAFT("Draft"),PUBLISHED ("Published"),ARCHIVED("Archived");
         private String value;

         JobStatus(String val) {this.value = val;}

         public String getValue() {return this.value;}
     }

    enum UPLOAD_STATUS {
        Success, Failure, Partial_Success
    }

    enum STAGE {
        Source,Screen,Interview,Offer
    }

    enum UPLOAD_FORMATS_SUPPORTED {
        LitmusBlox, Naukri;
    }

    enum CandidateSource {
        SingleCandidateUpload("Single Candidate Upload"), File("File Upload"), Plugin("Plugin Upload");
        private String value;

        CandidateSource(String val) {
            this.value = val;
        }

        public String getValue() {
            return this.value;
        }
    }

    enum LITMUSBLOX_FILE_COLUMNS {
        FirstName("First Name"), LastName("Last Name"), Email("Email"), Mobile("Mobile");
        private String value;

        LITMUSBLOX_FILE_COLUMNS(String val) { this.value = val; }

        public String getValue() { return this.value; }
    }

    enum NAUKRI_FILE_COLUMNS {
        SerialNumber("Serial Number"), CandidateName("Name of the Candidate"), ResumeId("Resume ID"), PostalAddress("Postal Address"), Telephone("Telephone No."), Mobile("Mobile No."), DOB("Date of Birth"), Email("Email"), WorkExperience("Work Experience"), ResumeTitle("Resume Title"), CurrentLocation("Current Location"), PreferredLocation("Preferred Location"), CurrentEmployer("Current Employer"), CurrentDesignation("Current Designation"), AnnualSalary("Annual Salary"), UGCourse("U.G. Course"), PGCourse("P. G. Course"), PPGCourse("P.P.G. Course"), LastActive("Last Active Date");
        private String value;

        NAUKRI_FILE_COLUMNS(String val) { this.value = val; }

        public String getValue() { return this.value; }
    }

    enum NAUKRI_XLS_FILE_COLUMNS {
        SerialNumber("Serial Number"), CandidateName("Name of the Candidate"), ResumeId("Resume ID"), PostalAddress("Postal Address"), Telephone("Telephone No."), Mobile("Mobile No."), DOB("Date of Birth"), Email("Email"), WorkExperience("Work Experience"), ResumeTitle("Resume Title"), CurrentLocation("Current Location"), PreferredLocation("Preferred Location"), CurrentEmployer("Current Employer"), CurrentDesignation("Current Designation"), AnnualSalary("Annual Salary"), UGCourse("U.G. Course"), PGCourse("P. G. Course"), PPGCourse("Post P. G. Course"), LastActive("Last Active Date");
        private String value;

        NAUKRI_XLS_FILE_COLUMNS(String val) { this.value = val; }

        public String getValue() { return this.value; }
    }

    String[] supportedExtensions = new String[] {"xls", "xlsx", "xml", "csv"};

}
