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
    String REGEX_FOR_JOB_TITLE = "^[\\-\\.\\,a-zA-Z0-9\\s\\t]+$";
    String REGEX_FOR_JOB_ID = "^[a-zA-Z0-9]+$";

    // lengths
    Integer TITLE_MAX_LENGTH = 100;

    int JOB_ID_MAX_LENGTH = 10;
    Integer SCREENING_QUESTIONS_LIST_MAX_SIZE = 10;
    String TOKEN_HEADER = "Authorization";
    String TOKEN_PREFIX = "Bearer";


    enum UserStatus {
        New, Active, Blocked;
    }

    public enum UserRole {
        RECRUITER(Names.RECRUITER),
        SUPER_ADMIN(Names.SUPER_ADMIN),
        CLIENT_ADMIN(Names.CLIENT_ADMIN);

        public class Names{
            public static final String RECRUITER = "Recruiter";
            public static final String SUPER_ADMIN = "SuperAdmin";
            public static final String CLIENT_ADMIN = "ClientAdmin";
        }

        private final String label;

        private UserRole(String label) {
            this.label = label;
        }

        public String toString() {
            return this.label;
        }
    }

    enum AddJobPages {
        overview, screeningQuestions, keySkills, capabilities, jobDetail, hiringTeam;
    }

    enum JobStatus {
        DRAFT("Draft"), PUBLISHED("Published"), ARCHIVED("Archived");
        private String value;

        JobStatus(String val) {
            this.value = val;
        }

        public String getValue() {
            return this.value;
        }
    }


}
