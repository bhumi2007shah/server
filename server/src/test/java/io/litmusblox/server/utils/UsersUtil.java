package io.litmusblox.server.utils;

import io.litmusblox.server.model.Company;
import io.litmusblox.server.model.User;
import org.mockito.Mock;
import org.mockito.Mockito;

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
        List<User> mockList = new ArrayList<>();
        User user1 = Mockito.mock(User.class);
        mockList.add(user1);
        User user2 = Mockito.mock(User.class);
        mockList.add(user2);
        return mockList;
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
