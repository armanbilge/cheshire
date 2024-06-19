package cheshire;

public final class constants {
	public static final int IORING_FEAT_SINGLE_MMAP = 1 << 0;
	public static final int IORING_SETUP_CQSIZE = 1 << 3;
	public static final int IORING_SETUP_CLAMP = 1 << 4;
	public static final int IORING_SETUP_SUBMIT_ALL = 1 << 7;
	public static final int IORING_SETUP_COOP_TASKRUN = 1 << 8;
	public static final int IORING_SETUP_TASKRUN_FLAG = 1 << 9;
	public static final int IORING_SETUP_SQE128 = 1 << 10;
	public static final int IORING_SETUP_CQE32 = 1 << 11;
	public static final int IORING_SETUP_SINGLE_ISSUER = 1 << 12;
	public static final int IORING_SETUP_DEFER_TASKRUN = 1 << 13;
	public static final int IORING_SETUP_NO_MMAP = 1 << 14;
	public static final int IORING_SETUP_REGISTERED_FD_ONLY = 1 << 15;
	public static final int IORING_SETUP_NO_SQARRAY = 1 << 16;

	public static final int IORING_OP_NOP = 0;
	public static final int IORING_OP_ACCEPT = 13;
	public static final int IORING_OP_ASYNC_CANCEL = 14;
	public static final int IORING_OP_CONNECT = 16;
	public static final int IORING_OP_CLOSE = 19;
	public static final int IORING_OP_SEND = 26;
	public static final int IORING_OP_RECV = 27;
	public static final int IORING_OP_SHUTDOWN = 34;
	public static final int IORING_OP_SOCKET = 45;

	public static final long IORING_OFF_SQ_RING = 0L;
	public static final long IORING_OFF_CQ_RING = 0x8000000L;
	public static final long IORING_OFF_SQES = 0x10000000L;

	public static final int INT_FLAG_REG_RING = 1;
	public static final int INT_FLAG_REG_REG_RING = 2;
	public static final int INT_FLAG_APP_MEM = 4;

	public static final int PROT_READ = 0x1;
	public static final int PROT_WRITE = 0x2;
	public static final int MAP_SHARED = 0x01;
	public static final int MAP_ANONYMOUS = 0x20;
	public static final int MAP_HUGETLB = 0x0040000;
	public static final int MAP_POPULATE = 0x8000;

	public static final int KERN_MAX_ENTRIES = 32768;
	public static final int KERN_MAX_CQ_ENTRIES = (2 * KERN_MAX_ENTRIES);

	public static final int __NR_io_uring_setup = 535; // or (__NR_Linux + 425) or 425 or

	// TODO: Review
	public static final int ENOMEM = 5;
	public static final int EINVAL = 22;
	public static final long MAP_FAILED = -1L;
}
