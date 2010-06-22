class Node():
	def __init__(self, order = 4):
		self.values = []
		self.entryCount = 0
		self.subTree = None
		for x in range(order):
			self.values.append(None)
