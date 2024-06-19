package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public final class io_uring {
	public static final GroupLayout layout = MemoryLayout
			.structLayout(io_uring_sq.layout.withName("sq"), io_uring_cq.layout.withName("cq"),
					ValueLayout.JAVA_INT.withName("flags"),
					ValueLayout.JAVA_INT.withName("ring_fd"), ValueLayout.JAVA_INT.withName("features"),
					ValueLayout.JAVA_INT.withName("enter_ring_fd"), ValueLayout.JAVA_CHAR.withName("int_flags"),
					MemoryLayout.sequenceLayout(3, ValueLayout.JAVA_CHAR).withName("pad"), ValueLayout.JAVA_INT.withName("pad2"))
			.withName("io_uring");

	public static VarHandle sqVarHandle = layout.varHandle(PathElement.groupElement("sq"));
	public static VarHandle cqVarHandle = layout.varHandle(PathElement.groupElement("cq"));
	public static VarHandle intFlagsVarHandle = layout.varHandle(PathElement.groupElement("int_flags"));
	public static VarHandle featuresVarHandle = layout.varHandle(PathElement.groupElement("features"));
	public static VarHandle flagsVarHandle = layout.varHandle(PathElement.groupElement("flags"));
	public static VarHandle enterRingFdVarHandle = layout.varHandle(PathElement.groupElement("enter_ring_fd"));
	public static VarHandle ringFdVarHandle = layout.varHandle(PathElement.groupElement("ring_fd"));
}
