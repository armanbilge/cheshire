package cheshire;

import java.lang.foreign.MemorySegment;

public class utils {
	/*
	 * Assuming
	 * "segment == MemorySegment.NULL" (Java code)
	 * is equals to
	 * "ptr == null" or "!ptr" (C code)
	 */
	public static boolean areSegmentsEquals(MemorySegment s1, MemorySegment s2) {
		return (s1.address() == s2.address()) && (s1.byteSize() == s2.byteSize());
	}
}