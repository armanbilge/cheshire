/*
 * Copyright 2024 Arman Bilge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cheshire;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/** mirrors liburing's C API */
public final class liburing {
	private static void io_uring_initialize_sqe(MemorySegment sqe) {
		io_uring_sqe.setIoprio(sqe, (short) 0);
		io_uring_sqe.setFlags(sqe, (char) 0); // Should be (byte)
		io_uring_sqe.setRwFlags(sqe, 0); // Should be 0L
		io_uring_sqe.setBufIndex(sqe, (short) 0);
		io_uring_sqe.setPersonality(sqe, (short) 0);
		io_uring_sqe.setFileIndex(sqe, 0);
		io_uring_sqe.setAddr3(sqe, 0L);
		io_uring_sqe.setPad2(sqe, 0L);
	};

	public static int __io_uring_peek_cqe(io_uring ring, MemorySegment cqe_ptr, MemorySegment nr_available,
			MemorySegment cqe) {
		int err = 0;
		int available;
		MemorySegment cq = io_uring.getCqSegment(ring.segment);
		int mask = io_uring_cq.getRingMask(cq);
		int shift = 0;

		int flags = io_uring.getFlags(ring.segment);
		if ((flags & constants.IORING_SETUP_CQE32) != 0) {
			shift = 1;
		}

		do {
			// unsigned tail = io_uring_smp_load_acquire(ring->cq.ktail);
			// unsigned head = *ring->cq.khead;
			int tail = (int) io_uring_cq.getAcquireKtail(io_uring.getCqSegment(ring.segment));
			int head = (int) io_uring_cq.getKhead(io_uring.getCqSegment(ring.segment));
			cqe = MemorySegment.NULL;
			available = tail - head;
			if (available == 0) {
				break;
			}
			MemorySegment cq2 = io_uring.getCqSegment(ring.segment);
			MemorySegment cqes = io_uring_cq.getCqesSegment(cq2).reinterpret(io_uring_sqe.layout.byteSize()); // TODO: enough
			cqe.copyFrom(
					cqes.asSlice(((head & mask) << shift) * io_uring_sqe.layout.byteSize(), io_uring_sqe.layout.byteSize()));

			int features = io_uring.getFeatures(ring.segment);
			long user_data = io_uring_cqe.getUserData(cqe);
			if (((features & constants.IORING_FEAT_EXT_ARG) == 0) && (user_data == constants.LIBURING_UDATA_TIMEOUT)) {
				int res = io_uring_cqe.getRes(cqe);
				if (res < 0) {
					err = res;
				}
				io_uring_cq_advance(ring, 1);
				if (err == 0) {
					continue;
				}
				cqe = MemorySegment.NULL;
			}
		} while (true);

		cqe_ptr.copyFrom(cqe);
		if (!utils.areSegmentsEquals(nr_available, MemorySegment.NULL)) {
			nr_available.set(ValueLayout.JAVA_INT, 0L, available);
		}
		return err;
	};

	public static int io_uring_queue_init(int entries, io_uring ring, int flags, io_uring_params p) {
		p.segment.fill((byte) 0);
		io_uring_params.setFlags(p.segment, flags);
		return setup.io_uring_queue_init_params(entries, ring.segment, p.segment, p.sqEntriesSegment, p.cqEntriesSegment);
	};

	public static void io_uring_queue_exit(io_uring ring, io_uring_rsrc_update up) {
		setup.io_uring_queue_exit(ring.segment, up.segment);
	};

	public static MemorySegment io_uring_get_sqe(io_uring ring, io_uring_sqe sqe) {
		MemorySegment sq = io_uring.getSqSegment(ring.segment);

		int head;
		int next = io_uring_sq.getSqeTail(sq) + 1;
		int shift = 0;

		int flags = io_uring.getFlags(ring.segment);
		if ((flags & constants.IORING_SETUP_SQE128) != 0) {
			shift = 1;
		}
		if ((flags & constants.IORING_SETUP_SQPOLL) == 0) {
			// head = *sq->khead;
			head = (int) io_uring_sq.getKhead(sq);
		} else {
			// head = io_uring_smp_load_acquire(khead);
			head = (int) io_uring_sq.getAcquireKhead(sq);
		}

		int ring_entries = io_uring_sq.getRingEntries(sq);
		if ((next - head) <= ring_entries) {
			// TODO: Review logic
			// sqe = &sq->sqes[(sq->sqe_tail & sq->ring_mask) << shift];
			MemorySegment sqes = io_uring_sq.getSqesSegment(sq).reinterpret(io_uring_sqe.layout.byteSize()); // TODO: enough?
			int sqe_tail = io_uring_sq.getSqeTail(sq);
			int ring_mask = io_uring_sq.getRingMask(sq);
			int index = (sqe_tail & ring_mask) << shift;
			sqe.segment.copyFrom(sqes.asSlice(index * io_uring_sqe.layout.byteSize(), io_uring_sqe.layout.byteSize()));
			io_uring_sq.setSqeTail(sq, next);
			io_uring_initialize_sqe(sqe.segment);
			return sqe.segment;
		}

		return MemorySegment.NULL;
	};

	public static int io_uring_submit(io_uring ring) {
		return queue.__io_uring_submit_and_wait(ring.segment, 0, ring.flags);
	};

	public static int io_uring_submit_and_wait_timeout(io_uring ring, MemorySegment cqePtr, int waitNr,
			MemorySegment ts, MemorySegment sigmask, io_uring_getevents_arg arg, get_data data, io_uring_sqe sqe,
			io_uring_cqe cqe) {
		return queue.io_uring_submit_and_wait_timeout(ring, cqePtr, waitNr, ts, sigmask, arg.segment, data.segment,
				sqe, cqe.segment, cqe.nr_avaliable, cqe.flags);
	};

	public static int io_uring_wait_cqe_timeout(io_uring ring, MemorySegment cqe_ptr, MemorySegment ts,
			io_uring_getevents_arg arg, get_data data, io_uring_sqe sqe, io_uring_cqe cqe) {
		return queue.io_uring_wait_cqes(ring, cqe_ptr, 1, ts, MemorySegment.NULL, sqe, arg.segment, data.segment,
				cqe.segment, cqe.nr_avaliable, cqe.flags);
	};

	public static int io_uring_peek_batch_cqe(
			io_uring ring,
			MemorySegment cqes,
			int count) {
		return queue.io_uring_peek_batch_cqe(ring, cqes, count);
	};

	public static void io_uring_cq_advance(io_uring ring, int nr) {
		if (nr != 0) {
			MemorySegment cq = io_uring.getCqSegment(ring.segment);
			io_uring_cq.setReleaseKhead(cq, io_uring_cq.getKhead(cq) + nr);
		}
	};

	public static int io_uring_cq_ready(io_uring ring) {
		MemorySegment cq = io_uring.getCqSegment(ring.segment);
		return (int) (io_uring_cq.getAcquireKtail(cq) - io_uring_cq.getKhead(cq));

	};

	public static void io_uring_prep_rw(int op, io_uring_sqe sqe, int fd, MemorySegment addr, int len, long offset) {
		io_uring_sqe.setOpcode(sqe.segment, (char) op);
		io_uring_sqe.setFlags(sqe.segment, (char) 0);
		io_uring_sqe.setIoprio(sqe.segment, (short) 0);
		io_uring_sqe.setFd(sqe.segment, fd);
		io_uring_sqe.setOff(sqe.segment, offset);
		io_uring_sqe.setAddr(sqe.segment, addr == null ? 0 : addr.address());
		io_uring_sqe.setLen(sqe.segment, len);
		io_uring_sqe.setRwFlags(sqe.segment, 0);
		io_uring_sqe.setPad2(sqe.segment, 0L);
	};

	public static void io_uring_prep_nop(io_uring_sqe sqe) {
		io_uring_prep_rw(constants.IORING_OP_NOP, sqe, -1, MemorySegment.NULL, 0, 0);
	};

	public static void io_uring_prep_accept(io_uring_sqe sqe, int fd, MemorySegment addr, MemorySegment addrlen,
			int flags) {
		io_uring_prep_rw(constants.IORING_OP_ACCEPT, sqe, fd, addr, 0, addrlen == null ? 0 : addrlen.address());
		io_uring_sqe.setAcceptFlags(sqe.segment, flags);
	};

	public static void io_uring_prep_cancel64(io_uring_sqe sqe, long userData, int flags) {
		io_uring_prep_rw(constants.IORING_OP_ASYNC_CANCEL, sqe, -1, MemorySegment.NULL, 0, 0);
		io_uring_sqe.setAddr(sqe.segment, userData);
		io_uring_sqe.setCancelFlags(sqe.segment, flags);
	};

	public static void io_uring_prep_close(io_uring_sqe sqe, int fd) {
		io_uring_prep_rw(constants.IORING_OP_CLOSE, sqe, fd, MemorySegment.NULL, 0, 0);
	};

	public static void io_uring_prep_connect(io_uring_sqe sqe, int fd, MemorySegment addr, int addrlen) {
		io_uring_prep_rw(constants.IORING_OP_CONNECT, sqe, fd, addr, 0, addrlen);
	};

	public static void io_uring_prep_recv(io_uring_sqe sqe, int sockfd, MemorySegment buf, int len, int flags) {
		io_uring_prep_rw(constants.IORING_OP_RECV, sqe, sockfd, buf, len, 0);
		io_uring_sqe.setMsgFlags(sqe.segment, flags);
	};

	public static void io_uring_prep_send(io_uring_sqe sqe, int sockfd, MemorySegment buf, int len, int flags) {
		io_uring_prep_rw(constants.IORING_OP_SEND, sqe, sockfd, buf, len, 0);
		io_uring_sqe.setMsgFlags(sqe.segment, flags);
	};

	public static void io_uring_sqe_set_data64(io_uring_sqe sqe, long data) {
		io_uring_sqe.setUserData(sqe.segment, data);
	};

	public static long io_uring_cqe_get_data64(MemorySegment cqe) {
		return io_uring_cqe.getUserData(cqe);
	};

	public static void io_uring_prep_shutdown(io_uring_sqe sqe, int fd, int how) {
		io_uring_prep_rw(constants.IORING_OP_SHUTDOWN, sqe, fd, MemorySegment.NULL, how, 0);
	};

	public static void io_uring_prep_socket(io_uring_sqe sqe, int domain, int type, int protocol, int flags) {
		io_uring_prep_rw(constants.IORING_OP_SOCKET, sqe, domain, MemorySegment.NULL, protocol, type);
		io_uring_sqe.setRwFlags(sqe.segment, flags);
	};

	public static void io_uring_prep_timeout(io_uring_sqe sqe, MemorySegment ts, int count, int flags) {
		io_uring_prep_rw(constants.IORING_OP_TIMEOUT, sqe, -1, ts, 1, count);
		io_uring_sqe.setTimeoutFlags(sqe.segment, flags);
	};
}
