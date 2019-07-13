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
     String REGEX_FOR_JOB_ID = "^[a-zA-Z0-9]+$";

     // lengths
     Integer TITLE_MAX_LENGTH=100;

     int JOB_ID_MAX_LENGTH=10;
     Integer SCREENING_QUESTIONS_LIST_MAX_SIZE=10;

/*     enum AddJobPages {
         OVERVIEW("overview"),SCREENING_QUESTIONS("screeningQuestions"),KEY_SKILLS("keySkills"),CAPABILITIES("capabilities");

        private String value;

        AddJobPages(String val) {this.value = val;}
        public String getValue() {return this.value;}
     }*/

    enum AddJobPages {
        overview,screeningQuestions,keySkills,capabilities;
    }

     enum JobStatus {
         DRAFT("Draft"),PUBLISHED ("Published"),ARCHIVED("Archived");
         private String value;

         JobStatus(String val) {this.value = val;}

         public String getValue() {return this.value;}
     }


}
