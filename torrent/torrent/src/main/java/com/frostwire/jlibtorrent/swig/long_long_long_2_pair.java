/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.frostwire.jlibtorrent.swig;

public class long_long_long_2_pair {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected long_long_long_2_pair(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(long_long_long_2_pair obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        libtorrent_jni.delete_long_long_long_2_pair(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public long_long_long_2_pair() {
    this(libtorrent_jni.new_long_long_long_2_pair__SWIG_0(), true);
  }

  public long_long_long_2_pair(long first, int second) {
    this(libtorrent_jni.new_long_long_long_2_pair__SWIG_1(first, second), true);
  }

  public long_long_long_2_pair(long_long_long_2_pair p) {
    this(libtorrent_jni.new_long_long_long_2_pair__SWIG_2(long_long_long_2_pair.getCPtr(p), p), true);
  }

  public void setFirst(long value) {
    libtorrent_jni.long_long_long_2_pair_first_set(swigCPtr, this, value);
  }

  public long getFirst() {
    return libtorrent_jni.long_long_long_2_pair_first_get(swigCPtr, this);
  }

  public void setSecond(int value) {
    libtorrent_jni.long_long_long_2_pair_second_set(swigCPtr, this, value);
  }

  public int getSecond() {
    return libtorrent_jni.long_long_long_2_pair_second_get(swigCPtr, this);
  }

}
