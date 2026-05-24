import java.util.Collection;
import java.util.TreeMap;

public class DirectoryNode extends Node {
    private TreeMap<String, Node> children = new TreeMap<String, Node>();

    public DirectoryNode(String name) {
        super(name);
    }

    public void addChild(Node node) {
        if (node == null) throw new IllegalArgumentException("node is null");
        node.setParent(this);
        children.put(node.getName(), node);
    }

    public void putChild(Node node) {
        if (node == null) throw new IllegalArgumentException("node is null");
        Node existing = children.put(node.getName(), node);
        node.setParent(this);
        if (existing != null && existing != node) {
            existing.setParent(null);
        }
    }

    public Node getChild(String name) {
        return children.get(name);
    }

    public Node removeChild(String name) {
        Node removed = children.remove(name);
        if (removed != null) {
            removed.setParent(null);
        }
        return removed;
    }

    @Override
    public long getSize() {
        long sum = 0L;
        for (Node n : children.values()) sum += n.getSize();
        return sum;
    }

    public Collection<Node> getChildren() {
        return children.values();
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }
}
