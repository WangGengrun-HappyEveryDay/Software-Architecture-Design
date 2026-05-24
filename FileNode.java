public class FileNode extends Node {
    private long size; // 模拟字节数

    public FileNode(String name, long size) {
        super(name);
        if (size < 0) throw new IllegalArgumentException("size must be non-negative");
        this.size = size;
    }

    @Override
    public long getSize() {
        return size;
    }
}
