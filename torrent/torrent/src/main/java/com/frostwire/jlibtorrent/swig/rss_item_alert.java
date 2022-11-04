/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.frostwire.jlibtorrent.swig;

public class rss_item_alert extends alert {
  private long swigCPtr;

  protected rss_item_alert(long cPtr, boolean cMemoryOwn) {
    super(libtorrent_jni.rss_item_alert_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(rss_item_alert obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        libtorrent_jni.delete_rss_item_alert(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public rss_item_alert(feed_handle h, feed_item item) {
    this(libtorrent_jni.new_rss_item_alert(feed_handle.getCPtr(h), h, feed_item.getCPtr(item), item), true);
  }

  public int type() {
    return libtorrent_jni.rss_item_alert_type(swigCPtr, this);
  }

  public int category() {
    return libtorrent_jni.rss_item_alert_category(swigCPtr, this);
  }

  public String what() {
    return libtorrent_jni.rss_item_alert_what(swigCPtr, this);
  }

  public String message() {
    return libtorrent_jni.rss_item_alert_message(swigCPtr, this);
  }

  public void setHandle(feed_handle value) {
    libtorrent_jni.rss_item_alert_handle_set(swigCPtr, this, feed_handle.getCPtr(value), value);
  }

  public feed_handle getHandle() {
    long cPtr = libtorrent_jni.rss_item_alert_handle_get(swigCPtr, this);
    return (cPtr == 0) ? null : new feed_handle(cPtr, false);
  }

  public void setItem(feed_item value) {
    libtorrent_jni.rss_item_alert_item_set(swigCPtr, this, feed_item.getCPtr(value), value);
  }

  public feed_item getItem() {
    long cPtr = libtorrent_jni.rss_item_alert_item_get(swigCPtr, this);
    return (cPtr == 0) ? null : new feed_item(cPtr, false);
  }

  public final static int alert_type = libtorrent_jni.rss_item_alert_alert_type_get();
  public final static int static_category = libtorrent_jni.rss_item_alert_static_category_get();
}
