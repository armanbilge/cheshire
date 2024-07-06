package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public final class io_cqring_offsets {
	public static final GroupLayout layout = MemoryLayout.structLayout(
			ValueLayout.JAVA_INT.withName("head"),
			ValueLayout.JAVA_INT.withName("tail"),
			ValueLayout.JAVA_INT.withName("ring_mask"),
			ValueLayout.JAVA_INT.withName("ring_entries"),
			ValueLayout.JAVA_INT.withName("overflow"),
			ValueLayout.JAVA_INT.withName("cqes"),
			ValueLayout.JAVA_INT.withName("flags"),
			ValueLayout.JAVA_INT.withName("resv1"),
			ValueLayout.JAVA_LONG.withName("user_addr")).withName("io_cqring_offsets");

	private static VarHandle headVarHandle = layout.varHandle(PathElement.groupElement("head"));
	private static VarHandle tailVarHandle = layout.varHandle(PathElement.groupElement("tail"));
	private static VarHandle ringMaskVarHandle = layout.varHandle(PathElement.groupElement("ring_mask"));
	private static VarHandle ringEntriesVarHandle = layout.varHandle(PathElement.groupElement("ring_entries"));
	private static VarHandle overflowVarHandle = layout.varHandle(PathElement.groupElement("overflow"));
	private static VarHandle cqesVarHandle = layout.varHandle(PathElement.groupElement("cqes"));
	private static VarHandle flagsVarHandle = layout.varHandle(PathElement.groupElement("flags"));
	private static VarHandle userAddrVarHandle = layout.varHandle(PathElement.groupElement("user_addr"));

	public static int getHead(MemorySegment data) {
		return (int) headVarHandle.get(data);
	}

	public static int getTail(MemorySegment data) {
		return (int) tailVarHandle.get(data);
	}

	public static int getRingMask(MemorySegment data) {
		return (int) ringMaskVarHandle.get(data);
	}

	public static int getRingEntries(MemorySegment data) {
		return (int) ringEntriesVarHandle.get(data);
	}

	public static int getOverflow(MemorySegment data) {
		return (int) overflowVarHandle.get(data);
	}

	public static int getCqes(MemorySegment data) {
		return (int) cqesVarHandle.get(data);
	}

	public static int getFlags(MemorySegment data) {
		return (int) flagsVarHandle.get(data);
	}

	public static long getUserAddr(MemorySegment data) {
		return (long) userAddrVarHandle.get(data);
	}

	public static void setHead(MemorySegment data, int value) {
		headVarHandle.set(data, value);
	}

	public static void setTail(MemorySegment data, int value) {
		tailVarHandle.set(data, value);
	}

	public static void setRingMask(MemorySegment data, int value) {
		ringMaskVarHandle.set(data, value);
	}

	public static void setRingEntries(MemorySegment data, int value) {
		ringEntriesVarHandle.set(data, value);
	}

	public static void setOverflow(MemorySegment data, int value) {
		overflowVarHandle.set(data, value);
	}

	public static void setCqes(MemorySegment data, int value) {
		cqesVarHandle.set(data, value);
	}

	public static void setFlags(MemorySegment data, int value) {
		flagsVarHandle.set(data, value);
	}

	public static void setUserAddr(MemorySegment data, long value) {
		userAddrVarHandle.set(data, value);
	}

}
