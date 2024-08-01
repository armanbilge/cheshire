package cheshire.playground;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import cheshire.__kernel_timespec;
import cheshire.constants;
import cheshire.io_uring;
import cheshire.io_uring_cqe;
import cheshire.io_uring_sqe;
import cheshire.liburing;
import cheshire.utils;

public class Test_link {

	static int IOSQE_IO_LINK = 1 << 2;
	static int IOSQE_IO_HARDLINK = 1 << 3;

	static int no_hardlink = 0;

	static int test_single_hardlink(io_uring ring, Arena memorySession) {
		__kernel_timespec ts = new __kernel_timespec(memorySession);
		io_uring_cqe cqe = new io_uring_cqe(memorySession);
		io_uring_sqe sqe = new io_uring_sqe(memorySession);
		int i, ret;

		sqe.segment = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe.segment, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}
		__kernel_timespec.setTvSec(ts.segment, 0L);
		__kernel_timespec.setTvNsec(ts.segment, 10000000L);
		liburing.io_uring_prep_timeout(sqe, ts, 0, 0);
		io_uring_sqe.setFlags(sqe.segment, (byte) (io_uring_sqe.getFlags(sqe.segment) | IOSQE_IO_LINK | IOSQE_IO_HARDLINK));
		io_uring_sqe.setUserData(sqe.segment, 1L);

		sqe.segment = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe.segment, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}
		liburing.io_uring_prep_nop(sqe);
		io_uring_sqe.setUserData(sqe.segment, 2L);

		ret = liburing.io_uring_submit(ring);
		if (ret <= 0) {
			System.out.println("sqe submit failed " + ret);
			return 1;
		}

		for (i = 0; i < 2; i++) {
			ret = liburing.io_uring_wait_cqe(ring, cqe);
			if (ret < 0) {
				System.out.println("wait completion " + ret);
				return 1;
			}
			if (utils.areSegmentsEquals(cqe.segment, MemorySegment.NULL)) {
				System.out.println("failed to get cqe");
				return 1;
			}
			if (no_hardlink != 0) {
				liburing.io_uring_cqe_seen(ring, cqe);
				continue;
			}
			long userData = io_uring_cqe.getUserData(cqe.segment);
			int res = io_uring_cqe.getRes(cqe.segment);
			if (userData == 1 && res == -constants.EINVAL) {
				System.out.println("Hard links not supported, skipping");
				no_hardlink = 1;
				continue;
			}
			if (userData == 1 && res == -constants.ETIME) {
				System.out.println("timeout failed with " + res);
				return 1;
			}
			if (userData == 2 && res != 0) {
				System.out.println("nop failed with " + res);
				return 1;
			}

			liburing.io_uring_cqe_seen(ring, cqe);
		}

		return 0;
	}

	static int test_double_hardlink(io_uring ring, Arena memorySession) {
		__kernel_timespec ts1 = new __kernel_timespec(memorySession);
		__kernel_timespec ts2 = new __kernel_timespec(memorySession);
		io_uring_cqe cqe = new io_uring_cqe(memorySession);
		io_uring_sqe sqe = new io_uring_sqe(memorySession);
		int i, ret;

		if (no_hardlink != 0) {
			return 0;
		}

		sqe.segment = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe.segment, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}
		__kernel_timespec.setTvSec(ts1.segment, 0L);
		__kernel_timespec.setTvNsec(ts1.segment, 10000000L);
		liburing.io_uring_prep_timeout(sqe, ts1, 0, 0);
		io_uring_sqe.setFlags(sqe.segment, (byte) (io_uring_sqe.getFlags(sqe.segment) | IOSQE_IO_LINK | IOSQE_IO_HARDLINK));
		io_uring_sqe.setUserData(sqe.segment, 1L);

		sqe.segment = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe.segment, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}
		__kernel_timespec.setTvSec(ts2.segment, 0L);
		__kernel_timespec.setTvNsec(ts2.segment, 15000000L);
		liburing.io_uring_prep_timeout(sqe, ts2, 0, 0);
		io_uring_sqe.setFlags(sqe.segment, (byte) (io_uring_sqe.getFlags(sqe.segment) | IOSQE_IO_LINK | IOSQE_IO_HARDLINK));
		io_uring_sqe.setUserData(sqe.segment, 2L);

		sqe.segment = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe.segment, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}
		liburing.io_uring_prep_nop(sqe);
		io_uring_sqe.setUserData(sqe.segment, 3L);

		ret = liburing.io_uring_submit(ring);
		if (ret <= 0) {
			System.out.println("sqe submit failed " + ret);
			return 1;
		}

		for (i = 0; i < 3; i++) {
			ret = liburing.io_uring_wait_cqe(ring, cqe);
			if (ret < 0) {
				System.out.println("wait completion " + ret);
				return 1;
			}
			if (utils.areSegmentsEquals(cqe.segment, MemorySegment.NULL)) {
				System.out.println("failed to get cqe");
				return 1;
			}
			long userData = io_uring_cqe.getUserData(cqe.segment);
			int res = io_uring_cqe.getRes(cqe.segment);
			if (userData == 1 && res == -constants.ETIME) {
				System.out.println("timeout failed with " + res);
				return 1;
			}
			if (userData == 2 && res == -constants.ETIME) {
				System.out.println("timeout failed with " + res);
				return 1;
			}
			if (userData == 3 && res != 0) {
				System.out.println("nop failed with " + res);
				return 1;
			}

			liburing.io_uring_cqe_seen(ring, cqe);
		}

		return 0;
	}

	static int test_double_chain(io_uring ring, Arena memorySession) {
		io_uring_cqe cqe = new io_uring_cqe(memorySession);
		MemorySegment sqe;
		int i, ret;

		sqe = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}

		liburing.io_uring_prep_nop(new io_uring_sqe(sqe));
		io_uring_sqe.setFlags(sqe, (byte) (io_uring_sqe.getFlags(sqe) | IOSQE_IO_LINK));

		sqe = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}

		liburing.io_uring_prep_nop(new io_uring_sqe(sqe));

		sqe = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}

		liburing.io_uring_prep_nop(new io_uring_sqe(sqe));
		io_uring_sqe.setFlags(sqe, (byte) (io_uring_sqe.getFlags(sqe) | IOSQE_IO_LINK));

		sqe = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}

		liburing.io_uring_prep_nop(new io_uring_sqe(sqe));

		ret = liburing.io_uring_submit(ring);
		if (ret <= 0) {
			System.out.println("sqe submit failed " + ret);
			return 1;
		}

		for (i = 0; i < 4; i++) {
			ret = liburing.io_uring_wait_cqe(ring, cqe);
			if (ret < 0) {
				System.out.println("wait completion " + ret);
				return 1;
			}
			liburing.io_uring_cqe_seen(ring, cqe);
		}

		return 0;
	}

	static int test_double_link(io_uring ring, Arena memorySession) {
		io_uring_cqe cqe = new io_uring_cqe(memorySession);
		MemorySegment sqe;
		int i, ret;

		sqe = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}

		liburing.io_uring_prep_nop(new io_uring_sqe(sqe));
		io_uring_sqe.setFlags(sqe, (byte) (io_uring_sqe.getFlags(sqe) | IOSQE_IO_LINK));

		sqe = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}

		liburing.io_uring_prep_nop(new io_uring_sqe(sqe));
		io_uring_sqe.setFlags(sqe, (byte) (io_uring_sqe.getFlags(sqe) | IOSQE_IO_LINK));

		sqe = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}

		liburing.io_uring_prep_nop(new io_uring_sqe(sqe));

		ret = liburing.io_uring_submit(ring);
		if (ret <= 0) {
			System.out.println("sqe submit failed " + ret);
			return 1;
		}

		for (i = 0; i < 3; i++) {
			ret = liburing.io_uring_wait_cqe(ring, cqe);
			if (ret < 0) {
				System.out.println("wait completion " + ret);
				return 1;
			}
			liburing.io_uring_cqe_seen(ring, cqe);
		}

		return 0;
	}

	static int test_single_link(io_uring ring, Arena memorySession) {
		io_uring_cqe cqe = new io_uring_cqe(memorySession);
		MemorySegment sqe;
		int i, ret;

		sqe = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}

		liburing.io_uring_prep_nop(new io_uring_sqe(sqe));
		io_uring_sqe.setFlags(sqe, (byte) (io_uring_sqe.getFlags(sqe) | IOSQE_IO_LINK));

		sqe = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
			System.out.println("get sqe failed");
			return 1;
		}

		liburing.io_uring_prep_nop(new io_uring_sqe(sqe));

		ret = liburing.io_uring_submit(ring);
		if (ret <= 0) {
			System.out.println("sqe submit failed " + ret);
			return 1;
		}

		for (i = 0; i < 2; i++) {
			ret = liburing.io_uring_wait_cqe(ring, cqe);
			if (ret < 0) {
				System.out.println("wait completion " + ret);
				return 1;
			}
			liburing.io_uring_cqe_seen(ring, cqe);
		}

		return 0;
	}

	public static void main(String[] args) {
		try (Arena memorySession = Arena.ofConfined()) {
			io_uring ring = new io_uring(memorySession);
			int ret;

			ret = liburing.io_uring_queue_init(8, ring, 0);
			if (ret != 0) {
				System.out.println("ring setup failed");
			}

			ret = test_single_link(ring, memorySession);
			if (ret != 0) {
				System.out.println("test_single_link failed");
			}

			ret = test_double_link(ring, memorySession);
			if (ret != 0) {
				System.out.println("test_double_link failed");
			}

			ret = test_double_chain(ring, memorySession);
			if (ret != 0) {
				System.out.println("test_double_chain failed");
			}

			ret = test_single_hardlink(ring, memorySession);
			if (ret != 0) {
				System.out.println("test_single_hardlink");
			}

			ret = test_double_hardlink(ring, memorySession);
			if (ret != 0) {
				System.out.println("test_double_hardlink");
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}