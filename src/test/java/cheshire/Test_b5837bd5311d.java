package cheshire;

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import cheshire.__kernel_timespec;
import cheshire.io_uring;
import cheshire.io_uring_cqe;
import cheshire.io_uring_sqe;
import cheshire.liburing;
import cheshire.utils;

public class Test_b5837bd5311d extends SuperclassTests {
	@Test
	public void testb5837bd5311d() {
		try (Arena memorySession = Arena.ofConfined()) {
			io_uring ring = new io_uring(memorySession);
			io_uring_cqe cqe = new io_uring_cqe(memorySession);
			MemorySegment sqe;
			int ret;
			__kernel_timespec ts1 = new __kernel_timespec(memorySession);

			__kernel_timespec.setTvSec(ts1.segment, 0L);
			__kernel_timespec.setTvNsec(ts1.segment, 10000000L);
			assertEquals(0, liburing.io_uring_queue_init(4, ring, 0));

			/*
			 * First, submit the timeout sqe so we can actually finish the test
			 * if everything is in working order.
			 */

			sqe = liburing.io_uring_get_sqe(ring);
			assertFalse(utils.areSegmentsEquals(sqe, MemorySegment.NULL));

			liburing.io_uring_prep_timeout(new io_uring_sqe(sqe), ts1, -1, 0);

			ret = liburing.io_uring_submit(ring);
			assertEquals(1, ret);

			/*
			 * Next, submit a nop and wait for two events. If everything is working
			 * as it should, we should be waiting for more than a millisecond and we
			 * should see two cqes. Otherwise, execution continues immediately
			 * and we see only one cqe.
			 */
			sqe = liburing.io_uring_get_sqe(ring);
			assertFalse(utils.areSegmentsEquals(sqe, MemorySegment.NULL));

			liburing.io_uring_prep_nop(new io_uring_sqe(sqe));

			ret = liburing.io_uring_submit_and_wait(ring, 2);
			assertEquals(1, ret);
			assertEquals(0, liburing.io_uring_peek_cqe(ring, cqe));

			liburing.io_uring_cqe_seen(ring, cqe);
			assertEquals(0, liburing.io_uring_peek_cqe(ring, cqe));

			liburing.io_uring_queue_exit(ring);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}