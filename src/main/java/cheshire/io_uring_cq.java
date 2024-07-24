package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.VarHandle;

public final class io_uring_cq {

	public static final GroupLayout layout = MemoryLayout.structLayout(
			ValueLayout.ADDRESS.withName("khead"),
			ValueLayout.ADDRESS.withName("ktail"),
			ValueLayout.ADDRESS.withName("kring_mask"),
			ValueLayout.ADDRESS.withName("kring_entries"),
			ValueLayout.ADDRESS.withName("kflags"),
			ValueLayout.ADDRESS.withName("koverflow"),
			ValueLayout.ADDRESS.withName("cqes"),
			ValueLayout.JAVA_LONG.withName("ring_sz"),
			ValueLayout.ADDRESS.withName("ring_ptr"),
			ValueLayout.JAVA_INT.withName("ring_mask"),
			ValueLayout.JAVA_INT.withName("ring_entries"),
			MemoryLayout.sequenceLayout(2, ValueLayout.JAVA_INT).withName("pad"))
			.withName("io_uring_cq");

	private static VarHandle kheadVarHandle = layout.varHandle(PathElement.groupElement("khead"));
	private static VarHandle ktailVarHandle = layout.varHandle(PathElement.groupElement("ktail"));
	private static VarHandle kringMaskVarHandle = layout.varHandle(PathElement.groupElement("kring_mask"));
	private static VarHandle kringEntriesVarHandle = layout.varHandle(PathElement.groupElement("kring_entries"));
	private static VarHandle kflagsVarHandle = layout.varHandle(PathElement.groupElement("kflags"));
	private static VarHandle koverflowVarHandle = layout.varHandle(PathElement.groupElement("koverflow"));
	private static VarHandle cqesVarHandle = layout.varHandle(PathElement.groupElement("cqes"));
	private static VarHandle ringSzVarHandle = layout.varHandle(PathElement.groupElement("ring_sz"));
	public static VarHandle ringPtrVarHandle = layout.varHandle(PathElement.groupElement("ring_ptr"));
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

	public static void setReleaseKhead(MemorySegment data, MemorySegment value) {
		kheadVarHandle.setRelease(data, value);
	};

	public static MemorySegment getKtail(MemorySegment data) {
		return (MemorySegment) ktailVarHandle.get(data);
	};

	public static MemorySegment getAcquireKtail(MemorySegment data) {
		return (MemorySegment) ktailVarHandle.getAcquire(data);
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

	public static void setKflags(MemorySegment data, MemorySegment value) {
		kflagsVarHandle.set(data, value);
	};

	public static MemorySegment getKoverflow(MemorySegment data) {
		return (MemorySegment) koverflowVarHandle.get(data);
	};

	public static void setKoverflow(MemorySegment data, MemorySegment value) {
		koverflowVarHandle.set(data, value);
	};

	public static MemorySegment getCqes(MemorySegment data) {
		return (MemorySegment) cqesVarHandle.get(data);
	};

	public static void setCqes(MemorySegment data, MemorySegment value) {
		cqesVarHandle.set(data, value);
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
