package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public final class io_uring_sq {
	public static final GroupLayout layout = MemoryLayout
			.structLayout(ValueLayout.ADDRESS.withName("khead"), ValueLayout.ADDRESS.withName("ktail"),
					ValueLayout.ADDRESS.withName("kring_mask"), ValueLayout.ADDRESS.withName("kring_entries"),
					ValueLayout.ADDRESS.withName("kflags"), ValueLayout.ADDRESS.withName("kdropped"),
					ValueLayout.ADDRESS.withName("array"), ValueLayout.ADDRESS.withName("sqes"),
					ValueLayout.JAVA_INT.withName("sqe_head"), ValueLayout.JAVA_INT.withName("sqe_tail"),
					ValueLayout.JAVA_LONG.withName("ring_sz"), ValueLayout.ADDRESS.withName("ring_ptr"),
					ValueLayout.JAVA_INT.withName("ring_mask"), ValueLayout.JAVA_INT.withName("ring_entries"),
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

	public static MemorySegment getSqesSegment(MemorySegment data) {
		return MemorySegment.ofAddress(getSqes(data));
	}

	public static MemorySegment getRingPtrSegment(MemorySegment data) {
		return MemorySegment.ofAddress(getRingPtr(data));
	}

	public static MemorySegment getSqArraySegment(MemorySegment data) {
		return MemorySegment.ofAddress(getArray(data));
	}

	public static MemorySegment getKringMaskSegment(MemorySegment data) {
		return MemorySegment.ofAddress(getKringMask(data));
	}

	public static MemorySegment getKringEntriesSegment(MemorySegment data) {
		return MemorySegment.ofAddress(getKringEntries(data));
	}

	public static MemorySegment getKheadSegment(MemorySegment data) {
		return MemorySegment.ofAddress(getKhead(data));
	}

	public static MemorySegment getKtailSegment(MemorySegment data) {
		return MemorySegment.ofAddress(getKtail(data));
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

	public static long getKringMask(MemorySegment data) {
		return (long) kringMaskVarHandle.get(data);
	}

	public static long getKringEntries(MemorySegment data) {
		return (long) kringEntriesVarHandle.get(data);
	}

	public static long getKflags(MemorySegment data) {
		return (long) kflagsVarHandle.get(data);
	}

	public static long getAcquireKflags(MemorySegment data) {
		return (long) kflagsVarHandle.getAcquire(data);
	}

	public static long getKdropped(MemorySegment data) {
		return (long) kdroppedVarHandle.get(data);
	}

	public static long getArray(MemorySegment data) {
		return (long) arrayVarHandle.get(data);
	}

	public static long getSqes(MemorySegment data) {
		return (long) sqesVarHandle.get(data);
	}

	public static int getSqeHead(MemorySegment data) {
		return (int) sqeHeadVarHandle.get(data);
	}

	public static int getSqeTail(MemorySegment data) {
		return (int) sqeTailVarHandle.get(data);
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

	public static void setKdropped(MemorySegment data, long value) {
		kdroppedVarHandle.set(data, value);
	}

	public static void setArray(MemorySegment data, long value) {
		arrayVarHandle.set(data, value);
	}

	public static void setSqes(MemorySegment data, long value) {
		sqesVarHandle.set(data, value);
	}

	public static void setSqeHead(MemorySegment data, int value) {
		sqeHeadVarHandle.set(data, value);
	}

	public static void setSqeTail(MemorySegment data, int value) {
		sqeTailVarHandle.set(data, value);
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
