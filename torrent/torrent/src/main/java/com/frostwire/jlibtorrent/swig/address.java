/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.frostwire.jlibtorrent.swig;

public class address {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected address(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(address obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        libtorrent_jni.delete_address(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public address() {
    this(libtorrent_jni.new_address__SWIG_0(), true);
  }

  public address(address_v4 ipv4_address) {
    this(libtorrent_jni.new_address__SWIG_1(address_v4.getCPtr(ipv4_address), ipv4_address), true);
  }

  public address(address_v6 ipv6_address) {
    this(libtorrent_jni.new_address__SWIG_2(address_v6.getCPtr(ipv6_address), ipv6_address), true);
  }

  public address(address other) {
    this(libtorrent_jni.new_address__SWIG_3(address.getCPtr(other), other), true);
  }

  public boolean is_v4() {
    return libtorrent_jni.address_is_v4(swigCPtr, this);
  }

  public boolean is_v6() {
    return libtorrent_jni.address_is_v6(swigCPtr, this);
  }

  public address_v4 to_v4() {
    return new address_v4(libtorrent_jni.address_to_v4(swigCPtr, this), true);
  }

  public String to_string() {
    return libtorrent_jni.address_to_string__SWIG_0(swigCPtr, this);
  }

  public String to_string(error_code ec) {
    return libtorrent_jni.address_to_string__SWIG_1(swigCPtr, this, error_code.getCPtr(ec), ec);
  }

  public boolean is_loopback() {
    return libtorrent_jni.address_is_loopback(swigCPtr, this);
  }

  public boolean is_unspecified() {
    return libtorrent_jni.address_is_unspecified(swigCPtr, this);
  }

  public boolean is_multicast() {
    return libtorrent_jni.address_is_multicast(swigCPtr, this);
  }

}
