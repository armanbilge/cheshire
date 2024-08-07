package cheshire.playground;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

public class ForeignCallsPlayground {
	public static void main(String[] args) {
		Linker nativeLinker = Linker.nativeLinker();
		SymbolLookup stdlibLookup = nativeLinker.defaultLookup();
		SymbolLookup loaderLookup = SymbolLookup.loaderLookup();

		FunctionDescriptor printfDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS);

		String symbolName = "printf";
		String greeting = "Hello World from Project Panama Baeldung Article \n";
		MethodHandle methodHandle = loaderLookup.find(symbolName)
				.or(() -> stdlibLookup.find(symbolName))
				.map(symbolSegment -> nativeLinker.downcallHandle(symbolSegment, printfDescriptor))
				.orElse(null);

		if (methodHandle == null) {
			System.err.println("Failed to find the symbol: " + symbolName);
			return;
		}

		try (Arena memorySession = Arena.ofConfined()) {
			byte[] greetingBytes = greeting.getBytes(StandardCharsets.UTF_8);
			MemorySegment greetingSegment = memorySession.allocate(greetingBytes.length + 1); // +1 for null terminator
			greetingSegment.copyFrom(MemorySegment.ofArray(greetingBytes));
			greetingSegment.set(ValueLayout.JAVA_BYTE, greetingBytes.length, (byte) 0); // null terminator
			methodHandle.invoke(greetingSegment);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}