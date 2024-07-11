package cheshire;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.VarHandle;

public class MemoryPlayground {
	public static void main(String[] args) {
		GroupLayout pointLayout = MemoryLayout.structLayout(
				ValueLayout.JAVA_DOUBLE.withName("x"),
				ValueLayout.JAVA_DOUBLE.withName("y"));

		VarHandle xvarHandle = pointLayout.varHandle(PathElement.groupElement("x"));
		VarHandle yvarHandle = pointLayout.varHandle(PathElement.groupElement("y"));

		try (Arena memorySession = Arena.ofConfined()) {
			// Set values
			MemorySegment pointSegment = memorySession.allocate(pointLayout);
			xvarHandle.set(pointSegment, 3d);
			yvarHandle.set(pointSegment, 4d);
			System.out.println("Point 1 - " + pointSegment.toString());
			System.out.println("Point 1x - " + xvarHandle.get(pointSegment));

			// Get segments
			long addr = pointSegment.address();
			MemorySegment pointSegment2 = MemorySegment.ofAddress(addr).reinterpret(pointLayout.byteSize()); // need
																																																				// reinterpret
			System.out.println("Point 2 - " + pointSegment2.toString());
			System.out.println("Point 2x - " + xvarHandle.get(pointSegment2));

			MemorySegment pointSegment3 = pointSegment2;
			System.out.println("Point 3 - " + pointSegment3.toString());
			System.out.println("Point 3x - " + xvarHandle.get(pointSegment3));

			// Comparing segments
			System.out.println("Point 1 == Point 2 - " + (pointSegment == pointSegment2)); // false
			System.out.println("Point 2 == Point 3 - " + (pointSegment2 == pointSegment3)); // true
			System.out.println("Point 1 == Point 3 - " + (pointSegment == pointSegment3)); // false

			System.out.println("Address 1 == Address 2 - " + (pointSegment.address() == pointSegment2.address())); // true
			System.out.println("Address 2 == Address 3 - " + (pointSegment2.address() == pointSegment3.address())); // true
			System.out.println("Address 1 == Address 3 - " + (pointSegment.address() == pointSegment3.address())); // true

			System.out.println("null == MemorySegment.NULL - " + (null == MemorySegment.NULL)); // false

		}
	}
}
