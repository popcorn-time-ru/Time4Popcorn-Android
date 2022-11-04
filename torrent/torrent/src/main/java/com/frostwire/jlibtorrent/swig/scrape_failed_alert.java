/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.frostwire.jlibtorrent.swig;

public class scrape_failed_alert extends tracker_alert {
  private long swigCPtr;

  protected scrape_failed_alert(long cPtr, boolean cMemoryOwn) {
    super(libtorrent_jni.scrape_failed_alert_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(scrape_failed_alert obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        libtorrent_jni.delete_scrape_failed_alert(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public scrape_failed_alert(torrent_handle h, String u, error_code e) {
    this(libtorrent_jni.new_scrape_failed_alert__SWIG_0(torrent_handle.getCPtr(h), h, u, error_code.getCPtr(e), e), true);
  }

  public scrape_failed_alert(torrent_handle h, String u, String m) {
    this(libtorrent_jni.new_scrape_failed_alert__SWIG_1(torrent_handle.getCPtr(h), h, u, m), true);
  }

  public int type() {
    return libtorrent_jni.scrape_failed_alert_type(swigCPtr, this);
  }

  public int category() {
    return libtorrent_jni.scrape_failed_alert_category(swigCPtr, this);
  }

  public String what() {
    return libtorrent_jni.scrape_failed_alert_what(swigCPtr, this);
  }

  public String message() {
    return libtorrent_jni.scrape_failed_alert_message(swigCPtr, this);
  }

  public void setMsg(String value) {
    libtorrent_jni.scrape_failed_alert_msg_set(swigCPtr, this, value);
  }

  public String getMsg() {
    return libtorrent_jni.scrape_failed_alert_msg_get(swigCPtr, this);
  }

  public final static int alert_type = libtorrent_jni.scrape_failed_alert_alert_type_get();
  public final static int static_category = libtorrent_jni.scrape_failed_alert_static_category_get();
}
