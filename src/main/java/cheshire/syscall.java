package cheshire;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

class syscall {
	private static Linker nativeLinker = Linker.nativeLinker();
	private static SymbolLookup stdlibLookup = nativeLinker.defaultLookup();
	private static SymbolLookup loaderLookup = SymbolLookup.loaderLookup();

	public static int __sys_io_uring_register(int fd, int opcode, MemorySegment arg, int nr_args) {
		FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
				ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT);
		String symbolName = "syscall";
		MethodHandle methodHandle = loaderLookup.find(symbolName)
				.or(() -> stdlibLookup.find(symbolName))
				.map(symbolSegment -> nativeLinker.downcallHandle(symbolSegment, descriptor))
				.orElse(null);
		if (methodHandle == null) {
			System.err.println("Failed to find the symbol: " + symbolName);
			return -1; // TODO: return -errno;
		}
		try {
			int ret = (int) methodHandle.invokeExact(constants.__NR_io_uring_register, fd, opcode, arg.address(), nr_args);
			if (ret < 0) {
				throw new RuntimeException("io_uring_register syscall failed");
			}
			return ret;
		} catch (Throwable e) {
			e.printStackTrace();
			return -1; // TODO: return -errno;
		}
	}

	public static int __sys_io_uring_setup(int entries, MemorySegment p) {
		FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
				ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
		String symbolName = "syscall";
		MethodHandle methodHandle = loaderLookup.find(symbolName)
				.or(() -> stdlibLookup.find(symbolName))
				.map(symbolSegment -> nativeLinker.downcallHandle(symbolSegment, descriptor))
				.orElse(null);
		if (methodHandle == null) {
			System.err.println("Failed to find the symbol: " + symbolName);
			return -1; // TODO: return -errno;
		}
		try {
			int ret = (int) methodHandle.invokeExact(constants.__NR_io_uring_setup, entries, p.address());
			if (ret < 0) {
				throw new RuntimeException("io_uring_setup syscall failed");
			}
			return ret;
		} catch (Throwable e) {
			return e.hashCode(); // TODO: return -errno;
		}
	}

	public static int __sys_io_uring_enter2(int fd, int to_submit, int min_complete, int flags, MemorySegment sig,
			long sz) {
		FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
				ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG);
		String symbolName = "syscall";
		MethodHandle methodHandle = loaderLookup.find(symbolName)
				.or(() -> stdlibLookup.find(symbolName))
				.map(symbolSegment -> nativeLinker.downcallHandle(symbolSegment, descriptor))
				.orElse(null);
		if (methodHandle == null) {
			System.err.println("Failed to find the symbol: " + symbolName);
			return -1; // TODO: return -errno;
		}
		try {
			int ret = (int) methodHandle.invokeExact(constants.__NR_io_uring_enter, fd, to_submit, min_complete, flags, sig,
					sz);
			if (ret < 0) {
				throw new RuntimeException("close syscall failed");
			}
			return ret;
		} catch (Throwable e) {
			return e.hashCode(); // TODO: return -errno;
		}
	}

	public static int __sys_io_uring_enter(int fd, int to_submit, int min_complete, int flags, MemorySegment sig) {
		return __sys_io_uring_enter2(fd, to_submit, min_complete, flags, sig, constants._NSIG / 8);
	}

	public static int __sys_close(int fd) {
		FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
				ValueLayout.JAVA_INT);
		String symbolName = "close";
		MethodHandle methodHandle = loaderLookup.find(symbolName)
				.or(() -> stdlibLookup.find(symbolName))
				.map(symbolSegment -> nativeLinker.downcallHandle(symbolSegment, descriptor))
				.orElse(null);
		if (methodHandle == null) {
			System.err.println("Failed to find the symbol: " + symbolName);
			return -1; // TODO: return -errno;
		}
		try {
			int ret = (int) methodHandle.invokeExact(constants.__NR_io_uring_setup, fd);
			if (ret < 0) {
				throw new RuntimeException("close syscall failed");
			}
			return ret;
		} catch (Throwable e) {
			return e.hashCode();
			// TODO: return -errno;
		}
	}

	public static int __sys_munmap(long addr, long length) {
		FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
				ValueLayout.JAVA_LONG);
		String symbolName = "munmap";
		MethodHandle methodHandle = loaderLookup.find(symbolName)
				.or(() -> stdlibLookup.find(symbolName))
				.map(symbolSegment -> nativeLinker.downcallHandle(symbolSegment, descriptor))
				.orElse(null);
		if (methodHandle == null) {
			System.err.println("Failed to find the symbol: " + symbolName);
			return -1; // TODO: return -errno;
		}
		try {
			int ret = (int) methodHandle.invokeExact(addr, length);
			if (ret < 0) {
				throw new RuntimeException("munmap syscall failed");
			}
			return ret;
		} catch (Throwable e) {
			return e.hashCode(); // TODO: return -errno;
		}
	};

	public static MemorySegment __sys_mmap(MemorySegment addr, long length, int prot, int flags, int fd, long offset) {
		FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS,
				ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG);
		String symbolName = "mmap";
		MethodHandle methodHandle = loaderLookup.find(symbolName)
				.or(() -> stdlibLookup.find(symbolName))
				.map(symbolSegment -> nativeLinker.downcallHandle(symbolSegment, descriptor))
				.orElse(null);
		if (methodHandle == null) {
			System.err.println("Failed to find the symbol: " + symbolName);
			return MemorySegment.NULL;// TODO
		}
		try {
			MemorySegment ret = (MemorySegment) methodHandle.invokeExact(addr.address(), length, prot, flags, fd, offset);
			if (ret.address() == constants.MAP_FAILED) {
				throw new RuntimeException("mmap syscall failed");
			}
			return addr;
		} catch (Throwable e) {
			return MemorySegment.NULL;// TODO
		}
	};
}
