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
			MemorySegment pointSegment = memorySession.allocate(pointLayout);
			xvarHandle.set(pointSegment, 3d);
			yvarHandle.set(pointSegment, 4d);
			System.out.println("Point 1 - " + pointSegment.toString());
			System.out.println("Point 1x - " + xvarHandle.get(pointSegment));

			long addr = pointSegment.address();
			MemorySegment pointSegment2 = MemorySegment.ofAddress(addr).reinterpret(pointLayout.byteSize());
			System.out.println("Point 2 - " + pointSegment2.toString());
			System.out.println("Point 2x - " + xvarHandle.get(pointSegment2));

			MemorySegment pointSegment3 = pointSegment2;
			System.out.println("Point 3 - " + pointSegment3.toString());
			System.out.println("Point 3x - " + xvarHandle.get(pointSegment3));
		}
	}
}
