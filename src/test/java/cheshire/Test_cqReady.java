package cheshire;

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import cheshire.io_uring;
import cheshire.io_uring_sqe;
import cheshire.liburing;
import cheshire.utils;

public class Test_cqReady extends SuperclassTests {

	static int queue_n_nops(io_uring ring, int n) {
		MemorySegment sqe;
		int i, ret;

		for (i = 0; i < n; i++) {
			sqe = liburing.io_uring_get_sqe(ring);
			if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
				System.out.println("get sqe failed");
				return 1;
			}
			liburing.io_uring_prep_nop(new io_uring_sqe(sqe));
		}

		ret = liburing.io_uring_submit(ring);
		if (ret < n) {
			System.out.println("Submitted only " + ret);
			return 1;
		} else if (ret < 0) {
			System.out.println("sqe submit failed: " + ret);
			return 1;
		}

		return 0;
	}

	static int CHECK_READY(io_uring ring, int expected) {
		int ready = liburing.io_uring_cq_ready(ring);
		if (ready != expected) {
			System.out.println("Got " + ready + " CQs ready, expected " + expected);
			liburing.io_uring_queue_exit(ring);
			return -1;
		}
		return ready;
	}

	@Test
	public void cqReady() {
		try (Arena memorySession = Arena.ofConfined()) {
			io_uring ring = new io_uring(memorySession);
			int ret, ready;

			ret = liburing.io_uring_queue_init(4, ring, 0);
			assertEquals(0, ret);

			ready = CHECK_READY(ring, 0);
			assertFalse(ready < 0);

			assertEquals(0, queue_n_nops(ring, 4));

			ready = CHECK_READY(ring, 4);
			assertFalse(ready < 0);

			liburing.io_uring_cq_advance(ring, 4);
			ready = CHECK_READY(ring, 0);
			assertFalse(ready < 0);
			assertEquals(0, queue_n_nops(ring, 4));

			ready = CHECK_READY(ring, 4);
			assertFalse(ready < 0);

			liburing.io_uring_cq_advance(ring, 1);
			ready = CHECK_READY(ring, 3);
			assertFalse(ready < 0);

			liburing.io_uring_cq_advance(ring, 2);
			ready = CHECK_READY(ring, 1);
			assertFalse(ready < 0);

			liburing.io_uring_cq_advance(ring, 1);
			ready = CHECK_READY(ring, 0);
			assertFalse(ready < 0);

			liburing.io_uring_queue_exit(ring);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}