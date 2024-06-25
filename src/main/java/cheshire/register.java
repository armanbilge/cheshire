package cheshire;

import java.lang.foreign.MemorySegment;

class register {
	public static int do_register(MemorySegment ring, int opcode, MemorySegment arg, int nr_args) {
		int fd;
		char int_flags = (char) io_uring.intFlagsVarHandle.get(ring);

		if ((int_flags & constants.INT_FLAG_REG_REG_RING) != 0) {
			opcode = opcode | constants.IORING_REGISTER_USE_REGISTERED_RING;
			fd = (int) io_uring.enterRingFdVarHandle.get(ring);
		} else {
			fd = (int) io_uring.ringFdVarHandle.get(ring);
		}

		return syscall.__sys_io_uring_register(fd, opcode, arg, nr_args);
	}

	public static int io_uring_unregister_ring_fd(MemorySegment ring, MemorySegment up) {
		io_uring_rsrc_update.offsetVarHandle.set(up, io_uring.enterRingFdVarHandle.get(ring));

		char int_flags = (char) io_uring.intFlagsVarHandle.get(ring);
		if ((int_flags & constants.INT_FLAG_REG_RING) == 0) {
			return -constants.EINVAL;
		}

		int ret = do_register(ring, constants.IORING_UNREGISTER_RING_FDS, up, 1);
		if (ret == 1) {
			io_uring.enterRingFdVarHandle.set(ring, io_uring.ringFdVarHandle.get(ring));
			io_uring.intFlagsVarHandle.set(ring,
					int_flags & ~(constants.INT_FLAG_REG_RING | constants.INT_FLAG_REG_REG_RING));
		}
		return ret;
	}
}
