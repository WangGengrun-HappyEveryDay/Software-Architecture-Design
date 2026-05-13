public abstract class Node {
    protected String name;

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // 返回节点大小，文件为其内容大小，目录为内部所有节点大小之和
    public abstract int getSize();

    @Override
    public String toString() {
        return String.format("%s (size=%d)", name, getSize());
    }
}
