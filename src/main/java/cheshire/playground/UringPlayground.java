package cheshire.playground;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import cheshire.io_uring;
import cheshire.io_uring_sqe;
import cheshire.liburing;
import cheshire.ring_allocations;

public class UringPlayground {
	public static void main(String[] args) {
		try (Arena memorySession = Arena.ofConfined()) {
			io_uring ring = new io_uring(memorySession);
			int ret = liburing.io_uring_queue_init(1, ring, 0);
			System.out.println(ret);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}