import java.util.ArrayList;

public class Node {
	Node leftChild;
	Node rightChild;
	Node parent;
	int index; // Index of node in node array of tree 
	int feature;//feature selected for this node
	String threshold;//the threshold for this feature. It could be any value
	ArrayList<Integer> leftIndices, rightIndices; // if feature value of data equals threshold they go into leftIndices else, they go into rightIndices
}
