package toxz.me.whizz.dateparser;

import android.support.v4.util.Pair;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(JUnit4.class)
public class ParserTest {

    @Test
    public void testMatch() {

    }

    @Test
    public void testParseDayPhase() {
        Calendar cl = Calendar.getInstance();
        cl.add(Calendar.DAY_OF_YEAR, -2);

        String[] phrases = {
                "前天下午去吃饭",
                "昨天下午去吃饭",
                "今天下午去吃饭",
                "明天下午去吃饭",
                "后天下午去吃饭"};

        for (String aPhrase : phrases) {
            List<ParsedDate> list = SimpleDateParser.parseDayPhase(aPhrase);
            assertEquals(1, list.size());
            assertEquals(0, list.get(0).start);
            assertEquals(2, list.get(0).end);
            testSameDay(cl, list.get(0).date);

            cl.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void testSameDay(Calendar excpeted, Calendar actual) {
        assertEquals(excpeted.get(Calendar.YEAR), actual.get(Calendar.YEAR));
        assertEquals(excpeted.get(Calendar.DAY_OF_YEAR), actual.get(Calendar.DAY_OF_YEAR));
    }

    @Test
    public void testParseWeekDate() {
        Calendar cl = Calendar.getInstance();

        ParsedDate date = SimpleDateParser.parseWeekDate("下周一去吃饭").get(0);
        cl.add(Calendar.WEEK_OF_YEAR, 1);
        cl.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        testSameDay(cl, date.date);

        date = SimpleDateParser.parseWeekDate("下星期一去吃饭").get(0);
        testSameDay(cl, date.date);

        date = SimpleDateParser.parseWeekDate("下个星期一去吃饭").get(0);
        testSameDay(cl, date.date);

        date = SimpleDateParser.parseWeekDate("下一个星期一去吃饭").get(0);
        testSameDay(cl, date.date);

        cl.add(Calendar.WEEK_OF_YEAR, 1);
        date = SimpleDateParser.parseWeekDate("下下星期一去吃饭").get(0);
        testSameDay(cl, date.date);

        assertTrue(SimpleDateParser.parseWeekDate("到底什么时候去吃饭").isEmpty());

        cl.add(Calendar.WEEK_OF_YEAR, -2);
        date = SimpleDateParser.parseWeekDate("星期一刚去吃过饭").get(0);
        testSameDay(cl, date.date);

        cl.add(Calendar.WEEK_OF_YEAR, -1);
        date = SimpleDateParser.parseWeekDate("上星期一刚去吃过饭").get(0);
        testSameDay(cl, date.date);
    }

    @Test
    public void testParseMonthDate() {

        ParsedDate date = SimpleDateParser.parseMonthDate("1天后").get(0);
        Calendar cl = Calendar.getInstance();
        cl.add(Calendar.DAY_OF_YEAR, 1);
        testSameDay(cl, date.date);

        date = SimpleDateParser.parseMonthDate("2天后").get(0);
        cl = Calendar.getInstance();
        cl.add(Calendar.DAY_OF_YEAR, 2);
        testSameDay(cl, date.date);


        date = SimpleDateParser.parseMonthDate("100天后").get(0);
        cl = Calendar.getInstance();
        cl.add(Calendar.DAY_OF_YEAR, 100);
        testSameDay(cl, date.date);

        date = SimpleDateParser.parseMonthDate("100天前").get(0);
        cl = Calendar.getInstance();
        cl.add(Calendar.DAY_OF_YEAR, -100);
        testSameDay(cl, date.date);

        date = SimpleDateParser.parseMonthDate("1000天前").get(0);
        cl = Calendar.getInstance();
        cl.add(Calendar.DAY_OF_YEAR, -1000);
        testSameDay(cl, date.date);


        date = SimpleDateParser.parseMonthDate("13号").get(0);
        cl = Calendar.getInstance();
        cl.set(Calendar.DAY_OF_MONTH, 13);
        testSameDay(cl, date.date);

        date = SimpleDateParser.parseMonthDate("13日").get(0);
        cl = Calendar.getInstance();
        cl.set(Calendar.DAY_OF_MONTH, 13);
        testSameDay(cl, date.date);

        assertTrue(SimpleDateParser.parseMonthDate("0号").isEmpty());
        //TODO 32 is matched by 2
//        assertTrue(SimpleDateParser.parseMonthDate("32号").isEmpty());
    }

    @Test
    public void testParserNum() {
        assertEquals(Pair.create(123, 3), SimpleDateParser.parseNum("我是123的爸爸。", 5, true));
        assertEquals(Pair.create(123, 3), SimpleDateParser.parseNum("123的爸爸。", 3, true));
        assertEquals(Pair.create(-1, -1), SimpleDateParser.parseNum("123的爸爸。", 4, true));

        assertEquals(Pair.create(-1, -1), SimpleDateParser.parseNum("我是123的爸爸", -1, true));
        assertEquals(Pair.create(-1, -1), SimpleDateParser.parseNum("我是123的爸爸", 20, true));
        assertEquals(Pair.create(-1, -1), SimpleDateParser.parseNum("", 0, true));
        assertEquals(Pair.create(123, 3), SimpleDateParser.parseNum("我是123", 5, true));


        assertEquals(Pair.create(-1, -1), SimpleDateParser.parseNum("我是123", 5, false));
        assertEquals(Pair.create(123, 3), SimpleDateParser.parseNum("我是123的爸爸。", 1, false));
        assertEquals(Pair.create(123, 3), SimpleDateParser.parseNum("123的爸爸。", -1, false));
        assertEquals(Pair.create(23, 2), SimpleDateParser.parseNum("我最喜欢123。", 4, false));
        assertEquals(Pair.create(123, 3), SimpleDateParser.parseNum("我最喜欢123。", 3, false));
        assertEquals(Pair.create(-1, -1), SimpleDateParser.parseNum("我最喜欢123。", 2, false));

        assertEquals(Pair.create(-1, -1), SimpleDateParser.parseNum("我是123的爸爸", -1, false));
        assertEquals(Pair.create(-1, -1), SimpleDateParser.parseNum("我是123的爸爸", 20, false));
        assertEquals(Pair.create(-1, -1), SimpleDateParser.parseNum("", 0, false));

    }
}