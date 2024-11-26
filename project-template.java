//in progress creating template for the project (movie dataset reading, masking, KNN for K=1)
import java.io.*;
import java.lang.Math;
public class Tests {

	// Use we use 'static' for all methods to keep things simple, so we can call those methods main

	static void Assert (boolean res) // We use this to test our results - don't delete or modify!
	{
	 if(!res)	{
		 System.out.print("Something went wrong.");
	 	 System.exit(0);
	 }
	}

	// Copy your vector operations here:
    static double [] mult(double a, double [] V) { // multiplying scalar and vector
    	// add your code
    	double [] result = new double [V.length];
        for(int i = 0; i < V.length; i++)
        	result[i] = a*V[i];
        return result;
    }
    static double [] add(double a, double [] V) { // adding scalar and vector
    	// add your code
    	double [] result = new double [V.length];
     
        for(int i = 0; i < V.length; i++)
        	result[i] = a + V[i];
        return result;
    	
    }
    static double [] sub(double a, double [] V) {  // subtracting a scalar from vector        	    
    	// add your code
    	double [] result = new double [V.length];
        
        for(int i = 0; i < V.length; i++)
        	result[i] = V[i] - a;
    	    	
        return result;    	    
    	
    }

        
    static double [] add(double [] U, double [] V) { // adding two vectors
    	// add your code
    	Assert(U.length == V.length);
    	double [] result = new double [U.length];
    
        for(int i = 0; i < U.length; i++)
        	result[i] = U[i] + V[i];
    	    	
        return result;    	    
    	
    }
    static double [] sub(double [] U, double [] V) { // subtracting vector from vector 
    	// add your code
    	return add(U, mult(-1, V));
    	
    }
    static double dot(double [] U, double [] V) { // dot product of two vectors 
    	// add your code
    	Assert(U.length == V.length);
    	double result = 0;     
        for(int i = 0; i < U.length; i++)
        	result += U[i]*V[i];
    	    	   	
    	return result;    
    	
    }
static int NumberOfFeatures = 7;
static double[] toFeatureVector(double id, String genre, double runtime, double year, double imdb, double rt, double budget, double boxOffice) {
	
	
   double[] feature = new double[NumberOfFeatures]; 
   feature[0] = id;  // We use the movie id as a numeric attribute.
  
   switch (genre) { // We also use represent each movie genre as an integer number:


	case "Action":  feature[1] = 0; break; 
	case "Fantasy":   feature[1] = 1; break;
	case "Romance": feature[1] = 2; break;
	case "Sci-Fi": feature[1] = 3; break;
	case "Adventure": feature[1] = 4; break;
	case "Horror": feature[1] = 5; break;
	case "Comedy": feature[1] = 6; break;
	case "Thriller": feature[1] = 7; break;                        
	default: Assert(false); 
                   
   } 
   // That is all. We don't use any other attributes for prediction.
   return feature;
}

// We are using the dot product to determine similarity:
static double similarity(double[] u, double[] v) {
  return dot(u, v);  
}
	
// We have implemented KNN classifier for the K=1 case only. You are welcome to modify it to support any K
static int knnClassify(double[][] trainingData, int[] trainingLabels, double[] testFeature) {
    int k_value = (int) Math.sqrt(trainingData.length); //set K as as square root
    double[]similarities = new double[trainingData.length]; // to store the similiarity for each training example
    int[] labels = new int[trainingData.length]; //storing corresponding labels
    int bestMatch = -1;
   double bestSimilarity = - Double.MAX_VALUE;

   for (int i = 0; i < trainingData.length; i++) {
       similarities[i] = similarity(testFeature, trainingData[i]);
       labels[i] = trainingLabels[i];
   }
  for (int i = 0; i < trainingData.length; i++) {
      for (int j = i + 1; j < trainingData.length; j++) {
          if (similarities[i] < similarities[j]) {
              double tempsimularity = similarities[i];
              int templabel = labels[i];
              //sort the data by K's closest neighbours by swapping them around
              similarities[i] = similarities[j];
              similarities[j] = tempsimularity;
              //swap corresponding labels too
              labels[i] = labels[j];
              labels[j] = templabel;

          }
      }
  }
      //use majority voting to determine the class of K's nearest neighbours
      int[] count = new int[100];
      for (int i = 0; i < k_value; i++){
          count[labels[i]]++;
      }
    //find label with the maximum count
      int maxCount = 0;
      for(int i = 0; i < count.length; i++){
        if (count[i] > maxCount){
             maxCount = count[i];
             bestMatch = i;
        }

    }

   return bestMatch;
}


static void loadData(String filePath, double[][] dataFeatures, int[] dataLabels) throws IOException {
   try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
       String line;
       int idx = 0;
       br.readLine(); // skip header line
       while ((line = br.readLine()) != null) {
           String[] values = line.split(",");
           // Assuming csv format: MovieID,Title,Genre,Runtime,Year,Lead Actor,Director,IMDB,RT(%),Budget,Box Office Revenue (in million $),Like it
           double id = Double.parseDouble(values[0]);
           String genre = values[2];
           double runtime = Double.parseDouble(values[10]);               
           double year = Double.parseDouble(values[3]);
           double imdb = Double.parseDouble(values[7]);                
           double rt = Double.parseDouble(values[6]);  
           double budget = Double.parseDouble(values[9]);  
           double boxOffice = Double.parseDouble(values[8]);  
           
           dataFeatures[idx] = toFeatureVector(id, genre, runtime, year, imdb, rt, budget, boxOffice);
           dataLabels[idx] = Integer.parseInt(values[11]); // Assuming the label is the last column and is numeric
           idx++;
       }
   }
}

public static void main(String[] args) {

   double[][] trainingData = new double[100][];
   int[] trainingLabels = new int[100];
   double[][] testingData = new double[100][]; 
   int[] testingLabels = new int[100]; 
   try {
       // You may need to change the path:        	        	
       loadData("C:\\Users\\hsb23125\\OneDrive - University of Strathclyde\\Documents\\CS259\\training-set.csv", trainingData, trainingLabels);
       loadData("C:\\Users\\hsb23125\\OneDrive - University of Strathclyde\\Documents\\CS259\\testing-set.csv", testingData, testingLabels);
   } 
   catch (IOException e) {
       System.out.println("Error reading data files: " + e.getMessage());
       return;
   }

   // Compute accuracy on the testing set
   int correctPredictions = 0;

    for (int i = 0; i < testingData.length; i++){
        int prediction = knnClassify(trainingData, trainingLabels, testingData[i]);
        if (prediction == testingLabels[i]){
            correctPredictions++;
        }
    }


   double accuracy = (double) correctPredictions / testingData.length * 100;
   System.out.printf("A: %.2f%%\n", accuracy);

}


