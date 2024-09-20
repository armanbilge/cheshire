package cheshire;

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class MyTestClass {
	@Test
	public void testAddition() {
		int result = 2 + 3;
		result = 10;
		assertEquals(10, result);
	}

	@Test
	public void simpleCheck() {
		try (Arena memorySession = Arena.ofConfined()) {
			io_uring ring = new io_uring(memorySession);
			int ret;

			ret = liburing.io_uring_queue_init(8, ring, 0);
			Assert.assertEquals(ret, 0);

			MemorySegment sqe = liburing.io_uring_get_sqe(ring);
			liburing.io_uring_prep_nop(new io_uring_sqe(sqe));
			ret = liburing.io_uring_submit(ring);
			Assert.assertEquals(ret, 1);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}