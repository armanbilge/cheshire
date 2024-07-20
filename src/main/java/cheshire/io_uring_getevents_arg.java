package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public class io_uring_getevents_arg {
	public static final GroupLayout layout = MemoryLayout.structLayout(
			ValueLayout.JAVA_LONG.withName("sigmask"),
			ValueLayout.JAVA_INT.withName("sigmask_sz"),
			ValueLayout.JAVA_INT.withName("pad"),
			ValueLayout.JAVA_LONG.withName("ts")).withName("io_uring_getevents_arg");

	private static VarHandle sigmaskVarHandle = layout.varHandle(PathElement.groupElement("sigmask"));
	private static VarHandle sigmaskSzVarHandle = layout.varHandle(PathElement.groupElement("sigmask_sz"));
	private static VarHandle tsVarHandle = layout.varHandle(PathElement.groupElement("ts"));

	public static long getSigmask(MemorySegment data) {
		return (long) sigmaskVarHandle.get(data);
	}

	public static int getSigmaskSz(MemorySegment data) {
		return (int) sigmaskSzVarHandle.get(data);
	}

	public static long getTs(MemorySegment data) {
		return (long) tsVarHandle.get(data);
	}

	public static void setSigmask(MemorySegment data, long value) {
		sigmaskVarHandle.set(data, value);
	}

	public static void setSigmaskSz(MemorySegment data, int value) {
		sigmaskSzVarHandle.set(data, value);
	}

	public static void setTs(MemorySegment data, long value) {
		tsVarHandle.set(data, value);
	}

}
