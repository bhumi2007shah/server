/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.uploadProcessor;

import lombok.Data;

import java.util.List;

/**
 * @author : Sumit
 * Date : 27/08/19
 * Time : 2:15 PM
 * Class Name : ResumeParserDataBean
 * Project Name : server
 */
@Data
public class ResumeParserDataRchilliBean {

    private String ResumeFileName;
    private String ResumeLanguage;
    private String ParsingDate;
    private String FullName;
    private String TitleName;
    private String FirstName;
    private String Middlename;
    private String LastName;
    private String DateOfBirth;
    private String Gender;
    private String FatherName;
    private String MotherName;
    private String MaritalStatus;
    private String Nationality;
    private String LanguageKnown;
    private String UniqueID;
    private String LicenseNo;
    private String PassportNo;
    private String PanNo;
    private String VisaStatus;
    private String Email;
    private String AlternateEmail;
    private String Phone;
    private String FormattedPhone;
    private String Mobile;
    private String FormattedMobile;
    private String FaxNo;
    private WebSites WebSites;
    private String Address;
    private String City;
    private String State;
    private String Country;
    private String ZipCode;
    private String FormattedAddress;
    private String PermanentAddress;
    private String PermanentCity;
    private String PermanentState;
    private String PermanentCountry;
    private String PermanentZipCode;
    private String FormattedPermanentAddress;
    private String Category;
    private String SubCategory;
    private String CurrentSalary;
    private String ExpectedSalary;
    private String Qualification;
    private SegregatedQualification SegregatedQualification;
    private String Skills;
    private SkillKeywords SkillKeywords;
    private String Experience;
    private SegregatedExperience SegregatedExperience;
    private String CurrentEmployer;
    private String JobProfile;
    private WorkedPeriod workedPeriod;
    private String GapPeriod;
    private String AverageStay;
    private String LongestStay;
    private String Summary;
    private String ExecutiveSummary;
    private String ManagementSummary;
    private String Coverletter;
    private String Certification;
    private String Publication;
    private String CurrentLocation;
    private String PreferredLocation;
    private String Availability;
    private String Hobbies;
    private String Objectives;
    private String Achievements;
    private String References;
    private String CustomFields;
    private EmailInfo EmailInfo;
    private Recommendations Recommendations;
    private String DetailResume;
    private String HtmlResume;
    private CandidateImage candidateImage;
    private TemplateOutput TemplateOutput;
    private String Platform;
}
    @Data
    class WebSites {
       private List<WebSite> Website;
    }

    @Data
    class WebSite {
        private String Type;
        private String Url;
    }

    @Data
    class SegregatedQualification {
        private List<EducationSplit> EducationSplit;
    }

    @Data
    class EducationSplit{
        private Institution Institution;
        private String Degree;
        private String StartDate;
        private String EndDate;
        private Aggregate Aggregate;
    }

    @Data
    class Institution{
        private String Name;
        private String Type;
        private String City;
        private String State;
        private String Country;
    }

    @Data
    class Aggregate{
        private String Value;
        private String MeasureType;
    }

    @Data
    class SkillKeywords{
        private List<SkillSet> SkillSet;
    }

    @Data
    class SkillSet{
        private String Skill;
        private String Type;
        private String Alias;
        private String FormattedName;
        private String Evidence;
        private String LastUsed;
        private String ExperienceInMonths;
    }

    @Data
    class SegregatedExperience{
        private List<WorkHistory> WorkHistory;
    }

    @Data
    class WorkHistory{
        private String Employer;
        private JobProfile jobProfile;
        private JobLocation JobLocation;
        private String JobPeriod;
        private String StartDate;
        private String EndDate;
        private String JobDescription;
        private List<Projects> projects;
    }

    @Data
    class JobProfile{
        private String Title;
        private String FormattedName;
        private String Alias;
        private String RelatedSkills;
    }

    @Data
    class JobLocation{
        private String EmployerCity;
        private String EmployerState;
        private String EmployerCountry;
        private String IsoCountry;
    }

    @Data
    class Projects{
        private String UsedSkills;
        private String ProjectName;
        private String TeamSize;
    }

    @Data
    class WorkedPeriod{
        private String TotalExperienceInMonths;
        private String TotalExperienceInYear;
        private String TotalExperienceRange;
    }

    @Data
    class EmailInfo{
        private String EmailTo;
        private String EmailBody;
        private String EmailReplyTo;
        private String EmailSignature;
        private String EmailFrom;
        private String EmailSubject;
        private String EmailCC;
    }

    @Data
    class Recommendations{
        private List<Recommendation> Recommendation;
    }

    @Data
    class Recommendation{
        private String PersonName;
        private String PositionTitle;
        private String CompanyName;
        private String Relation;
        private String Description;
    }

    @Data
    class CandidateImage{
        private String CandidateImageData;
        private String CandidateImageFormat;
    }

    @Data
    class TemplateOutput{
        private String TemplateOutputFileName;
        private String TemplateOutputData;
    }
