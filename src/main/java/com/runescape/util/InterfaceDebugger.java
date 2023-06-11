package com.runescape.util;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.runescape.cache.graphics.widget.Widget;

public class InterfaceDebugger extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private DefaultMutableTreeNode interfaces;
	
	private JTree tree;
	
	private JTable details;
	
	private List<String> detailsData = new ArrayList<String>(Arrays.asList(new String[] { "Id", "Type", "ParentID", "Seen on", "Invisible", "Tooltip", "EnabledMessage", "DisabledMessage", "EnabledSprite", "DisabledSprite", "MediaID", "Children", "Width", "Height", "X", "Y", "valueIndexArray", "requiredValues", "valueCompareType" }));
	
	public InterfaceDebugger() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Interface Debugger");
		setSize(550, 600);
		setVisible(true);
		
		interfaces = new DefaultMutableTreeNode("Interfaces");
		
		tree = new JTree(interfaces);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				
				if (node == null) {
					return;
				}
				
				if (node.getParent() != null) {
					int interfaceId = Integer.parseInt(node.getUserObject().toString());
					
					loadDetails(Widget.interfaceCache[interfaceId]);
				}
				
			}
		});
		
		JScrollPane treeView = new JScrollPane(tree);
		treeView.setPreferredSize(new Dimension(200, 500));
		
		JPanel left = new JPanel();
		left.add(treeView);
		
		Object rowData[][] = new Object[detailsData.size()][2];
		
		for (int i = 0; i < detailsData.size(); i++) {
			String name = detailsData.get(i);
			rowData[i][0] = name;
			rowData[i][1] = "";
		}

		Object columnNames[] = { "", "" };
		
		details = new JTable(rowData, columnNames);
		details.setTableHeader(null);
		
		JScrollPane tableView = new JScrollPane(details);
		tableView.setPreferredSize(new Dimension(300, 500));

		JPanel right = new JPanel();
		right.add(tableView);
		
		getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));
		getContentPane().add(left);
		getContentPane().add(right);
		
		loadInterfaces();
	}
	
	public void loadDetails(Widget rsi) {
		int column = 0;
		details.getModel().setValueAt(rsi.id, column++, 1);
		details.getModel().setValueAt(getType(rsi.type), column++, 1);
		details.getModel().setValueAt(rsi.parent, column++, 1);
		List<Integer> seenOn = new ArrayList<>();
		for (int i = 0; i < Widget.interfaceCache.length; i++) {
			Widget on = Widget.interfaceCache[i];
			
			if (on != null && on.children != null) {
				for (int j = 0; j < on.children.length; j++) {
					if (on.children[j] == rsi.id) {
						seenOn.add(on.id);
					}
				}
			}
		}
		details.getModel().setValueAt(Arrays.asList(seenOn.toArray()), column++, 1);
		details.getModel().setValueAt(rsi.invisible, column++, 1);
		details.getModel().setValueAt(rsi.getDefaultText() != null ? rsi.getDefaultText() : "", column++, 1);
		details.getModel().setValueAt(rsi.secondaryText != null ? rsi.secondaryText : "", column++, 1);
		details.getModel().setValueAt(rsi.tooltip != null ? rsi.tooltip : "", column++, 1);
		details.getModel().setValueAt(rsi.enabledSprite != null ? rsi.enabledSprite : -1, column++, 1);
		details.getModel().setValueAt(rsi.disabledSprite != null ? rsi.disabledSprite : -1, column++, 1);
		details.getModel().setValueAt(rsi.defaultMedia, column++, 1);
		details.getModel().setValueAt(rsi.children != null ? rsi.children.length : 0, column++, 1);
		details.getModel().setValueAt(rsi.width, column++, 1);
		details.getModel().setValueAt(rsi.height, column++, 1);
		details.getModel().setValueAt(-1, column++, 1);
		details.getModel().setValueAt(-1, column++, 1);
		if (rsi.id != rsi.parent) {
			Widget parent = Widget.interfaceCache[rsi.parent];

			if (parent != null) {
				for (int i = 0; i < parent.children.length; i++) {
					if (parent.children[i] == rsi.id) {
						details.getModel().setValueAt(parent.childX[i], column - 1, 1);
						details.getModel().setValueAt(parent.childY[i], column - 2, 1);
						break;
					}
				}
			}
		}
		details.getModel().setValueAt(Arrays.toString(rsi.valueIndexArray != null && rsi.valueIndexArray.length > 2 ? rsi.valueIndexArray[2] : null), column++, 1);
		details.getModel().setValueAt(Arrays.toString(rsi.requiredValues), column++, 1);
		details.getModel().setValueAt(Arrays.toString(rsi.valueCompareType), column++, 1);
	}
	
	public void loadInterfaces() {
		for (Widget rsi : Widget.interfaceCache) {
			
			if (rsi == null) {
				continue;
			}
			
			DefaultMutableTreeNode interfaceNode = new DefaultMutableTreeNode(rsi.id);
			
			if (rsi.id == rsi.parent) {
				interfaces.add(interfaceNode);
			}
			
			if (rsi.children == null) {
				continue;
			}
			
			for (int i = 0; i < rsi.children.length; i++) {
				Widget child = Widget.interfaceCache[rsi.children[i]];

				if (child != null) {
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child.id);
					
					interfaceNode.add(childNode);
					
					Widget child2 = Widget.interfaceCache[child.id];
					
					if (child2.children != null) {
						for (int j = 0; j < child2.children.length; j++) {
							Widget child3 = Widget.interfaceCache[child2.children[j]];
							
							if (child3 != null) {
								DefaultMutableTreeNode childNode2 = new DefaultMutableTreeNode(child3.id);
								
								childNode.add(childNode2);
								
								if (child3.children != null) {
									for (int k = 0; k < child3.children.length; k++) {
										childNode2.add(new DefaultMutableTreeNode(child3.children[k]));
									}
								}
							}
						}
					}
				}
			}
			
		}
		
		tree.expandPath(tree.getPathForRow(0));
	}
	
	public String getType(int type) {
		switch (type) {
		case Widget.TYPE_CONTAINER:
			return "Container";
		case Widget.TYPE_TEXT:
			return "Text";
		case Widget.TYPE_SPRITE:
		case Widget.TYPE_CONFIG:
			return "Sprite/Config";
		case Widget.TYPE_MODEL:
			return "Model";
		case Widget.TYPE_HOVER:
			return "Tooltip";
		case Widget.TYPE_WINDOW:
			return "Window";
		case Widget.TYPE_DARK_BOX:
			return "Dark box";
		}

		return Integer.toString(type);
	}

}
