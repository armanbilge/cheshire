package cheshire;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public final class io_uring_sqe {
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

	public static VarHandle opcodeVarHandle = layout.varHandle(PathElement.groupElement("opcode"));
	public static VarHandle flagsVarHandle = layout.varHandle(PathElement.groupElement("flags"));
	public static VarHandle ioprioVarHandle = layout.varHandle(PathElement.groupElement("ioprio"));
	public static VarHandle fdVarHandle = layout.varHandle(PathElement.groupElement("fd"));
	public static VarHandle offVarHandle = layout.varHandle(PathElement.groupElement("off"));
	public static VarHandle addrVarHandle = layout.varHandle(PathElement.groupElement("addr"));
	public static VarHandle lenVarHandle = layout.varHandle(PathElement.groupElement("len"));
	public static VarHandle rwFlagsVarHandle = layout.varHandle(PathElement.groupElement("rw_flags"));
	public static VarHandle pad2VarHandle = layout.varHandle(PathElement.groupElement("__pad2"));
	public static VarHandle acceptFlagsVarHandle = layout.varHandle(PathElement.groupElement("accept_flags"));
	public static VarHandle cancelFlagsVarHandle = layout.varHandle(PathElement.groupElement("cancel_flags"));
	public static VarHandle msgFlagsVarHandle = layout.varHandle(PathElement.groupElement("msg_flags"));
	public static VarHandle userDataVarHandle = layout.varHandle(PathElement.groupElement("user_data"));
}
