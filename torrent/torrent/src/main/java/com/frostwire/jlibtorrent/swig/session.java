/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.frostwire.jlibtorrent.swig;

public class session {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected session(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(session obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        libtorrent_jni.delete_session(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public session(fingerprint print, int flags, long alert_mask) {
    this(libtorrent_jni.new_session__SWIG_0(fingerprint.getCPtr(print), print, flags, alert_mask), true);
  }

  public session(fingerprint print, int flags) {
    this(libtorrent_jni.new_session__SWIG_1(fingerprint.getCPtr(print), print, flags), true);
  }

  public session(fingerprint print) {
    this(libtorrent_jni.new_session__SWIG_2(fingerprint.getCPtr(print), print), true);
  }

  public session() {
    this(libtorrent_jni.new_session__SWIG_3(), true);
  }

  public session(fingerprint print, int_int_pair listen_port_range, String listen_interface, int flags, int alert_mask) {
    this(libtorrent_jni.new_session__SWIG_4(fingerprint.getCPtr(print), print, int_int_pair.getCPtr(listen_port_range), listen_port_range, listen_interface, flags, alert_mask), true);
  }

  public session(fingerprint print, int_int_pair listen_port_range, String listen_interface, int flags) {
    this(libtorrent_jni.new_session__SWIG_5(fingerprint.getCPtr(print), print, int_int_pair.getCPtr(listen_port_range), listen_port_range, listen_interface, flags), true);
  }

  public session(fingerprint print, int_int_pair listen_port_range, String listen_interface) {
    this(libtorrent_jni.new_session__SWIG_6(fingerprint.getCPtr(print), print, int_int_pair.getCPtr(listen_port_range), listen_port_range, listen_interface), true);
  }

  public session(fingerprint print, int_int_pair listen_port_range) {
    this(libtorrent_jni.new_session__SWIG_7(fingerprint.getCPtr(print), print, int_int_pair.getCPtr(listen_port_range), listen_port_range), true);
  }

  public void save_state(entry e, long flags) {
    libtorrent_jni.session_save_state__SWIG_0(swigCPtr, this, entry.getCPtr(e), e, flags);
  }

  public void save_state(entry e) {
    libtorrent_jni.session_save_state__SWIG_1(swigCPtr, this, entry.getCPtr(e), e);
  }

  public void load_state(lazy_entry e) {
    libtorrent_jni.session_load_state(swigCPtr, this, lazy_entry.getCPtr(e), e);
  }

  public void refresh_torrent_status(torrent_status_vector ret, long flags) {
    libtorrent_jni.session_refresh_torrent_status__SWIG_0(swigCPtr, this, torrent_status_vector.getCPtr(ret), ret, flags);
  }

  public void refresh_torrent_status(torrent_status_vector ret) {
    libtorrent_jni.session_refresh_torrent_status__SWIG_1(swigCPtr, this, torrent_status_vector.getCPtr(ret), ret);
  }

  public void post_torrent_updates() {
    libtorrent_jni.session_post_torrent_updates(swigCPtr, this);
  }

  public torrent_handle find_torrent(sha1_hash info_hash) {
    return new torrent_handle(libtorrent_jni.session_find_torrent(swigCPtr, this, sha1_hash.getCPtr(info_hash), info_hash), true);
  }

  public torrent_handle_vector get_torrents() {
    return new torrent_handle_vector(libtorrent_jni.session_get_torrents(swigCPtr, this), true);
  }

  public torrent_handle add_torrent(add_torrent_params params) {
    return new torrent_handle(libtorrent_jni.session_add_torrent__SWIG_0(swigCPtr, this, add_torrent_params.getCPtr(params), params), true);
  }

  public torrent_handle add_torrent(add_torrent_params params, error_code ec) {
    return new torrent_handle(libtorrent_jni.session_add_torrent__SWIG_1(swigCPtr, this, add_torrent_params.getCPtr(params), params, error_code.getCPtr(ec), ec), true);
  }

  public void async_add_torrent(add_torrent_params params) {
    libtorrent_jni.session_async_add_torrent(swigCPtr, this, add_torrent_params.getCPtr(params), params);
  }

  public session_proxy abort() {
    return new session_proxy(libtorrent_jni.session_abort(swigCPtr, this), true);
  }

  public void pause() {
    libtorrent_jni.session_pause(swigCPtr, this);
  }

  public void resume() {
    libtorrent_jni.session_resume(swigCPtr, this);
  }

  public boolean is_paused() {
    return libtorrent_jni.session_is_paused(swigCPtr, this);
  }

  public session_status status() {
    return new session_status(libtorrent_jni.session_status(swigCPtr, this), true);
  }

  public cache_status get_cache_status() {
    return new cache_status(libtorrent_jni.session_get_cache_status(swigCPtr, this), true);
  }

  public void get_cache_info(sha1_hash ih, cached_piece_info_vector ret) {
    libtorrent_jni.session_get_cache_info(swigCPtr, this, sha1_hash.getCPtr(ih), ih, cached_piece_info_vector.getCPtr(ret), ret);
  }

  public feed_handle add_feed(feed_settings feed) {
    return new feed_handle(libtorrent_jni.session_add_feed(swigCPtr, this, feed_settings.getCPtr(feed), feed), true);
  }

  public void remove_feed(feed_handle h) {
    libtorrent_jni.session_remove_feed(swigCPtr, this, feed_handle.getCPtr(h), h);
  }

  public void get_feeds(feed_handle_vector f) {
    libtorrent_jni.session_get_feeds(swigCPtr, this, feed_handle_vector.getCPtr(f), f);
  }

  public void start_dht() {
    libtorrent_jni.session_start_dht(swigCPtr, this);
  }

  public void stop_dht() {
    libtorrent_jni.session_stop_dht(swigCPtr, this);
  }

  public void set_dht_settings(dht_settings settings) {
    libtorrent_jni.session_set_dht_settings(swigCPtr, this, dht_settings.getCPtr(settings), settings);
  }

  public boolean is_dht_running() {
    return libtorrent_jni.session_is_dht_running(swigCPtr, this);
  }

  public void add_dht_node(string_int_pair node) {
    libtorrent_jni.session_add_dht_node(swigCPtr, this, string_int_pair.getCPtr(node), node);
  }

  public void add_dht_router(string_int_pair node) {
    libtorrent_jni.session_add_dht_router(swigCPtr, this, string_int_pair.getCPtr(node), node);
  }

  public void dht_get_item(sha1_hash target) {
    libtorrent_jni.session_dht_get_item__SWIG_0(swigCPtr, this, sha1_hash.getCPtr(target), target);
  }

  public sha1_hash dht_put_item(entry data) {
    return new sha1_hash(libtorrent_jni.session_dht_put_item__SWIG_0(swigCPtr, this, entry.getCPtr(data), data), true);
  }

  public void load_asnum_db(String file) {
    libtorrent_jni.session_load_asnum_db(swigCPtr, this, file);
  }

  public void load_country_db(String file) {
    libtorrent_jni.session_load_country_db(swigCPtr, this, file);
  }

  public int as_for_ip(address addr) {
    return libtorrent_jni.session_as_for_ip(swigCPtr, this, address.getCPtr(addr), addr);
  }

  public void set_ip_filter(ip_filter f) {
    libtorrent_jni.session_set_ip_filter(swigCPtr, this, ip_filter.getCPtr(f), f);
  }

  public ip_filter get_ip_filter() {
    return new ip_filter(libtorrent_jni.session_get_ip_filter(swigCPtr, this), true);
  }

  public void set_port_filter(port_filter f) {
    libtorrent_jni.session_set_port_filter(swigCPtr, this, port_filter.getCPtr(f), f);
  }

  public void set_peer_id(sha1_hash pid) {
    libtorrent_jni.session_set_peer_id(swigCPtr, this, sha1_hash.getCPtr(pid), pid);
  }

  public sha1_hash id() {
    return new sha1_hash(libtorrent_jni.session_id(swigCPtr, this), true);
  }

  public void set_key(int key) {
    libtorrent_jni.session_set_key(swigCPtr, this, key);
  }

  public void listen_on(int_int_pair port_range, error_code ec, String net_interface, int flags) {
    libtorrent_jni.session_listen_on__SWIG_0(swigCPtr, this, int_int_pair.getCPtr(port_range), port_range, error_code.getCPtr(ec), ec, net_interface, flags);
  }

  public void listen_on(int_int_pair port_range, error_code ec, String net_interface) {
    libtorrent_jni.session_listen_on__SWIG_1(swigCPtr, this, int_int_pair.getCPtr(port_range), port_range, error_code.getCPtr(ec), ec, net_interface);
  }

  public void listen_on(int_int_pair port_range, error_code ec) {
    libtorrent_jni.session_listen_on__SWIG_2(swigCPtr, this, int_int_pair.getCPtr(port_range), port_range, error_code.getCPtr(ec), ec);
  }

  public int listen_port() {
    return libtorrent_jni.session_listen_port(swigCPtr, this);
  }

  public int ssl_listen_port() {
    return libtorrent_jni.session_ssl_listen_port(swigCPtr, this);
  }

  public boolean is_listening() {
    return libtorrent_jni.session_is_listening(swigCPtr, this);
  }

  public void remove_torrent(torrent_handle h, int options) {
    libtorrent_jni.session_remove_torrent__SWIG_0(swigCPtr, this, torrent_handle.getCPtr(h), h, options);
  }

  public void remove_torrent(torrent_handle h) {
    libtorrent_jni.session_remove_torrent__SWIG_1(swigCPtr, this, torrent_handle.getCPtr(h), h);
  }

  public void set_settings(session_settings s) {
    libtorrent_jni.session_set_settings(swigCPtr, this, session_settings.getCPtr(s), s);
  }

  public session_settings settings() {
    return new session_settings(libtorrent_jni.session_settings(swigCPtr, this), true);
  }

  public void set_pe_settings(pe_settings settings) {
    libtorrent_jni.session_set_pe_settings(swigCPtr, this, pe_settings.getCPtr(settings), settings);
  }

  public pe_settings get_pe_settings() {
    return new pe_settings(libtorrent_jni.session_get_pe_settings(swigCPtr, this), true);
  }

  public void set_proxy(proxy_settings s) {
    libtorrent_jni.session_set_proxy(swigCPtr, this, proxy_settings.getCPtr(s), s);
  }

  public proxy_settings proxy() {
    return new proxy_settings(libtorrent_jni.session_proxy(swigCPtr, this), true);
  }

  public void set_i2p_proxy(proxy_settings s) {
    libtorrent_jni.session_set_i2p_proxy(swigCPtr, this, proxy_settings.getCPtr(s), s);
  }

  public proxy_settings i2p_proxy() {
    return new proxy_settings(libtorrent_jni.session_i2p_proxy(swigCPtr, this), true);
  }

  public alert pop_alert() {
     long cPtr = libtorrent_jni.session_pop_alert(swigCPtr, this);
     return (cPtr == 0) ? null : new alert(cPtr, true);
   }

  public void pop_alerts(alert_ptr_deque alerts) {
    libtorrent_jni.session_pop_alerts(swigCPtr, this, alert_ptr_deque.getCPtr(alerts), alerts);
  }

  public alert wait_for_alert(time_duration max_wait) {
    long cPtr = libtorrent_jni.session_wait_for_alert(swigCPtr, this, time_duration.getCPtr(max_wait), max_wait);
    return (cPtr == 0) ? null : new alert(cPtr, false);
  }

  public void set_alert_mask(long m) {
    libtorrent_jni.session_set_alert_mask(swigCPtr, this, m);
  }

  public void start_lsd() {
    libtorrent_jni.session_start_lsd(swigCPtr, this);
  }

  public void stop_lsd() {
    libtorrent_jni.session_stop_lsd(swigCPtr, this);
  }

  public void start_upnp() {
    libtorrent_jni.session_start_upnp(swigCPtr, this);
  }

  public void stop_upnp() {
    libtorrent_jni.session_stop_upnp(swigCPtr, this);
  }

  public int add_port_mapping(protocol_type t, int external_port, int local_port) {
    return libtorrent_jni.session_add_port_mapping(swigCPtr, this, t.swigValue(), external_port, local_port);
  }

  public void delete_port_mapping(int handle) {
    libtorrent_jni.session_delete_port_mapping(swigCPtr, this, handle);
  }

  public void start_natpmp() {
    libtorrent_jni.session_start_natpmp(swigCPtr, this);
  }

  public void stop_natpmp() {
    libtorrent_jni.session_stop_natpmp(swigCPtr, this);
  }

  public void add_lt_trackers_extension() {
    libtorrent_jni.session_add_lt_trackers_extension(swigCPtr, this);
  }

  public void add_smart_ban_extension() {
    libtorrent_jni.session_add_smart_ban_extension(swigCPtr, this);
  }

  public void dht_get_item(char_vector key_v, String salt) {
    libtorrent_jni.session_dht_get_item__SWIG_1(swigCPtr, this, char_vector.getCPtr(key_v), key_v, salt);
  }

  public void dht_get_item(char_vector key_v) {
    libtorrent_jni.session_dht_get_item__SWIG_2(swigCPtr, this, char_vector.getCPtr(key_v), key_v);
  }

  public void dht_put_item(char_vector public_key, char_vector private_key, entry data, String salt) {
    libtorrent_jni.session_dht_put_item__SWIG_1(swigCPtr, this, char_vector.getCPtr(public_key), public_key, char_vector.getCPtr(private_key), private_key, entry.getCPtr(data), data, salt);
  }

  public void dht_put_item(char_vector public_key, char_vector private_key, entry data) {
    libtorrent_jni.session_dht_put_item__SWIG_2(swigCPtr, this, char_vector.getCPtr(public_key), public_key, char_vector.getCPtr(private_key), private_key, entry.getCPtr(data), data);
  }

  public void dht_get_peers(sha1_hash info_hash) {
    libtorrent_jni.session_dht_get_peers(swigCPtr, this, sha1_hash.getCPtr(info_hash), info_hash);
  }

  public void dht_announce(sha1_hash info_hash, int port, int flags) {
    libtorrent_jni.session_dht_announce__SWIG_0(swigCPtr, this, sha1_hash.getCPtr(info_hash), info_hash, port, flags);
  }

  public void dht_announce(sha1_hash info_hash) {
    libtorrent_jni.session_dht_announce__SWIG_1(swigCPtr, this, sha1_hash.getCPtr(info_hash), info_hash);
  }

  public enum save_state_flags_t {
    save_settings(libtorrent_jni.session_save_settings_get()),
    save_dht_settings(libtorrent_jni.session_save_dht_settings_get()),
    save_dht_state(libtorrent_jni.session_save_dht_state_get()),
    save_proxy(libtorrent_jni.session_save_proxy_get()),
    save_i2p_proxy(libtorrent_jni.session_save_i2p_proxy_get()),
    save_encryption_settings(libtorrent_jni.session_save_encryption_settings_get()),
    save_as_map(libtorrent_jni.session_save_as_map_get()),
    save_feeds(libtorrent_jni.session_save_feeds_get());

    public final int swigValue() {
      return swigValue;
    }

    public static save_state_flags_t swigToEnum(int swigValue) {
      save_state_flags_t[] swigValues = save_state_flags_t.class.getEnumConstants();
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (save_state_flags_t swigEnum : swigValues)
        if (swigEnum.swigValue == swigValue)
          return swigEnum;
      throw new IllegalArgumentException("No enum " + save_state_flags_t.class + " with value " + swigValue);
    }

    @SuppressWarnings("unused")
    private save_state_flags_t() {
      this.swigValue = SwigNext.next++;
    }

    @SuppressWarnings("unused")
    private save_state_flags_t(int swigValue) {
      this.swigValue = swigValue;
      SwigNext.next = swigValue+1;
    }

    @SuppressWarnings("unused")
    private save_state_flags_t(save_state_flags_t swigEnum) {
      this.swigValue = swigEnum.swigValue;
      SwigNext.next = this.swigValue+1;
    }

    private final int swigValue;

    private static class SwigNext {
      private static int next = 0;
    }
  }

  public enum listen_on_flags_t {
    listen_no_system_port(libtorrent_jni.session_listen_no_system_port_get());

    public final int swigValue() {
      return swigValue;
    }

    public static listen_on_flags_t swigToEnum(int swigValue) {
      listen_on_flags_t[] swigValues = listen_on_flags_t.class.getEnumConstants();
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (listen_on_flags_t swigEnum : swigValues)
        if (swigEnum.swigValue == swigValue)
          return swigEnum;
      throw new IllegalArgumentException("No enum " + listen_on_flags_t.class + " with value " + swigValue);
    }

    @SuppressWarnings("unused")
    private listen_on_flags_t() {
      this.swigValue = SwigNext.next++;
    }

    @SuppressWarnings("unused")
    private listen_on_flags_t(int swigValue) {
      this.swigValue = swigValue;
      SwigNext.next = swigValue+1;
    }

    @SuppressWarnings("unused")
    private listen_on_flags_t(listen_on_flags_t swigEnum) {
      this.swigValue = swigEnum.swigValue;
      SwigNext.next = this.swigValue+1;
    }

    private final int swigValue;

    private static class SwigNext {
      private static int next = 0;
    }
  }

  public enum options_t {
    delete_files(libtorrent_jni.session_delete_files_get());

    public final int swigValue() {
      return swigValue;
    }

    public static options_t swigToEnum(int swigValue) {
      options_t[] swigValues = options_t.class.getEnumConstants();
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (options_t swigEnum : swigValues)
        if (swigEnum.swigValue == swigValue)
          return swigEnum;
      throw new IllegalArgumentException("No enum " + options_t.class + " with value " + swigValue);
    }

    @SuppressWarnings("unused")
    private options_t() {
      this.swigValue = SwigNext.next++;
    }

    @SuppressWarnings("unused")
    private options_t(int swigValue) {
      this.swigValue = swigValue;
      SwigNext.next = swigValue+1;
    }

    @SuppressWarnings("unused")
    private options_t(options_t swigEnum) {
      this.swigValue = swigEnum.swigValue;
      SwigNext.next = this.swigValue+1;
    }

    private final int swigValue;

    private static class SwigNext {
      private static int next = 0;
    }
  }

  public enum session_flags_t {
    add_default_plugins(libtorrent_jni.session_add_default_plugins_get()),
    start_default_features(libtorrent_jni.session_start_default_features_get());

    public final int swigValue() {
      return swigValue;
    }

    public static session_flags_t swigToEnum(int swigValue) {
      session_flags_t[] swigValues = session_flags_t.class.getEnumConstants();
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (session_flags_t swigEnum : swigValues)
        if (swigEnum.swigValue == swigValue)
          return swigEnum;
      throw new IllegalArgumentException("No enum " + session_flags_t.class + " with value " + swigValue);
    }

    @SuppressWarnings("unused")
    private session_flags_t() {
      this.swigValue = SwigNext.next++;
    }

    @SuppressWarnings("unused")
    private session_flags_t(int swigValue) {
      this.swigValue = swigValue;
      SwigNext.next = swigValue+1;
    }

    @SuppressWarnings("unused")
    private session_flags_t(session_flags_t swigEnum) {
      this.swigValue = swigEnum.swigValue;
      SwigNext.next = this.swigValue+1;
    }

    private final int swigValue;

    private static class SwigNext {
      private static int next = 0;
    }
  }

  public enum protocol_type {
    udp(libtorrent_jni.session_udp_get()),
    tcp(libtorrent_jni.session_tcp_get());

    public final int swigValue() {
      return swigValue;
    }

    public static protocol_type swigToEnum(int swigValue) {
      protocol_type[] swigValues = protocol_type.class.getEnumConstants();
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (protocol_type swigEnum : swigValues)
        if (swigEnum.swigValue == swigValue)
          return swigEnum;
      throw new IllegalArgumentException("No enum " + protocol_type.class + " with value " + swigValue);
    }

    @SuppressWarnings("unused")
    private protocol_type() {
      this.swigValue = SwigNext.next++;
    }

    @SuppressWarnings("unused")
    private protocol_type(int swigValue) {
      this.swigValue = swigValue;
      SwigNext.next = swigValue+1;
    }

    @SuppressWarnings("unused")
    private protocol_type(protocol_type swigEnum) {
      this.swigValue = swigEnum.swigValue;
      SwigNext.next = this.swigValue+1;
    }

    private final int swigValue;

    private static class SwigNext {
      private static int next = 0;
    }
  }

}
