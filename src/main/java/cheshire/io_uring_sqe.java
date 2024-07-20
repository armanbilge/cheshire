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

	public static final GroupLayout layout = MemoryLayout
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

	private static VarHandle opcodeVarHandle = layout.varHandle(PathElement.groupElement("opcode"));
	private static VarHandle flagsVarHandle = layout.varHandle(PathElement.groupElement("flags"));
	private static VarHandle ioprioVarHandle = layout.varHandle(PathElement.groupElement("ioprio"));
	private static VarHandle fdVarHandle = layout.varHandle(PathElement.groupElement("fd"));
	private static VarHandle offVarHandle = layout.varHandle(PathElement.groupElement("off"));
	private static VarHandle addrVarHandle = layout.varHandle(PathElement.groupElement("addr"));
	private static VarHandle lenVarHandle = layout.varHandle(PathElement.groupElement("len"));
	private static VarHandle rwFlagsVarHandle = layout.varHandle(PathElement.groupElement("rw_flags"));
	private static VarHandle msgFlagsVarHandle = layout.varHandle(PathElement.groupElement("msg_flags"));
	private static VarHandle timeoutFlagsVarHandle = layout.varHandle(PathElement.groupElement("timeout_flags"));
	private static VarHandle acceptFlagsVarHandle = layout.varHandle(PathElement.groupElement("accept_flags"));
	private static VarHandle cancelFlagsVarHandle = layout.varHandle(PathElement.groupElement("cancel_flags"));
	private static VarHandle userDataVarHandle = layout.varHandle(PathElement.groupElement("user_data"));
	private static VarHandle bufIndexVarHandle = layout.varHandle(PathElement.groupElement("buf_index"));
	private static VarHandle personalityVarHandle = layout.varHandle(PathElement.groupElement("personality"));
	private static VarHandle fileIndexVarHandle = layout.varHandle(PathElement.groupElement("file_index"));
	private static VarHandle addr3VarHandle = layout.varHandle(PathElement.groupElement("addr3"));
	private static VarHandle pad2VarHandle = layout.varHandle(PathElement.groupElement("__pad2"));

	public static char getOpcode(MemorySegment data) {
		return (char) opcodeVarHandle.get(data);
	}

	public static char getFlags(MemorySegment data) {
		return (char) flagsVarHandle.get(data);
	}

	public static short getIoprio(MemorySegment data) {
		return (short) ioprioVarHandle.get(data);
	}

	public static int getFd(MemorySegment data) {
		return (int) fdVarHandle.get(data);
	}

	public static long getOff(MemorySegment data) {
		return (long) offVarHandle.get(data);
	}

	public static long getAddr(MemorySegment data) {
		return (long) addrVarHandle.get(data);
	}

	public static int getLen(MemorySegment data) {
		return (int) lenVarHandle.get(data);
	}

	public static int getRwFlags(MemorySegment data) {
		return (int) rwFlagsVarHandle.get(data);
	}

	public static int getTimeoutFlags(MemorySegment data) {
		return (int) timeoutFlagsVarHandle.get(data);
	}

	public static int getMsgFlags(MemorySegment data) {
		return (int) msgFlagsVarHandle.get(data);
	}

	public static int getAcceptFlags(MemorySegment data) {
		return (int) acceptFlagsVarHandle.get(data);
	}

	public static int getCancelFlags(MemorySegment data) {
		return (int) cancelFlagsVarHandle.get(data);
	}

	public static long getUserData(MemorySegment data) {
		return (long) userDataVarHandle.get(data);
	}

	public static short getBufIndex(MemorySegment data) {
		return (short) bufIndexVarHandle.get(data);
	}

	public static short getPersonality(MemorySegment data) {
		return (short) personalityVarHandle.get(data);
	}

	public static int getFileIndex(MemorySegment data) {
		return (int) fileIndexVarHandle.get(data);
	}

	public static long getAddr3(MemorySegment data) {
		return (long) addr3VarHandle.get(data);
	}

	public static long getPad2(MemorySegment data) {
		return (long) pad2VarHandle.get(data);
	}

	public static void setOpcode(MemorySegment data, char value) {
		opcodeVarHandle.set(data, value);
	}

	public static void setFlags(MemorySegment data, char value) {
		flagsVarHandle.set(data, value);
	}

	public static void setIoprio(MemorySegment data, short value) {
		ioprioVarHandle.set(data, value);
	}

	public static void setFd(MemorySegment data, int value) {
		fdVarHandle.set(data, value);
	}

	public static void setOff(MemorySegment data, long value) {
		offVarHandle.set(data, value);
	}

	public static void setAddr(MemorySegment data, long value) {
		addrVarHandle.set(data, value);
	}

	public static void setLen(MemorySegment data, int value) {
		lenVarHandle.set(data, value);
	}

	public static void setRwFlags(MemorySegment data, int value) {
		rwFlagsVarHandle.set(data, value);
	}

	public static void setTimeoutFlags(MemorySegment data, int value) {
		timeoutFlagsVarHandle.set(data, value);
	}

	public static void setMsgFlags(MemorySegment data, int value) {
		msgFlagsVarHandle.set(data, value);
	}

	public static void setAcceptFlags(MemorySegment data, int value) {
		acceptFlagsVarHandle.set(data, value);
	}

	public static void setCancelFlags(MemorySegment data, int value) {
		cancelFlagsVarHandle.set(data, value);
	}

	public static void setUserData(MemorySegment data, long value) {
		userDataVarHandle.set(data, value);
	}

	public static void setBufIndex(MemorySegment data, short value) {
		bufIndexVarHandle.set(data, value);
	}

	public static void setPersonality(MemorySegment data, short value) {
		personalityVarHandle.set(data, value);
	}

	public static void setFileIndex(MemorySegment data, int value) {
		fileIndexVarHandle.set(data, value);
	}

	public static void setAddr3(MemorySegment data, long value) {
		addr3VarHandle.set(data, value);
	}

	public static void setPad2(MemorySegment data, long value) {
		pad2VarHandle.set(data, value);
	}
}
