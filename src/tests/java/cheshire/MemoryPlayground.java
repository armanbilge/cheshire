package cheshire;

public class MemoryPlayground {
	public static void main(String[] args) {
		GroupLayout pointLayout = structLayout(
				JAVA_DOUBLE.withName("x"),
				JAVA_DOUBLE.withName("y"));

		VarHandle xvarHandle = pointLayout.varHandle(PathElement.groupElement("x"));
		VarHandle yvarHandle = pointLayout.varHandle(PathElement.groupElement("y"));

		try (Arena memorySession = Arena.ofConfined()) {
			MemorySegment pointSegment = memorySession.allocate(pointLayout);
			xvarHandle.set(pointSegment, 3d);
			yvarHandle.set(pointSegment, 4d);
			System.out.println(pointSegment.toString());
		}
	}
}
