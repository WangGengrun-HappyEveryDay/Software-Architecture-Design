public abstract class Node {
    protected String name;
    protected DirectoryNode parent;

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public DirectoryNode getParent() {
        return parent;
    }

    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    public String getPath() {
        if (parent == null) {
            return "/";
        }
        String parentPath = parent.getPath();
        return "/".equals(parentPath) ? "/" + name : parentPath + "/" + name;
    }

    public abstract long getSize();

    @Override
    public String toString() {
        return String.format("%s (size=%d)", name, getSize());
    }
}
