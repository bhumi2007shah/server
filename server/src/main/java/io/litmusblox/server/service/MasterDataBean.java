package io.litmusblox.server.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.litmusblox.server.model.Country;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Shital Raval
 * Date : 4/7/19
 * Time : 1:34 PM
 * Class Name : MasterDataBean
 * Project Name : server
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MasterDataBean {
    @JsonIgnore
    private static MasterDataBean instance;

    private boolean loaded = false;

    public static MasterDataBean getInstance() {
        if (instance == null) {
            synchronized (MasterDataBean.class) {
                if (instance == null) {
                    instance = new MasterDataBean();
                }
            }
        }
        return instance;
    }

    private List<Country> countryList = new ArrayList<Country>();

    //names in the class variable below do not follow camel case to leverage reflection
    private Map<Long, String> ImportanceLevel = new HashMap<>();
    private Map<Long, String> QuestionType = new HashMap<>();
    private Map<Long, String> ExperienceRange = new HashMap<>();
    private Map<Long, String> AddressType = new HashMap<>();
    private Map<Long, String> Stage = new HashMap<>();
    private Map<Long, String> Process = new HashMap<>();
    private Map<Long, String> Function = new HashMap<>();
    private Map<Long, String> Expertise = new HashMap<>();
    private Map<Long, String> Education = new HashMap<>();
}
