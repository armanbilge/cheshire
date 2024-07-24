package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public final class io_uring_sq {

	public static final GroupLayout layout = MemoryLayout.structLayout(
			ValueLayout.ADDRESS.withName("khead"),
			ValueLayout.ADDRESS.withName("ktail"),
			ValueLayout.ADDRESS.withName("kring_mask"),
			ValueLayout.ADDRESS.withName("kring_entries"),
			ValueLayout.ADDRESS.withName("kflags"),
			ValueLayout.ADDRESS.withName("kdropped"),
			ValueLayout.ADDRESS.withName("array"),
			ValueLayout.ADDRESS.withName("sqes"),
			ValueLayout.JAVA_INT.withName("sqe_head"),
			ValueLayout.JAVA_INT.withName("sqe_tail"),
			ValueLayout.JAVA_LONG.withName("ring_sz"),
			ValueLayout.ADDRESS.withName("ring_ptr"),
			ValueLayout.JAVA_INT.withName("ring_mask"),
			ValueLayout.JAVA_INT.withName("ring_entries"),
			MemoryLayout.sequenceLayout(2, ValueLayout.JAVA_INT).withName("pad"))
			.withName("io_uring_sq");

	private static VarHandle kheadVarHandle = layout.varHandle(PathElement.groupElement("khead"));
	private static VarHandle ktailVarHandle = layout.varHandle(PathElement.groupElement("ktail"));
	private static VarHandle kringMaskVarHandle = layout.varHandle(PathElement.groupElement("kring_mask"));
	private static VarHandle kringEntriesVarHandle = layout.varHandle(PathElement.groupElement("kring_entries"));
	private static VarHandle kflagsVarHandle = layout.varHandle(PathElement.groupElement("kflags"));
	private static VarHandle kdroppedVarHandle = layout.varHandle(PathElement.groupElement("kdropped"));
	private static VarHandle arrayVarHandle = layout.varHandle(PathElement.groupElement("array"));
	private static VarHandle sqesVarHandle = layout.varHandle(PathElement.groupElement("sqes"));
	private static VarHandle sqeHeadVarHandle = layout.varHandle(PathElement.groupElement("sqe_head"));
	private static VarHandle sqeTailVarHandle = layout.varHandle(PathElement.groupElement("sqe_tail"));
	private static VarHandle ringSzVarHandle = layout.varHandle(PathElement.groupElement("ring_sz"));
	private static VarHandle ringPtrVarHandle = layout.varHandle(PathElement.groupElement("ring_ptr"));
	private static VarHandle ringMaskVarHandle = layout.varHandle(PathElement.groupElement("ring_mask"));
	private static VarHandle ringEntriesVarHandle = layout.varHandle(PathElement.groupElement("ring_entries"));

	public static MemorySegment getKhead(MemorySegment data) {
		return (MemorySegment) kheadVarHandle.get(data);
	};

	public static MemorySegment getAcquireKhead(MemorySegment data) {
		return (MemorySegment) kheadVarHandle.getAcquire(data);
	};

	public static void setKhead(MemorySegment data, MemorySegment value) {
		kheadVarHandle.set(data, value);
	};

	public static MemorySegment getKtail(MemorySegment data) {
		return (MemorySegment) ktailVarHandle.get(data);
	};

	public static void setKtail(MemorySegment data, MemorySegment value) {
		ktailVarHandle.set(data, value);
	};

	public static void setReleaseKtail(MemorySegment data, MemorySegment value) {
		ktailVarHandle.setRelease(data, value);
	};

	public static MemorySegment getKringMask(MemorySegment data) {
		return (MemorySegment) kringMaskVarHandle.get(data);
	};

	public static void setKringMask(MemorySegment data, MemorySegment value) {
		kringMaskVarHandle.set(data, value);
	};

	public static MemorySegment getKringEntries(MemorySegment data) {
		return (MemorySegment) kringEntriesVarHandle.get(data);
	};

	public static void setKringEntries(MemorySegment data, MemorySegment value) {
		kringEntriesVarHandle.set(data, value);
	};

	public static MemorySegment getKflags(MemorySegment data) {
		return (MemorySegment) kflagsVarHandle.get(data);
	};

	public static MemorySegment getAcquireKflags(MemorySegment data) {
		return (MemorySegment) kflagsVarHandle.getAcquire(data);
	};

	public static void setKflags(MemorySegment data, MemorySegment value) {
		kflagsVarHandle.set(data, value);
	};

	public static MemorySegment getKdropped(MemorySegment data) {
		return (MemorySegment) kdroppedVarHandle.get(data);
	};

	public static void setKdropped(MemorySegment data, MemorySegment value) {
		kdroppedVarHandle.set(data, value);
	};

	public static MemorySegment getArray(MemorySegment data) {
		return (MemorySegment) arrayVarHandle.get(data);
	};

	public static void setArray(MemorySegment data, MemorySegment value) {
		arrayVarHandle.set(data, value);
	};

	public static MemorySegment getSqes(MemorySegment data) {
		return (MemorySegment) sqesVarHandle.get(data);
	};

	public static void setSqes(MemorySegment data, MemorySegment value) {
		sqesVarHandle.set(data, value);
	};

	public static int getSqeHead(MemorySegment data) {
		return (int) sqeHeadVarHandle.get(data);
	};

	public static void setSqeHead(MemorySegment data, int value) {
		sqeHeadVarHandle.set(data, value);
	};

	public static int getSqeTail(MemorySegment data) {
		return (int) sqeTailVarHandle.get(data);
	};

	public static void setSqeTail(MemorySegment data, int value) {
		sqeTailVarHandle.set(data, value);
	};

	public static long getRingSz(MemorySegment data) {
		return (long) ringSzVarHandle.get(data);
	};

	public static void setRingSz(MemorySegment data, long value) {
		ringSzVarHandle.set(data, value);
	};

	public static MemorySegment getRingPtr(MemorySegment data) {
		return (MemorySegment) ringPtrVarHandle.get(data);
	};

	public static void setRingPtr(MemorySegment data, MemorySegment value) {
		ringPtrVarHandle.set(data, value);
	};

	public static int getRingMask(MemorySegment data) {
		return (int) ringMaskVarHandle.get(data);
	};

	public static void setRingMask(MemorySegment data, int value) {
		ringMaskVarHandle.set(data, value);
	};

	public static int getRingEntries(MemorySegment data) {
		return (int) ringEntriesVarHandle.get(data);
	};

	public static void setRingEntries(MemorySegment data, int value) {
		ringEntriesVarHandle.set(data, value);
	};

};
