import org.junit.*;
import static org.junit.Assert.assertEquals;

public class Tests {
    private ToBeTested t = new ToBeTested();

    @Test
    public void testSumFunctionFor3and4() {
        assertEquals(9, t.sum(4,5));
        assertEquals(12, t.sum(30, 2));
    }

}