package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public final class io_uring_rsrc_update {
	public static final GroupLayout layout = MemoryLayout.structLayout(
			ValueLayout.JAVA_INT.withName("offset"),
			ValueLayout.JAVA_INT.withName("resv"),
			ValueLayout.JAVA_LONG.withName("data")).withName("io_uring_rsrc_update");

	private static VarHandle offsetVarHandle = layout.varHandle(PathElement.groupElement("offset"));

	public static int getOffset(MemorySegment data) {
		return (int) offsetVarHandle.get(data);
	}

	public static void setOffset(MemorySegment data, int value) {
		offsetVarHandle.set(data, value);
	}

}
