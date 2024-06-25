package cheshire;

import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

class setup {

	private static int PTR_ERR(MemorySegment ptr) {
		return (int) ptr.address();
	}

	private static boolean IS_ERR(MemorySegment ptr) {
		return ptr.address() >= -4095L;
	}

	private static void io_uring_setup_ring_pointers(MemorySegment p, MemorySegment sq, MemorySegment cq) {
		long sq_ring_ptr = (long) io_uring_sq.ringPtrVarHandle.get(sq);
		long cq_ring_ptr = (long) io_uring_cq.ringPtrVarHandle.get(sq);
		MemorySegment sq_off = p.asSlice(io_uring_params.layout.byteOffset(PathElement.groupElement("sq_off")),
				io_sqring_offsets.layout);
		MemorySegment cq_off = p.asSlice(io_uring_params.layout.byteOffset(PathElement.groupElement("cq_off")),
				io_cqring_offsets.layout);

		io_uring_sq.kheadVarHandle.set(sq, sq_ring_ptr + (int) io_sqring_offsets.headVarHandle.get(sq_off));
		io_uring_sq.ktailVarHandle.set(sq, sq_ring_ptr + (int) io_sqring_offsets.tailVarHandle.get(sq_off));
		io_uring_sq.kringMaskVarHandle.set(sq, sq_ring_ptr + (int) io_sqring_offsets.ringMaskVarHandle.get(sq_off));
		io_uring_sq.kringEntriesVarHandle.set(sq, sq_ring_ptr + (int) io_sqring_offsets.ringEntriesVarHandle.get(sq_off));
		io_uring_sq.kflagsVarHandle.set(sq, sq_ring_ptr + (int) io_sqring_offsets.flagsVarHandle.get(sq_off));
		io_uring_sq.kdroppedVarHandle.set(sq, sq_ring_ptr + (int) io_sqring_offsets.droppedVarHandle.get(sq_off));
		if ((((int) io_uring_params.flagsVarHandle.get(p)) & constants.IORING_SETUP_NO_SQARRAY) == 0) {
			io_uring_sq.arrayVarHandle.set(sq, sq_ring_ptr + (int) io_sqring_offsets.arrayVarHandle.get(sq_off));
		}

		io_uring_cq.kheadVarHandle.set(cq, cq_ring_ptr + (int) io_cqring_offsets.headVarHandle.get(cq_off));
		io_uring_cq.ktailVarHandle.set(cq, cq_ring_ptr + (int) io_cqring_offsets.tailVarHandle.get(cq_off));
		io_uring_cq.kringMaskVarHandle.set(cq, cq_ring_ptr + (int) io_cqring_offsets.ringMaskVarHandle.get(cq_off));
		io_uring_cq.kringEntriesVarHandle.set(cq, cq_ring_ptr + (int) io_cqring_offsets.ringEntriesVarHandle.get(cq_off));
		io_uring_cq.koverflowVarHandle.set(cq, cq_ring_ptr + (int) io_cqring_offsets.overflowVarHandle.get(cq_off));
		io_uring_cq.cqesVarHandle.set(cq, cq_ring_ptr + (int) io_cqring_offsets.cqesVarHandle.get(cq_off));
		if (((int) io_cqring_offsets.flagsVarHandle.get(p)) != 0) {
			io_uring_cq.kflagsVarHandle.set(cq, cq_ring_ptr + (int) io_cqring_offsets.flagsVarHandle.get(cq_off));
		}

		// TODO: sq->ring_mask = *sq->kring_mask;
		io_uring_sq.ringMaskVarHandle.set(sq, io_uring_sq.kringMaskVarHandle.get(sq));
		io_uring_sq.ringEntriesVarHandle.set(sq, io_uring_sq.kringEntriesVarHandle.get(sq));
		io_uring_cq.ringMaskVarHandle.set(cq, io_uring_cq.kringMaskVarHandle.get(cq));
		io_uring_cq.ringEntriesVarHandle.set(cq, io_uring_cq.kringEntriesVarHandle.get(cq));
	};

	private static void io_uring_unmap_rings(MemorySegment sq, MemorySegment cq) {
		long sqRingSz = (long) io_uring_sq.ringSzVarHandle.get(sq);
		long sqRingPtr = (long) io_uring_sq.ringPtrVarHandle.get(sq);

		long cqRingSz = (long) io_uring_cq.ringSzVarHandle.get(cq);
		long cqRingPtr = (long) io_uring_cq.ringPtrVarHandle.get(cq);

		if (sqRingSz != 0)
			syscall.__sys_munmap(sqRingPtr, sqRingSz);
		if (cqRingPtr != 0 && cqRingSz != 0 && cqRingPtr != sqRingPtr)
			syscall.__sys_munmap(cqRingPtr, cqRingSz);
	}

	private static int io_uring_mmap(int fd, MemorySegment p, MemorySegment sq, MemorySegment cq) {
		long size;
		int ret;

		size = io_uring_cqe.layout.byteSize();
		int flags = (int) io_uring_params.flagsVarHandle.get(p);
		if ((flags & constants.IORING_SETUP_CQE32) != 0) {
			size += io_uring_cqe.layout.byteSize();
		}

		MemorySegment sq_off = p.asSlice(io_uring_params.layout.byteOffset(PathElement.groupElement("sq_off")),
				io_sqring_offsets.layout);
		MemorySegment cq_off = p.asSlice(io_uring_params.layout.byteOffset(PathElement.groupElement("cq_off")),
				io_cqring_offsets.layout);

		io_uring_sq.ringSzVarHandle.set(sq, ((int) io_sqring_offsets.arrayVarHandle.get(sq_off))
				+ ((int) io_uring_params.sqEntriesVarHandle.get(p)) * Integer.BYTES);
		io_uring_cq.ringSzVarHandle.set(cq, ((int) io_cqring_offsets.cqesVarHandle.get(cq_off))
				+ ((int) io_uring_params.cqEntriesVarHandle.get(p)) * size);

		int features = (int) io_uring_params.featuresVarHandle.get(p);

		if ((features & constants.IORING_FEAT_SINGLE_MMAP) != 0) {
			long cq_ring_sz = (long) io_uring_cq.ringSzVarHandle.get(cq);
			long sq_ring_sz = (long) io_uring_sq.ringSzVarHandle.get(sq);
			if (cq_ring_sz > sq_ring_sz) {
				io_uring_sq.ringSzVarHandle.set(sq, cq_ring_sz);
			}
			io_uring_cq.ringSzVarHandle.set(cq, sq_ring_sz);
		}
		// TODO: MemorySegment.NULL == 0 ??
		io_uring_sq.ringPtrVarHandle.set(sq,
				syscall.__sys_mmap(MemorySegment.NULL, (long) io_uring_sq.ringSzVarHandle.get(sq),
						constants.PROT_READ | constants.PROT_WRITE,
						constants.MAP_SHARED | constants.MAP_POPULATE, fd, constants.IORING_OFF_SQ_RING));
		MemorySegment sq_ring_ptr = sq.asSlice(io_uring_sq.layout.byteOffset(PathElement.groupElement("ring_ptr")),
				ValueLayout.ADDRESS);
		if (IS_ERR(sq_ring_ptr))
			return PTR_ERR(sq_ring_ptr);

		if ((features & constants.IORING_FEAT_SINGLE_MMAP) != 0) {
			io_uring_cq.ringPtrVarHandle.set(cq, io_uring_sq.ringPtrVarHandle.get(sq));
		} else {
			// TODO: MemorySegment.NULL == 0 ??
			io_uring_cq.ringPtrVarHandle.set(sq,
					syscall.__sys_mmap(MemorySegment.NULL, (long) io_uring_cq.ringSzVarHandle.get(cq),
							constants.PROT_READ | constants.PROT_WRITE,
							constants.MAP_SHARED | constants.MAP_POPULATE, fd, constants.IORING_OFF_CQ_RING));
			MemorySegment cq_ring_ptr = cq.asSlice(io_uring_cq.layout.byteOffset(PathElement.groupElement("ring_ptr")),
					ValueLayout.ADDRESS);
			if (IS_ERR(cq_ring_ptr)) {
				ret = PTR_ERR(cq_ring_ptr);
				io_uring_cq.ringPtrVarHandle.set(cq, null);
				io_uring_unmap_rings(sq, cq);
				return ret;
			}
		}

		size = io_uring_sqe.layout.byteSize();
		if ((flags & constants.IORING_SETUP_SQE128) != 0) {
			size += 64;
		}
		// TODO: MemorySegment.NULL == 0 ??
		io_uring_sq.sqesVarHandle.set(sq,
				syscall.__sys_mmap(MemorySegment.NULL, size * ((long) io_uring_params.sqEntriesVarHandle.get(p)),
						constants.PROT_READ | constants.PROT_WRITE, constants.MAP_SHARED | constants.MAP_POPULATE, fd,
						constants.IORING_OFF_SQES)); // TODO: MemorySegment 0

		MemorySegment sq_sqes_ptr = sq.asSlice(io_uring_sq.layout.byteOffset(PathElement.groupElement("sqes")),
				ValueLayout.ADDRESS);
		if (IS_ERR(sq_sqes_ptr)) {
			ret = PTR_ERR(sq_sqes_ptr);
			io_uring_unmap_rings(sq, cq);
			return ret;
		}

		io_uring_setup_ring_pointers(p, sq, cq);
		return 0;
	}

	private static int io_uring_queue_mmap(int fd, MemorySegment p, MemorySegment ring) {
		ring.fill((byte) 0);
		MemorySegment sq = ring.asSlice(io_uring.layout.byteOffset(PathElement.groupElement("sq")), io_uring_sq.layout);
		MemorySegment cq = ring.asSlice(io_uring.layout.byteOffset(PathElement.groupElement("cq")), io_uring_cq.layout);
		return io_uring_mmap(fd, p, sq, cq);
	};

	private static int fls(int x) {
		if (x == 0) {
			return 0;
		}
		return Integer.SIZE - Integer.numberOfLeadingZeros(x);
	}

	private static int roundup_pow2(int depth) {
		if (depth <= 1) {
			return 1;
		}
		return 1 << fls(depth - 1);
	}

	private static int get_sq_cq_entries(int entries, MemorySegment p, MemorySegment sq, MemorySegment cq) {
		int cq_entries;
		int flags = (int) io_uring_params.flagsVarHandle.get(p);

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
			int p_cq_entries = (int) io_uring_params.cqEntriesVarHandle.get(p);
			if (p_cq_entries == 0) {
				return -constants.EINVAL;
			}
			cq_entries = p_cq_entries;
			if (cq_entries > constants.KERN_MAX_CQ_ENTRIES) {
				if ((flags & constants.IORING_SETUP_CLAMP) == 0) {
					return -constants.EINVAL;
				}
				cq_entries = constants.KERN_MAX_CQ_ENTRIES;
			}
			cq_entries = roundup_pow2(cq_entries);
			if (cq_entries < entries) {
				return -constants.EINVAL;
			}
		} else {
			cq_entries = 2 * entries;
		}

		sq.set(ValueLayout.JAVA_INT, 0L, entries);
		cq.set(ValueLayout.JAVA_INT, 0L, cq_entries);
		return 0;
	}

	/* FIXME */
	private static long huge_page_size = 2 * 1024 * 1024;

	private static long get_page_size() {
		return 4096; // TODO
	}

	private static int io_uring_alloc_huge(int entries, MemorySegment p, MemorySegment sq, MemorySegment cq,
			MemorySegment buf, long buf_size, MemorySegment sq_entries_segment, MemorySegment cq_entries_segment) {
		long page_size = get_page_size();
		long ring_mem, sqes_mem = 0;
		long mem_used = 0;
		MemorySegment ptr;

		int ret = get_sq_cq_entries(entries, p, sq_entries_segment, cq_entries_segment);
		if (ret != 0) {
			return ret;
		}

		int sq_entries = sq_entries_segment.get(ValueLayout.JAVA_INT, 0);
		int cq_entries = cq_entries_segment.get(ValueLayout.JAVA_INT, 0);

		sqes_mem = sq_entries * io_uring_sqe.layout.byteSize();
		sqes_mem = (sqes_mem + page_size - 1) & ~(page_size - 1);
		ring_mem = cq_entries * io_uring_cqe.layout.byteSize();
		int flags = (int) io_uring_params.flagsVarHandle.get(p);
		if ((flags & constants.IORING_SETUP_CQE32) != 0) {
			ring_mem *= 2;
		}
		if ((flags & constants.IORING_SETUP_NO_SQARRAY) == 0) {
			ring_mem += sq_entries * Integer.BYTES;
		}
		mem_used = sqes_mem + ring_mem;
		sqes_mem = (mem_used + page_size - 1) & ~(page_size - 1);

		if (buf == null && (sqes_mem > huge_page_size || ring_mem > huge_page_size)) {
			return -constants.ENOMEM;
		}

		if (buf != null) {
			if (mem_used > buf_size) {
				return -constants.ENOMEM;
			}
			ptr = buf; // TODO: Review if allocation is needed
		} else {
			int map_hugetlb = 0;
			if (sqes_mem <= page_size) {
				buf_size = page_size;
			} else {
				buf_size = huge_page_size;
				map_hugetlb = constants.MAP_HUGETLB;
			}
			ptr = syscall.__sys_mmap(null, buf_size, constants.PROT_READ | constants.PROT_WRITE,
					constants.MAP_SHARED | constants.MAP_ANONYMOUS | map_hugetlb, -1, 0); // TODO: Review if allocation is needed
			if (IS_ERR(ptr)) {
				return PTR_ERR(ptr);
			}
		}

		io_uring_sq.sqesVarHandle.set(sq, ptr);
		if (mem_used <= buf_size) {
			io_uring_sq.ringPtrVarHandle.set(sq, ((long) io_uring_sq.sqesVarHandle.get(sq)) + sqes_mem);
			io_uring_cq.ringSzVarHandle.set(cq, 0);
			io_uring_sq.ringSzVarHandle.set(sq, 0);
		} else {
			int map_hugetlb = 0;
			if (ring_mem <= page_size) {
				buf_size = page_size;
			} else {
				buf_size = huge_page_size;
				map_hugetlb = constants.MAP_HUGETLB;
			}
			ptr = syscall.__sys_mmap(null, buf_size, constants.PROT_READ | constants.PROT_WRITE,
					constants.MAP_SHARED | constants.MAP_ANONYMOUS | map_hugetlb, -1, 0); // TODO: Review if allocation is needed
			if (IS_ERR(ptr)) {
				long sqes = (long) io_uring_sq.sqesVarHandle.get(sq);
				syscall.__sys_munmap(sqes, 1);
				return PTR_ERR(ptr);
			}
			io_uring_sq.ringPtrVarHandle.set(sq, ptr);
			io_uring_sq.ringSzVarHandle.set(sq, buf_size);
			io_uring_cq.ringSzVarHandle.set(cq, 0);
		}

		io_uring_cq.ringPtrVarHandle.set(cq, io_uring_sq.ringPtrVarHandle.get(sq));
		MemorySegment sq_off = p.asSlice(io_uring_params.layout.byteOffset(PathElement.groupElement("sq_off")),
				io_sqring_offsets.layout);
		MemorySegment cq_off = p.asSlice(io_uring_params.layout.byteOffset(PathElement.groupElement("cq_off")),
				io_cqring_offsets.layout);
		io_sqring_offsets.userAddrVarHandle.set(sq_off, io_uring_sq.sqesVarHandle.get(sq));
		io_cqring_offsets.userAddrVarHandle.set(cq_off, io_uring_sq.ringPtrVarHandle.get(sq));
		return (int) mem_used;
	};

	private static int __io_uring_queue_init_params(int entries, MemorySegment ring, MemorySegment p,
			MemorySegment buf, long buf_size, MemorySegment sq_entries_segment, MemorySegment cq_entries_segment) {
		int fd = 0;
		int ret = 0;

		ring.fill((byte) 0);

		int flags = (int) io_uring_params.flagsVarHandle.get(p);
		if (((flags & constants.IORING_SETUP_REGISTERED_FD_ONLY) != 0)
				&& ((flags & constants.IORING_SETUP_NO_MMAP) == 0)) {
			return -constants.EINVAL;
		}
		if ((flags & constants.IORING_SETUP_NO_MMAP) != 0) {
			MemorySegment sq = ring.asSlice(io_uring.layout.byteOffset(PathElement.groupElement("sq")), io_uring_sq.layout);
			MemorySegment cq = ring.asSlice(io_uring.layout.byteOffset(PathElement.groupElement("cq")), io_uring_cq.layout);
			ret = io_uring_alloc_huge(entries, p, sq, cq, buf, buf_size, sq_entries_segment, cq_entries_segment);
			if (ret < 0) {
				return ret;
			}
			if (buf != null) {
				char int_flags = (char) io_uring.intFlagsVarHandle.get(ring);
				io_uring.intFlagsVarHandle.set(ring, int_flags | constants.INT_FLAG_APP_MEM);
			}
		}

		fd = syscall.__sys_io_uring_setup(entries, p);
		int flags2 = (int) io_uring_params.flagsVarHandle.get(p);
		if (fd < 0) {
			char int_flags = (char) io_uring.intFlagsVarHandle.get(ring);
			if (((flags2 & constants.IORING_SETUP_NO_MMAP) != 0) && ((int_flags & constants.INT_FLAG_APP_MEM) == 0)) {
				MemorySegment sq2 = ring.asSlice(io_uring.layout.byteOffset(PathElement.groupElement("sq")),
						io_uring_sq.layout);
				long sqes = (long) io_uring_sq.sqesVarHandle.get(sq2);
				syscall.__sys_munmap(sqes, 1);
				MemorySegment sq3 = ring.asSlice(io_uring.layout.byteOffset(PathElement.groupElement("sq")),
						io_uring_sq.layout);
				MemorySegment cq3 = ring.asSlice(io_uring.layout.byteOffset(PathElement.groupElement("cq")),
						io_uring_cq.layout);
				io_uring_unmap_rings(sq3, cq3);
			}
			return fd;
		}

		if (((flags2 & constants.IORING_SETUP_NO_MMAP) == 0)) {
			ret = io_uring_queue_mmap(fd, p, ring);
			if (ret != 0) {
				syscall.__sys_close(fd);
				return ret;
			}
		} else {
			MemorySegment sq4 = ring.asSlice(io_uring.layout.byteOffset(PathElement.groupElement("sq")), io_uring_sq.layout);
			MemorySegment cq4 = ring.asSlice(io_uring.layout.byteOffset(PathElement.groupElement("cq")), io_uring_cq.layout);
			io_uring_setup_ring_pointers(p, sq4, cq4);
		}

		MemorySegment sq5 = ring.asSlice(io_uring.layout.byteOffset(PathElement.groupElement("sq")), io_uring_sq.layout);
		int sq_entries = (int) io_uring_sq.ringEntriesVarHandle.get(sq5);
		if ((flags & constants.IORING_SETUP_NO_SQARRAY) == 0) {
			MemorySegment sq_array = sq5.asSlice(io_uring_sq.layout.byteOffset(PathElement.groupElement("array")),
					ValueLayout.ADDRESS);
			for (int index = 0; index < sq_entries; index++) {
				sq_array.setAtIndex(ValueLayout.JAVA_INT, index, index);
			}
		}
		io_uring.featuresVarHandle.set(ring, io_uring_params.featuresVarHandle.get(p));
		io_uring.flagsVarHandle.set(ring, io_uring_params.flagsVarHandle.get(p));
		io_uring.enterRingFdVarHandle.set(ring, fd);
		if ((flags & constants.IORING_SETUP_REGISTERED_FD_ONLY) != 0) {
			io_uring.ringFdVarHandle.set(ring, -1);
			char int_flags = (char) io_uring.intFlagsVarHandle.get(ring);
			io_uring.intFlagsVarHandle.set(ring, int_flags | constants.INT_FLAG_REG_RING | constants.INT_FLAG_REG_REG_RING);
		} else {
			io_uring.ringFdVarHandle.set(ring, fd);
		}

		return ret;
	};

	private static int io_uring_queue_init_try_nosqarr(int entries, MemorySegment ring, MemorySegment p,
			MemorySegment buf, long buf_size, MemorySegment sq_entries_segment, MemorySegment cq_entries_segment) {
		int flags = (int) io_uring_params.flagsVarHandle.get(p);
		io_uring_params.flagsVarHandle.set(p, flags | constants.IORING_SETUP_NO_SQARRAY);

		int ret = __io_uring_queue_init_params(entries, ring, p, buf, buf_size, sq_entries_segment, cq_entries_segment);
		if (ret != -constants.EINVAL || ((flags & constants.IORING_SETUP_NO_SQARRAY) != 0)) {
			return ret;
		}
		io_uring_params.flagsVarHandle.set(p, flags);
		return __io_uring_queue_init_params(entries, ring, p, buf, buf_size, sq_entries_segment, cq_entries_segment);
	}

	public static int io_uring_queue_init_params(int entries, MemorySegment ring, MemorySegment p,
			MemorySegment sq_entries_segment, MemorySegment cq_entries_segment) {
		int ret = io_uring_queue_init_try_nosqarr(entries, ring, p, null, 0L, sq_entries_segment, cq_entries_segment);
		return ret >= 0 ? 0 : ret;
	};

	public static void io_uring_queue_exit(MemorySegment ring, MemorySegment up) {
		MemorySegment sq = ring.asSlice(io_uring.layout.byteOffset(PathElement.groupElement("sq")), io_uring_sq.layout);
		MemorySegment cq = ring.asSlice(io_uring.layout.byteOffset(PathElement.groupElement("cq")), io_uring_cq.layout);
		long sqe_size;

		long ring_sz = (long) io_uring_sq.ringSzVarHandle.get(sq);
		long sqes = (long) io_uring_sq.sqesVarHandle.get(sq);
		if (ring_sz == 0) {
			sqe_size = io_uring_sqe.layout.byteSize();
			int flags = (int) io_uring.flagsVarHandle.get(ring);
			if ((flags & constants.IORING_SETUP_SQE128) != 0) {
				sqe_size += 64;
			}
			int ring_entries = (int) io_uring_sq.ringEntriesVarHandle.get(sq);
			syscall.__sys_munmap(sqes, sqe_size * ring_entries);
			io_uring_unmap_rings(sq, cq);
		} else {
			char int_flags = (char) io_uring.intFlagsVarHandle.get(ring);
			if ((int_flags & constants.INT_FLAG_APP_MEM) == 0) {
				long kring_entries = (long) io_uring_sq.kringEntriesVarHandle.get(sq);
				syscall.__sys_munmap(sqes, kring_entries * io_uring_sqe.layout.byteSize());
				io_uring_unmap_rings(sq, cq);
			}
		}

		char int_flags = (char) io_uring.intFlagsVarHandle.get(ring);
		if ((int_flags & constants.INT_FLAG_REG_RING) != 0) {
			register.io_uring_unregister_ring_fd(ring, up);
		}
		int ring_fd = (int) io_uring.ringFdVarHandle.get(ring);
		if (ring_fd != -1) {
			syscall.__sys_close(ring_fd);
		}
	}
}
