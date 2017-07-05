package co.moosic.music;

import java.util.regex.Pattern;

/**
 * Thanks sedmelluq
 * https://github.com/sedmelluq
 */
class Patterns {
    private static final String VIDEO_ID_REGEX = "([a-zA-Z0-9_-]{11})";
    private static final String PLAYLIST_REGEX = "((PL|LL|FL|UU)[a-zA-Z0-9_-]+)";
    private static final String MIX_REGEX = "(RD`[a-zA-Z0-9_-]+)";
    private static final String PROTOCOL_REGEX = "(?:http://|https://|)";
    private static final String SUFFIX_REGEX = "(?:\\?.*|&.*|)";

    static final Pattern[] validTrackPatterns = new Pattern[]{
            Pattern.compile("^" + VIDEO_ID_REGEX + "$"),
            Pattern.compile("^" + PROTOCOL_REGEX + "(?:www\\.|)youtube.com/watch\\?v=" + VIDEO_ID_REGEX + SUFFIX_REGEX + "$"),
            Pattern.compile("^" + PROTOCOL_REGEX + "(?:www\\.|)youtu.be/" + VIDEO_ID_REGEX + SUFFIX_REGEX + "$")
    };

    private static final String LIST_PARAMETER = "&list=";
    static final Pattern playlistEmbeddedPattern = Pattern.compile(LIST_PARAMETER + PLAYLIST_REGEX);
    static final Pattern mixEmbeddedPattern = Pattern.compile(LIST_PARAMETER + MIX_REGEX);
}
