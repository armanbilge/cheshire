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

	private static MethodHandle getMethod(String symbolName, FunctionDescriptor descriptor) {
		MethodHandle methodHandle = loaderLookup.find(symbolName)
				.or(() -> stdlibLookup.find(symbolName))
				.map(symbolSegment -> nativeLinker.downcallHandle(symbolSegment, descriptor))
				.orElse(null);
		if (methodHandle == null) {
			throw new RuntimeException("Failed to find the symbol: " + symbolName);
		}
		return methodHandle;
	};

	private static MethodHandle syscall3() {
		FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
				ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
		return getMethod("syscall", descriptor);
	};

	private static MethodHandle syscall5() {
		FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
				ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT);
		return getMethod("syscall", descriptor);
	};

	private static MethodHandle syscall7() {
		FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
				ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
				ValueLayout.JAVA_LONG);
		return getMethod("syscall", descriptor);
	};

	private static MethodHandle close() {
		FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
				ValueLayout.JAVA_INT);
		return getMethod("close", descriptor);
	};

	private static MethodHandle munmap() {
		FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
				ValueLayout.JAVA_LONG);
		return getMethod("munmap", descriptor);
	};

	private static MethodHandle mmap() {
		FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS,
				ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG);
		return getMethod("mmap", descriptor);
	};

	public static int __sys_io_uring_register(int fd, int opcode, MemorySegment arg, int nrArgs) {
		try {
			int ret = (int) syscall5().invokeExact(constants.__NR_io_uring_register, fd, opcode, arg, nrArgs);
			if (ret < 0) {
				throw new RuntimeException("io_uring_register syscall failed");
			}
			return ret;
		} catch (Throwable cause) {
			throw new RuntimeException(cause);
		}
	};

	public static int __sys_io_uring_setup(int entries, MemorySegment p) {
		try {
			int ret = (int) syscall3().invokeExact(constants.__NR_io_uring_setup, entries, p);
			if (ret < 0) {
				throw new RuntimeException("io_uring_setup syscall failed");
			}
			return ret;
		} catch (Throwable cause) {
			throw new RuntimeException(cause);
		}
	};

	public static int __sys_io_uring_enter2(int fd, int toSubmit, int minComplete, int flags, MemorySegment sig,
			long sz) {
		try {
			int ret = (int) syscall7().invokeExact(constants.__NR_io_uring_enter, fd, toSubmit, minComplete, flags, sig,
					sz);
			if (ret < 0) {
				throw new RuntimeException("io_uring_enter2 syscall failed");
			}
			return ret;
		} catch (Throwable cause) {
			throw new RuntimeException(cause);
		}
	};

	public static int __sys_io_uring_enter(int fd, int toSubmit, int minComplete, int flags, MemorySegment sig) {
		return __sys_io_uring_enter2(fd, toSubmit, minComplete, flags, sig, constants._NSIG / 8);
	};

	public static int __sys_close(int fd) {
		try {
			int ret = (int) close().invokeExact(constants.__NR_io_uring_setup, fd);
			if (ret < 0) {
				throw new RuntimeException("close syscall failed");
			}
			return ret;
		} catch (Throwable cause) {
			throw new RuntimeException(cause);
		}
	};

	public static int __sys_munmap(long addr, long length) {
		try {
			int ret = (int) munmap().invokeExact(addr, length);
			if (ret < 0) {
				throw new RuntimeException("munmap syscall failed");
			}
			return ret;
		} catch (Throwable cause) {
			throw new RuntimeException(cause);
		}
	};

	public static MemorySegment __sys_mmap(MemorySegment addr, long length, int prot, int flags, int fd, long offset) {
		try {
			MemorySegment ret = (MemorySegment) mmap().invokeExact(addr, length, prot, flags, fd, offset);
			if (ret.address() == constants.MAP_FAILED) {
				throw new RuntimeException("mmap syscall failed");
			}
			return addr;
		} catch (Throwable cause) {
			throw new RuntimeException(cause);
		}
	};

};
