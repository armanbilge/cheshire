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
		return utils.getIntReinterpreted(kheadVarHandle.get(data, 0));
	};

	public static MemorySegment getAcquireKhead(MemorySegment data) {
		return utils.getIntReinterpreted(kheadVarHandle.getAcquire(data, 0));
	};

	public static void setKhead(MemorySegment data, MemorySegment value) {
		kheadVarHandle.set(data, 0, value);
	};

	public static void setReleaseKhead(MemorySegment data, MemorySegment value) {
		kheadVarHandle.setRelease(data, 0, value);
	};

	public static MemorySegment getKtail(MemorySegment data) {
		return utils.getIntReinterpreted(ktailVarHandle.get(data, 0));
	};

	public static MemorySegment getAcquireKtail(MemorySegment data) {
		return utils.getIntReinterpreted(ktailVarHandle.getAcquire(data, 0));
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

	public static void setKflags(MemorySegment data, MemorySegment value) {
		kflagsVarHandle.set(data, 0, value);
	};

	public static MemorySegment getKoverflow(MemorySegment data) {
		return utils.getIntReinterpreted(koverflowVarHandle.get(data, 0));
	};

	public static void setKoverflow(MemorySegment data, MemorySegment value) {
		koverflowVarHandle.set(data, 0, value);
	};

	public static MemorySegment getCqes(MemorySegment data) {
		return (MemorySegment) cqesVarHandle.get(data, 0); // Reinterpreted when used
	};

	public static void setCqes(MemorySegment data, MemorySegment value) {
		cqesVarHandle.set(data, 0, value);
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
