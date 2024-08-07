package cheshire;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public class __kernel_timespec {

	public MemorySegment segment;

	public __kernel_timespec(Arena session) {
		this.segment = session.allocate(layout);
	};

	public __kernel_timespec(MemorySegment s) {
		this.segment = s;
	};

	public __kernel_timespec() {
		try (Arena session = Arena.ofShared()) {
			this.segment = session.allocate(layout);
		} catch (Throwable cause) {
			throw new RuntimeException(cause);
		}
	};

	public static final GroupLayout layout = MemoryLayout.structLayout(
			ValueLayout.JAVA_LONG.withName("tv_sec"),
			ValueLayout.JAVA_LONG.withName("tv_nsec"))
			.withName("__kernel_timespec");

	private static VarHandle tvSecVarHandle = layout.varHandle(PathElement.groupElement("tv_sec"));
	private static VarHandle tvNsecVarHandle = layout.varHandle(PathElement.groupElement("tv_nsec"));

	public static long getTvSec(MemorySegment data) {
		return (long) tvSecVarHandle.get(data);
	};

	public static void setTvSec(MemorySegment data, long value) {
		tvSecVarHandle.set(data, value);
	};

	public static long getTvNsec(MemorySegment data) {
		return (long) tvNsecVarHandle.get(data);
	};

	public static void setTvNsec(MemorySegment data, long value) {
		tvNsecVarHandle.set(data, value);
	};

};
