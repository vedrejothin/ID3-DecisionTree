
public class Tree {
	Node[] nodeArray;//the index for root node is 0, root's leftChild is 1, root's rightChild is 2, ...
	int depth;//We have at most 2^(depth-1) nodes in this tree
	//e.g. if root's rightChild does not exist, nodeArray[2] will be null
}
