/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.frostwire.jlibtorrent.swig;

public enum special_values {
  not_a_date_time,
  neg_infin,
  pos_infin,
  min_date_time,
  max_date_time,
  not_special,
  NumSpecialValues;

  public final int swigValue() {
    return swigValue;
  }

  public static special_values swigToEnum(int swigValue) {
    special_values[] swigValues = special_values.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (special_values swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + special_values.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  private special_values() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  private special_values(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  private special_values(special_values swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

