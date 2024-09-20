package cheshire.playground;

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

		GroupLayout lineLayout = MemoryLayout.structLayout(
				pointLayout.withName("start"),
				pointLayout.withName("end"));

		GroupLayout addrTestLayout = MemoryLayout.structLayout(
				ValueLayout.ADDRESS.withName("point"));

		VarHandle xvarHandle = pointLayout.varHandle(PathElement.groupElement("x"));
		VarHandle yvarHandle = pointLayout.varHandle(PathElement.groupElement("y"));

		VarHandle addrPointvarHandle = addrTestLayout.varHandle(PathElement.groupElement("point"));

		try (Arena memorySession = Arena.ofConfined()) {
			// Set values
			MemorySegment pointSegment = memorySession.allocate(pointLayout);
			MemorySegment addrTest = memorySession.allocate(addrTestLayout);

			xvarHandle.set(pointSegment, 0, 3d);
			yvarHandle.set(pointSegment, 0, 4d);
			System.out.println("Point 1 - " + pointSegment.toString());
			System.out.println("Point 1x - " + xvarHandle.get(pointSegment, 0));

			MemorySegment lineSegment = memorySession.allocate(lineLayout);
			MemorySegment start = lineSegment.asSlice(lineLayout.byteOffset(PathElement.groupElement("start")), pointLayout);
			MemorySegment end = lineSegment.asSlice(lineLayout.byteOffset(PathElement.groupElement("end")), pointLayout);

			xvarHandle.set(start, 0, 1d);
			yvarHandle.set(start, 0, 2d);
			xvarHandle.set(end, 0, 5d);
			yvarHandle.set(end, 0, 6d);

			addrPointvarHandle.set(addrTest, 0, pointSegment);

			System.out.println("Line - " + lineSegment.toString());

			// Get segments
			long addr = pointSegment.address();
			MemorySegment pointSegment2 = MemorySegment.ofAddress(addr).reinterpret(pointLayout.byteSize()); // need
																																																				// reinterpret
			System.out.println("Point 2 - " + pointSegment2.toString());
			System.out.println("Point 2x - " + xvarHandle.get(pointSegment2, 0));

			MemorySegment pointSegment3 = pointSegment2;
			System.out.println("Point 3 - " + pointSegment3.toString());
			System.out.println("Point 3x - " + xvarHandle.get(pointSegment3, 0));

			MemorySegment endPoint = lineSegment.asSlice(lineLayout.byteOffset(PathElement.groupElement("end")), pointLayout);
			System.out.println("Line end point - " + endPoint.toString());
			System.out.println("Line end point X - " + xvarHandle.get(endPoint, 0));

			// MemorySegment pointAddr = (MemorySegment) addrPointvarHandle.get(addrTest);
			MemorySegment pointAddr = ((MemorySegment) addrPointvarHandle.get(addrTest, 0))
					.reinterpret(pointLayout.byteSize());
			System.out.println("Point Addr - " + pointAddr.toString());
			System.out.println("Point Addrx - " + xvarHandle.get(pointAddr, 0));

			// Comparing segments
			System.out.println("Point 1 == Point 2 - " + (pointSegment == pointSegment2)); // false
			System.out.println("Point 2 == Point 3 - " + (pointSegment2 == pointSegment3)); // true
			System.out.println("Point 1 == Point 3 - " + (pointSegment == pointSegment3)); // false

			System.out.println("Address 1 == Address 2 - " + (pointSegment.address() == pointSegment2.address())); // true
			System.out.println("Address 2 == Address 3 - " + (pointSegment2.address() == pointSegment3.address())); // true
			System.out.println("Address 1 == Address 3 - " + (pointSegment.address() == pointSegment3.address())); // true

			System.out.println("null == MemorySegment.NULL - " + (null == MemorySegment.NULL)); // false

			// Reassigning segments
			System.out.println("Start point X (original) - " + xvarHandle.get(start, 0));
			start = pointSegment;
			System.out.println("Start point X = Point 1x - " + xvarHandle.get(start, 0));
		}
	}
}
