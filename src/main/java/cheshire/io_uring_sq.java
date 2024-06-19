package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
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

	public static VarHandle kheadVarHandle = layout.varHandle(PathElement.groupElement("khead"));
	public static VarHandle ktailVarHandle = layout.varHandle(PathElement.groupElement("ktail"));
	public static VarHandle kringMaskVarHandle = layout.varHandle(PathElement.groupElement("kring_mask"));
	public static VarHandle kringEntrieskVarHandle = layout.varHandle(PathElement.groupElement("kring_entries"));
	public static VarHandle kflagsVarHandle = layout.varHandle(PathElement.groupElement("kflags"));
	public static VarHandle kdroppedVarHandle = layout.varHandle(PathElement.groupElement("kdropped"));
	public static VarHandle arrayVarHandle = layout.varHandle(PathElement.groupElement("array"));
	public static VarHandle sqesVarHandle = layout.varHandle(PathElement.groupElement("sqes"));
	public static VarHandle ringSzVarHandle = layout.varHandle(PathElement.groupElement("ring_sz"));
	public static VarHandle ringPtrVarHandle = layout.varHandle(PathElement.groupElement("ring_ptr"));
	public static VarHandle ringMaskVarHandle = layout.varHandle(PathElement.groupElement("ring_mask"));
	public static VarHandle ringEntriesVarHandle = layout.varHandle(PathElement.groupElement("ring_entries"));
}
