package cheshire;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

class setup {

	private static int PTR_ERR(MemorySegment ptr) {
		return (int) ptr.address();
	};

	private static boolean IS_ERR(MemorySegment ptr) {
		return ptr.address() >= -4095L;
	};

	private static void io_uring_setup_ring_pointers(MemorySegment p, MemorySegment sq, MemorySegment cq) {
		long sqRingPtr = io_uring_sq.getRingPtr(sq);
		long cqRingPtr = io_uring_cq.getRingPtr(sq);
		MemorySegment sqOff = io_uring_params.getSqOffSegment(p);
		MemorySegment cqOff = io_uring_params.getCqOffSegment(p);

		io_uring_sq.setKhead(sq, sqRingPtr + io_sqring_offsets.getHead(sqOff));
		io_uring_sq.setKtail(sq, sqRingPtr + io_sqring_offsets.getTail(sqOff));
		io_uring_sq.setKringMask(sq, sqRingPtr + io_sqring_offsets.getRingMask(sqOff));
		io_uring_sq.setKringEntries(sq, sqRingPtr + io_sqring_offsets.getRingEntries(sqOff));
		io_uring_sq.setKflags(sq, sqRingPtr + io_sqring_offsets.getFlags(sqOff));
		io_uring_sq.setKdropped(sq, sqRingPtr + io_sqring_offsets.getDropped(sqOff));
		if ((io_uring_params.getFlags(p) & constants.IORING_SETUP_NO_SQARRAY) == 0) {
			io_uring_sq.setArray(sq, sqRingPtr + io_sqring_offsets.getArray(sqOff));
		}

		io_uring_cq.setKhead(cq, cqRingPtr + io_cqring_offsets.getHead(cqOff));
		io_uring_cq.setKtail(cq, cqRingPtr + io_cqring_offsets.getTail(cqOff));
		io_uring_cq.setKringMask(cq, cqRingPtr + io_cqring_offsets.getRingMask(cqOff));
		io_uring_cq.setKringEntries(cq, cqRingPtr + io_cqring_offsets.getRingEntries(cqOff));
		io_uring_cq.setKoverflow(cq, cqRingPtr + io_cqring_offsets.getOverflow(cqOff));
		io_uring_cq.setCqes(cq, cqRingPtr + io_cqring_offsets.getCqes(cqOff));
		if (io_cqring_offsets.getFlags(p) != 0) {
			io_uring_cq.setKflags(cq, cqRingPtr + io_cqring_offsets.getFlags(cqOff));
		}

		io_uring_sq.setRingMask(sq, utils.getIntFromSegment(io_uring_sq.getKringMaskSegment(sq)));
		io_uring_sq.setRingEntries(sq, utils.getIntFromSegment(io_uring_sq.getKringEntriesSegment(sq)));
		io_uring_cq.setRingMask(cq, utils.getIntFromSegment(io_uring_cq.getKringMaskSegment(cq)));
		io_uring_cq.setRingEntries(cq, utils.getIntFromSegment(io_uring_cq.getKringEntriesSegment(cq)));
	};

	private static void io_uring_unmap_rings(MemorySegment sq, MemorySegment cq) {
		long sqRingSz = io_uring_sq.getRingSz(sq);
		long sqRingPtr = io_uring_sq.getRingPtr(sq);

		long cqRingSz = io_uring_cq.getRingSz(cq);
		long cqRingPtr = io_uring_cq.getRingPtr(cq);

		if (sqRingSz != 0)
			syscall.__sys_munmap(sqRingPtr, sqRingSz);
		if (cqRingPtr != 0 && cqRingSz != 0 && cqRingPtr != sqRingPtr)
			syscall.__sys_munmap(cqRingPtr, cqRingSz);
	};

	private static int io_uring_mmap(int fd, MemorySegment p, MemorySegment sq, MemorySegment cq) {
		long size;
		int ret;

		size = io_uring_cqe.layout.byteSize();
		int flags = io_uring_params.getFlags(p);
		if ((flags & constants.IORING_SETUP_CQE32) != 0) {
			size += io_uring_cqe.layout.byteSize();
		}

		MemorySegment sqOff = io_uring_params.getSqOffSegment(p);
		MemorySegment cqOff = io_uring_params.getCqOffSegment(p);

		io_uring_sq.setRingSz(sq, io_sqring_offsets.getArray(sqOff)
				+ io_uring_params.getSqEntries(p) * Integer.BYTES);
		io_uring_cq.setRingSz(cq, io_cqring_offsets.getCqes(cqOff)
				+ io_uring_params.getCqEntries(p) * size);

		int features = io_uring_params.getFeatures(p);
		if ((features & constants.IORING_FEAT_SINGLE_MMAP) != 0) {
			long cqRingSz = io_uring_cq.getRingSz(cq);
			long sqRingSz = io_uring_sq.getRingSz(sq);
			if (cqRingSz > sqRingSz) {
				io_uring_sq.setRingSz(sq, cqRingSz);
			}
			io_uring_cq.setRingSz(cq, sqRingSz);
		}
		io_uring_sq.setRingPtr(sq,
				syscall.__sys_mmap(MemorySegment.NULL, io_uring_sq.getRingSz(sq),
						constants.PROT_READ | constants.PROT_WRITE,
						constants.MAP_SHARED | constants.MAP_POPULATE, fd, constants.IORING_OFF_SQ_RING).address());
		MemorySegment sqRingPtr = io_uring_sq.getRingPtrSegment(sq); // only address needed
		if (IS_ERR(sqRingPtr))
			return PTR_ERR(sqRingPtr);

		if ((features & constants.IORING_FEAT_SINGLE_MMAP) != 0) {
			io_uring_cq.setRingPtr(cq, io_uring_sq.getRingPtr(sq));
		} else {
			io_uring_cq.setRingPtr(sq,
					syscall.__sys_mmap(MemorySegment.NULL, io_uring_cq.getRingSz(cq),
							constants.PROT_READ | constants.PROT_WRITE,
							constants.MAP_SHARED | constants.MAP_POPULATE, fd, constants.IORING_OFF_CQ_RING).address());
			MemorySegment cqRingPtr = io_uring_cq.getRingPtrSegment(cq); // only address needed
			if (IS_ERR(cqRingPtr)) {
				ret = PTR_ERR(cqRingPtr);
				io_uring_cq.ringPtrVarHandle.set(cq, MemorySegment.NULL); // Should be NULL
				io_uring_unmap_rings(sq, cq);
				return ret;
			}
		}

		size = io_uring_sqe.layout.byteSize();
		if ((flags & constants.IORING_SETUP_SQE128) != 0) {
			size += 64;
		}
		io_uring_sq.setSqes(sq,
				syscall.__sys_mmap(MemorySegment.NULL, size * io_uring_params.getSqEntries(p),
						constants.PROT_READ | constants.PROT_WRITE, constants.MAP_SHARED | constants.MAP_POPULATE, fd,
						constants.IORING_OFF_SQES).address()); // MemorySegment 0

		MemorySegment sqSqes = io_uring_sq.getSqesSegment(sq); // only address needed
		if (IS_ERR(sqSqes)) {
			ret = PTR_ERR(sqSqes);
			io_uring_unmap_rings(sq, cq);
			return ret;
		}

		io_uring_setup_ring_pointers(p, sq, cq);
		return 0;
	}

	private static int io_uring_queue_mmap(int fd, MemorySegment p, MemorySegment ring) {
		ring.fill((byte) 0);
		MemorySegment sq = io_uring.getSqSegment(ring);
		MemorySegment cq = io_uring.getCqSegment(ring);
		return io_uring_mmap(fd, p, sq, cq);
	};

	private static int fls(int x) {
		if (x == 0) {
			return 0;
		}
		return Integer.SIZE - Integer.numberOfLeadingZeros(x);
	};

	private static int roundup_pow2(int depth) {
		if (depth <= 1) {
			return 1;
		}
		return 1 << fls(depth - 1);
	};

	private static int get_sq_cq_entries(int entries, MemorySegment p, MemorySegment sq, MemorySegment cq) {
		int cqEntries;
		int flags = io_uring_params.getFlags(p);

		if (entries == 0) {
			return -constants.EINVAL;
		}
		if (entries > constants.KERN_MAX_ENTRIES) {
			if ((flags & constants.IORING_SETUP_CLAMP) == 0) {
				return -constants.EINVAL;
			}
			entries = constants.KERN_MAX_ENTRIES;
		}

		entries = roundup_pow2(entries);
		if ((flags & constants.IORING_SETUP_CQSIZE) != 0) {
			int pCqEntries = io_uring_params.getCqEntries(p);
			if (pCqEntries == 0) {
				return -constants.EINVAL;
			}
			cqEntries = pCqEntries;
			if (cqEntries > constants.KERN_MAX_CQ_ENTRIES) {
				if ((flags & constants.IORING_SETUP_CLAMP) == 0) {
					return -constants.EINVAL;
				}
				cqEntries = constants.KERN_MAX_CQ_ENTRIES;
			}
			cqEntries = roundup_pow2(cqEntries);
			if (cqEntries < entries) {
				return -constants.EINVAL;
			}
		} else {
			cqEntries = 2 * entries;
		}

		sq.set(ValueLayout.JAVA_INT, 0L, entries);
		cq.set(ValueLayout.JAVA_INT, 0L, cqEntries);
		return 0;
	};

	/* FIXME */
	private static long hugePageSize = 2 * 1024 * 1024;

	private static long get_page_size() {
		return 4096; // TODO
	};

	private static int io_uring_alloc_huge(int entries, MemorySegment p, MemorySegment sq, MemorySegment cq,
			MemorySegment buf, long bufSize) {
		long pageSize = get_page_size();
		long ringMem, sqesMem = 0;
		long memUsed = 0;
		MemorySegment ptr;

		int ret = get_sq_cq_entries(entries, p, io_uring_params.getSqEntriesSegment(p),
				io_uring_params.getCqEntriesSegment(p));
		if (ret != 0) {
			return ret;
		}

		int sqEntries = io_uring_params.getSqEntries(p);
		int cqEntries = io_uring_params.getCqEntries(p);

		sqesMem = sqEntries * io_uring_sqe.layout.byteSize();
		sqesMem = (sqesMem + pageSize - 1) & ~(pageSize - 1);
		ringMem = cqEntries * io_uring_cqe.layout.byteSize();
		int flags = io_uring_params.getFlags(p);
		if ((flags & constants.IORING_SETUP_CQE32) != 0) {
			ringMem *= 2;
		}
		if ((flags & constants.IORING_SETUP_NO_SQARRAY) == 0) {
			ringMem += sqEntries * Integer.BYTES;
		}
		memUsed = sqesMem + ringMem;
		memUsed = (memUsed + pageSize - 1) & ~(pageSize - 1);

		if (utils.areSegmentsEquals(buf, MemorySegment.NULL) && (sqesMem > hugePageSize || ringMem > hugePageSize)) {
			return -constants.ENOMEM;
		}

		if (!utils.areSegmentsEquals(buf, MemorySegment.NULL)) {
			if (memUsed > bufSize) {
				return -constants.ENOMEM;
			}
			ptr = buf;
		} else {
			int mapHugetlb = 0;
			if (sqesMem <= pageSize) {
				bufSize = pageSize;
			} else {
				bufSize = hugePageSize;
				mapHugetlb = constants.MAP_HUGETLB;
			}
			ptr = syscall.__sys_mmap(MemorySegment.NULL, bufSize, constants.PROT_READ | constants.PROT_WRITE,
					constants.MAP_SHARED | constants.MAP_ANONYMOUS | mapHugetlb, -1, 0);
			if (IS_ERR(ptr)) {
				return PTR_ERR(ptr);
			}
		}

		io_uring_sq.setSqes(sq, ptr.address());
		if (memUsed <= bufSize) {
			io_uring_sq.setRingPtr(sq, io_uring_sq.getSqes(sq) + sqesMem);
			io_uring_cq.setRingSz(cq, 0L);
			io_uring_sq.setRingSz(sq, 0L);
		} else {
			int mapHugetlb = 0;
			if (ringMem <= pageSize) {
				bufSize = pageSize;
			} else {
				bufSize = hugePageSize;
				mapHugetlb = constants.MAP_HUGETLB;
			}
			ptr = syscall.__sys_mmap(MemorySegment.NULL, bufSize, constants.PROT_READ | constants.PROT_WRITE,
					constants.MAP_SHARED | constants.MAP_ANONYMOUS | mapHugetlb, -1, 0);
			if (IS_ERR(ptr)) {
				long sqes = io_uring_sq.getSqes(sq);
				syscall.__sys_munmap(sqes, 1);
				return PTR_ERR(ptr);
			}
			io_uring_sq.setRingPtr(sq, ptr.address());
			io_uring_sq.setRingSz(sq, bufSize);
			io_uring_cq.setRingSz(cq, 0);
		}

		io_uring_cq.setRingPtr(cq, io_uring_sq.getRingPtr(sq));
		MemorySegment sqOff = io_uring_params.getSqOffSegment(p);
		MemorySegment cqOff = io_uring_params.getCqOffSegment(p);
		io_sqring_offsets.setUserAddr(sqOff, io_uring_sq.getSqes(sq));
		io_cqring_offsets.setUserAddr(cqOff, io_uring_sq.getRingPtr(sq));
		return (int) memUsed;
	};

	private static int __io_uring_queue_init_params(int entries, MemorySegment ring, MemorySegment p, MemorySegment buf,
			long bufSize) {
		int fd = 0;
		int ret = 0;

		ring.fill((byte) 0);

		MemorySegment sq = io_uring.getSqSegment(ring);
		MemorySegment cq = io_uring.getCqSegment(ring);
		int flags = io_uring_params.getFlags(p);

		if (((flags & constants.IORING_SETUP_REGISTERED_FD_ONLY) != 0)
				&& ((flags & constants.IORING_SETUP_NO_MMAP) == 0)) {
			return -constants.EINVAL;
		}
		if ((flags & constants.IORING_SETUP_NO_MMAP) != 0) {
			ret = io_uring_alloc_huge(entries, p, sq, cq, buf, bufSize);
			if (ret < 0) {
				return ret;
			}
			if (!utils.areSegmentsEquals(buf, MemorySegment.NULL)) {
				io_uring.setIntFlags(ring, (char) (io_uring.getIntFlags(ring) | constants.INT_FLAG_APP_MEM));
			}
		}

		fd = syscall.__sys_io_uring_setup(entries, p);
		if (fd < 0) {
			if (((flags & constants.IORING_SETUP_NO_MMAP) != 0)
					&& ((io_uring.getIntFlags(ring) & constants.INT_FLAG_APP_MEM) == 0)) {
				syscall.__sys_munmap(io_uring_sq.getSqes(sq), 1);
				io_uring_unmap_rings(sq, cq);
			}
			return fd;
		}

		if (((flags & constants.IORING_SETUP_NO_MMAP) == 0)) {
			ret = io_uring_queue_mmap(fd, p, ring);
			if (ret != 0) {
				syscall.__sys_close(fd);
				return ret;
			}
		} else {
			io_uring_setup_ring_pointers(p, sq, cq);
		}

		if ((flags & constants.IORING_SETUP_NO_SQARRAY) == 0) {
			int sqEntries = io_uring_sq.getRingEntries(sq);
			MemorySegment sqArray = io_uring_sq.getArraySegment(sq)
					.reinterpret(sqEntries * ValueLayout.JAVA_INT.byteSize()); // Enough?

			for (int index = 0; index < sqEntries; index++) {
				sqArray.setAtIndex(ValueLayout.JAVA_INT, index, index);
			}
		}

		io_uring.setFeatures(ring, io_uring_params.getFeatures(p));
		io_uring.setFlags(ring, io_uring_params.getFlags(p));
		io_uring.setEnterRingFd(ring, fd);
		if ((flags & constants.IORING_SETUP_REGISTERED_FD_ONLY) != 0) {
			io_uring.setRingFd(ring, -1);
			io_uring.setIntFlags(ring,
					(char) (io_uring.getIntFlags(ring) | constants.INT_FLAG_REG_RING | constants.INT_FLAG_REG_REG_RING));
		} else {
			io_uring.setRingFd(ring, fd);
		}

		return ret;
	};

	private static int io_uring_queue_init_try_nosqarr(int entries, MemorySegment ring, MemorySegment p,
			MemorySegment buf, long bufSize) {
		int flags = io_uring_params.getFlags(p);
		io_uring_params.setFlags(p, flags | constants.IORING_SETUP_NO_SQARRAY);

		int ret = __io_uring_queue_init_params(entries, ring, p, buf, bufSize);
		if (ret != -constants.EINVAL || ((flags & constants.IORING_SETUP_NO_SQARRAY) != 0)) {
			return ret;
		}

		io_uring_params.setFlags(p, flags);
		return __io_uring_queue_init_params(entries, ring, p, buf, bufSize);
	};

	public static int io_uring_queue_init_params(int entries, MemorySegment ring, MemorySegment p) {
		int ret = io_uring_queue_init_try_nosqarr(entries, ring, p, MemorySegment.NULL, 0L);
		return ret >= 0 ? 0 : ret;
	};

	public static void io_uring_queue_exit(MemorySegment ring, MemorySegment up) {
		MemorySegment sq = io_uring.getSqSegment(ring);
		MemorySegment cq = io_uring.getCqSegment(ring);
		long sqeSize;

		long sqes = io_uring_sq.getSqes(sq);
		if (io_uring_sq.getRingSz(sq) == 0) {
			sqeSize = io_uring_sqe.layout.byteSize();
			if ((io_uring.getFlags(ring) & constants.IORING_SETUP_SQE128) != 0) {
				sqeSize += 64;
			}
			int ringEntries = io_uring_sq.getRingEntries(sq);
			syscall.__sys_munmap(sqes, sqeSize * ringEntries);
			io_uring_unmap_rings(sq, cq);
		} else {
			if ((io_uring.getIntFlags(ring) & constants.INT_FLAG_APP_MEM) == 0) {
				long kringEntries = io_uring_sq.getKringEntries(sq);
				syscall.__sys_munmap(sqes, kringEntries * io_uring_sqe.layout.byteSize());
				io_uring_unmap_rings(sq, cq);
			}
		}

		if ((io_uring.getIntFlags(ring) & constants.INT_FLAG_REG_RING) != 0) {
			register.io_uring_unregister_ring_fd(ring, up);
		}
		int ringFd = io_uring.getRingFd(ring);
		if (ringFd != -1) {
			syscall.__sys_close(ringFd);
		}
	};

};
