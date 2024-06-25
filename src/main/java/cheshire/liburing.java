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

/** mirrors liburing's C API */
public final class liburing {
	public static int io_uring_queue_init(int entries, MemorySegment ring, int flags, io_uring_params p) {
		p.segment.fill((byte) 0);
		io_uring_params.flagsVarHandle.set(p, flags);
		return setup.io_uring_queue_init_params(entries, ring, p.segment, p.sqEntriesSegment, p.cqEntriesSegment);
	};

	public static void io_uring_queue_exit(MemorySegment ring, io_uring_rsrc_update up) {
		setup.io_uring_queue_exit(ring, up.segment);
	}

	// public static MemorySegment io_uring_get_sqe(MemorySegment ring);
	// public static int io_uring_submit(MemorySegment ring);
	// public static int io_uring_submit_and_wait_timeout(
	// MemorySegment ring,
	// MemorySegment cqePtr,
	// int waitNr,
	// MemorySegment ts,
	// MemorySegment sigmask
	// );
	// public static int io_uring_wait_cqe_timeout(
	// MemorySegment ring,
	// MemorySegment cqePtr,
	// MemorySegment ts
	// );
	// public static int io_uring_peek_batch_cqe(
	// MemorySegment ring,
	// MemorySegment cqes,
	// int count
	// );
	// public static void io_uring_cq_advance(MemorySegment ring, int nr);

	// Prep methods
	public static void io_uring_prep_rw(int op, MemorySegment sqe, int fd, MemorySegment addr, int len, long offset) {
		io_uring_sqe.opcodeVarHandle.set(sqe, (byte) op);
		io_uring_sqe.flagsVarHandle.set(sqe, (byte) 0);
		io_uring_sqe.ioprioVarHandle.set(sqe, (short) 0);
		io_uring_sqe.fdVarHandle.set(sqe, fd);
		io_uring_sqe.offVarHandle.set(sqe, offset);
		io_uring_sqe.addrVarHandle.set(sqe, addr == null ? 0 : addr.address());
		io_uring_sqe.lenVarHandle.set(sqe, len);
		io_uring_sqe.rwFlagsVarHandle.set(sqe, 0L);
		io_uring_sqe.pad2VarHandle.set(sqe, 0L);
	}

	public static void io_uring_prep_nop(MemorySegment sqe) {
		io_uring_prep_rw(constants.IORING_OP_NOP, sqe, -1, null, 0, 0);
	}

	public static void io_uring_prep_accept(MemorySegment sqe, int fd, MemorySegment addr, MemorySegment addrlen,
			int flags) {
		io_uring_prep_rw(constants.IORING_OP_ACCEPT, sqe, fd, addr, 0, addrlen == null ? 0 : addrlen.address());
		io_uring_sqe.acceptFlagsVarHandle.set(sqe, flags);
	}

	public static void io_uring_prep_cancel64(MemorySegment sqe, long userData, int flags) {
		io_uring_prep_rw(constants.IORING_OP_ASYNC_CANCEL, sqe, -1, null, 0, 0);
		io_uring_sqe.addrVarHandle.set(sqe, userData);
		io_uring_sqe.cancelFlagsVarHandle.set(sqe, flags);
	}

	public static void io_uring_prep_close(MemorySegment sqe, int fd) {
		io_uring_prep_rw(constants.IORING_OP_CLOSE, sqe, fd, null, 0, 0);
	}

	public static void io_uring_prep_connect(MemorySegment sqe, int fd, MemorySegment addr, int addrlen) {
		io_uring_prep_rw(constants.IORING_OP_CONNECT, sqe, fd, addr, 0, addrlen);
	}

	public static void io_uring_prep_recv(MemorySegment sqe, int sockfd, MemorySegment buf, int len, int flags) {
		io_uring_prep_rw(constants.IORING_OP_RECV, sqe, sockfd, buf, len, 0);
		io_uring_sqe.msgFlagsVarHandle.set(sqe, flags);
	}

	public static void io_uring_prep_send(MemorySegment sqe, int sockfd, MemorySegment buf, int len, int flags) {
		io_uring_prep_rw(constants.IORING_OP_SEND, sqe, sockfd, buf, len, 0);
		io_uring_sqe.msgFlagsVarHandle.set(sqe, flags);
	}

	public static void io_uring_sqe_set_data64(MemorySegment sqe, long data) {
		io_uring_sqe.userDataVarHandle.set(sqe, data);
	}

	public static long io_uring_cqe_get_data64(MemorySegment cqe) {
		long userData = (long) io_uring_cqe.userDataVarHandle.get(cqe);
		return userData;
	}

	public static void io_uring_prep_shutdown(MemorySegment sqe, int fd, int how) {
		io_uring_prep_rw(constants.IORING_OP_SHUTDOWN, sqe, fd, null, how, 0);
	}

	public static void io_uring_prep_socket(MemorySegment sqe, int domain, int type, int protocol, int flags) {
		io_uring_prep_rw(constants.IORING_OP_SOCKET, sqe, domain, null, protocol, type);
		io_uring_sqe.rwFlagsVarHandle.set(sqe, flags);
	}
}
