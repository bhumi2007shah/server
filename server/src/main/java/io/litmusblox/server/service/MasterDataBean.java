package io.litmusblox.server.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.litmusblox.server.model.Country;
import io.litmusblox.server.model.MasterData;
import io.litmusblox.server.model.ScreeningQuestions;
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

    private Map<Long, String> keySkills = new HashMap<>();

    private Map<Long, String> questionType = new HashMap<>();
    private Map<Long, String> experienceRange = new HashMap<>();
    private Map<Long, String> addressType = new HashMap<>();
    private Map<Long, String> stage = new HashMap<>();
    private Map<Long, String> process = new HashMap<>();
    private Map<Long, String> function = new HashMap<>();
    private Map<Long, String> expertise = new HashMap<>();
    private Map<Long, String> education = new HashMap<>();
    private Map<Long, String> industry = new HashMap<>();
    private List<ScreeningQuestions> screeningQuestions = new ArrayList<>();

    //added the master data for 'Source' status as that will be used for all candidates uploaded
    private MasterData sourceStage = null;

    private ConfigSettings configSettings = new ConfigSettings();

    // sentryDSN is only read from application.properties file as per profile it is not save in database
    private String sentryDSN=null;
}
