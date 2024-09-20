package cheshire;

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import cheshire.io_uring;
import cheshire.io_uring_cqe;
import cheshire.io_uring_sqe;
import cheshire.liburing;
import cheshire.utils;

public class Test_cqPeekBatch extends SuperclassTests {

	static int queue_n_nops(io_uring ring, int n, int offset) {
		MemorySegment sqe;
		int i, ret;

		for (i = 0; i < n; i++) {
			sqe = liburing.io_uring_get_sqe(ring);
			if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
				System.out.println("get sqe failed");
				return 1;
			}

			liburing.io_uring_prep_nop(new io_uring_sqe(sqe));
			io_uring_sqe.setUserData(sqe, i + offset);
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

	static int CHECK_BATCH(io_uring ring, MemorySegment cqes, int count, int expected) {
		int got = liburing.io_uring_peek_batch_cqe(ring, cqes, count);
		if (got != expected) {
			System.out.println("Got " + got + " CQs, expected " + expected);
			liburing.io_uring_queue_exit(ring);
			return -1;
		}
		return got;
	}

	@Test
	public void cqPeekBatch() {
		try (Arena memorySession = Arena.ofConfined()) {
			io_uring ring = new io_uring(memorySession);
			MemorySegment cqes = memorySession.allocate(io_uring_cqe.layout.byteSize() * 4);
			int ret, i, got;

			ret = liburing.io_uring_queue_init(4, ring, 0);
			assertEquals(0, ret);

			got = CHECK_BATCH(ring, cqes, 4, 0);
			assertFalse(got < 0);
			assertEquals(0, queue_n_nops(ring, 4, 0));

			got = CHECK_BATCH(ring, cqes, 4, 4);
			assertFalse(got < 0);

			long offset = io_uring_cqe.layout.byteSize();
			for (i = 0; i < 4; i++) {
				long index = i * offset;
				MemorySegment cqe = cqes.asSlice(index, offset);
				if (index == 6) {
					return;
				}
				long userData = io_uring_cqe.getUserData(cqe);
				assertFalse(i != (int) userData);
			}

			assertEquals(0, queue_n_nops(ring, 4, 4));

			liburing.io_uring_cq_advance(ring, 4);
			got = CHECK_BATCH(ring, cqes, 4, 4);
			assertFalse(got < 0);

			for (i = 0; i < 4; i++) {
				long index = i * offset;
				int userData = (int) io_uring_cqe.getUserData(cqes.asSlice(index, offset));
				assertFalse((i + 4) != userData);
			}

			liburing.io_uring_cq_advance(ring, 8);

			liburing.io_uring_queue_exit(ring);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}