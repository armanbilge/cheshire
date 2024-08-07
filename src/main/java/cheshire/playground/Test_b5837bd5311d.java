package cheshire.playground;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import cheshire.__kernel_timespec;
import cheshire.io_uring;
import cheshire.io_uring_cqe;
import cheshire.io_uring_sqe;
import cheshire.liburing;
import cheshire.utils;

public class Test_b5837bd5311d {
	public static void main(String[] args) {
		try (Arena memorySession = Arena.ofConfined()) {
			io_uring ring = new io_uring(memorySession);
			io_uring_cqe cqe = new io_uring_cqe(memorySession);
			MemorySegment sqe;
			int ret;
			__kernel_timespec ts1 = new __kernel_timespec(memorySession);

			__kernel_timespec.setTvSec(ts1.segment, 0L);
			__kernel_timespec.setTvNsec(ts1.segment, 10000000L);

			if (liburing.io_uring_queue_init(4, ring, 0) != 0) {
				System.out.println("ring setup failed");
				liburing.io_uring_queue_exit(ring);
				return;
			}

			/*
			 * First, submit the timeout sqe so we can actually finish the test
			 * if everything is in working order.
			 */

			sqe = liburing.io_uring_get_sqe(ring);
			if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
				System.out.println("get sqe failed");
				liburing.io_uring_queue_exit(ring);
				return;
			}
			liburing.io_uring_prep_timeout(new io_uring_sqe(sqe), ts1, -1, 0);

			ret = liburing.io_uring_submit(ring);
			if (ret != 1) {
				System.out.println("submit 1 failed");
				liburing.io_uring_queue_exit(ring);
				return;
			}

			/*
			 * Next, submit a nop and wait for two events. If everything is working
			 * as it should, we should be waiting for more than a millisecond and we
			 * should see two cqes. Otherwise, execution continues immediately
			 * and we see only one cqe.
			 */
			sqe = liburing.io_uring_get_sqe(ring);
			if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
				System.out.println("get sqe 2 failed");
				liburing.io_uring_queue_exit(ring);
				return;
			}
			liburing.io_uring_prep_nop(new io_uring_sqe(sqe));

			ret = liburing.io_uring_submit_and_wait(ring, 2);
			if (ret != 1) {
				System.out.println("submit 1 failed");
				liburing.io_uring_queue_exit(ring);
				return;
			}

			if (liburing.io_uring_peek_cqe(ring, cqe) != 0) {
				System.out.println("peek cqe failed");
				liburing.io_uring_queue_exit(ring);
				return;
			}

			liburing.io_uring_cqe_seen(ring, cqe);

			if (liburing.io_uring_peek_cqe(ring, cqe) != 0) {
				System.out.println("peek cqe 2 failed");
				liburing.io_uring_queue_exit(ring);
				return;
			}

			liburing.io_uring_queue_exit(ring);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}