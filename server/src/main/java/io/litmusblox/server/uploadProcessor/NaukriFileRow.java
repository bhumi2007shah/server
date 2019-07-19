/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.uploadProcessor;

/**
 * @author : Sumit
 * Date : 19/7/19
 * Time : 4:44 PM
 * Class Name : NaukriFileRow
 * Project Name : server
 */


import lombok.Data;

/**
 * Field names in this class purposely start with a capital letter
 * This is to make sure reflection can be leverage to set values from each row read from the Naukri file
 * They are also made public to ensure reflection can be used to access them
 */
@Data
public class NaukriFileRow {

    public String SerialNumber;
    public String CandidateName;
    public String ResumeId;
    public String PostalAddress;
    public String Telephone;
    public String Mobile;
    public String DOB;
    public String Email;
    public String WorkExperience;
    public String ResumeTitle;
    public String CurrentLocation;
    public String PreferredLocation;
    public String CurrentEmployer;
    public String CurrentDesignation;
    public String AnnualSalary;
    public String UGCourse;
    public String PGCourse;
    public String PPGCourse;
    public String LastActive;
}
