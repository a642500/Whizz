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

    @Override public boolean equals(final Object obj) {

        if (obj != null && obj instanceof ParsedDate) {
            Calendar calendar = ((ParsedDate) obj).date;
            if (calendar != null && this.date != null) {
                return calendar.get(Calendar.YEAR) == date.get(Calendar.YEAR)
                        && calendar.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getName() + '[' +
                "calendar=" +
                (date == null ? null : date.toString()) +
                ",play=" +
                start +
                ",end=" +
                end +
                ']';
    }
}