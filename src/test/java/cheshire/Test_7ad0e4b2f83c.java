package cheshire;

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import cheshire.__kernel_timespec;
import cheshire.io_uring;
import cheshire.io_uring_cqe;
import cheshire.io_uring_sqe;
import cheshire.liburing;

public class Test_7ad0e4b2f83c extends SuperclassTests {
	@Test
	public void test7ad0e4b2f83c() {
		try (Arena memorySession = Arena.ofConfined()) {
			io_uring ring = new io_uring(memorySession);
			io_uring_cqe cqe = new io_uring_cqe(memorySession);
			__kernel_timespec ts1 = new __kernel_timespec(memorySession);
			__kernel_timespec ts2 = new __kernel_timespec(memorySession);
			MemorySegment sqe;
			int ret;

			ret = liburing.io_uring_queue_init(32, ring, 0);
			assertEquals(0, ret);

			sqe = liburing.io_uring_get_sqe(ring);
			liburing.io_uring_prep_nop(new io_uring_sqe(sqe));
			ret = liburing.io_uring_submit(ring);
			assertEquals(1, ret);

			__kernel_timespec.setTvSec(ts1.segment, 5L);
			__kernel_timespec.setTvNsec(ts1.segment, 0L);
			ret = liburing.io_uring_wait_cqe_timeout(ring, cqe, ts1);
			assertEquals(0, ret);

			liburing.io_uring_cqe_seen(ring, cqe);

			long init = System.currentTimeMillis();

			__kernel_timespec.setTvSec(ts2.segment, 1L);
			__kernel_timespec.setTvNsec(ts2.segment, 0L);
			sqe = liburing.io_uring_get_sqe(ring);
			liburing.io_uring_prep_timeout(new io_uring_sqe(sqe), ts2, 0, 0);
			io_uring_sqe.setUserData(sqe, 89);
			ret = liburing.io_uring_submit(ring);
			assertEquals(1, ret);

			liburing.io_uring_wait_cqe(ring, cqe);
			liburing.io_uring_cqe_seen(ring, cqe);

			long end = System.currentTimeMillis();

			long duration = (end - init);
			assertTrue(duration > 900 && duration < 1100);

			liburing.io_uring_queue_exit(ring);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}