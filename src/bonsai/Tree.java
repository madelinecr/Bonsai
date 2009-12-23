/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details:
 * http://www.gnu.org/licenses/gpl.txt
 */

package info.bpace.bonsai;

import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author Blaine Pace <blainepace at gmail.com>
 */
public class Tree
{

	private TreeNode root;
	private Integer order;
	private Integer fields;

	/**
	 * default: Creates tree of order 4.
	 */
	public Tree()
	{
		order = 4;
		fields = order - 1;
	}

	/**
	 * Inserts node into b-tree
	 * @param data
	 */
	public void insert(Integer data)
	{

		TreeEntry upEntry = new TreeEntry();
		boolean higher = insertNode(root, data, upEntry);

		if (higher == true)
		{
			TreeNode newRoot = new TreeNode();
			newRoot.entries[0] = upEntry;
			newRoot.subTree = root;
			newRoot.entryCount = 1;
			root = newRoot;
		}
	}

	public boolean search(Integer target)
	{
		return searchTree(target, root);
	}

	public String traverse()
	{
		StringBuilder values = new StringBuilder();
		traverseInOrder(root, values); // recursive call
		return values.toString();
	}

	public File graph()
	{
		GraphViz graph = new GraphViz();

		graph.addln(graph.start_graph());
		graph.addln("node[shape=record,height=.1];");
		buildGraph(graph, root);
		graph.addln(graph.end_graph());

		File out = new File("out.svg");
		graph.writeGraphToFile( graph.getGraph( graph.getDotSource() ), out );
		System.out.println( graph.getDotSource() );
		
		try
		{
			FileWriter outText = new FileWriter(new File("outtext.txt"));
			outText.write( graph.getDotSource() );
			outText.close();
		}
		catch(Exception e)
		{
			System.err.println("File error: " + e);
		}

		return out;
	}


// ----------------------------------------------------------------------------
// Private internal functions
// ----------------------------------------------------------------------------
	/**
	 * Private function for inserting node into b-tree
	 * @param tree
	 * @param data
	 * @param upEntry
	 * @return
	 */
	private boolean insertNode(TreeNode tree, Integer data, TreeEntry upEntry)
	{
		boolean higher = false;

		// if there is no root node
		if (tree == null)
		{
			upEntry.key = data;
			higher = true;
		}
		else
		{
			// find which node data fits between
			Integer entryIndex = searchNode(tree, data);
			TreeNode subTree;

			// if the value was found in a node while traversing, return
			if(entryIndex == -1)
			{
				higher = false;
				return higher;
			}

			if (entryIndex > 0) // if data is NOT less than leftmost node
			{
				subTree = tree.entries[entryIndex - 1].subTree;
			}
			else // else data is less than leftmost node
			{
				subTree = tree.subTree;
			}

			// recurse!
			higher = insertNode(subTree, data, upEntry);

			if (higher == true && entryIndex != -1)
			{
				if (tree.entryCount == fields) // node full
				{
					splitNode(tree, entryIndex, upEntry);
					higher = true;
				}
				else
				{
					insertEntry(tree, entryIndex, upEntry);
					higher = false;
				}
			}
		}
		return higher;
	}

	/**
	 * Returns the index of the largest entry smaller than the target.
	 * Used for traversing down the tree.
	 * @param node The node to search through
	 * @param target The value to search for
	 * @return the index of the largest entry smaller than the target.
	 */
	private Integer searchNode(TreeNode node, Integer target)
	{
		Integer walker = 0;
		if (target < node.entries[0].key) // target less than first
		{
			walker = 0;
		}
		else if(target > node.entries[0].key) // target greater than first
		{
			walker = node.entryCount;

			while (target < node.entries[walker - 1].key)
			{
				if(target == node.entries[walker - 1].key)
				{
					walker = -1;
					return walker;
				}
				else
				{
					walker -= 1;
				}
			}
		}
		else
		{
			walker = -1;
		}
		return walker;
	}

	/**
	 * Splits a full node and prepares value that travels back into parent node
	 * @param node
	 * @param entryIndex
	 * @param upEntry
	 */
	private void splitNode(TreeNode node, Integer entryIndex,
						   TreeEntry upEntry)
	{
		Integer minEntries = (order / 2) - 1; // default 1
		Integer medianIndex = minEntries + 1; // default 2
		
		Integer fromIndex = 0;
		Integer toIndex = 1;
		
		TreeNode rightNode = new TreeNode();

		if (entryIndex <= minEntries)
		{
			fromIndex = minEntries + 1; // default 2
		}
		else
		{
			fromIndex = minEntries + 2; // default 3
		}

		while (fromIndex <= node.entryCount)
		{
			rightNode.entries[toIndex - 1] = node.entries[fromIndex - 1];
			rightNode.entryCount += 1;
			node.entries[fromIndex - 1] = null;
			fromIndex += 1;
			toIndex += 1;
		}
		node.entryCount -= rightNode.entryCount;

		if (entryIndex <= minEntries)
		{
			insertEntry(node, entryIndex, upEntry);
		}
		else
		{
			Integer newEntryIndex = searchNode(rightNode, upEntry.key);
			if(newEntryIndex != -1)
				insertEntry(rightNode, searchNode(rightNode, upEntry.key), upEntry);
		}

		// build entry for parent
		upEntry.key = node.entries[medianIndex - 1].key;
		rightNode.subTree = node.entries[medianIndex - 1].subTree;
		node.entries[medianIndex - 1] = null;
		node.entryCount -= 1;
		upEntry.subTree = rightNode;
	}

	/**
	 * Shifts entries inside a node to the right and inserts a new entry.
	 * @param node
	 * @param entryIndex
	 * @param newEntry
	 */
	private void insertEntry(TreeNode node, Integer entryIndex,
							 TreeEntry newEntry)
	{
		Integer shifter = node.entryCount;
		while (shifter > entryIndex)
		{
			node.entries[shifter] = node.entries[shifter - 1];
			shifter -= 1;
		}
		TreeEntry tempEntry = new TreeEntry();
		tempEntry.key = newEntry.key;
		tempEntry.subTree = newEntry.subTree;

		node.entries[shifter] = tempEntry;
		node.entryCount += 1;
		return;
	}
	
	private boolean searchTree(Integer target, TreeNode node)
	{
		// hit bottom of tree
		if (node == null)
		{
			return false;
		}

		// target is less than first entry -- recurse
		if (target < node.entries[0].key)
		{
			searchTree(target, node.subTree);
		} 
		else if (target == node.entries[0].key) // target is first entry
		{
			return true;
		} 
		else // target is greater than first entry
		{
			// check through entries in node
			for (Integer walker = 0; walker < node.entryCount; walker++)
			{
				if (target > node.entries[walker].key)
				{
					searchTree(target, node.subTree);
				}
				else if (target == node.entries[walker].key)
				{
					return true;
				}
			}
			return false;
		}
		return false;
	}


	private void traverseInOrder(TreeNode node, StringBuilder values)
	{
		if(node == null)
			return;

		traverseInOrder(node.subTree, values);

		for(Integer walker = 0; walker < node.entryCount; walker++)
		{
			if(values.length() != 0)
			{
				values.append(", ");
			}
			values.append(node.entries[walker].key);
			traverseInOrder(node.entries[walker].subTree, values);
		}
	}

	private void buildGraph(GraphViz graph, TreeNode node)
	{
		StringBuilder string = new StringBuilder();

		// build node itself
		string.append("node" + node.hashCode() + "[label=\"");
		for(Integer i = 0; i < node.entryCount; i++)
		{
			if(i != 0)
				string.append("|");
			string.append("<f" + i + ">|" + node.entries[i].key);
			
		}
		string.append("|<f" + node.entryCount + ">" + "\"];");
		graph.addln( string.toString() );
		string.setLength(0);

		
		// build leftmost node link
		if(node.subTree != null)
		{
			string.append("node" + node.hashCode() + ":f0");
			string.append("->");
			string.append("node" + node.subTree.hashCode() + ";");
		}
		graph.addln(string.toString());
		string.setLength(0);

		// build the rest of the node links
		for(Integer i = 1; i <= node.entryCount; i++)
		{
			if(node.entries[i - 1].subTree != null)
			{
				string.append("node" + node.hashCode() + ":f" + i);
				string.append("->");
				string.append("node" + node.entries[i - 1].subTree.hashCode() + ";");
				graph.addln( string.toString() );
				string.setLength(0);
			}
		}

		if(node.subTree != null)
			buildGraph(graph, node.subTree);

		for(Integer i = 0; i < node.entryCount; i++)
		{
			if(node.entries[i].subTree != null)
				buildGraph(graph, node.entries[i].subTree);
		}

	}
// ----------------------------------------------------------------------------
// Private internal classes
// ----------------------------------------------------------------------------

	private class TreeEntry
	{
		private Integer key;
		private TreeNode subTree;
	}

	private class TreeNode
	{
		private TreeNode subTree;
		private Integer entryCount;
		private TreeEntry[] entries;

		public TreeNode()
		{
			entryCount = 0;
			entries = new TreeEntry[fields];
		}
	}
}
