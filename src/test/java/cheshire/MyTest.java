package cheshire;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.BlockJUnit4ClassRunner;

public class MyTest extends SuperclassTests {

	@Test
	public void simpleCheck() {
		try (Arena memorySession = Arena.ofConfined()) {
			io_uring ring = new io_uring(memorySession);
			int ret;

			Assert.assertEquals(1, 1);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}