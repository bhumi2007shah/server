/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.Constant;

/**
 * @author : Sumit
 * Date : 4/7/19
 * Time : 11:35 PM
 * Class Name : IConstant
 * Project Name : server
 */
public interface IConstant {


     // Regex
     String REGEX_FOR_JOB_TITLE = "^[a-zA-Z0-9.,-]*$";
     String REGEX_FOR_JOB_ID = "^[a-zA-Z0-9]*$";
     String REGEX_FOR_NO_OF_POSITIONS = "^[0-9]$";

     // lengths
     Integer TITLE_MAX_LENGTH=100;
     Integer JOB_ID_MAX_LENGTH=10;

     String OVERVIEW="overview";
     String SCREENING_QUESTIONS="screening question";


}
