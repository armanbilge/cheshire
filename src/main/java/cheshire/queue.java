package cheshire;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

class queue {

	private static boolean sq_ring_needs_enter(MemorySegment ring, int submit, MemorySegment flags) {
		if (submit == 0) {
			return false;
		}

		if ((io_uring.getFlags(ring) & constants.IORING_SETUP_SQPOLL) == 0) {
			return true;
		}

		// io_uring_smp_mb();
		// std::atomic_thread_fence(std::memory_order_seq_cst);
		// Review inside if -> uring_unlikely(IO_URING_READ_ONCE(*ring->sq.kflags)
		int kflags = io_uring_sq.getAcquireKflags(io_uring.getSqSegment(ring)).get(ValueLayout.JAVA_INT, 0L);
		if ((kflags & constants.IORING_SQ_NEED_WAKEUP) != 0) {
			flags.set(ValueLayout.JAVA_INT, 0L, (flags.get(ValueLayout.JAVA_INT, 0L) | constants.IORING_ENTER_SQ_WAKEUP));
			return true;
		}

		return false;
	};

	private static int _io_uring_get_cqe(io_uring ring, io_uring_cqe cqePtr, MemorySegment data) {
		MemorySegment cqe = ring_allocations.getCqeSegment(ring.allocations);
		MemorySegment cqeFlags = ring_allocations.getCqeFlagsSegment(ring.allocations);
		MemorySegment nrAvailable = ring_allocations.getNrAvailableSegment(ring.allocations);

		cqe = MemorySegment.NULL;
		io_uring_cqe cqePtrAux = new io_uring_cqe(cqe);
		boolean looped = false;
		int err = 0;

		while (true) {
			boolean needEnter = false;
			cqeFlags.set(ValueLayout.JAVA_INT, 0L, 0);
			int ret;

			int waitNr = get_data.getWaitNr(data);
			int submit = get_data.getSubmit(data);
			boolean hasTs = get_data.getHasTs(data) != 0;
			long sz = get_data.getSz(data);
			ret = liburing.__io_uring_peek_cqe(ring, cqePtrAux, nrAvailable);
			if (ret != 0) {
				if (err == 0) {
					err = ret;
				}
				break;
			}
			if (utils.areSegmentsEquals(cqePtrAux.segment, MemorySegment.NULL) && waitNr == 0 && submit == 0) {
				if (looped || !cq_ring_needs_enter(ring.segment)) {
					if (err == 0) {
						err = -constants.EAGAIN;
					}
					break;
				}
				needEnter = true;
			}
			if ((waitNr > nrAvailable.get(ValueLayout.JAVA_INT, 0L)) || needEnter) {
				cqeFlags.set(ValueLayout.JAVA_INT, 0L, constants.IORING_ENTER_GETEVENTS | get_data.getGetFlags(data));
				needEnter = true;
			}
			if (sq_ring_needs_enter(ring.segment, submit, cqeFlags)) {
				needEnter = true;
			}
			if (!needEnter) {
				break;
			}
			MemorySegment arg = get_data.getArg(data)
					.reinterpret(io_uring_getevents_arg.layout.byteSize()); // Enough?
			if (looped && hasTs) {
				long ts = io_uring_getevents_arg.getTs(arg);
				if (utils.areSegmentsEquals(cqePtrAux.segment, MemorySegment.NULL) && ts == 0 && err == 0) {
					err = -constants.ETIME;
				}
				break;
			}

			if ((io_uring.getIntFlags(ring.segment) & constants.INT_FLAG_REG_RING) != 0) {
				cqeFlags.set(ValueLayout.JAVA_INT, 0L,
						cqeFlags.get(ValueLayout.JAVA_INT, 0L) | constants.IORING_ENTER_REGISTERED_RING);
			}
			ret = syscall.__sys_io_uring_enter2(io_uring.getEnterRingFd(ring.segment), submit, waitNr,
					cqeFlags.get(ValueLayout.JAVA_INT, 0L), arg, sz);
			if (ret < 0) {
				if (err == 0) {
					err = ret;
				}
				break;
			}

			get_data.setSubmit(data, get_data.getSubmit(data) - ret);
			if (!utils.areSegmentsEquals(cqe, MemorySegment.NULL)) {
				break;
			}
			if (!looped) {
				looped = true;
				err = ret;
			}
		}

		cqePtr.segment = cqePtrAux.segment;
		return err;
	};

	public static int __io_uring_get_cqe(io_uring ring, io_uring_cqe cqePtr, int submit, int waitNr,
			MemorySegment sigmask) {
		MemorySegment data = ring_allocations.getDataSegment(ring.allocations);
		get_data.setSubmit(data, submit);
		get_data.setWaitNr(data, waitNr);
		get_data.setGetFlags(data, 0);
		get_data.setSz(data, constants._NSIG / 8);
		get_data.setArg(data, sigmask);
		return _io_uring_get_cqe(ring, cqePtr, data);
	};

	private static int io_uring_get_events(MemorySegment ring) {
		int flags = constants.IORING_ENTER_GETEVENTS;
		if ((io_uring.getIntFlags(ring) & constants.INT_FLAG_REG_RING) != 0) {
			flags |= constants.IORING_ENTER_REGISTERED_RING;
		}
		return syscall.__sys_io_uring_enter(io_uring.getEnterRingFd(ring), 0, 0, flags, MemorySegment.NULL);
	};

	private static int __io_uring_flush_sq(MemorySegment ring) {
		MemorySegment sq = io_uring.getSqSegment(ring);
		int tail = io_uring_sq.getSqeTail(sq);
		int head = io_uring_sq.getSqeHead(sq);
		if (head != tail) {
			io_uring_sq.setSqeHead(sq, tail);

			int flags = io_uring.getFlags(ring);
			MemorySegment ktail = io_uring_sq.getKtail(sq);
			if ((flags & constants.IORING_SETUP_SQPOLL) == 0) {
				ktail.set(ValueLayout.JAVA_INT, 0L, tail);
			} else {
				// Review workaround
				ktail.set(ValueLayout.JAVA_INT, 0L, tail);
				io_uring_sq.setReleaseKtail(sq, ktail);
			}
		}
		// return tail - IO_URING_READ_ONCE(*sq->khead);
		return tail - io_uring_sq.getAcquireKhead(sq).get(ValueLayout.JAVA_INT, 0L);
	};

	private static boolean cq_ring_needs_flush(MemorySegment ring) {
		MemorySegment sq = io_uring.getSqSegment(ring);
		// IO_URING_READ_ONCE(*ring->sq.kflags) // std::memory_order_relaxed
		int kflags = io_uring_sq.getAcquireKflags(sq).get(ValueLayout.JAVA_INT, 0L);
		return ((kflags & (constants.IORING_SQ_CQ_OVERFLOW | constants.IORING_SQ_TASKRUN)) != 0);
	};

	private static boolean cq_ring_needs_enter(MemorySegment ring) {
		int flags = io_uring.getFlags(ring);
		return ((flags & constants.IORING_SETUP_IOPOLL) != 0) || cq_ring_needs_flush(ring);
	};

	private static int io_uring_wait_cqes_new(io_uring ring, io_uring_cqe cqePtr, int waitNr, MemorySegment ts,
			MemorySegment sigmask) {
		MemorySegment arg = ring_allocations.getArgSegment(ring.allocations);
		MemorySegment data = ring_allocations.getDataSegment(ring.allocations);

		io_uring_getevents_arg.setSigmask(arg, sigmask.address());
		io_uring_getevents_arg.setSigmaskSz(arg, constants._NSIG / 8);
		io_uring_getevents_arg.setTs(arg, ts.address());

		get_data.setWaitNr(data, waitNr);
		get_data.setGetFlags(data, constants.IORING_ENTER_EXT_ARG);
		get_data.setSz(data, (int) arg.byteSize());
		get_data.setHasTs(data, utils.areSegmentsEquals(ts, MemorySegment.NULL) ? 0 : 1);
		get_data.setArg(data, arg);
		return _io_uring_get_cqe(ring, cqePtr, data);
	};

	private static int __io_uring_submit_timeout(io_uring ring, int waitNr, MemorySegment ts) {
		int ret;
		MemorySegment sqe = liburing.io_uring_get_sqe(ring);
		if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
			ret = liburing.io_uring_submit(ring);
			if (ret < 0) {
				return ret;
			}
			sqe = liburing.io_uring_get_sqe(ring);
			if (utils.areSegmentsEquals(sqe, MemorySegment.NULL)) {
				return -constants.EAGAIN;
			}
		}
		liburing.io_uring_prep_timeout(new io_uring_sqe(sqe), new __kernel_timespec(ts), waitNr, 0);
		io_uring_sqe.setUserData(sqe, constants.LIBURING_UDATA_TIMEOUT);
		return __io_uring_flush_sq(ring.segment);
	};

	private static int __io_uring_submit(MemorySegment ring, int submitted, int waitNr, boolean getevents,
			MemorySegment flags) {
		boolean cqNeedsEnter = getevents || waitNr != 0 || cq_ring_needs_enter(ring);
		int ret;

		flags.set(ValueLayout.JAVA_INT, 0L, 0);
		if (sq_ring_needs_enter(ring, submitted, flags) || cqNeedsEnter) {
			if (cqNeedsEnter) {
				flags.set(ValueLayout.JAVA_INT, 0L,
						flags.get(ValueLayout.JAVA_INT, 0L) | constants.IORING_ENTER_GETEVENTS);
			}
			if ((io_uring.getIntFlags(ring) & constants.INT_FLAG_REG_RING) != 0) {
				flags.set(ValueLayout.JAVA_INT, 0L,
						flags.get(ValueLayout.JAVA_INT, 0L) | constants.IORING_ENTER_REGISTERED_RING);
			}

			ret = syscall.__sys_io_uring_enter(io_uring.getEnterRingFd(ring), submitted, waitNr,
					flags.get(ValueLayout.JAVA_INT, 0L), MemorySegment.NULL);
		} else {
			ret = submitted;
		}
		return ret;
	};

	public static int io_uring_wait_cqes(io_uring ring, io_uring_cqe cqePtr, int waitNr, MemorySegment ts,
			MemorySegment sigmask) {
		int toSubmit = 0;
		if (!utils.areSegmentsEquals(ts, MemorySegment.NULL)) {
			if ((io_uring.getFeatures(ring.segment) & constants.IORING_FEAT_EXT_ARG) != 0) {
				return io_uring_wait_cqes_new(ring, cqePtr, waitNr, ts, sigmask);
			}
			toSubmit = __io_uring_submit_timeout(ring, waitNr, ts);
			if (toSubmit < 0) {
				return toSubmit;
			}
		}
		return __io_uring_get_cqe(ring, cqePtr, toSubmit, waitNr, sigmask);
	};

	public static int io_uring_submit_and_wait_timeout(io_uring ring, io_uring_cqe cqePtr, int waitNr,
			MemorySegment ts, MemorySegment sigmask) {
		int toSubmit;
		if (!utils.areSegmentsEquals(ts, MemorySegment.NULL)) {
			if ((io_uring.getFeatures(ring.segment) & constants.IORING_FEAT_EXT_ARG) != 0) {
				MemorySegment arg = ring_allocations.getArgSegment(ring.allocations);
				MemorySegment data = ring_allocations.getDataSegment(ring.allocations);
				io_uring_getevents_arg.setSigmask(arg, sigmask.address());
				io_uring_getevents_arg.setSigmaskSz(arg, constants._NSIG / 8);
				io_uring_getevents_arg.setTs(arg, ts.address());

				get_data.setSubmit(data, __io_uring_flush_sq(ring.segment));
				get_data.setWaitNr(data, waitNr);
				get_data.setGetFlags(data, constants.IORING_ENTER_EXT_ARG);
				get_data.setSz(data, (int) arg.byteSize());
				get_data.setHasTs(data, utils.areSegmentsEquals(ts, MemorySegment.NULL) ? 0 : 1);
				get_data.setArg(data, arg);
				return _io_uring_get_cqe(ring, cqePtr, data);
			}
			toSubmit = __io_uring_submit_timeout(ring, waitNr, ts);
			if (toSubmit < 0) {
				return toSubmit;
			}
		} else {
			toSubmit = __io_uring_flush_sq(ring.segment);
		}
		return __io_uring_get_cqe(ring, cqePtr, toSubmit, waitNr, sigmask);
	};

	public static int __io_uring_submit_and_wait(MemorySegment ring, int waitNr, MemorySegment flags) {
		return __io_uring_submit(ring, __io_uring_flush_sq(ring), waitNr, false, flags);
	};

	public static int io_uring_peek_batch_cqe(io_uring ring, MemorySegment cqes, int count) {
		int ready;
		boolean overflowChecked = false;
		int shift = 0;

		if ((io_uring.getFlags(ring.segment) & constants.IORING_SETUP_CQE32) != 0) {
			shift = 1;
		}

		while (true) {
			ready = liburing.io_uring_cq_ready(ring);
			if (ready != 0) {
				MemorySegment cq = io_uring.getCqSegment(ring.segment);
				int head = io_uring_cq.getKhead(cq).get(ValueLayout.JAVA_INT, 0L);
				int mask = io_uring_cq.getRingMask(cq);
				int last;

				count = count > ready ? ready : count;
				last = head + count;
				long offset = io_uring_cqe.layout.byteSize();
				MemorySegment cqesSegment = io_uring_cq.getCqes(cq).reinterpret((last + 1) * offset); // Enough?;
				for (int i = 0; head != last; head++, i++) {
					long index = ((head & mask) << shift) * offset;
					cqes.asSlice(i * offset, offset).copyFrom(cqesSegment.asSlice(index, offset));
				}
				return count;
			}

			if (overflowChecked) {
				return 0;
			}

			if (cq_ring_needs_flush(ring.segment)) {
				io_uring_get_events(ring.segment);
				overflowChecked = true;
				continue;
			}
			return 0;
		}
	};

};
