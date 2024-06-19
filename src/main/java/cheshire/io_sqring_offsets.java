package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public final class io_sqring_offsets {
	public static final GroupLayout layout = MemoryLayout.structLayout(
			ValueLayout.JAVA_INT.withName("head"),
			ValueLayout.JAVA_INT.withName("tail"),
			ValueLayout.JAVA_INT.withName("ring_mask"),
			ValueLayout.JAVA_INT.withName("ring_entries"),
			ValueLayout.JAVA_INT.withName("flags"),
			ValueLayout.JAVA_INT.withName("dropped"),
			ValueLayout.JAVA_INT.withName("array"),
			ValueLayout.JAVA_INT.withName("resv1"),
			ValueLayout.JAVA_LONG.withName("user_addr")).withName("io_sqring_offsets");

	public static VarHandle headVarHandle = layout.varHandle(PathElement.groupElement("head"));
	public static VarHandle tailVarHandle = layout.varHandle(PathElement.groupElement("tail"));
	public static VarHandle ringMaskVarHandle = layout.varHandle(PathElement.groupElement("ring_mask"));
	public static VarHandle ringEntriesVarHandle = layout.varHandle(PathElement.groupElement("ring_entries"));
	public static VarHandle flagsVarHandle = layout.varHandle(PathElement.groupElement("flags"));
	public static VarHandle droppedVarHandle = layout.varHandle(PathElement.groupElement("dropped"));
	public static VarHandle arrayVarHandle = layout.varHandle(PathElement.groupElement("array"));
	public static VarHandle userAddrVarHandle = layout.varHandle(PathElement.groupElement("user_addr"));
}
