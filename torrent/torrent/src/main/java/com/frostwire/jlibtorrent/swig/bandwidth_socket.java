/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.frostwire.jlibtorrent.swig;

public class bandwidth_socket {
  private long swigCPtr;
  private boolean swigCMemOwn;

  protected bandwidth_socket(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(bandwidth_socket obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        libtorrent_jni.delete_bandwidth_socket(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void assign_bandwidth(int channel, int amount) {
    libtorrent_jni.bandwidth_socket_assign_bandwidth(swigCPtr, this, channel, amount);
  }

  public boolean is_disconnecting() {
    return libtorrent_jni.bandwidth_socket_is_disconnecting(swigCPtr, this);
  }

}
