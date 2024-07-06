package cheshire;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public class get_data {
	MemorySegment segment;

	public get_data(Arena session) {
		this.segment = session.allocate(layout);
	}

	private static final GroupLayout layout = MemoryLayout.structLayout(
			ValueLayout.JAVA_INT.withName("submit"),
			ValueLayout.JAVA_INT.withName("wait_nr"),
			ValueLayout.JAVA_INT.withName("get_flags"),
			ValueLayout.JAVA_LONG.withName("sz"),
			ValueLayout.JAVA_BOOLEAN.withName("has_ts"),
			ValueLayout.ADDRESS.withName("arg")).withName("get_data");

	private static VarHandle submitVarHandle = layout.varHandle(PathElement.groupElement("submit"));
	private static VarHandle waitNrVarHandle = layout.varHandle(PathElement.groupElement("wait_nr"));
	private static VarHandle getFlagsVarHandle = layout.varHandle(PathElement.groupElement("get_flags"));
	private static VarHandle szVarHandle = layout.varHandle(PathElement.groupElement("sz"));
	private static VarHandle hasTsVarHandle = layout.varHandle(PathElement.groupElement("has_ts"));
	private static VarHandle argVarHandle = layout.varHandle(PathElement.groupElement("arg"));

	public static int getSubmit(MemorySegment data) {
		return (int) submitVarHandle.get(data);
	}

	public static int getWaitNr(MemorySegment data) {
		return (int) waitNrVarHandle.get(data);
	}

	public static int getGetFlags(MemorySegment data) {
		return (int) getFlagsVarHandle.get(data);
	}

	public static long getSz(MemorySegment data) {
		return (long) szVarHandle.get(data);
	}

	public static boolean getHasTs(MemorySegment data) {
		return (boolean) hasTsVarHandle.get(data);
	}

	public static long getArg(MemorySegment data) {
		return (long) argVarHandle.get(data);
	}

	public static void setSubmit(MemorySegment data, int value) {
		submitVarHandle.set(data, value);
	}

	public static void setWaitNr(MemorySegment data, int value) {
		waitNrVarHandle.set(data, value);
	}

	public static void setGetFlags(MemorySegment data, int value) {
		getFlagsVarHandle.set(data, value);
	}

	public static void setSz(MemorySegment data, long value) {
		szVarHandle.set(data, value);
	}

	public static void setHasTs(MemorySegment data, boolean value) {
		hasTsVarHandle.set(data, value);
	}

	public static void setArg(MemorySegment data, long value) {
		argVarHandle.set(data, value);
	}

}
