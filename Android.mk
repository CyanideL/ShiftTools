# Copyright 2011 Crossbones Software

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4 roottools

LOCAL_SRC_FILES := $(call all-java-files-under,src)

LOCAL_PACKAGE_NAME := ShiftTools

LOCAL_MODULE_TAGS := optional

# disable proguard to make easy debugging
#LOCAL_PROGUARD_FLAG_FILES := proguard.cfg

# sign apk with shifttools key
LOCAL_CERTIFICATE := shifttools

include $(BUILD_PACKAGE)

##################################################
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := roottools:RootTools.jar

include $(BUILD_MULTI_PREBUILT)
