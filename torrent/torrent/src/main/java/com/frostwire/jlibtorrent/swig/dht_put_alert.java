/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.frostwire.jlibtorrent.swig;

public class dht_put_alert extends alert {
  private long swigCPtr;

  protected dht_put_alert(long cPtr, boolean cMemoryOwn) {
    super(libtorrent_jni.dht_put_alert_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(dht_put_alert obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        libtorrent_jni.delete_dht_put_alert(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public int type() {
    return libtorrent_jni.dht_put_alert_type(swigCPtr, this);
  }

  public int category() {
    return libtorrent_jni.dht_put_alert_category(swigCPtr, this);
  }

  public String what() {
    return libtorrent_jni.dht_put_alert_what(swigCPtr, this);
  }

  public String message() {
    return libtorrent_jni.dht_put_alert_message(swigCPtr, this);
  }

  public void setTarget(sha1_hash value) {
    libtorrent_jni.dht_put_alert_target_set(swigCPtr, this, sha1_hash.getCPtr(value), value);
  }

  public sha1_hash getTarget() {
    long cPtr = libtorrent_jni.dht_put_alert_target_get(swigCPtr, this);
    return (cPtr == 0) ? null : new sha1_hash(cPtr, false);
  }

  public void setSalt(String value) {
    libtorrent_jni.dht_put_alert_salt_set(swigCPtr, this, value);
  }

  public String getSalt() {
    return libtorrent_jni.dht_put_alert_salt_get(swigCPtr, this);
  }

  public void setSeq(java.math.BigInteger value) {
    libtorrent_jni.dht_put_alert_seq_set(swigCPtr, this, value);
  }

  public java.math.BigInteger getSeq() {
    return libtorrent_jni.dht_put_alert_seq_get(swigCPtr, this);
  }

  public char_vector public_key_v() {
    return new char_vector(libtorrent_jni.dht_put_alert_public_key_v(swigCPtr, this), true);
  }

  public char_vector signature_v() {
    return new char_vector(libtorrent_jni.dht_put_alert_signature_v(swigCPtr, this), true);
  }

  public final static int alert_type = libtorrent_jni.dht_put_alert_alert_type_get();
  public final static int static_category = libtorrent_jni.dht_put_alert_static_category_get();
}
