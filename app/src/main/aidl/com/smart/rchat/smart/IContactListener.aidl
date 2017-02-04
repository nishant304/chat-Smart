// IContactListener.aidl
package com.smart.rchat.smart;

import com.smart.rchat.smart.IActivityCallBack;
// Declare any non-default types here with import statements

interface IContactListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

     void registerActivityCallBack(IActivityCallBack callBack);
     void stopService();

}
