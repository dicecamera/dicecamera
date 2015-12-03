#include <jni.h>
#include <android/log.h>
#include "include/libyuv.h"


JNIEXPORT void JNICALL Java_com_sorasoft_dicecam_engine_DicecamNativeLibrary_YUVtoRBGA(JNIEnv * env, jobject obj, jbyteArray yuv420sp, jint width, jint height, jintArray rgbOut)
{
    uint8 *rgba = (uint8*) ((*env)->GetPrimitiveArrayCritical(env, rgbOut, 0));
    uint8 *yuv = (uint8*) (*env)->GetPrimitiveArrayCritical(env, yuv420sp, 0);
    uint8 *argb = (uint8*)malloc(width*height*4);

    //I420ToRGBA(yuv, width, yuv+(width*height), width, yuv+(width*height+width/2*height), width, rgba, width*4, width, height);
    NV21ToARGB(yuv, width, yuv+(width*height), width, argb, width*4, width, height);
    ABGRToARGB(argb, width*4, rgba, width*4, width, height);

    free(argb); argb = NULL;

    (*env)->ReleasePrimitiveArrayCritical(env, rgbOut, rgba, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, yuv420sp, yuv, 0);
}

JNIEXPORT void JNICALL Java_com_sorasoft_dicecam_engine_DicecamNativeLibrary_YUVtoARBG(JNIEnv * env, jobject obj, jbyteArray yuv420sp, jint width, jint height, jintArray rgbOut)
{
    int             sz;
    int             i;
    int             j;
    int             Y;
    int             Cr = 0;
    int             Cb = 0;
    int             pixPtr = 0;
    int             jDiv2 = 0;
    int             R = 0;
    int             G = 0;
    int             B = 0;
    int             cOff;
    int w = width;
    int h = height;
    sz = w * h;

    jint *rgbData = (jint*) ((*env)->GetPrimitiveArrayCritical(env, rgbOut, 0));
    jbyte* yuv = (jbyte*) (*env)->GetPrimitiveArrayCritical(env, yuv420sp, 0);

    for(j = 0; j < h; j++) {
             pixPtr = j * w;
             jDiv2 = j >> 1;
             for(i = 0; i < w; i++) {
                     Y = yuv[pixPtr];
                     if(Y < 0) Y += 255;
                     if((i & 0x1) != 1) {
                             cOff = sz + jDiv2 * w + (i >> 1) * 2;
                             Cb = yuv[cOff];
                             if(Cb < 0) Cb += 127; else Cb -= 128;
                             Cr = yuv[cOff + 1];
                             if(Cr < 0) Cr += 127; else Cr -= 128;
                     }
                     R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
                     if(R < 0) R = 0; else if(R > 255) R = 255;
                     G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1) + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
                     if(G < 0) G = 0; else if(G > 255) G = 255;
                     B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
                     if(B < 0) B = 0; else if(B > 255) B = 255;
                     rgbData[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
             }
    }

    (*env)->ReleasePrimitiveArrayCritical(env, rgbOut, rgbData, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, yuv420sp, yuv, 0);
}




JNIEXPORT void JNICALL Java_com_sorasoft_dicecam_engine_DicecamNativeLibrary_TEST(JNIEnv * env, jobject obj, jbyteArray yuv420sp, jint width, jint height, jintArray rgbOut)
{
    /*
    int I420ToRGBA(const uint8* src_y, int src_stride_y,
               const uint8* src_u, int src_stride_u,
               const uint8* src_v, int src_stride_v,
               uint8* dst_rgba, int dst_stride_rgba,
               int width, int height);
int NV21ToARGB(const uint8* src_y, int src_stride_y,
               const uint8* src_vu, int src_stride_vu,
               uint8* dst_argb, int dst_stride_argb,
               int width, int height);
*/
        /*
    int ARGBToRGBA(const uint8* src_frame, int src_stride_frame,
               uint8* dst_argb, int dst_stride_argb,
               int width, int height);
*/
    uint8 *rgba = (uint8*) ((*env)->GetPrimitiveArrayCritical(env, rgbOut, 0));
    uint8 *yuv = (uint8*) (*env)->GetPrimitiveArrayCritical(env, yuv420sp, 0);
    uint8 *argb = (uint8*)malloc(width*height*4);

    //I420ToRGBA(yuv, width, yuv+(width*height), width, yuv+(width*height+width/2*height), width, rgba, width*4, width, height);
    NV21ToARGB(yuv, width, yuv+(width*height), width, argb, width*4, width, height);
    ABGRToARGB(argb, width*4, rgba, width*4, width, height);

    free(argb); argb = NULL;

    (*env)->ReleasePrimitiveArrayCritical(env, rgbOut, rgba, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, yuv420sp, yuv, 0);
}
