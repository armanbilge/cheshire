package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.VarHandle;

public final class io_uring_cq {
	public static final GroupLayout layout = MemoryLayout.structLayout(ValueLayout.ADDRESS.withName("khead"),
			ValueLayout.ADDRESS.withName("ktail"), ValueLayout.ADDRESS.withName("kring_mask"),
			ValueLayout.ADDRESS.withName("kring_entries"), ValueLayout.ADDRESS.withName("kflags"),
			ValueLayout.ADDRESS.withName("koverflow"), ValueLayout.ADDRESS.withName("cqes"),
			ValueLayout.JAVA_LONG.withName("ring_sz"), ValueLayout.ADDRESS.withName("ring_ptr"),
			ValueLayout.JAVA_INT.withName("ring_mask"), ValueLayout.JAVA_INT.withName("ring_entries"),
			MemoryLayout.sequenceLayout(2, ValueLayout.JAVA_INT).withName("pad")).withName("io_uring_cq");

	private static VarHandle kheadVarHandle = layout.varHandle(PathElement.groupElement("khead"));
	private static VarHandle ktailVarHandle = layout.varHandle(PathElement.groupElement("ktail"));
	private static VarHandle kringMaskVarHandle = layout.varHandle(PathElement.groupElement("kring_mask"));
	private static VarHandle kringEntriesVarHandle = layout.varHandle(PathElement.groupElement("kring_entries"));
	private static VarHandle kflagsVarHandle = layout.varHandle(PathElement.groupElement("kflags"));
	private static VarHandle koverflowVarHandle = layout.varHandle(PathElement.groupElement("koverflow"));
	private static VarHandle cqesVarHandle = layout.varHandle(PathElement.groupElement("cqes"));
	private static VarHandle ringSzVarHandle = layout.varHandle(PathElement.groupElement("ring_sz"));
	private static VarHandle ringPtrVarHandle = layout.varHandle(PathElement.groupElement("ring_ptr"));
	private static VarHandle ringMaskVarHandle = layout.varHandle(PathElement.groupElement("ring_mask"));
	private static VarHandle ringEntriesVarHandle = layout.varHandle(PathElement.groupElement("ring_entries"));

	public static MemorySegment getCqesSegment(MemorySegment data) {
		return MemorySegment.ofAddress(getCqes(data));
	}

	public static MemorySegment getRingPtrSegment(MemorySegment data) {
		return MemorySegment.ofAddress(getRingPtr(data));
	}

	public static MemorySegment getKringMaskSegment(MemorySegment data) {
		return MemorySegment.ofAddress(getKringMask(data));
	}

	public static MemorySegment getKringEntriesSegment(MemorySegment data) {
		return MemorySegment.ofAddress(getKringEntries(data));
	}

	public static long getKhead(MemorySegment data) {
		return (long) kheadVarHandle.get(data);
	}

	public static long getAcquireKhead(MemorySegment data) {
		return (long) kheadVarHandle.getAcquire(data);
	}

	public static long getKtail(MemorySegment data) {
		return (long) ktailVarHandle.get(data);
	}

	public static long getAcquireKtail(MemorySegment data) {
		return (long) ktailVarHandle.getAcquire(data);
	}

	public static long getKringMask(MemorySegment data) {
		return (long) kringMaskVarHandle.get(data);
	}

	public static long getKringEntries(MemorySegment data) {
		return (long) kringEntriesVarHandle.get(data);
	}

	public static long getKflags(MemorySegment data) {
		return (long) kflagsVarHandle.get(data);
	}

	public static long getKoverflow(MemorySegment data) {
		return (long) koverflowVarHandle.get(data);
	}

	public static long getCqes(MemorySegment data) {
		return (long) cqesVarHandle.get(data);
	}

	public static long getRingSz(MemorySegment data) {
		return (long) ringSzVarHandle.get(data);
	}

	public static long getRingPtr(MemorySegment data) {
		return (long) ringPtrVarHandle.get(data);
	}

	public static int getRingMask(MemorySegment data) {
		return (int) ringMaskVarHandle.get(data);
	}

	public static int getRingEntries(MemorySegment data) {
		return (int) ringEntriesVarHandle.get(data);
	}

	public static void setKhead(MemorySegment data, long value) {
		kheadVarHandle.set(data, value);
	}

	public static void setReleaseKhead(MemorySegment data, long value) {
		kheadVarHandle.setRelease(data, value);
	}

	public static void setKtail(MemorySegment data, long value) {
		ktailVarHandle.set(data, value);
	}

	public static void setReleaseKtail(MemorySegment data, long value) {
		ktailVarHandle.setRelease(data, value);
	}

	public static void setKringMask(MemorySegment data, long value) {
		kringMaskVarHandle.set(data, value);
	}

	public static void setKringEntries(MemorySegment data, long value) {
		kringEntriesVarHandle.set(data, value);
	}

	public static void setKflags(MemorySegment data, long value) {
		kflagsVarHandle.set(data, value);
	}

	public static void setKoverflow(MemorySegment data, long value) {
		koverflowVarHandle.set(data, value);
	}

	public static void setCqes(MemorySegment data, long value) {
		cqesVarHandle.set(data, value);
	}

	public static void setRingSz(MemorySegment data, long value) {
		ringSzVarHandle.set(data, value);
	}

	public static void setRingPtr(MemorySegment data, long value) {
		ringPtrVarHandle.set(data, value);
	}

	public static void setRingMask(MemorySegment data, int value) {
		ringMaskVarHandle.set(data, value);
	}

	public static void setRingEntries(MemorySegment data, int value) {
		ringEntriesVarHandle.set(data, value);
	}

}
