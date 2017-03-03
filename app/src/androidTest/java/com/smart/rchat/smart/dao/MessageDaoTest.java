package com.smart.rchat.smart.dao;

import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.smart.rchat.smart.models.MessageRequest;

import org.junit.After;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by nishant on 26.02.17.
 */

public class MessageDaoTest {

    @Test
    public void testMessageInsertion(){
        Context context = InstrumentationRegistry.getContext();
        Uri uri = MessageDao.insertValue(context,new MessageRequest(UUID.randomUUID()+"",UUID.randomUUID()+"","testMessage",1),
                UUID.randomUUID()+"");
        assert (uri!=null);
    }

    @After
    public void clean(){
        MessageDao.clearData(InstrumentationRegistry.getContext());
    }

}
