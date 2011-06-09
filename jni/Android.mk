LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := msaFw

LOCAL_SRC_FILES := msaFw.c


include $(BUILD_SHARED_LIBRARY)
