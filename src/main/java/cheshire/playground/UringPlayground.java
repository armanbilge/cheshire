package cheshire.playground;

import java.lang.foreign.Arena;

import cheshire.io_uring;
import cheshire.liburing;

public class UringPlayground {
	public static void main(String[] args) {
		try (Arena memorySession = Arena.ofConfined()) {
			io_uring ring = new io_uring(memorySession);
			long init = System.currentTimeMillis();
			int ret = liburing.io_uring_queue_init(8, ring, 0);
			System.out.println("Duration (ms): " + (System.currentTimeMillis() - init));
			System.out.println(ret);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}