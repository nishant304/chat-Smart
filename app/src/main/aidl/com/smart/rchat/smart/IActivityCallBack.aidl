// IActivityCallBack.aidl
package com.smart.rchat.smart;

// Declare any non-default types here with import statements

interface IActivityCallBack {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    String getFriendIdInChat();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
