package cheshire;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public final class io_uring_sqe {

	MemorySegment segment;

	public io_uring_sqe(Arena session) {
		this.segment = session.allocate(layout);
	}

	public io_uring_sqe(MemorySegment s) {
		this.segment = s;
	}

	public static final GroupLayout layout = MemoryLayout.structLayout(
			ValueLayout.JAVA_CHAR.withName("opcode"),
			ValueLayout.JAVA_CHAR.withName("flags"),
			ValueLayout.JAVA_SHORT.withName("ioprio"),
			MemoryLayout.paddingLayout(2),
			ValueLayout.JAVA_INT.withName("fd"),
			MemoryLayout.paddingLayout(4),
			MemoryLayout.unionLayout(
					ValueLayout.JAVA_LONG.withName("off"),
					ValueLayout.JAVA_LONG.withName("addr2"),
					MemoryLayout.structLayout(
							ValueLayout.JAVA_INT.withName("cmd_op"),
							ValueLayout.JAVA_INT.withName("__pad1"))
							.withName("$anon$38:3"))
					.withName("$anon$35:2"),
			MemoryLayout.unionLayout(
					ValueLayout.JAVA_LONG.withName("addr"),
					ValueLayout.JAVA_LONG.withName("splice_off_in"),
					MemoryLayout.structLayout(
							ValueLayout.JAVA_INT.withName("level"),
							ValueLayout.JAVA_INT.withName("optname"))
							.withName("$anon$46:3"))
					.withName("$anon$43:2"),
			ValueLayout.JAVA_INT.withName("len"),
			MemoryLayout.paddingLayout(4),
			MemoryLayout.unionLayout(
					ValueLayout.JAVA_INT.withName("rw_flags"),
					ValueLayout.JAVA_INT.withName("fsync_flags"),
					ValueLayout.JAVA_SHORT.withName("poll_events"),
					ValueLayout.JAVA_INT.withName("poll32_events"),
					ValueLayout.JAVA_INT.withName("sync_range_flags"),
					ValueLayout.JAVA_INT.withName("msg_flags"),
					ValueLayout.JAVA_INT.withName("timeout_flags"),
					ValueLayout.JAVA_INT.withName("accept_flags"),
					ValueLayout.JAVA_INT.withName("cancel_flags"),
					ValueLayout.JAVA_INT.withName("open_flags"),
					ValueLayout.JAVA_INT.withName("statx_flags"),
					ValueLayout.JAVA_INT.withName("fadvise_advice"),
					ValueLayout.JAVA_INT.withName("splice_flags"),
					ValueLayout.JAVA_INT.withName("rename_flags"),
					ValueLayout.JAVA_INT.withName("unlink_flags"),
					ValueLayout.JAVA_INT.withName("hardlink_flags"),
					ValueLayout.JAVA_INT.withName("xattr_flags"),
					ValueLayout.JAVA_INT.withName("msg_ring_flags"),
					ValueLayout.JAVA_INT.withName("uring_cmd_flags"),
					ValueLayout.JAVA_INT.withName("waitid_flags"),
					ValueLayout.JAVA_INT.withName("futex_flags"),
					ValueLayout.JAVA_INT.withName("install_fd_flags"),
					ValueLayout.JAVA_INT.withName("nop_flags"))
					.withName("$anon$52:2"),
			MemoryLayout.paddingLayout(4),
			ValueLayout.JAVA_LONG.withName("user_data"),
			MemoryLayout.unionLayout(
					ValueLayout.JAVA_SHORT.withName("buf_index"),
					ValueLayout.JAVA_SHORT.withName("buf_group"))
					.withName("$anon$79:2"),
			ValueLayout.JAVA_SHORT.withName("personality"),
			MemoryLayout.unionLayout(
					ValueLayout.JAVA_INT.withName("splice_fd_in"),
					ValueLayout.JAVA_INT.withName("file_index"),
					ValueLayout.JAVA_INT.withName("optlen"),
					MemoryLayout.structLayout(
							ValueLayout.JAVA_SHORT.withName("addr_len"),
							MemoryLayout.sequenceLayout(1, ValueLayout.JAVA_SHORT).withName("__pad3")).withName("$anon$91:3"))
					.withName("$anon$87:2"),
			MemoryLayout.unionLayout(
					MemoryLayout.structLayout(
							ValueLayout.JAVA_LONG.withName("addr3"),
							MemoryLayout.sequenceLayout(1, ValueLayout.JAVA_LONG).withName("__pad2")).withName("$anon$97:3"),
					ValueLayout.JAVA_LONG.withName("optval"),
					MemoryLayout.sequenceLayout(0, ValueLayout.JAVA_CHAR).withName("cmd"))
					.withName("$anon$96:2"))
			.withName("io_uring_sqe");

	private static VarHandle opcodeVarHandle = layout.varHandle(PathElement.groupElement("opcode"));
	private static VarHandle flagsVarHandle = layout.varHandle(PathElement.groupElement("flags"));
	private static VarHandle ioprioVarHandle = layout.varHandle(PathElement.groupElement("ioprio"));
	private static VarHandle fdVarHandle = layout.varHandle(PathElement.groupElement("fd"));
	private static long offOffset = layout.byteOffset(PathElement.groupElement("fd")) + 8;
	private static long addrOffset = layout.byteOffset(PathElement.groupElement("len")) - 8;
	private static VarHandle lenVarHandle = layout.varHandle(PathElement.groupElement("len"));
	private static long anon522Offset = layout.byteOffset(PathElement.groupElement("len")) + 8;
	private static VarHandle userDataVarHandle = layout.varHandle(PathElement.groupElement("user_data"));
	private static long bufIndexOffset = layout.byteOffset(PathElement.groupElement("user_data")) + 8;
	private static VarHandle personalityVarHandle = layout.varHandle(PathElement.groupElement("personality"));
	private static long fileIndexOffset = layout.byteOffset(PathElement.groupElement("personality")) + 2;
	private static long addr3Offset = layout.byteOffset(PathElement.groupElement("personality")) + 6;
	private static long pad2Offset = layout.byteOffset(PathElement.groupElement("personality")) + 14;

	public static char getOpcode(MemorySegment data) {
		return (char) opcodeVarHandle.get(data);
	};

	public static void setOpcode(MemorySegment data, char value) {
		opcodeVarHandle.set(data, value);
	};

	public static char getFlags(MemorySegment data) {
		return (char) flagsVarHandle.get(data);
	};

	public static void setFlags(MemorySegment data, char value) {
		flagsVarHandle.set(data, value);
	};

	public static short getIoprio(MemorySegment data) {
		return (short) ioprioVarHandle.get(data);
	};

	public static void setIoprio(MemorySegment data, short value) {
		ioprioVarHandle.set(data, value);
	};

	public static int getFd(MemorySegment data) {
		return (int) fdVarHandle.get(data);
	};

	public static void setFd(MemorySegment data, int value) {
		fdVarHandle.set(data, value);
	};

	public static long getOff(MemorySegment data) {
		return (long) data.get(ValueLayout.JAVA_LONG, offOffset);
	};

	public static void setOff(MemorySegment data, long value) {
		data.set(ValueLayout.JAVA_LONG, offOffset, value);
	};

	public static long getAddr(MemorySegment data) {
		return (long) data.get(ValueLayout.JAVA_LONG, addrOffset);
	};

	public static void setAddr(MemorySegment data, long value) {
		data.set(ValueLayout.JAVA_LONG, addrOffset, value);
	};

	public static int getLen(MemorySegment data) {
		return (int) lenVarHandle.get(data);
	};

	public static void setLen(MemorySegment data, int value) {
		lenVarHandle.set(data, value);
	};

	public static int getRwFlags(MemorySegment data) {
		return (int) data.get(ValueLayout.JAVA_INT, anon522Offset);
	};

	public static void setRwFlags(MemorySegment data, int value) {
		data.set(ValueLayout.JAVA_INT, anon522Offset, value);
	};

	public static int getMsgFlags(MemorySegment data) {
		return (int) data.get(ValueLayout.JAVA_INT, anon522Offset);
	};

	public static void setMsgFlags(MemorySegment data, int value) {
		data.set(ValueLayout.JAVA_INT, anon522Offset, value);
	};

	public static int getTimeoutFlags(MemorySegment data) {
		return (int) data.get(ValueLayout.JAVA_INT, anon522Offset);
	};

	public static void setTimeoutFlags(MemorySegment data, int value) {
		data.set(ValueLayout.JAVA_INT, anon522Offset, value);
	};

	public static int getAcceptFlags(MemorySegment data) {
		return (int) data.get(ValueLayout.JAVA_INT, anon522Offset);
	};

	public static void setAcceptFlags(MemorySegment data, int value) {
		data.set(ValueLayout.JAVA_INT, anon522Offset, value);
	};

	public static int getCancelFlags(MemorySegment data) {
		return (int) data.get(ValueLayout.JAVA_INT, anon522Offset);
	};

	public static void setCancelFlags(MemorySegment data, int value) {
		data.set(ValueLayout.JAVA_INT, anon522Offset, value);
	};

	public static long getUserData(MemorySegment data) {
		return (long) userDataVarHandle.get(data);
	};

	public static void setUserData(MemorySegment data, long value) {
		userDataVarHandle.set(data, value);
	};

	public static short getBufIndex(MemorySegment data) {
		return (short) data.get(ValueLayout.JAVA_SHORT, bufIndexOffset);
	};

	public static void setBufIndex(MemorySegment data, short value) {
		data.set(ValueLayout.JAVA_SHORT, bufIndexOffset, value);
	};

	public static short getPersonality(MemorySegment data) {
		return (short) personalityVarHandle.get(data);
	};

	public static void setPersonality(MemorySegment data, short value) {
		personalityVarHandle.set(data, value);
	};

	public static int getFileIndex(MemorySegment data) {
		return (int) data.get(ValueLayout.JAVA_INT, fileIndexOffset);
	};

	public static void setFileIndex(MemorySegment data, int value) {
		data.set(ValueLayout.JAVA_INT, fileIndexOffset, value);
	};

	public static long getAddr3(MemorySegment data) {
		return (long) data.get(ValueLayout.JAVA_LONG, addr3Offset);
	};

	public static void setAddr3(MemorySegment data, long value) {
		data.set(ValueLayout.JAVA_LONG, addr3Offset, value);
	};

	public static long getPad2(MemorySegment data) {
		return (long) data.get(ValueLayout.JAVA_LONG, pad2Offset);
	};

	public static void setPad2(MemorySegment data, long value) {
		data.set(ValueLayout.JAVA_LONG, pad2Offset, value);
	};

};
