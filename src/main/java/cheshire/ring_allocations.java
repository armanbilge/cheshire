package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public final class ring_allocations {

	public static final GroupLayout layout = MemoryLayout.structLayout(
			io_uring_params.layout.withName("params"),
			io_uring_getevents_arg.layout.withName("arg"),
			get_data.layout.withName("data"),
			io_uring_rsrc_update.layout.withName("up"),
			ValueLayout.JAVA_INT.withName("flags"),
			ValueLayout.JAVA_INT.withName("cqe_flags"),
			ValueLayout.JAVA_LONG.withName("nr_available"))
			.withName("allocations");

	public static MemorySegment getParamsSegment(MemorySegment data) {
		return data.asSlice(ring_allocations.layout.byteOffset(PathElement.groupElement("params")),
				io_uring_params.layout);
	};

	public static MemorySegment getArgSegment(MemorySegment data) {
		return data.asSlice(ring_allocations.layout.byteOffset(PathElement.groupElement("arg")),
				io_uring_getevents_arg.layout);
	};

	public static MemorySegment getDataSegment(MemorySegment data) {
		return data.asSlice(ring_allocations.layout.byteOffset(PathElement.groupElement("data")),
				get_data.layout);
	};

	public static MemorySegment getUpSegment(MemorySegment data) {
		return data.asSlice(ring_allocations.layout.byteOffset(PathElement.groupElement("up")),
				io_uring_rsrc_update.layout);
	};

	public static MemorySegment getFlagsSegment(MemorySegment data) {
		return data.asSlice(ring_allocations.layout.byteOffset(PathElement.groupElement("flags")),
				ValueLayout.JAVA_INT);
	};

	public static MemorySegment getNrAvailableSegment(MemorySegment data) {
		return data.asSlice(ring_allocations.layout.byteOffset(PathElement.groupElement("nr_available")),
				ValueLayout.JAVA_LONG);
	};

	public static MemorySegment getCqeFlagsSegment(MemorySegment data) {
		return data.asSlice(ring_allocations.layout.byteOffset(PathElement.groupElement("cqe_flags")),
				ValueLayout.JAVA_INT);
	};

};
