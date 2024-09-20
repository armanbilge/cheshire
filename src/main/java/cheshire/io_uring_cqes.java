package cheshire;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public final class io_uring_cqes {

	public MemorySegment segment;

	private static long offset = io_uring_cqe.layout.byteSize();

	public io_uring_cqes(Arena session, int length) {
		this.segment = session.allocate(offset * length);
	};

	public io_uring_cqes(MemorySegment s) {
		this.segment = s;
	};

	public io_uring_cqes(int length) {
		try (Arena session = Arena.ofShared()) {
			this.segment = session.allocate(offset * length);
		} catch (Throwable cause) {
			throw new RuntimeException(cause);
		}
	};

	public static MemorySegment getCqeAtIndex(MemorySegment data, int index) {
		long i = index * offset;
		return data.asSlice(i, offset);
	};

};
