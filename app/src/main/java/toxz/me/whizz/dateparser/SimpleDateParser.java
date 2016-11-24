package toxz.me.whizz.dateparser;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Carlos on 11/19/16.
 */
class SimpleDateParser {
    private static List<Character> days = Arrays.asList('天', '一', '二', '三', '四', '五', '六', '日');

    @SuppressWarnings("WeakerAccess")
    public static List<ParsedDate> parseDate(String text) {
        List<ParsedDate> result = new ArrayList<>();

        if (TextUtils.isEmpty(text) || text.trim().length() == 0) {
            return null;
        }

        result.addAll(parseHoliday(text));
        result.addAll(parseDayPhase(text));
        result.addAll(parseWeekDate(text));
        result.addAll(parseMonthDate(text));


        return result;
    }


    private static List<ParsedDate> parseHoliday(@NonNull final String text) {
        List<ParsedDate> result = new ArrayList<>();
        //TODO add holiday support
        return result;
    }

    private static final List<String> DAY_PHRASE = Arrays.asList("前天", "昨天", "今天", "明天", "后天");

    private static List<ParsedDate> parseDayPhase(@NonNull final String text) {
        List<ParsedDate> result = new ArrayList<>();

        for (int i = 0; i < DAY_PHRASE.size(); i++) {
            int index;
            while ((index = text.indexOf(DAY_PHRASE.get(i))) != -1) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, i - 2);
                result.add(new ParsedDate(calendar, index, index + 2));
            }
        }
        return result;
    }


    private static List<ParsedDate> parseWeekDate(@NonNull final String text) {
        List<ParsedDate> result = new ArrayList<>();

        final String[] keys = {"周", "星期"};

        for (String key : keys) {
            int index = -1;
            while ((index = text.indexOf(key, index)) >= 0) {
                int whatDay = getWhatDay(text, index);
                if (whatDay >= 0) {
                    int offset = getOffset(text, index);

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.WEEK_OF_YEAR, offset);
                    int value = (whatDay + 1) % 7;// 周一（1） -> value 2
                    calendar.set(Calendar.DAY_OF_WEEK, value);

                    result.add(new ParsedDate(calendar, index, index + key.length() + 1));
                }
            }
        }

        return result;
    }

    private static int getWhatDay(final String text, final int start) {
        int suffixIndex = start + 1;

        if (suffixIndex >= text.length()) {
            return -1;
        }

//        if (suffixIndex)

        return days.indexOf(text.charAt(suffixIndex));
    }


    private static List<ParsedDate> parseMonthDate(@NonNull final String text) {
        List<ParsedDate> result = new ArrayList<>();

        int index = -1;

        final String[] keys = {"日", "号", "天"};

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            while ((index = text.indexOf(key, index)) >= 0) {
                // -1 前， 1 后， 0 无意义
                int suffixOffset = getSuffixOffset(text, index);

                // x日后, x天后
                if (i != 1 && suffixOffset != 0) {
                    Pair<Integer, Integer> pair = parseNum(text, index, true);
                    if (pair.first > 0) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_YEAR, suffixOffset * pair.first);

                        result.add(new ParsedDate(calendar, index - pair.second, index));
                    }
                }

                // 23日, 12号
                if (i != 2 && suffixOffset == 0) {
                    Pair<Integer, Integer> pa = getDayNumInMonth(text, index);

                    if (pa.first > 0) {
                        int offset = getOffset(text, index);

                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.MONTH, offset);
                        calendar.set(Calendar.DAY_OF_MONTH, pa.first);

                        result.add(new ParsedDate(calendar, pa.second, index));
                    }
                }

            }
        }

        return result;
    }

    /**
     * @param text
     * @param center
     * @param reserve
     * @return (number, numberTextLength)
     */
    private static Pair<Integer, Integer> parseNum(@NonNull final String text, final int center, boolean reserve) {
        int index = reserve ? center - 1 : center + 1;
        while (text.length() > index && index > 0
                && text.charAt(index) == ' '
                ) {
            if (reserve) index--;
            else index++;
        }

        StringBuilder sb = new StringBuilder();
        while (text.length() > index && index > 0
                && text.charAt(index) > '0'
                && text.charAt(index) < '9'
                ) {
            if (reserve) index--;
            else index++;
            sb.append(text.charAt(index));
        }

        String str = sb.toString();
        if (str.length() > 0) {
            try {
                int day = Integer.parseInt(str);
                return Pair.create(day, center - index);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return Pair.create(-1, -1);
    }

    private static int getSuffixOffset(@NonNull final String text, final int center) {
        int suffixIndex = center + 1;
        if (suffixIndex >= text.length()) {
            return 0;
        }

        if (match("后", text, center, false)
                || match("之后", text, center, false)
                || match("以后", text, center, false)
                ) {
            return 1;
        }

        if (match("前", text, center, false)
                || match("之前", text, center, false)
                || match("以前", text, center, false)
                ) {
            return 1;
        }

        return 0;
    }

    private static final List<String> DAY_NUM = Arrays.asList(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
    );
    private static final List<String> DAY_UP = Arrays.asList(
            "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
            "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "廿",
            "二十一", "二十二", "二十三", "二十四", "二十五", "二十六", "二十七", "二十八", "二十九",
            "三十", "三十一"
    );
    private static final List<String> DAY_UP_2 = Arrays.asList(
            "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十", "三一"
    );

    private static final List<List<String>> dayss = Arrays.asList(DAY_NUM, DAY_UP, DAY_UP_2);


    /**
     * @param text
     * @param start
     * @return (1, startIndex) if 一号
     */
    @NonNull
    private static Pair<Integer, Integer> getDayNumInMonth(final String text, final int start) {
        int index = start;
        while (index > 0) {
            char c = text.charAt(index - 1);
            index--;

            if (c == ' ')
                continue;

            int i, j = 0;
            out:
            for (i = 0; i < dayss.size(); i++) {
                List<String> list = dayss.get(i);

                for (j = 0; j < list.size(); j++) {
                    String s = list.get(j);
                    if (match(s, text, index, true)) {
                        break out;
                    }
                }
            }

            int startIndex = start - dayss.get(i).get(j).length();
            if (i == 2) {
                return Pair.create(j + 21, startIndex);
            } else
                return Pair.create(j + 1, startIndex);
        }
        return Pair.create(-1, -1);
    }


    private static int getOffset(final String text, final int start) {
        if (match("下下", text, start, true)
                || match("下下个", text, start, true)
                ) {
            return 2;
        }
        if (match("上上", text, start, true)
                || match("上上个", text, start, true)
                ) {
            return -2;
        }
        if (match("下", text, start, true)
                || match("下个", text, start, true)
                || match("下一个", text, start, true)
                ) {
            return 1;
        }

        if (match("上", text, start, true)
                || match("上个", text, start, true)
                || match("上一个", text, start, true)
                ) {
            return -1;
        }

        return 0;
    }


    private static boolean match(@NonNull String pattern, String text, int start, boolean reserve) {
        if (!reserve && start + pattern.length() >= text.length()) {
            return false;
        }

        if (reserve && start - pattern.length() < 0) {
            return false;
        }

        if (reserve) {
            start = start - pattern.length();
        }

        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) != text.charAt(start + i)) {
                return false;
            }
        }

        return true;
    }
}
