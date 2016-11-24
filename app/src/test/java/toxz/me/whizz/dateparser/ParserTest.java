package toxz.me.whizz.dateparser;

import android.support.v4.util.Pair;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(JUnit4.class)
public class ParserTest {

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