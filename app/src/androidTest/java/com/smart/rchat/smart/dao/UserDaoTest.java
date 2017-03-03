package com.smart.rchat.smart.dao;

import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.smart.rchat.smart.models.User;

import org.junit.After;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by nishant on 26.02.17.
 */

public class UserDaoTest  {

    @Test
    public void insertUserTest(){
        Context context = InstrumentationRegistry.getContext();
        Uri uri = UserDao.insertValues(context,new User(UUID.randomUUID()+"","","",""));
        assert (uri != null);
    }

    @After
    public void clean(){
        UserDao.clearData(InstrumentationRegistry.getContext());
    }

}
