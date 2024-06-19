package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public final class io_uring_cqe {
	public static final GroupLayout layout = MemoryLayout.structLayout(ValueLayout.JAVA_LONG.withName("user_data"),
			ValueLayout.JAVA_INT.withName("res"), ValueLayout.JAVA_INT.withName("flags"),
			MemoryLayout.sequenceLayout(0, ValueLayout.JAVA_LONG).withName("big_cqe")).withName("io_uring_cqe");

	public static VarHandle userDataVarHandle = layout.varHandle(PathElement.groupElement("user_data"));
}
