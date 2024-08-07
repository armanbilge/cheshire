package cheshire;

import java.lang.foreign.MemorySegment;

class register {

	public static int do_register(MemorySegment ring, int opcode, MemorySegment arg, int nr_args) {
		int fd;
		if ((io_uring.getIntFlags(ring) & constants.INT_FLAG_REG_REG_RING) != 0) {
			opcode = opcode | constants.IORING_REGISTER_USE_REGISTERED_RING;
			fd = io_uring.getEnterRingFd(ring);
		} else {
			fd = io_uring.getRingFd(ring);
		}
		return syscall.__sys_io_uring_register(fd, opcode, arg, nr_args);
	};

	public static int io_uring_unregister_ring_fd(MemorySegment ring, MemorySegment up) {
		io_uring_rsrc_update.setOffset(up, io_uring.getEnterRingFd(ring));
		byte intFlags = io_uring.getIntFlags(ring);
		if ((intFlags & constants.INT_FLAG_REG_RING) == 0) {
			return -constants.EINVAL;
		}
		int ret = do_register(ring, constants.IORING_UNREGISTER_RING_FDS, up, 1);
		if (ret == 1) {
			io_uring.setEnterRingFd(ring, io_uring.getRingFd(ring));
			io_uring.setIntFlags(ring,
					(byte) (intFlags & ~(constants.INT_FLAG_REG_RING | constants.INT_FLAG_REG_REG_RING)));
		}
		return ret;
	};

};
