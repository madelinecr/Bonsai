class Value():
	def __init__(self, newKey = None):
		if(newKey == None):
			self.key = None
		else:
			self.key = newKey
		self.subTree = None
