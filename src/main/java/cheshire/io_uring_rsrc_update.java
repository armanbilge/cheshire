package cheshire;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public final class io_uring_rsrc_update {
	MemorySegment segment;

	public io_uring_rsrc_update(Arena session) {
		this.segment = session.allocate(io_uring_rsrc_update.layout);
	}

	public static final GroupLayout layout = MemoryLayout.structLayout(
			ValueLayout.JAVA_INT.withName("offset"),
			ValueLayout.JAVA_INT.withName("resv"),
			ValueLayout.JAVA_LONG.withName("data")).withName("io_uring_rsrc_update");

	public static VarHandle offsetVarHandle = layout.varHandle(PathElement.groupElement("offset"));
}
