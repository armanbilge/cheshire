package cheshire;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public final class io_uring {
	MemorySegment segment;
	MemorySegment flags;

	public io_uring(Arena session) {
		this.segment = session.allocate(layout);
		this.flags = session.allocate(ValueLayout.JAVA_INT);
	}

	public static final GroupLayout layout = MemoryLayout
			.structLayout(io_uring_sq.layout.withName("sq"), io_uring_cq.layout.withName("cq"),
					ValueLayout.JAVA_INT.withName("flags"),
					ValueLayout.JAVA_INT.withName("ring_fd"), ValueLayout.JAVA_INT.withName("features"),
					ValueLayout.JAVA_INT.withName("enter_ring_fd"), ValueLayout.JAVA_CHAR.withName("int_flags"),
					MemoryLayout.sequenceLayout(3, ValueLayout.JAVA_CHAR).withName("pad"), ValueLayout.JAVA_INT.withName("pad2"))
			.withName("io_uring");

	private static VarHandle flagsVarHandle = layout.varHandle(PathElement.groupElement("flags"));
	private static VarHandle ringFdVarHandle = layout.varHandle(PathElement.groupElement("ring_fd"));
	private static VarHandle featuresVarHandle = layout.varHandle(PathElement.groupElement("features"));
	private static VarHandle enterRingFdVarHandle = layout.varHandle(PathElement.groupElement("enter_ring_fd"));
	private static VarHandle intFlagsVarHandle = layout.varHandle(PathElement.groupElement("int_flags"));

	public static int getFlags(MemorySegment data) {
		return (int) flagsVarHandle.get(data);
	}

	public static int getRingFd(MemorySegment data) {
		return (int) ringFdVarHandle.get(data);
	}

	public static int getFeatures(MemorySegment data) {
		return (int) featuresVarHandle.get(data);
	}

	public static int getEnterRingFd(MemorySegment data) {
		return (int) enterRingFdVarHandle.get(data);
	}

	public static char getIntFlags(MemorySegment data) {
		return (char) intFlagsVarHandle.get(data);
	}

	public static void setFlags(MemorySegment data, int value) {
		flagsVarHandle.set(data, value);
	}

	public static void setRingFd(MemorySegment data, int value) {
		ringFdVarHandle.set(data, value);
	}

	public static void setFeatures(MemorySegment data, int value) {
		featuresVarHandle.set(data, value);
	}

	public static void setEnterRingFd(MemorySegment data, int value) {
		enterRingFdVarHandle.set(data, value);
	}

	public static void setIntFlags(MemorySegment data, char value) {
		intFlagsVarHandle.set(data, value);
	}
}
