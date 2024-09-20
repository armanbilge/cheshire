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
		return utils.getIntReinterpreted(kheadVarHandle.get(data, 0));
	};

	public static MemorySegment getAcquireKhead(MemorySegment data) {
		return utils.getIntReinterpreted(kheadVarHandle.getAcquire(data, 0));
	};

	public static void setKhead(MemorySegment data, MemorySegment value) {
		kheadVarHandle.set(data, 0, value);
	};

	public static MemorySegment getKtail(MemorySegment data) {
		return utils.getIntReinterpreted(ktailVarHandle.get(data, 0));
	};

	public static void setKtail(MemorySegment data, MemorySegment value) {
		ktailVarHandle.set(data, 0, value);
	};

	public static void setReleaseKtail(MemorySegment data, MemorySegment value) {
		ktailVarHandle.setRelease(data, 0, value);
	};

	public static MemorySegment getKringMask(MemorySegment data) {
		return utils.getIntReinterpreted(kringMaskVarHandle.get(data, 0));
	};

	public static void setKringMask(MemorySegment data, MemorySegment value) {
		kringMaskVarHandle.set(data, 0, value);
	};

	public static MemorySegment getKringEntries(MemorySegment data) {
		return utils.getIntReinterpreted(kringEntriesVarHandle.get(data, 0));
	};

	public static void setKringEntries(MemorySegment data, MemorySegment value) {
		kringEntriesVarHandle.set(data, 0, value);
	};

	public static MemorySegment getKflags(MemorySegment data) {
		return utils.getIntReinterpreted(kflagsVarHandle.get(data, 0));
	};

	public static MemorySegment getAcquireKflags(MemorySegment data) {
		return utils.getIntReinterpreted(kflagsVarHandle.get(data, 0));
	};

	public static void setKflags(MemorySegment data, MemorySegment value) {
		kflagsVarHandle.set(data, 0, value);
	};

	public static MemorySegment getKdropped(MemorySegment data) {
		return utils.getIntReinterpreted(kdroppedVarHandle.get(data, 0));
	};

	public static void setKdropped(MemorySegment data, MemorySegment value) {
		kdroppedVarHandle.set(data, 0, value);
	};

	public static MemorySegment getArray(MemorySegment data) {
		return (MemorySegment) arrayVarHandle.get(data, 0); // reinterpreted when needed
	};

	public static void setArray(MemorySegment data, MemorySegment value) {
		arrayVarHandle.set(data, 0, value);
	};

	public static MemorySegment getSqes(MemorySegment data) {
		return (MemorySegment) sqesVarHandle.get(data, 0); // needs reinterpret?
	};

	public static void setSqes(MemorySegment data, MemorySegment value) {
		sqesVarHandle.set(data, 0, value);
	};

	public static int getSqeHead(MemorySegment data) {
		return (int) sqeHeadVarHandle.get(data, 0);
	};

	public static void setSqeHead(MemorySegment data, int value) {
		sqeHeadVarHandle.set(data, 0, value);
	};

	public static int getSqeTail(MemorySegment data) {
		return (int) sqeTailVarHandle.get(data, 0);
	};

	public static void setSqeTail(MemorySegment data, int value) {
		sqeTailVarHandle.set(data, 0, value);
	};

	public static long getRingSz(MemorySegment data) {
		return (long) ringSzVarHandle.get(data, 0);
	};

	public static void setRingSz(MemorySegment data, long value) {
		ringSzVarHandle.set(data, 0, value);
	};

	public static MemorySegment getRingPtr(MemorySegment data) {
		return (MemorySegment) ringPtrVarHandle.get(data, 0); // needs reinterpret?
	};

	public static void setRingPtr(MemorySegment data, MemorySegment value) {
		ringPtrVarHandle.set(data, 0, value);
	};

	public static int getRingMask(MemorySegment data) {
		return (int) ringMaskVarHandle.get(data, 0);
	};

	public static void setRingMask(MemorySegment data, int value) {
		ringMaskVarHandle.set(data, 0, value);
	};

	public static int getRingEntries(MemorySegment data) {
		return (int) ringEntriesVarHandle.get(data, 0);
	};

	public static void setRingEntries(MemorySegment data, int value) {
		ringEntriesVarHandle.set(data, 0, value);
	};

};
