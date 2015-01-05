import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DecisonTree {
	public static ArrayList<String[]> data;
	public static ArrayList<String[]> testData;
	public static Map<Integer, String> thresholdMap; // Features with their threshold values
	public static void main(String[] args) throws IOException {
		//Prompt User to provide training and test data file and also the depth of the tree
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		System.out.println("Training File Name: ");
		String trainFile = input.next(); // File should be in the eclipse project folder // zoo-train.csv or resale-train.csv
		System.out.println("Testing File Name: ");
		String testFile = input.next(); // File should be in eclipse project folder // zoo-test.csv or resale-test.csv
		// get the data and place it in a 2D vector
		data = DataImportUtility.readCSVFile(trainFile);
		testData = DataImportUtility.readCSVFile(testFile);
		System.out.println("Tree Depth: ");
		int depth = Integer.valueOf(input.next());	
		int numFeature = data.get(0).length-1; // Last column is considered as the classifier and all columns before it are features.
		Tree tree = new Tree();
		tree.depth = depth;		
		tree.nodeArray = new Node[((int)(Math.pow(2,depth)))-1]; // Maximum Number of nodes is (2^depth) - 1
		Map<Integer, Boolean> featureStatus = new HashMap<Integer, Boolean>();
		Map<Integer, String> tMap = new HashMap<Integer, String>();
		for(int i=0; i<numFeature; i++) {
			featureStatus.put(i, false); // Set feature status as true once it is added to the tree
			tMap.put(i, data.get(0)[i]); // The feature values in the first row of data set are considered the threshold of the feature
		}
		thresholdMap = tMap;
		for(int i=0; i<tree.nodeArray.length; i++) {
			if(i > 0) { // if a feature has already been assigned to the root node
				int parentIndex = 0;
				ArrayList<String[]> dataSubset; // Subset after split in the parent
				if(i % 2 == 0) { // If index is even, go right
					parentIndex = (int)i/2 - 1;
					if(tree.nodeArray[parentIndex] != null) // If there is no node at parentIndex, move on to the next node
						if(tree.nodeArray[parentIndex].threshold != null) // If the data can be split at this node
							dataSubset = getSubset(tree.nodeArray[parentIndex].rightIndices); // Get the data with parent feature value != parent threshold
						else
							dataSubset = null;
					else
						dataSubset = null;
				} else { // If index is odd, go left
					parentIndex = (int)(i - 1)/2;
					if(tree.nodeArray[parentIndex] != null) // If there is no node at parentIndex, move on to the next node 
						if(tree.nodeArray[parentIndex].threshold != null) // If the data can be split at this node
							dataSubset = getSubset(tree.nodeArray[parentIndex].leftIndices); // Get the data with parent feature value == parent threshold
						else
							dataSubset = null;
					else
						dataSubset = null;
				}
				if(dataSubset != null && dataSubset.size() > 0) {
					Map<Integer, Double> gainMap = new HashMap<Integer, Double>();
					// Get information gain of each feature at current node and put them in a map
					for(int f = 0; f < numFeature; f++) {
						if(!featureStatus.get(f)) {
							gainMap.put(f, getInfoGain(f, dataSubset));
							//System.out.println("******"+f+"*******"+getInfoGain(f, dataSubset)+"PARENT"+parentIndex);
						}
					}
					int thisFeature = getBestFeature(gainMap); // get feature with maximum information gain
					if(gainMap.get(thisFeature) != null && gainMap.get(thisFeature) != -1) {
						tree.nodeArray[i] = new Node();
						tree.nodeArray[i].parent = tree.nodeArray[parentIndex];
						tree.nodeArray[i].feature = thisFeature;
						tree.nodeArray[i].index = i;
						featureStatus.put(thisFeature, true);
						if(i%2 != 0) // If index is odd, it goes as left child
							tree.nodeArray[i].parent.leftChild = tree.nodeArray[i];
						else
							tree.nodeArray[i].parent.rightChild = tree.nodeArray[i];					
					
						String threshold = thresholdMap.get(thisFeature);
						tree.nodeArray[i].leftIndices = new ArrayList<Integer>();
						tree.nodeArray[i].rightIndices = new ArrayList<Integer>();
						tree.nodeArray[i].threshold = threshold;
						for(int index = 0; index < dataSubset.size(); index++) {
							if(data.get(index)[thisFeature].equals(threshold)) { // if feature value == threshold then add index to left
								tree.nodeArray[i].leftIndices.add(index);
							} else {
								tree.nodeArray[i].rightIndices.add(index);
							}
						}
					} 
				} 
			} else {				
				Map<Integer, Double> gainMap = new HashMap<Integer, Double>();
				for(int f = 0; f < numFeature; f++) {
					gainMap.put(f, getInfoGain(f, data));
				}
				int thisFeature = getBestFeature(gainMap);
				if(gainMap.get(thisFeature) != null && gainMap.get(thisFeature) != -1) {
					tree.nodeArray[i] = new Node();
					tree.nodeArray[i].parent = null;
					tree.nodeArray[i].feature = thisFeature;
					tree.nodeArray[i].index = i;
					featureStatus.put(thisFeature, true);
					String threshold = thresholdMap.get(thisFeature);
					tree.nodeArray[i].leftIndices = new ArrayList<Integer>();
					tree.nodeArray[i].rightIndices = new ArrayList<Integer>();
					tree.nodeArray[i].threshold = threshold;
					for(int index = 0; index < data.size(); index++) {
						if(data.get(index)[thisFeature].equals(threshold)) {
							tree.nodeArray[i].leftIndices.add(index);
						} else {
							tree.nodeArray[i].rightIndices.add(index);
						}
					}
				}	
			}
			if(tree.nodeArray[i] != null) 	
				System.out.println("INDEX*****"+i+"FEATURE"+tree.nodeArray[i].feature);
			else
				System.out.println("INDEX*****"+i+"FEATURE"+null);
		}
		ArrayList<String> prediction = new ArrayList<String>();
		for(String[] s : testData) {
			int i = 0;
			while (i < tree.nodeArray.length) {
				Node thisNode = tree.nodeArray[i];
				if(thisNode.threshold != null) {
					if(s[thisNode.feature].equals(thisNode.threshold)) { 
						if(thisNode.leftChild != null) {
							i = thisNode.leftChild.index;
						} else {
							prediction.add(getPrediction(thisNode.leftIndices));
							break;
						}
					} else {
						if(thisNode.rightChild != null) {
							i = thisNode.rightChild.index;
						} else {
							prediction.add(getPrediction(thisNode.rightIndices));
							break;
						}
					}
				} else {
					System.out.println("Nothing learnt from training data");
					break;
				}
			}
		}
		System.out.println("Prediction: ");
		for(String s : prediction) {
			System.out.println(s);
		}
	}

	private static int getBestFeature(Map<Integer, Double> gainMap) {
		// Get feature with maximum gain in map
		int bestFeature = -1;
		double maxValue = -1;
		for(int i : gainMap.keySet()) {
			if(gainMap.get(i) != -1) {
				if(gainMap.get(i) > maxValue) {
					maxValue = gainMap.get(i);
					bestFeature = i;
				}
			} else {
				maxValue = -1;
				bestFeature = i;
			}
		}
		return bestFeature;
	}

	public static double getInfoGain(int f, ArrayList<String[]> dataSubset) {
		double entropyBefore = getEntropy(dataSubset); //Entropy before split
		if(entropyBefore != 0){ // Calculate information gain if entropy is not 0
			String threshold = thresholdMap.get(f); // Get threshold value of the feature
			ArrayList<String[]> leftData = new ArrayList<String[]>();
			ArrayList<String[]> rightData = new ArrayList<String[]>();
			for(String[] d : dataSubset) {
				if(d[f].equals(threshold)) {
					leftData.add(d); // If feature value of data == threshold, add it to leftData
				} else {
					rightData.add(d); // If feature value of data != threshold, add it to leftData
				}
			}
			if(leftData.size() > 0 && rightData.size() > 0) {
				double leftProb = (double)leftData.size()/dataSubset.size(); 
				double rightProb = (double)rightData.size()/dataSubset.size();
				double entropyLeft = getEntropy(leftData); //Entropy after split - left
				double entropyRight = getEntropy(rightData); //Entropy after split - right
				double gain = entropyBefore - (leftProb * entropyLeft) - (rightProb * entropyRight);
				return gain;
			} else { // If entropy = 0 on either subsets of data, return 0
				return 0;
			}
		} else { // If entropy = 0 before split, return 1
			return -1;
		}
	}
	
	public static double getEntropy(ArrayList<String[]> dataSubset) {
		double entropy = 0;
		if(dataSubset.size() > 0){
			int y = dataSubset.get(0).length - 1; // column of target or class
			Map<String, Integer> freqMap = new HashMap<String, Integer>();
			// Put class value and it's frequency in current dataset in a map
			for(String[] d : dataSubset) {
				if(freqMap.containsKey(d[y])) 
					freqMap.put(d[y], freqMap.get(d[y]) + 1); 
				else
					freqMap.put(d[y], 1);
			}
			for(String s : freqMap.keySet()) {
				double prob = (double) freqMap.get(s)/dataSubset.size(); // Probability of class value in current dataset
				entropy -= prob * Math.log(prob)/Math.log(2);  
			}
		}
		return entropy;
	}

	public static ArrayList<String[]> getSubset(ArrayList<Integer> indices) {
		// return data subset, given the indices
		ArrayList<String[]> subSet = new ArrayList<String[]>();
		for(Integer i : indices) {
			subSet.add(data.get(i));
		}
		return subSet;
	}
	
	public static String getPrediction(ArrayList<Integer> indices) {
		// return data subset, given the indices
		int y = data.get(indices.get(0)).length - 1;
		Map<String, Integer> freqMap = new HashMap<String, Integer>();
		// Put class value and it's frequency in current dataset in a map
		for(Integer i : indices) {
			if(freqMap.containsKey(data.get(i)[y])) 
				freqMap.put(data.get(i)[y], freqMap.get(data.get(i)[y]) + 1); 
			else
				freqMap.put(data.get(i)[y], 1);
		}
		String prediction = null;
		int maxValue = 0;
		for(String i : freqMap.keySet()) {
			if(freqMap.get(i) > maxValue) {
				maxValue = freqMap.get(i);
				prediction = i;
			}
		}
		return prediction;
	}
}