import value
import node
import pdb

class Tree:


	def __init__(self):
		self.order = 4
		self.fields = self.order - 1
		self.root = None
		print("Tree initialized")
		
		
	def insert(self, data):
		upValue = value.Value()
		higher = self.__insert(self.root, data, upValue)
		
		if(higher == True):
			newRoot = node.Node()
			newRoot.values[0] = upValue
			newRoot.subTree = self.root
			newRoot.entryCount = 1
			self.root = newRoot
		return
		
		
	def traverse(self):
		values = []
		self.__traverseInOrder(self.root, values)
		return values
	
	# ----------------------------- PRIVATE METHODS ---------------------------
		
	def __insert(self, tree, data, upValue):
		higher = False
		if(tree == None):
			upValue.key = data
			higher = True
		else:
			entryIndex = self.__searchtree(tree, data)
			subTree = None
			if(entryIndex == -1):
				higher = False
				return higher
			
			if(entryIndex > 0): # if data is NOT less than leftmost entry
				subTree = tree.values[entryIndex - 1].subTree
			else: # else data IS less than leftmost entry
				subTree = tree.subTree
			
			higher = self.__insert(subTree, data, upValue)
			
			if(higher == True):
				if(tree.entryCount == self.fields): # tree full
					# pdb.set_trace()
					self.__splittree(tree, entryIndex, upValue)
					higher = True
				else:
					self.__insertEntry(tree, entryIndex, upValue)
					higher = False
		return higher
		
	
	def __insertEntry(self, tree, entryIndex, newEntry):
		shifter = tree.entryCount
		while(shifter > entryIndex):
			tree.values[shifter] = tree.values[shifter - 1]
			shifter -= 1
		tempValue = value.Value(newEntry.key)
		tempValue.subTree = newEntry.subTree
		tree.values[shifter] = tempValue
		tree.entryCount += 1
		return
	
		
	def __searchtree(self, tree, data):
		walker = 0
		if(data < tree.values[0].key):
			walker = 0
		elif(data > tree.values[0].key):
			walker = tree.entryCount
			
			while(data < tree.values[walker - 1].key):
				if(data == tree.values[walker - 1].key):
					walker = -1
					return walker
				else:
					walker -= 1
		
		else:
			walker = -1
		return walker
		
	
	def __splittree(self, tree, entryIndex, upValue):
		minEntries = (self.order / 2) - 1
		medianIndex = minEntries + 1
		fromIndex = 0
		toIndex = 1
		
		rightNode = node.Node()
		if(entryIndex <= minEntries):
			fromIndex = minEntries + 1
		else:
			fromIndex = minEntries + 2
			
		while(fromIndex <= tree.entryCount):
			rightNode.values[toIndex - 1] = tree.values[fromIndex - 1]
			rightNode.entryCount += 1
			tree.values[fromIndex - 1] = None
			fromIndex += 1
			toIndex += 1
		tree.entryCount -= rightNode.entryCount;
		
		if(entryIndex <= minEntries):
			self.__insertEntry(tree, entryIndex, upValue)
		else:
			newEntryIndex = self.__searchtree(rightNode, upValue.key)
			self.__insertEntry(rightNode, self.__searchtree(rightNode, upValue.key), upValue)
		
		# build entry for parent
		upValue.key = tree.values[medianIndex - 1].key
		rightNode.subTree = tree.values[medianIndex - 1].subTree
		tree.values[medianIndex - 1] = None
		tree.entryCount -= 1
		upValue.subTree = rightNode
		
	def __traverseInOrder(self, tree, values):
		if(tree == None):
			return
		
		self.__traverseInOrder(tree.subTree, values)
		
		for x in range(tree.entryCount):
			print(x)
			values.append(tree.values[x].key)
			self.__traverseInOrder(tree.values[x].subTree, values)
		return
