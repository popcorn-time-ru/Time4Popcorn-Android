/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.frostwire.jlibtorrent.swig;

public class bitfield {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected bitfield(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(bitfield obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        libtorrent_jni.delete_bitfield(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public bitfield() {
    this(libtorrent_jni.new_bitfield__SWIG_0(), true);
  }

  public bitfield(int bits) {
    this(libtorrent_jni.new_bitfield__SWIG_1(bits), true);
  }

  public bitfield(int bits, boolean val) {
    this(libtorrent_jni.new_bitfield__SWIG_2(bits, val), true);
  }

  public bitfield(String b, int bits) {
    this(libtorrent_jni.new_bitfield__SWIG_3(b, bits), true);
  }

  public bitfield(bitfield rhs) {
    this(libtorrent_jni.new_bitfield__SWIG_4(bitfield.getCPtr(rhs), rhs), true);
  }

  public void borrow_bytes(String b, int bits) {
    libtorrent_jni.bitfield_borrow_bytes(swigCPtr, this, b, bits);
  }

  public void assign(String b, int bits) {
    libtorrent_jni.bitfield_assign(swigCPtr, this, b, bits);
  }

  public boolean op_get_at(int index) {
    return libtorrent_jni.bitfield_op_get_at(swigCPtr, this, index);
  }

  public boolean get_bit(int index) {
    return libtorrent_jni.bitfield_get_bit(swigCPtr, this, index);
  }

  public void clear_bit(int index) {
    libtorrent_jni.bitfield_clear_bit(swigCPtr, this, index);
  }

  public void set_bit(int index) {
    libtorrent_jni.bitfield_set_bit(swigCPtr, this, index);
  }

  public boolean all_set() {
    return libtorrent_jni.bitfield_all_set(swigCPtr, this);
  }

  public long size() {
    return libtorrent_jni.bitfield_size(swigCPtr, this);
  }

  public boolean empty() {
    return libtorrent_jni.bitfield_empty(swigCPtr, this);
  }

  public String bytes() {
    return libtorrent_jni.bitfield_bytes(swigCPtr, this);
  }

  public int count() {
    return libtorrent_jni.bitfield_count(swigCPtr, this);
  }

  static public class const_iterator {
    private long swigCPtr;
    protected boolean swigCMemOwn;
  
    protected const_iterator(long cPtr, boolean cMemoryOwn) {
      swigCMemOwn = cMemoryOwn;
      swigCPtr = cPtr;
    }
  
    protected static long getCPtr(const_iterator obj) {
      return (obj == null) ? 0 : obj.swigCPtr;
    }
  
    protected void finalize() {
      delete();
    }
  
    public synchronized void delete() {
      if (swigCPtr != 0) {
        if (swigCMemOwn) {
          swigCMemOwn = false;
          libtorrent_jni.delete_bitfield_const_iterator(swigCPtr);
        }
        swigCPtr = 0;
      }
    }
  
    public boolean __ref__() {
      return libtorrent_jni.bitfield_const_iterator___ref__(swigCPtr, this);
    }
  
    public const_iterator() {
      this(libtorrent_jni.new_bitfield_const_iterator(), true);
    }
  
    public boolean op_eq(const_iterator rhs) {
      return libtorrent_jni.bitfield_const_iterator_op_eq(swigCPtr, this, const_iterator.getCPtr(rhs), rhs);
    }
  
    public boolean op_neq(const_iterator rhs) {
      return libtorrent_jni.bitfield_const_iterator_op_neq(swigCPtr, this, const_iterator.getCPtr(rhs), rhs);
    }
  
  }

  public const_iterator begin() {
    return new const_iterator(libtorrent_jni.bitfield_begin(swigCPtr, this), true);
  }

  public const_iterator end() {
    return new const_iterator(libtorrent_jni.bitfield_end(swigCPtr, this), true);
  }

  public void resize(int bits, boolean val) {
    libtorrent_jni.bitfield_resize__SWIG_0(swigCPtr, this, bits, val);
  }

  public void resize(int bits) {
    libtorrent_jni.bitfield_resize__SWIG_1(swigCPtr, this, bits);
  }

  public void set_all() {
    libtorrent_jni.bitfield_set_all(swigCPtr, this);
  }

  public void clear_all() {
    libtorrent_jni.bitfield_clear_all(swigCPtr, this);
  }

  public void clear() {
    libtorrent_jni.bitfield_clear(swigCPtr, this);
  }

}
