public class FileNode extends Node {
    private int size; // 模拟字节数

    public FileNode(String name, int size) {
        super(name);
        if (size < 0) throw new IllegalArgumentException("size must be non-negative");
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }
}
