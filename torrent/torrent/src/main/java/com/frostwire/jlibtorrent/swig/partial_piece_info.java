/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.frostwire.jlibtorrent.swig;

public class partial_piece_info {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected partial_piece_info(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(partial_piece_info obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        libtorrent_jni.delete_partial_piece_info(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setPiece_index(int value) {
    libtorrent_jni.partial_piece_info_piece_index_set(swigCPtr, this, value);
  }

  public int getPiece_index() {
    return libtorrent_jni.partial_piece_info_piece_index_get(swigCPtr, this);
  }

  public void setBlocks_in_piece(int value) {
    libtorrent_jni.partial_piece_info_blocks_in_piece_set(swigCPtr, this, value);
  }

  public int getBlocks_in_piece() {
    return libtorrent_jni.partial_piece_info_blocks_in_piece_get(swigCPtr, this);
  }

  public void setFinished(int value) {
    libtorrent_jni.partial_piece_info_finished_set(swigCPtr, this, value);
  }

  public int getFinished() {
    return libtorrent_jni.partial_piece_info_finished_get(swigCPtr, this);
  }

  public void setWriting(int value) {
    libtorrent_jni.partial_piece_info_writing_set(swigCPtr, this, value);
  }

  public int getWriting() {
    return libtorrent_jni.partial_piece_info_writing_get(swigCPtr, this);
  }

  public void setRequested(int value) {
    libtorrent_jni.partial_piece_info_requested_set(swigCPtr, this, value);
  }

  public int getRequested() {
    return libtorrent_jni.partial_piece_info_requested_get(swigCPtr, this);
  }

  public void setBlocks(block_info value) {
    libtorrent_jni.partial_piece_info_blocks_set(swigCPtr, this, block_info.getCPtr(value), value);
  }

  public block_info getBlocks() {
    long cPtr = libtorrent_jni.partial_piece_info_blocks_get(swigCPtr, this);
    return (cPtr == 0) ? null : new block_info(cPtr, false);
  }

  public void setPiece_state(state_t value) {
    libtorrent_jni.partial_piece_info_piece_state_set(swigCPtr, this, value.swigValue());
  }

  public state_t getPiece_state() {
    return state_t.swigToEnum(libtorrent_jni.partial_piece_info_piece_state_get(swigCPtr, this));
  }

  public partial_piece_info() {
    this(libtorrent_jni.new_partial_piece_info(), true);
  }

  public enum state_t {
    none,
    slow,
    medium,
    fast;

    public final int swigValue() {
      return swigValue;
    }

    public static state_t swigToEnum(int swigValue) {
      state_t[] swigValues = state_t.class.getEnumConstants();
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (state_t swigEnum : swigValues)
        if (swigEnum.swigValue == swigValue)
          return swigEnum;
      throw new IllegalArgumentException("No enum " + state_t.class + " with value " + swigValue);
    }

    @SuppressWarnings("unused")
    private state_t() {
      this.swigValue = SwigNext.next++;
    }

    @SuppressWarnings("unused")
    private state_t(int swigValue) {
      this.swigValue = swigValue;
      SwigNext.next = swigValue+1;
    }

    @SuppressWarnings("unused")
    private state_t(state_t swigEnum) {
      this.swigValue = swigEnum.swigValue;
      SwigNext.next = this.swigValue+1;
    }

    private final int swigValue;

    private static class SwigNext {
      private static int next = 0;
    }
  }

}
