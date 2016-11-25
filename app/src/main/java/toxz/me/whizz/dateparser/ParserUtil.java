package toxz.me.whizz.dateparser;

import java.util.List;

/**
 * Created by Carlos on 11/19/16.
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class ParserUtil {
    public static ParsedDate parseDeadline(String text) {
        try {
            List<ParsedDate> list = parse(text);
            if (list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<ParsedDate> parse(String text) {
        return SimpleDateParser.parseDate(text);
    }
}
