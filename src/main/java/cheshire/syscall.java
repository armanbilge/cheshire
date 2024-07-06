package cheshire;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

class syscall {
	private static Linker linker = Linker.nativeLinker();

	public static int __sys_io_uring_register(int fd, int opcode, MemorySegment arg, int nr_args) {
		try {
			MethodHandle syscallHandle = linker.downcallHandle(
					linker.defaultLookup().find("syscall").get(),
					FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
							ValueLayout.JAVA_INT));
			int ret = (int) syscallHandle.invokeExact(constants.__NR_io_uring_register, fd, opcode, arg.address(), nr_args);
			if (ret < 0) {
				throw new RuntimeException("io_uring_register syscall failed");
			}
			return ret;
		} catch (Throwable e) {
			return e.hashCode();
			// TODO: return -errno;
		}
	}

	public static int __sys_io_uring_setup(int entries, MemorySegment p) {
		try {
			MethodHandle syscallHandle = linker.downcallHandle(
					linker.defaultLookup().find("syscall").get(),
					FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS));
			int ret = (int) syscallHandle.invokeExact(constants.__NR_io_uring_setup, entries, p.address());
			if (ret < 0) {
				throw new RuntimeException("io_uring_setup syscall failed");
			}
			return ret;
		} catch (Throwable e) {
			return e.hashCode();
			// TODO: return -errno;
		}
	}

	public static int __sys_io_uring_enter2(int fd, int to_submit, int min_complete, int flags, MemorySegment sig,
			long sz) {
		try {
			MethodHandle syscallHandle = linker.downcallHandle(
					linker.defaultLookup().find("syscall").orElseThrow(),
					FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
							ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG));
			int ret = (int) syscallHandle.invokeExact(constants.__NR_io_uring_enter, fd, to_submit, min_complete, flags, sig,
					sz);
			if (ret < 0) {
				throw new RuntimeException("close syscall failed");
			}
			return ret;
		} catch (Throwable e) {
			return e.hashCode();
			// TODO: return -errno;
		}
	}

	public static int __sys_io_uring_enter(int fd, int to_submit, int min_complete, int flags, MemorySegment sig) {
		return __sys_io_uring_enter2(fd, to_submit, min_complete, flags, sig, constants._NSIG / 8);
	}

	public static int __sys_close(int fd) {
		try {
			MethodHandle syscallHandle = linker.downcallHandle(
					linker.defaultLookup().find("close").orElseThrow(),
					FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
			int ret = (int) syscallHandle.invokeExact(constants.__NR_io_uring_setup, fd);
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
		try {
			MethodHandle syscallHandle = linker.downcallHandle(
					linker.defaultLookup().find("munmap").orElseThrow(),
					FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG));
			int ret = (int) syscallHandle.invokeExact(addr, length);
			if (ret < 0) {
				throw new RuntimeException("munmap syscall failed");
			}
			return ret;
		} catch (Throwable e) {
			return e.hashCode();
			// TODO: return -errno;
		}
	};

	public static MemorySegment __sys_mmap(MemorySegment addr, long length, int prot, int flags, int fd, long offset) {
		try {
			// TODO
			MethodHandle syscallHandle = linker.downcallHandle(
					linker.defaultLookup().find("mmap").orElseThrow(),
					FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT,
							ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG));
			MemorySegment ret = (MemorySegment) syscallHandle.invokeExact(addr.address(), length, prot, flags, fd, offset);
			if (ret.address() == constants.MAP_FAILED) {
				throw new RuntimeException("mmap syscall failed");
			}
			return addr;
		} catch (Throwable e) {
			// TODO
			return MemorySegment.NULL;
		}
	};
}
