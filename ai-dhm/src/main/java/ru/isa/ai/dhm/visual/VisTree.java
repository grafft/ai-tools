package ru.isa.ai.dhm.visual;

/**
 * Created by luba on 21.11.2014.
 */
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class VisTree extends JPanel {
    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    private NewTreeCellRenderer renderer;

    public VisTree() {
        super(new GridLayout(1,0));

        rootNode = new DefaultMutableTreeNode("HTM Network");
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(new MyTreeModelListener());
        tree = new JTree(treeModel);
        tree.setToolTipText("Pink - uninitialized object, Green - initialized object, Yellow - selected one");
        tree.setEditable(false);
        renderer = new NewTreeCellRenderer() ; //////////////сделать параметр
        tree.setCellRenderer(renderer);

        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
    }

    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                    (currentSelection.getLastPathComponent());

            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                if (parent.toString() != "HTM Network" && parent.getChildCount() == 0) addObject((DefaultMutableTreeNode)parent, "Picture", true);
                return;
            }
        }

        toolkit.beep();
    }

    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();
        DefaultMutableTreeNode newNode = null, newNodePic = null;

        if (parentPath == null) {
            parentNode = rootNode;

        } else {
            parentNode = (DefaultMutableTreeNode)
                    (parentPath.getLastPathComponent());
            if (parentNode.isLeaf()){ //"Picture"
                parentNode = (DefaultMutableTreeNode)parentNode.getParent();
                if (parentNode != null) treeModel.removeNodeFromParent(parentNode.getFirstLeaf());

            }
            else{ //"Region"
                for (int i = 0; i < parentNode.getChildCount(); i++){
                    if (parentNode.getChildAt(i).isLeaf())
                        treeModel.removeNodeFromParent(parentNode.getFirstLeaf());
                }

            }
        }
        newNodePic = addObject(newNode = addObject(parentNode, child, true), "Picture", true);
        return newNode;
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child) {
        return addObject(parent, child, false);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child,
                                            boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode =
                new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }


        treeModel.insertNodeInto(childNode, parent,
                parent.getChildCount());

        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    class MyTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());
            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode)(node.getChildAt(index));
            System.out.println("The user has finished editing the node.");
            System.out.println("New value: " + node.getUserObject());
        }
        public void treeNodesInserted(TreeModelEvent e) {
        }
        public void treeNodesRemoved(TreeModelEvent e) {
        }
        public void treeStructureChanged(TreeModelEvent e) {
        }
    }
}