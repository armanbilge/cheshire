package cheshire;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public final class io_uring_cqe {

	public MemorySegment segment;

	public io_uring_cqe(Arena session) {
		this.segment = session.allocate(layout);
	}

	public io_uring_cqe(MemorySegment s) {
		this.segment = s;
	}

	public static final GroupLayout layout = MemoryLayout.structLayout(
			ValueLayout.JAVA_LONG.withName("user_data"),
			ValueLayout.JAVA_INT.withName("res"),
			ValueLayout.JAVA_INT.withName("flags"),
			MemoryLayout.sequenceLayout(0, ValueLayout.JAVA_LONG).withName("big_cqe"))
			.withName("io_uring_cqe");

	private static VarHandle userDataVarHandle = layout.varHandle(PathElement.groupElement("user_data"));
	private static VarHandle resVarHandle = layout.varHandle(PathElement.groupElement("res"));

	public static long getUserData(MemorySegment data) {
		return (long) userDataVarHandle.get(data);
	};

	public static void setUserData(MemorySegment data, long value) {
		userDataVarHandle.set(data, value);
	};

	public static int getRes(MemorySegment data) {
		return (int) resVarHandle.get(data);
	};

	public static void setRes(MemorySegment data, int value) {
		resVarHandle.set(data, value);
	};

};
