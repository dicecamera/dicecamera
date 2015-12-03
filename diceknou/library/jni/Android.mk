LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libyuv
LOCAL_SRC_FILES := libyuv_static.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include

include $(PREBUILT_STATIC_LIBRARY)

# 2
#LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := dicecam-library

LOCAL_SRC_FILES := yuv-decoder.c
ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
    LOCAL_CFLAGS := -DHAVE_NEON=1
#    LOCAL_SRC_FILES += yuv-decoder-intrinsics.c.neon
    LOCAL_SRC_FILES := yuv-decoder-intrinsics.c.neon
    LOCAL_STATIC_LIBRARIES := libyuv
endif

#LOCAL_STATIC_LIBRARIES := cpufeatures

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)

#$(call import-module,cpufeatures)
