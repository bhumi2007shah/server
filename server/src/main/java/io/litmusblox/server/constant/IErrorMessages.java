/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.constant;

/**
 * @author : Sumit
 * Date : 5/7/19
 * Time : 11:55 AM
 * Class Name : IErrorMessages
 * Project Name : server
 */
public interface IErrorMessages {

    String NULL_MESSAGE="should not be null";
    String ALPHANUMERIC_MESSAGE="should be alphanumeric";
    String SPECIAL_CHARACTER_MESSAGE="should not contain special character";
    String EMPTY_AND_NULL_MESSAGE="should not be null or empty ";
    String SCREENING_QUESTIONS_VALIDATION_MESSAGE="Screening questions should not be more than 10 ";

    String MOBILE_NULL_OR_BLANK = "Mobile number cannot be null or blank";
    String MOBILE_INVALID_DATA = "Mobile number can contain only digits 0-9 in any combination";
    String INVALID_INDIAN_MOBILE_NUMBER = "Mobile number is not a valid Indian Mobile number ";
    String NAME_NULL_OR_BLANK = "Name cannot be null or blank";
    String NAME_FIELD_TOO_LONG = "Name cannot exceed 45 characters";
    String NAME_FIELD_SPECIAL_CHARACTERS = "Name should be only letters (A-Z)";
    String EMAIL_TOO_LONG = "Email address cannot exceed 50 characters";
    String EMAIL_NULL_OR_BLANK = "Email address cannot be null or blank";
    String INVALID_EMAIL = "Invalid Email address";
    String JUNK_MOBILE_NUMBER = "Mobile number is junk as it contains the same digits.";
    String INTERNAL_SERVER_ERROR = "Something went wrong into server";
    String MAX_CANDIDATE_PER_FILE_EXCEEDED = "Maximum candidates per file limit exceeded";
    String MAX_CANDIDATES_PER_USER_PER_DAY_EXCEEDED = "Daily limit for maximum candidates per user exceeded";
    String UNSUPPORTED_FILE_SOURCE = "Unsupported file source : ";
    String UNSUPPORTED_FILE_TYPE = "Unsupported file type with extension";
    String INVALID_SETTINGS = "Invalid System Settings";
    String MISSING_COLUMN_NAMES_FIRST_ROW = "First row in the file does not have correct column names";

}
