package toxz.me.whizz.dateparser;

import java.util.Calendar;

/**
 * Created by Carlos on 11/23/16.
 */

@SuppressWarnings("WeakerAccess")
public class ParsedDate {
    public final Calendar date;
    public final int start;
    public final int end;

    public ParsedDate(Calendar date, int start, int end) {
        this.date = date;
        this.start = start;
        this.end = end;
    }
}