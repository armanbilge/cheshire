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

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.VarHandle;

/** mirrors liburing's C API */
public final class liburing {

	// constants
	public static final int IORING_SETUP_SUBMIT_ALL = 1 << 7;
	public static final int IORING_SETUP_COOP_TASKRUN = 1 << 8;
	public static final int IORING_SETUP_TASKRUN_FLAG = 1 << 9;
	public static final int IORING_SETUP_SINGLE_ISSUER = 1 << 12;
	public static final int IORING_SETUP_DEFER_TASKRUN = 1 << 13;

	public static final int IORING_OP_NOP = 0;
	public static final int IORING_OP_ACCEPT = 13;
	public static final int IORING_OP_ASYNC_CANCEL = 14;
	public static final int IORING_OP_CONNECT = 16;
	public static final int IORING_OP_CLOSE = 19;
	public static final int IORING_OP_SEND = 26;
	public static final int IORING_OP_RECV = 27;
	public static final int IORING_OP_SHUTDOWN = 34;
	public static final int IORING_OP_SOCKET = 45;

	// structs
	private static final GroupLayout io_uring_cq = MemoryLayout.structLayout(ValueLayout.ADDRESS.withName("khead"),
			ValueLayout.ADDRESS.withName("ktail"), ValueLayout.ADDRESS.withName("kring_mask"),
			ValueLayout.ADDRESS.withName("kring_entries"), ValueLayout.ADDRESS.withName("kflags"),
			ValueLayout.ADDRESS.withName("koverflow"), ValueLayout.ADDRESS.withName("cqes"),
			ValueLayout.JAVA_LONG.withName("ring_sz"), ValueLayout.ADDRESS.withName("ring_ptr"),
			ValueLayout.JAVA_INT.withName("ring_mask"), ValueLayout.JAVA_INT.withName("ring_entries"),
			MemoryLayout.sequenceLayout(2, ValueLayout.JAVA_INT).withName("pad")).withName("io_uring_cq");

	private static final GroupLayout io_uring_cqe = MemoryLayout.structLayout(ValueLayout.JAVA_LONG.withName("user_data"),
			ValueLayout.JAVA_INT.withName("res"), ValueLayout.JAVA_INT.withName("flags"),
			MemoryLayout.sequenceLayout(0, ValueLayout.JAVA_LONG).withName("big_cqe")).withName("io_uring_cqe");

	public static final GroupLayout io_uring_sq = MemoryLayout
			.structLayout(ValueLayout.ADDRESS.withName("khead"), ValueLayout.ADDRESS.withName("ktail"),
					ValueLayout.ADDRESS.withName("kring_mask"), ValueLayout.ADDRESS.withName("kring_entries"),
					ValueLayout.ADDRESS.withName("kflags"), ValueLayout.ADDRESS.withName("kdropped"),
					ValueLayout.ADDRESS.withName("array"), ValueLayout.ADDRESS.withName("sqes"),
					ValueLayout.JAVA_INT.withName("sqe_head"), ValueLayout.JAVA_INT.withName("sqe_tail"),
					ValueLayout.JAVA_LONG.withName("ring_sz"), ValueLayout.ADDRESS.withName("ring_ptr"),
					ValueLayout.JAVA_INT.withName("ring_mask"), ValueLayout.JAVA_INT.withName("ring_entries"),
					MemoryLayout.sequenceLayout(2, ValueLayout.JAVA_INT).withName("pad"))
			.withName("io_uring_sq");

	private static final GroupLayout io_uring_sqe = MemoryLayout
			.structLayout(ValueLayout.JAVA_CHAR.withName("opcode"), ValueLayout.JAVA_CHAR.withName("flags"),
					ValueLayout.JAVA_SHORT.withName("ioprio"), ValueLayout.JAVA_INT.withName("fd"),
					MemoryLayout.unionLayout(ValueLayout.JAVA_LONG.withName("off"), ValueLayout.JAVA_LONG.withName("addr2"),
							MemoryLayout
									.structLayout(ValueLayout.JAVA_INT.withName("cmd_op"), ValueLayout.JAVA_INT.withName("__pad1"))
									.withName("$anon$38:3"))
							.withName("$anon$35:2"),
					MemoryLayout
							.unionLayout(ValueLayout.JAVA_LONG.withName("addr"), ValueLayout.JAVA_LONG.withName("splice_off_in"),
									MemoryLayout
											.structLayout(ValueLayout.JAVA_INT.withName("level"), ValueLayout.JAVA_INT.withName("optname"))
											.withName("$anon$46:3"))
							.withName("$anon$43:2"),
					ValueLayout.JAVA_INT.withName("len"),
					MemoryLayout.unionLayout(ValueLayout.JAVA_INT.withName("rw_flags"),
							ValueLayout.JAVA_INT.withName("fsync_flags"), ValueLayout.JAVA_SHORT.withName("poll_events"),
							ValueLayout.JAVA_INT.withName("poll32_events"), ValueLayout.JAVA_INT.withName("sync_range_flags"),
							ValueLayout.JAVA_INT.withName("msg_flags"), ValueLayout.JAVA_INT.withName("timeout_flags"),
							ValueLayout.JAVA_INT.withName("accept_flags"), ValueLayout.JAVA_INT.withName("cancel_flags"),
							ValueLayout.JAVA_INT.withName("open_flags"), ValueLayout.JAVA_INT.withName("statx_flags"),
							ValueLayout.JAVA_INT.withName("fadvise_advice"), ValueLayout.JAVA_INT.withName("splice_flags"),
							ValueLayout.JAVA_INT.withName("rename_flags"), ValueLayout.JAVA_INT.withName("unlink_flags"),
							ValueLayout.JAVA_INT.withName("hardlink_flags"), ValueLayout.JAVA_INT.withName("xattr_flags"),
							ValueLayout.JAVA_INT.withName("msg_ring_flags"), ValueLayout.JAVA_INT.withName("uring_cmd_flags"),
							ValueLayout.JAVA_INT.withName("waitid_flags"), ValueLayout.JAVA_INT
									.withName("futex_flags"),
							ValueLayout.JAVA_INT.withName("install_fd_flags"), ValueLayout.JAVA_INT.withName("nop_flags"))
							.withName("$anon$52:2"),
					ValueLayout.JAVA_LONG.withName("user_data"),
					MemoryLayout.unionLayout(ValueLayout.JAVA_SHORT.withName("buf_index"),
							ValueLayout.JAVA_SHORT.withName("buf_group")).withName("$anon$79:2"),
					ValueLayout.JAVA_SHORT.withName("personality"),
					MemoryLayout.unionLayout(ValueLayout.JAVA_INT.withName("splice_fd_in"),
							ValueLayout.JAVA_INT.withName("file_index"), ValueLayout.JAVA_INT.withName("optlen"),
							MemoryLayout.structLayout(ValueLayout.JAVA_SHORT.withName("addr_len"),
									MemoryLayout.sequenceLayout(1, ValueLayout.JAVA_SHORT).withName("__pad3")).withName("$anon$91:3"))
							.withName("$anon$87:2"),
					MemoryLayout
							.unionLayout(
									MemoryLayout
											.structLayout(ValueLayout.JAVA_LONG.withName("addr3"),
													MemoryLayout.sequenceLayout(1, ValueLayout.JAVA_LONG).withName("__pad2"))
											.withName("$anon$97:3"),
									ValueLayout.JAVA_LONG.withName("optval"),
									MemoryLayout.sequenceLayout(0, ValueLayout.JAVA_CHAR).withName("cmd"))
							.withName("$anon$96:2"))
			.withName("io_uring_sqe");

	public static final GroupLayout io_uring = MemoryLayout
			.structLayout(io_uring_sq.withName("sq"), io_uring_cq.withName("cq"), ValueLayout.JAVA_INT.withName("flags"),
					ValueLayout.JAVA_INT.withName("ring_fd"), ValueLayout.JAVA_INT.withName("features"),
					ValueLayout.JAVA_INT.withName("enter_ring_fd"), ValueLayout.JAVA_CHAR.withName("int_flags"),
					MemoryLayout.sequenceLayout(3, ValueLayout.JAVA_CHAR).withName("pad"), ValueLayout.JAVA_INT.withName("pad2"))
			.withName("io_uring");

	private static VarHandle opcodeVarHandle = io_uring_sqe.varHandle(PathElement.groupElement("opcode"));
	private static VarHandle flagsVarHandle = io_uring_sqe.varHandle(PathElement.groupElement("flags"));
	private static VarHandle ioprioVarHandle = io_uring_sqe.varHandle(PathElement.groupElement("ioprio"));
	private static VarHandle fdVarHandle = io_uring_sqe.varHandle(PathElement.groupElement("fd"));
	private static VarHandle offVarHandle = io_uring_sqe.varHandle(PathElement.groupElement("off"));
	private static VarHandle addrVarHandle = io_uring_sqe.varHandle(PathElement.groupElement("addr"));
	private static VarHandle lenVarHandle = io_uring_sqe.varHandle(PathElement.groupElement("len"));
	private static VarHandle rwFlagsVarHandle = io_uring_sqe.varHandle(PathElement.groupElement("rw_flags"));
	private static VarHandle pad2VarHandle = io_uring_sqe.varHandle(PathElement.groupElement("__pad2"));
	private static VarHandle acceptFlagsVarHandle = io_uring_sqe.varHandle(PathElement.groupElement("accept_flags"));
	private static VarHandle cancelFlagsVarHandle = io_uring_sqe.varHandle(PathElement.groupElement("cancel_flags"));
	private static VarHandle msgFlagsVarHandle = io_uring_sqe.varHandle(PathElement.groupElement("msg_flags"));
	private static VarHandle sqeUserDataVarHandle = io_uring_sqe.varHandle(PathElement.groupElement("user_data"));
	private static VarHandle cqeUserDataVarHandle = io_uring_cqe.varHandle(PathElement.groupElement("user_data"));

	// TODO
	// public static int io_uring_queue_init(int entries, MemorySegment ring, int
	// flags);
	// public static void io_uring_queue_exit(MemorySegment ring);
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
		opcodeVarHandle.set(sqe, (byte) op);
		flagsVarHandle.set(sqe, (byte) 0);
		ioprioVarHandle.set(sqe, (short) 0);
		fdVarHandle.set(sqe, fd);
		offVarHandle.set(sqe, offset);
		addrVarHandle.set(sqe, addr == null ? 0 : addr.address());
		lenVarHandle.set(sqe, len);
		rwFlagsVarHandle.set(sqe, 0L);
		pad2VarHandle.set(sqe, 0L);
		// sqe.set(ValueLayout.JAVA_BYTE, 0, (byte) op);
		// sqe.set(ValueLayout.JAVA_BYTE, 1, (byte) 0);
		// sqe.set(ValueLayout.JAVA_SHORT, 2, (short) 0);
		// sqe.set(ValueLayout.JAVA_INT, 4, fd);
		// sqe.set(ValueLayout.JAVA_LONG, 8, offset);
		// sqe.set(ValueLayout.JAVA_LONG, 16, addr == null ? 0 : addr.address());
		// sqe.set(ValueLayout.JAVA_INT, 24, len);
		// sqe.set(ValueLayout.JAVA_INT, 28, 0);
		// sqe.set(ValueLayout.JAVA_LONG, 32, 0);
		// sqe.set(ValueLayout.JAVA_LONG, 40, 0);
		// sqe.set(ValueLayout.JAVA_LONG, 48, 0);
	}

	public static void io_uring_prep_nop(MemorySegment sqe) {
		io_uring_prep_rw(IORING_OP_NOP, sqe, -1, null, 0, 0);
	}

	public static void io_uring_prep_accept(MemorySegment sqe, int fd, MemorySegment addr, MemorySegment addrlen,
			int flags) {
		io_uring_prep_rw(IORING_OP_ACCEPT, sqe, fd, addr, 0, addrlen == null ? 0 : addrlen.address());
		acceptFlagsVarHandle.set(sqe, flags);
		// sqe.set(ValueLayout.JAVA_INT, 28, flags);
	}

	public static void io_uring_prep_cancel64(MemorySegment sqe, long userData, int flags) {
		io_uring_prep_rw(IORING_OP_ASYNC_CANCEL, sqe, -1, null, 0, 0);
		addrVarHandle.set(sqe, userData);
		cancelFlagsVarHandle.set(sqe, flags);
		// sqe.set(ValueLayout.JAVA_LONG, 16, userData);
		// sqe.set(ValueLayout.JAVA_INT, 28, flags);
	}

	public static void io_uring_prep_close(MemorySegment sqe, int fd) {
		io_uring_prep_rw(IORING_OP_CLOSE, sqe, fd, null, 0, 0);
	}

	public static void io_uring_prep_connect(MemorySegment sqe, int fd, MemorySegment addr, int addrlen) {
		io_uring_prep_rw(IORING_OP_CONNECT, sqe, fd, addr, 0, addrlen);
	}

	public static void io_uring_prep_recv(MemorySegment sqe, int sockfd, MemorySegment buf, int len, int flags) {
		io_uring_prep_rw(IORING_OP_RECV, sqe, sockfd, buf, len, 0);
		msgFlagsVarHandle.set(sqe, flags);
		// sqe.set(ValueLayout.JAVA_INT, 28, flags);
	}

	public static void io_uring_prep_send(MemorySegment sqe, int sockfd, MemorySegment buf, int len, int flags) {
		io_uring_prep_rw(IORING_OP_SEND, sqe, sockfd, buf, len, 0);
		msgFlagsVarHandle.set(sqe, flags);
		// sqe.set(ValueLayout.JAVA_INT, 28, flags);
	}

	public static void io_uring_sqe_set_data64(MemorySegment sqe, long data) {
		sqeUserDataVarHandle.set(sqe, data);
		// sqe.set(ValueLayout.JAVA_LONG, 32, data);
	}

	public static long io_uring_cqe_get_data64(MemorySegment cqe) {
		long userData = (long) cqeUserDataVarHandle.get(cqe);
		// long userData = cqe.get(ValueLayout.JAVA_LONG, 0);
		return userData;
	}

	public static void io_uring_prep_shutdown(MemorySegment sqe, int fd, int how) {
		io_uring_prep_rw(IORING_OP_SHUTDOWN, sqe, fd, null, how, 0);
	}

	public static void io_uring_prep_socket(MemorySegment sqe, int domain, int type, int protocol, int flags) {
		io_uring_prep_rw(IORING_OP_SOCKET, sqe, domain, null, protocol, type);
		rwFlagsVarHandle.set(sqe, flags);
		// sqe.set(ValueLayout.JAVA_INT, 28, flags);
	}
}
