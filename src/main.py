import btree.tree as btree

mytree = btree.Tree()
while(True):
	newValue = input("Please enter a value: ")
	if(newValue == 0):
		break
	if(newValue != None):
		mytree.insert(newValue)
	
print(mytree.traverse())
