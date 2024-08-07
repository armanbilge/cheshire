package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public class get_data {

	public static final GroupLayout layout = MemoryLayout.structLayout(
			ValueLayout.JAVA_INT.withName("submit"),
			ValueLayout.JAVA_INT.withName("wait_nr"),
			ValueLayout.JAVA_INT.withName("get_flags"),
			ValueLayout.JAVA_INT.withName("sz"),
			ValueLayout.JAVA_INT.withName("has_ts"),
			MemoryLayout.paddingLayout(4),
			ValueLayout.ADDRESS.withName("arg"))
			.withName("get_data");

	private static VarHandle submitVarHandle = layout.varHandle(PathElement.groupElement("submit"));
	private static VarHandle waitNrVarHandle = layout.varHandle(PathElement.groupElement("wait_nr"));
	private static VarHandle getFlagsVarHandle = layout.varHandle(PathElement.groupElement("get_flags"));
	private static VarHandle szVarHandle = layout.varHandle(PathElement.groupElement("sz"));
	private static VarHandle hasTsVarHandle = layout.varHandle(PathElement.groupElement("has_ts"));
	private static VarHandle argVarHandle = layout.varHandle(PathElement.groupElement("arg"));

	public static int getSubmit(MemorySegment data) {
		return (int) submitVarHandle.get(data);
	};

	public static void setSubmit(MemorySegment data, int value) {
		submitVarHandle.set(data, value);
	};

	public static int getWaitNr(MemorySegment data) {
		return (int) waitNrVarHandle.get(data);
	};

	public static void setWaitNr(MemorySegment data, int value) {
		waitNrVarHandle.set(data, value);
	};

	public static int getGetFlags(MemorySegment data) {
		return (int) getFlagsVarHandle.get(data);
	};

	public static void setGetFlags(MemorySegment data, int value) {
		getFlagsVarHandle.set(data, value);
	};

	public static int getSz(MemorySegment data) {
		return (int) szVarHandle.get(data);
	};

	public static void setSz(MemorySegment data, int value) {
		szVarHandle.set(data, value);
	};

	public static int getHasTs(MemorySegment data) {
		return (int) hasTsVarHandle.get(data);
	};

	public static void setHasTs(MemorySegment data, int value) {
		hasTsVarHandle.set(data, value);
	};

	public static MemorySegment getArg(MemorySegment data) {
		return (MemorySegment) argVarHandle.get(data);
	};

	public static void setArg(MemorySegment data, MemorySegment value) {
		argVarHandle.set(data, value);
	};

};
