package cheshire.playground;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import cheshire.__kernel_timespec;
import cheshire.io_uring;
import cheshire.io_uring_cqe;
import cheshire.io_uring_sqe;
import cheshire.liburing;
import cheshire.utils;

public class UringPlayground {
	public static void main(String[] args) {
		try (Arena memorySession = Arena.ofConfined()) {
			io_uring ring = new io_uring(memorySession);
			io_uring_cqe cqe = new io_uring_cqe(memorySession);
			__kernel_timespec ts1 = new __kernel_timespec(memorySession);
			__kernel_timespec ts2 = new __kernel_timespec(memorySession);
			MemorySegment sqe;
			int ret;

			ret = liburing.io_uring_queue_init(32, ring, 0);
			System.out.println("RET === 0 -> " + ret);

			sqe = liburing.io_uring_get_sqe(ring);
			liburing.io_uring_prep_nop(new io_uring_sqe(sqe));
			ret = liburing.io_uring_submit(ring);
			System.out.println("RET === 1 -> " + ret);

			__kernel_timespec.setTvSec(ts1.segment, 5L);
			__kernel_timespec.setTvNsec(ts1.segment, 0L);
			ret = liburing.io_uring_wait_cqe_timeout(ring, cqe, ts1);
			System.out.println("RET === 0 -> " + ret);
			liburing.io_uring_cqe_seen(ring, cqe);

			long init = System.currentTimeMillis();

			__kernel_timespec.setTvSec(ts2.segment, 1L);
			__kernel_timespec.setTvNsec(ts2.segment, 0L);
			sqe = liburing.io_uring_get_sqe(ring);
			liburing.io_uring_prep_timeout(new io_uring_sqe(sqe), ts2, 0, 0);
			io_uring_sqe.setUserData(sqe, 89);
			ret = liburing.io_uring_submit(ring);
			System.out.println("RET === 1 -> " + ret);

			liburing.io_uring_wait_cqe(ring, cqe);
			liburing.io_uring_cqe_seen(ring, cqe);

			long end = System.currentTimeMillis();

			System.out.println("Duration should be > 900 and < 1100: " + (end - init));
			// liburing.io_uring_queue_exit(ring);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}