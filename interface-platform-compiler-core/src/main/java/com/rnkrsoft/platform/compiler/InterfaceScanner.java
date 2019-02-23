package com.rnkrsoft.platform.compiler;

import com.rnkrsoft.platform.protocol.*;

import javax.web.doc.*;
import javax.web.doc.enums.ElementSetType;
import javax.web.doc.enums.FilterType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by rnkrsoft.com on 2019/2/3.
 */
public class InterfaceScanner {
    public static DocScanner scan(String ... basePackages){
        DocScanner docScanner = DocScannerFactory.newInstance();
        docScanner.register(new InterfaceColumnFilter() {

            @Override
            public ElementSetType getObjectType() {
                return ElementSetType.REQUEST;
            }

            @Override
            public FilterType match(Method method, Class objectClass, Field field, String fullColumnName) {
                if ("sessionId".equals(fullColumnName)) {
                    if (SessionIdAble.class.isAssignableFrom(objectClass) || SessionIdReadable.class.isAssignableFrom(objectClass) || SessionIdWritable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("clientIp".equals(fullColumnName)) {
                    if (ClientNetworkAble.class.isAssignableFrom(objectClass) || ClientNetworkReadable.class.isAssignableFrom(objectClass) || ClientNetworkWritable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("clientPort".equals(fullColumnName)) {
                    if (ClientNetworkAble.class.isAssignableFrom(objectClass) || ClientNetworkReadable.class.isAssignableFrom(objectClass) || ClientNetworkWritable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("lng".equals(fullColumnName)) {
                    if (GisPosAble.class.isAssignableFrom(objectClass) || GisPosReadable.class.isAssignableFrom(objectClass) || GisPosWritable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("lat".equals(fullColumnName)) {
                    if (GisPosAble.class.isAssignableFrom(objectClass) || GisPosReadable.class.isAssignableFrom(objectClass) || GisPosWritable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("token".equals(fullColumnName)) {
                    if (TokenAble.class.isAssignableFrom(objectClass) || TokenReadable.class.isAssignableFrom(objectClass) || TokenWritable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("userId".equals(fullColumnName)) {
                    if (UserInfoAble.class.isAssignableFrom(objectClass) || UserInfoWritable.class.isAssignableFrom(objectClass) || UserInfoReadable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("userName".equals(fullColumnName)) {
                    if (UserInfoAble.class.isAssignableFrom(objectClass) || UserInfoWritable.class.isAssignableFrom(objectClass) || UserInfoReadable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("appVersion".equals(fullColumnName)){
                    if (DeviceInfoAble.class.isAssignableFrom(objectClass) || DeviceInfoWritable.class.isAssignableFrom(objectClass) || DeviceInfoReadable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("osVersion".equals(fullColumnName)){
                    if (DeviceInfoAble.class.isAssignableFrom(objectClass) || DeviceInfoWritable.class.isAssignableFrom(objectClass) || DeviceInfoReadable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("deviceType".equals(fullColumnName)){
                    if (DeviceInfoAble.class.isAssignableFrom(objectClass) || DeviceInfoWritable.class.isAssignableFrom(objectClass) || DeviceInfoReadable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("deviceModel".equals(fullColumnName)){
                    if (DeviceInfoAble.class.isAssignableFrom(objectClass) || DeviceInfoWritable.class.isAssignableFrom(objectClass) || DeviceInfoReadable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("deviceManufacturer".equals(fullColumnName)){
                    if (DeviceInfoAble.class.isAssignableFrom(objectClass) || DeviceInfoWritable.class.isAssignableFrom(objectClass) || DeviceInfoReadable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("channelNo".equals(fullColumnName)){
                    if (ChannelInfoAble.class.isAssignableFrom(objectClass) || ChannelInfoWritable.class.isAssignableFrom(objectClass) || ChannelInfoReadable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                if ("channelName".equals(fullColumnName)){
                    if (ChannelInfoAble.class.isAssignableFrom(objectClass) || ChannelInfoWritable.class.isAssignableFrom(objectClass) || ChannelInfoReadable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                return FilterType.ACCEPT;
            }
        });
        docScanner.register(new InterfaceColumnFilter() {

            @Override
            public ElementSetType getObjectType() {
                return ElementSetType.RESPONSE;
            }

            @Override
            public FilterType match(Method method, Class objectClass, Field field, String fullColumnName) {
                if ("rspCode".equals(fullColumnName) || "rspDesc".equals(fullColumnName)) {
                    if (Responseable.class.isAssignableFrom(objectClass) || RspCodeWritable.class.isAssignableFrom(objectClass) || RspCodeReadable.class.isAssignableFrom(objectClass)) {
                        return FilterType.IGNORE;
                    }
                }
                return FilterType.ACCEPT;
            }
        });
        docScanner.init(false, basePackages);
        return docScanner;
    }
}
