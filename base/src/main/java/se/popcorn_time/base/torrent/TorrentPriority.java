package se.popcorn_time.base.torrent;

public class TorrentPriority {

//    0 - piece is not downloaded at all
//    1 - normal priority. Download order is dependent on availability
//    2 - higher than normal priority. Pieces are preferred over pieces with the same availability, but not over pieces with lower availability
//    3 - pieces are as likely to be picked as partial pieces.
//    4 - pieces are preferred over partial pieces, but not over pieces with lower availability
//    5 - currently the same as 4
//    6 - piece is as likely to be picked as any piece with availability 1
//    7 - maximum priority, availability is disregarded, the piece is preferred over any other piece with lower priority

    public static final int NOT_DOWNLOADED = 0;
    public static final int NORMAL = 1;
    public static final int HIGHER_THAN_NORMAL = 2;
    public static final int PICKED_AS_PARTIAL_PIECES = 3;
    public static final int PREFERRED_OVER_PARTIAL_PIECES = 4;
    public static final int SAME_PREFERRED_OVER_PARTIAL_PIECES = 5;
    public static final int LIKELY_NORMAL = 6;
    public static final int MAXIMUM = 7;
}