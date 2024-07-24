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
		io_uring_sqe.setFlags(sqe, (char) 0);
		io_uring_sqe.setRwFlags(sqe, 0);
		io_uring_sqe.setBufIndex(sqe, (short) 0);
		io_uring_sqe.setPersonality(sqe, (short) 0);
		io_uring_sqe.setFileIndex(sqe, 0);
		io_uring_sqe.setAddr3(sqe, 0L);
		io_uring_sqe.setPad2(sqe, 0L);
	};

	public static int __io_uring_peek_cqe(io_uring ring, MemorySegment cqePtr, MemorySegment nrAvailable) {
		int err = 0;
		int available;
		MemorySegment cq = io_uring.getCqSegment(ring.segment);
		int mask = io_uring_cq.getRingMask(cq);
		int shift = 0;

		int flags = io_uring.getFlags(ring.segment);
		if ((flags & constants.IORING_SETUP_CQE32) != 0) {
			shift = 1;
		}
		MemorySegment cqe = ring_allocations.getCqeSegment(ring.allocations);
		do {
			int tail = utils.getIntFromSegment(io_uring_cq.getAcquireKtail(cq));
			int head = utils.getIntFromSegment(io_uring_cq.getKhead(cq));
			cqe = MemorySegment.NULL;
			available = tail - head;
			if (available == 0) {
				break;
			}

			long offset = io_uring_cqe.layout.byteSize();
			long index = ((head & mask) << shift) * offset;
			MemorySegment cqes = io_uring_cq.getCqes(cq);
			cqe.copyFrom(cqes.asSlice(index, offset));

			int features = io_uring.getFeatures(ring.segment);
			long userData = io_uring_cqe.getUserData(cqe);
			if (((features & constants.IORING_FEAT_EXT_ARG) == 0) && (userData == constants.LIBURING_UDATA_TIMEOUT)) {
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
			break;
		} while (true);

		cqePtr.reinterpret(ValueLayout.ADDRESS.byteSize()).set(ValueLayout.ADDRESS, 0L, cqe);
		if (!utils.areSegmentsEquals(nrAvailable, MemorySegment.NULL)) {
			nrAvailable.set(ValueLayout.JAVA_INT, 0L, available);
		}
		return err;
	};

	public static int io_uring_queue_init(int entries, io_uring ring, int flags) {
		MemorySegment params = ring_allocations.getParamsSegment(ring.allocations);
		params.fill((byte) 0);
		io_uring_params.setFlags(params, flags);
		return setup.io_uring_queue_init_params(entries, ring.segment, params);
	};

	public static void io_uring_queue_exit(io_uring ring) {
		MemorySegment up = ring_allocations.getUpSegment(ring.allocations);
		setup.io_uring_queue_exit(ring.segment, up);
	};

	public static MemorySegment io_uring_get_sqe(io_uring ring) {
		MemorySegment sq = io_uring.getSqSegment(ring.segment);

		int head;
		int next = io_uring_sq.getSqeTail(sq) + 1;
		int shift = 0;

		int flags = io_uring.getFlags(ring.segment);
		if ((flags & constants.IORING_SETUP_SQE128) != 0) {
			shift = 1;
		}
		if ((flags & constants.IORING_SETUP_SQPOLL) == 0) {
			head = utils.getIntFromSegment(io_uring_sq.getKhead(sq));
		} else {
			head = utils.getIntFromSegment(io_uring_sq.getAcquireKhead(sq));
		}

		if ((next - head) <= io_uring_sq.getRingEntries(sq)) {
			int sqeTail = io_uring_sq.getSqeTail(sq);
			int ringMask = io_uring_sq.getRingMask(sq);

			long offset = io_uring_sqe.layout.byteSize();
			long index = ((sqeTail & ringMask) << shift) * offset;

			MemorySegment sqes = io_uring_sq.getSqes(sq);
			MemorySegment sqe = ring_allocations.getSqeSegment(ring.allocations);

			sqe.copyFrom(sqes.asSlice(index, offset));
			io_uring_sq.setSqeTail(sq, next);
			io_uring_initialize_sqe(sqe);
			return sqe;
		}

		return MemorySegment.NULL;
	};

	public static int io_uring_submit(io_uring ring) {
		MemorySegment flags = ring_allocations.getFlagsSegment(ring.allocations);
		return queue.__io_uring_submit_and_wait(ring.segment, 0, flags);
	};

	public static int io_uring_submit_and_wait_timeout(io_uring ring, MemorySegment cqePtr, int waitNr,
			MemorySegment ts, MemorySegment sigmask) {
		return queue.io_uring_submit_and_wait_timeout(ring, cqePtr, waitNr, ts, sigmask);
	};

	public static int io_uring_wait_cqe_timeout(io_uring ring, MemorySegment cqePtr, MemorySegment ts) {
		return queue.io_uring_wait_cqes(ring, cqePtr, 1, ts, MemorySegment.NULL);
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
			MemorySegment khead = io_uring_cq.getKhead(cq);
			io_uring_cq.setReleaseKhead(cq, MemorySegment.ofAddress(khead.address() + nr).reinterpret(khead.byteSize()));
		}
	};

	public static int io_uring_cq_ready(io_uring ring) {
		MemorySegment cq = io_uring.getCqSegment(ring.segment);
		return (int) (io_uring_cq.getAcquireKtail(cq).address() - io_uring_cq.getKhead(cq).address());
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

	public static long io_uring_cqe_get_data64(io_uring_cqe cqe) {
		return io_uring_cqe.getUserData(cqe.segment);
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

};
