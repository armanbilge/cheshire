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
		long sq_ring_ptr = io_uring_sq.getRingPtr(sq);
		long cq_ring_ptr = io_uring_cq.getRingPtr(sq);
		MemorySegment sq_off = io_uring_params.getSqOffSegment(p);
		MemorySegment cq_off = io_uring_params.getCqOffSegment(p);

		io_uring_sq.setKhead(sq, sq_ring_ptr + io_sqring_offsets.getHead(sq_off));
		io_uring_sq.setKtail(sq, sq_ring_ptr + io_sqring_offsets.getTail(sq_off));
		io_uring_sq.setKringMask(sq, sq_ring_ptr + io_sqring_offsets.getRingMask(sq_off));
		io_uring_sq.setKringEntries(sq, sq_ring_ptr + io_sqring_offsets.getRingEntries(sq_off));
		io_uring_sq.setKflags(sq, sq_ring_ptr + io_sqring_offsets.getFlags(sq_off));
		io_uring_sq.setKdropped(sq, sq_ring_ptr + io_sqring_offsets.getDropped(sq_off));
		if ((io_uring_params.getFlags(p) & constants.IORING_SETUP_NO_SQARRAY) == 0) {
			io_uring_sq.setArray(sq, sq_ring_ptr + io_sqring_offsets.getArray(sq_off));
		}

		io_uring_cq.setKhead(cq, cq_ring_ptr + io_cqring_offsets.getHead(cq_off));
		io_uring_cq.setKtail(cq, cq_ring_ptr + io_cqring_offsets.getTail(cq_off));
		io_uring_cq.setKringMask(cq, cq_ring_ptr + io_cqring_offsets.getRingMask(cq_off));
		io_uring_cq.setKringEntries(cq, cq_ring_ptr + io_cqring_offsets.getRingEntries(cq_off));
		io_uring_cq.setKoverflow(cq, cq_ring_ptr + io_cqring_offsets.getOverflow(cq_off));
		io_uring_cq.setCqes(cq, cq_ring_ptr + io_cqring_offsets.getCqes(cq_off));
		if (io_cqring_offsets.getFlags(p) != 0) {
			io_uring_cq.setKflags(cq, cq_ring_ptr + io_cqring_offsets.getFlags(cq_off));
		}

		// TODO: Review
		io_uring_sq.setRingMask(sq, MemorySegment.ofAddress(io_uring_sq.getKringMask(sq)).get(ValueLayout.JAVA_INT, 0));
		io_uring_sq.setRingEntries(sq,
				MemorySegment.ofAddress(io_uring_sq.getKringEntries(sq)).get(ValueLayout.JAVA_INT, 0));
		io_uring_cq.setRingMask(cq, MemorySegment.ofAddress(io_uring_cq.getKringMask(cq)).get(ValueLayout.JAVA_INT, 0));
		io_uring_cq.setRingEntries(cq,
				MemorySegment.ofAddress(io_uring_cq.getKringEntries(cq)).get(ValueLayout.JAVA_INT, 0));
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
	}

	private static int io_uring_mmap(int fd, MemorySegment p, MemorySegment sq, MemorySegment cq) {
		long size;
		int ret;

		size = io_uring_cqe.layout.byteSize();
		int flags = io_uring_params.getFlags(p);
		if ((flags & constants.IORING_SETUP_CQE32) != 0) {
			size += io_uring_cqe.layout.byteSize();
		}

		MemorySegment sq_off = io_uring_params.getSqOffSegment(p);
		MemorySegment cq_off = io_uring_params.getCqOffSegment(p);

		io_uring_sq.setRingSz(sq, io_sqring_offsets.getArray(sq_off)
				+ io_uring_params.getSqEntries(p) * Integer.BYTES);
		io_uring_cq.setRingSz(cq, io_cqring_offsets.getCqes(cq_off)
				+ io_uring_params.getCqEntries(p) * size);

		int features = io_uring_params.getFeatures(p);
		if ((features & constants.IORING_FEAT_SINGLE_MMAP) != 0) {
			long cq_ring_sz = io_uring_cq.getRingSz(cq);
			long sq_ring_sz = io_uring_sq.getRingSz(sq);
			if (cq_ring_sz > sq_ring_sz) {
				io_uring_sq.setRingSz(sq, cq_ring_sz);
			}
			io_uring_cq.setRingSz(cq, sq_ring_sz);
		}
		io_uring_sq.setRingPtr(sq,
				syscall.__sys_mmap(MemorySegment.NULL, io_uring_sq.getRingSz(sq),
						constants.PROT_READ | constants.PROT_WRITE,
						constants.MAP_SHARED | constants.MAP_POPULATE, fd, constants.IORING_OFF_SQ_RING).address());
		MemorySegment sq_ring_ptr = io_uring_sq.getRingPtrSegment(sq);
		if (IS_ERR(sq_ring_ptr))
			return PTR_ERR(sq_ring_ptr);

		if ((features & constants.IORING_FEAT_SINGLE_MMAP) != 0) {
			io_uring_cq.setRingPtr(cq, io_uring_sq.getRingPtr(sq));
		} else {
			io_uring_cq.setRingPtr(sq,
					syscall.__sys_mmap(MemorySegment.NULL, io_uring_cq.getRingSz(cq),
							constants.PROT_READ | constants.PROT_WRITE,
							constants.MAP_SHARED | constants.MAP_POPULATE, fd, constants.IORING_OFF_CQ_RING).address());
			MemorySegment cq_ring_ptr = io_uring_cq.getRingPtrSegment(cq);
			if (IS_ERR(cq_ring_ptr)) {
				ret = PTR_ERR(cq_ring_ptr);
				io_uring_cq.setRingPtr(cq, 0L); // Should be null
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
						constants.IORING_OFF_SQES).address()); // TODO: MemorySegment 0

		MemorySegment sq_sqes = io_uring_sq.getSqesSegment(sq);
		if (IS_ERR(sq_sqes)) {
			ret = PTR_ERR(sq_sqes);
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
	}

	private static int roundup_pow2(int depth) {
		if (depth <= 1) {
			return 1;
		}
		return 1 << fls(depth - 1);
	}

	private static int get_sq_cq_entries(int entries, MemorySegment p, MemorySegment sq, MemorySegment cq) {
		int cq_entries;
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
			int p_cq_entries = io_uring_params.getCqEntries(p);
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

		int sq_entries = sq_entries_segment.get(ValueLayout.JAVA_INT, 0L);
		int cq_entries = cq_entries_segment.get(ValueLayout.JAVA_INT, 0L);

		sqes_mem = sq_entries * io_uring_sqe.layout.byteSize();
		sqes_mem = (sqes_mem + page_size - 1) & ~(page_size - 1);
		ring_mem = cq_entries * io_uring_cqe.layout.byteSize();
		int flags = io_uring_params.getFlags(p);
		if ((flags & constants.IORING_SETUP_CQE32) != 0) {
			ring_mem *= 2;
		}
		if ((flags & constants.IORING_SETUP_NO_SQARRAY) == 0) {
			ring_mem += sq_entries * Integer.BYTES;
		}
		mem_used = sqes_mem + ring_mem;
		mem_used = (mem_used + page_size - 1) & ~(page_size - 1);

		if (buf == MemorySegment.NULL && (sqes_mem > huge_page_size || ring_mem > huge_page_size)) {
			return -constants.ENOMEM;
		}

		if (buf != MemorySegment.NULL) {
			if (mem_used > buf_size) {
				return -constants.ENOMEM;
			}
			ptr = buf;
		} else {
			int map_hugetlb = 0;
			if (sqes_mem <= page_size) {
				buf_size = page_size;
			} else {
				buf_size = huge_page_size;
				map_hugetlb = constants.MAP_HUGETLB;
			}
			ptr = syscall.__sys_mmap(null, buf_size, constants.PROT_READ | constants.PROT_WRITE,
					constants.MAP_SHARED | constants.MAP_ANONYMOUS | map_hugetlb, -1, 0);
			if (IS_ERR(ptr)) {
				return PTR_ERR(ptr);
			}
		}

		io_uring_sq.setSqes(sq, ptr.address());
		if (mem_used <= buf_size) {
			io_uring_sq.setRingPtr(sq, io_uring_sq.getSqes(sq) + sqes_mem);
			io_uring_cq.setRingSz(cq, 0L);
			io_uring_sq.setRingSz(sq, 0L);
		} else {
			int map_hugetlb = 0;
			if (ring_mem <= page_size) {
				buf_size = page_size;
			} else {
				buf_size = huge_page_size;
				map_hugetlb = constants.MAP_HUGETLB;
			}
			ptr = syscall.__sys_mmap(null, buf_size, constants.PROT_READ | constants.PROT_WRITE,
					constants.MAP_SHARED | constants.MAP_ANONYMOUS | map_hugetlb, -1, 0);
			if (IS_ERR(ptr)) {
				long sqes = io_uring_sq.getSqes(sq);
				syscall.__sys_munmap(sqes, 1);
				return PTR_ERR(ptr);
			}
			io_uring_sq.setRingPtr(sq, ptr.address());
			io_uring_sq.setRingSz(sq, buf_size);
			io_uring_cq.setRingSz(cq, 0);
		}

		io_uring_cq.setRingPtr(cq, io_uring_sq.getRingPtr(sq));
		MemorySegment sq_off = io_uring_params.getSqOffSegment(p);
		MemorySegment cq_off = io_uring_params.getCqOffSegment(p);
		io_sqring_offsets.setUserAddr(sq_off, io_uring_sq.getSqes(sq));
		io_cqring_offsets.setUserAddr(cq_off, io_uring_sq.getRingPtr(sq));
		return (int) mem_used;
	};

	private static int __io_uring_queue_init_params(int entries, MemorySegment ring, MemorySegment p,
			MemorySegment buf, long buf_size, MemorySegment sq_entries_segment, MemorySegment cq_entries_segment) {
		int fd = 0;
		int ret = 0;

		ring.fill((byte) 0);

		int flags = io_uring_params.getFlags(p);
		if (((flags & constants.IORING_SETUP_REGISTERED_FD_ONLY) != 0)
				&& ((flags & constants.IORING_SETUP_NO_MMAP) == 0)) {
			return -constants.EINVAL;
		}
		if ((flags & constants.IORING_SETUP_NO_MMAP) != 0) {
			MemorySegment sq = io_uring.getSqSegment(ring);
			MemorySegment cq = io_uring.getCqSegment(ring);
			ret = io_uring_alloc_huge(entries, p, sq, cq, buf, buf_size, sq_entries_segment, cq_entries_segment);
			if (ret < 0) {
				return ret;
			}
			if (buf != null) {
				char int_flags = io_uring.getIntFlags(ring);
				io_uring.setIntFlags(ring, (char) (int_flags | constants.INT_FLAG_APP_MEM));
			}
		}

		fd = syscall.__sys_io_uring_setup(entries, p);
		int flags2 = io_uring_params.getFlags(p);
		if (fd < 0) {
			char int_flags = io_uring.getIntFlags(ring);
			if (((flags2 & constants.IORING_SETUP_NO_MMAP) != 0) && ((int_flags & constants.INT_FLAG_APP_MEM) == 0)) {
				MemorySegment sq2 = io_uring.getSqSegment(ring);
				long sqes = io_uring_sq.getSqes(sq2);
				syscall.__sys_munmap(sqes, 1);
				MemorySegment sq3 = io_uring.getSqSegment(ring);
				MemorySegment cq3 = io_uring.getCqSegment(ring);
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
			MemorySegment sq4 = io_uring.getSqSegment(ring);
			MemorySegment cq4 = io_uring.getCqSegment(ring);
			io_uring_setup_ring_pointers(p, sq4, cq4);
		}

		MemorySegment sq5 = io_uring.getSqSegment(ring);
		int sq_entries = io_uring_sq.getRingEntries(sq5);
		if ((flags & constants.IORING_SETUP_NO_SQARRAY) == 0) {
			MemorySegment sq_array = io_uring_sq.getSqArraySegment(sq5);
			for (int index = 0; index < sq_entries; index++) {
				sq_array.setAtIndex(ValueLayout.JAVA_INT, index, index);
			}
		}
		io_uring.setFeatures(ring, io_uring_params.getFeatures(p));
		io_uring.setFlags(ring, io_uring_params.getFlags(p));
		io_uring.setEnterRingFd(ring, fd);
		if ((flags & constants.IORING_SETUP_REGISTERED_FD_ONLY) != 0) {
			io_uring.setRingFd(ring, -1);
			char int_flags = io_uring.getIntFlags(ring);
			io_uring.setIntFlags(ring, (char) (int_flags | constants.INT_FLAG_REG_RING | constants.INT_FLAG_REG_REG_RING));
		} else {
			io_uring.setRingFd(ring, fd);
		}

		return ret;
	};

	private static int io_uring_queue_init_try_nosqarr(int entries, MemorySegment ring, MemorySegment p,
			MemorySegment buf, long buf_size, MemorySegment sq_entries_segment, MemorySegment cq_entries_segment) {
		int flags = io_uring_params.getFlags(p);
		io_uring_params.setFlags(p, flags | constants.IORING_SETUP_NO_SQARRAY);

		int ret = __io_uring_queue_init_params(entries, ring, p, buf, buf_size, sq_entries_segment, cq_entries_segment);
		if (ret != -constants.EINVAL || ((flags & constants.IORING_SETUP_NO_SQARRAY) != 0)) {
			return ret;
		}
		io_uring_params.setFlags(p, flags);
		return __io_uring_queue_init_params(entries, ring, p, buf, buf_size, sq_entries_segment, cq_entries_segment);
	}

	public static int io_uring_queue_init_params(int entries, MemorySegment ring, MemorySegment p,
			MemorySegment sq_entries_segment, MemorySegment cq_entries_segment) {
		int ret = io_uring_queue_init_try_nosqarr(entries, ring, p, MemorySegment.NULL, 0L, sq_entries_segment,
				cq_entries_segment);
		return ret >= 0 ? 0 : ret;
	};

	public static void io_uring_queue_exit(MemorySegment ring, MemorySegment up) {
		MemorySegment sq = io_uring.getSqSegment(ring);
		MemorySegment cq = io_uring.getCqSegment(ring);
		long sqe_size;

		long ring_sz = io_uring_sq.getRingSz(sq);
		long sqes = io_uring_sq.getSqes(sq);
		if (ring_sz == 0) {
			sqe_size = io_uring_sqe.layout.byteSize();
			int flags = io_uring.getFlags(ring);
			if ((flags & constants.IORING_SETUP_SQE128) != 0) {
				sqe_size += 64;
			}
			int ring_entries = io_uring_sq.getRingEntries(sq);
			syscall.__sys_munmap(sqes, sqe_size * ring_entries);
			io_uring_unmap_rings(sq, cq);
		} else {
			char int_flags = io_uring.getIntFlags(ring);
			if ((int_flags & constants.INT_FLAG_APP_MEM) == 0) {
				long kring_entries = io_uring_sq.getKringEntries(sq);
				syscall.__sys_munmap(sqes, kring_entries * io_uring_sqe.layout.byteSize());
				io_uring_unmap_rings(sq, cq);
			}
		}

		char int_flags = io_uring.getIntFlags(ring);
		if ((int_flags & constants.INT_FLAG_REG_RING) != 0) {
			register.io_uring_unregister_ring_fd(ring, up);
		}
		int ring_fd = io_uring.getRingFd(ring);
		if (ring_fd != -1) {
			syscall.__sys_close(ring_fd);
		}
	}
}
