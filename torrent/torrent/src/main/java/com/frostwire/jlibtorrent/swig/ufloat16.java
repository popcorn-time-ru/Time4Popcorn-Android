/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.frostwire.jlibtorrent.swig;

public class ufloat16 {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected ufloat16(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ufloat16 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        libtorrent_jni.delete_ufloat16(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public ufloat16() {
    this(libtorrent_jni.new_ufloat16__SWIG_0(), true);
  }

  public ufloat16(int v) {
    this(libtorrent_jni.new_ufloat16__SWIG_1(v), true);
  }

}
