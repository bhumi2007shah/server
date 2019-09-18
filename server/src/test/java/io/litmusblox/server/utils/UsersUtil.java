package io.litmusblox.server.utils;

import io.litmusblox.server.model.Company;
import io.litmusblox.server.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author : sameer
 * Date : 18/09/19
 * Time : 11:46 AM
 * Class Name : Users
 * Project Name : server
 */
public class UsersUtil {
    private List<User> userList= new ArrayList<>();

    public List<User> getUserList() {
        if(this.userList.size()==0){
            addUser(new User(1L, "sameer@hexagonsearch.com", "123456", "sameer", "khan", "8109698905", "SuperAdmin", "SE", "active", new Company("Hexagon", true, new Date(), 1L), CountryUtil.getCountry(), UUID.randomUUID()));
            addUser(new User(1L, "sumit@litmusblox.io", "123456", "sumit", "bagul", "8856835916", "ClientAdmin", "SE", "active", new Company("Hexagon", true, new Date(), 1L), CountryUtil.getCountry(), UUID.randomUUID()));
        }
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void addUser(User user){
        this.userList.add(user);
    }

    public void deleteUser(User user){
        this.userList.remove(user);
    }
}
