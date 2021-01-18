/**
 * FibonacciHeap 
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{
    private int size;
    private HeapNode min;
    private HeapNode first;
    private int treeCounter;
    private int markedCounter;
    private static int linkCounter;
    private static int cutCounter;


    public FibonacciHeap(){
        this.size=0;
        this.min= null;
        this.first=null;
    }

   /**
    * public boolean isEmpty()
    *
    * precondition: none
    *
    * The method returns true if and only if the heap
    * is empty.
    *
    * The complexity is O(1)
    */
    public boolean isEmpty()
    {
    	return (this.size==0);
    }

   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    *
    * Returns the new node created.
    * The complexity is O(1)
    */
    public HeapNode insert(int key)
    {
        treeCounter++;
        HeapNode newNode = new HeapNode(key);
        if (!isEmpty()) {
        newNode.setNext(first);
        newNode.setPrev(first.prev);
        first.prev.setNext(newNode);
        this.first.setPrev(newNode);
        this.first = newNode;
        if (key< this.min.getKey()) this.min=newNode;
    }
        else{
            newNode.setNext(newNode);
            newNode.setPrev(newNode);
            first= newNode;
            min = newNode;
        }
        size++;
        return newNode;
    }


   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *The complexity is O(log n) amortized - n is the size of the fibonacci heap
    */
    public void deleteMin()
    {
        // the case that min is the only node
        if (this.size==1){
            this.size=0;
            this.first=null;
            this.min= null;
            treeCounter = 0;
            return;
        }
        //the case that min has no children
        if (min.getChild()==null) {
            min.getPrev().setNext(min.getNext());
            min.getNext().setPrev(min.getPrev());
            // min has no children and he's the first node
            if (first.getKey() == min.getKey()) {
                first = first.getNext();
            }
        }
        // the case that min node has children
        else {
            int i = min.getRank();
            HeapNode curr= min.getChild();
            while(i>0){
                i--;
                curr.setParent(null);
                if (curr.isMark()) {
                    curr.setMark(false);
                    markedCounter--;
                }
                curr = curr.getNext();
                }
                min.getPrev().setNext(min.getChild());
                min.getNext().setPrev(min.getChild().getPrev());
                min.getChild().getPrev().setNext(min.getNext());
                min.getChild().setPrev(min.getPrev());
            // the case that min node has children and he's the first node
                if (first.getKey() == min.getKey()) {
                    first = min.getChild();
            }
            }
        this.first= linking(first);
        size--;
     	return;

    }

    /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal.
    * by maintaining the field of min the complexity is O(1)
    */
    public HeapNode findMin()
    {
    	return this.min;
    }

   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    * The complexity is O(1)
    */
    public void meld (FibonacciHeap heap2){
        this.treeCounter = this.treeCounter + heap2.getTreeCounter();

        if (this.min.getKey() > heap2.findMin().getKey()) {
            this.min = heap2.findMin();
        }
        this.size = this.size + heap2.size();

        HeapNode heap2end = heap2.getFirst().getPrev();
        this.first.getPrev().setNext(heap2.getFirst());
        heap2.getFirst().setPrev(this.first.getPrev());
        this.first.setPrev(heap2end);
        heap2end.setNext(this.first);

    	  return;
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   by maintaining the field of size the complexity is O(1)
    */
    public int size()
    {
    	return this.size;
    }

    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap.
    * worst case- complexity is O(n) its the case that the size of the heap is n and each node is a single root.
    */
    public int[] countersRep()
    {
        int[] res = new int[50];
        if (!isEmpty()){
            int k= first.getRank();
            res[k]++;
            HeapNode curr= first.getNext();
            while(curr.getKey()!=first.getKey()){
                k=curr.getRank();
                res[k]++;
                curr= curr.getNext();
            }
        }
        return res;
    }

   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
    * The complexity is O(log n)
    */
    public void delete(HeapNode x)
    {
        this.decreaseKey(x, Integer.MIN_VALUE + x.getKey());
        this.deleteMin();
    	return;
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    * The worst case complexity is O(log n)
//    * amortize complexity is O(1)
    */
   public void decreaseKey(HeapNode x, int delta)
   {
       x.setKey(x.getKey()-delta);
       HeapNode parent= x.getParent();
       if (x.getKey()< min.getKey()) min= x; //updating the min node
       if(parent==null) return; //if x is a root
       if (x.getKey()> parent.getKey()) return;
       cascadingCuts(x,parent);


       while (parent != null && parent.isMark()){
           HeapNode temp=parent.getParent();
           cascadingCuts(parent,parent.getParent());
           parent= temp;
       }

       // mark the parent if its not a root
       if (parent != null && parent.getParent()!=null) {
       parent.setMark(true);
       markedCounter++;
       }

       return;
   }


   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap.
    * By maintaining treeCounter and markedCounter the complexity is O(1)
    */
    public int potential() 
    {
    	return treeCounter + 2 * markedCounter;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    *  By maintaining links counter the complexity is O(1)
    */
    public static int totalLinks()
    {    
    	return linkCounter;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
    *  By maintaining cuts counter the complexity is O(1)
    */
    public static int totalCuts()
    {    
    	return cutCounter;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k*deg(H)). 
    * You are not allowed to change H.
      * the complexity is O(k*deg(H))! the full explanation is in the doc.
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        int[] res = new int[k];
        FibonacciHeap helpHip = new FibonacciHeap();
        if (k == 0) {return res;}
        int deg = H.findMin().getRank();
        //start from the root
        HeapNode currChild = H.findMin().getChild();
        res[0] = H.findMin().getKey();

        for (int i = 1; i < k; i++) {
            HeapNode temp= currChild;
            if (temp!=null) {
                for (int j = 0; j < deg; j++) {
                    helpHip.insert(currChild.getKey());
                    helpHip.getFirst().setFibChild(currChild.getChild());
                    currChild = currChild.getNext();
                    // breaking if the number of child is less than degH
                    if (currChild.getKey() == temp.getKey()) break;
                }
            }
            res[i]= helpHip.findMin().getKey();
            currChild= helpHip.findMin().getFibChild();
            helpHip.deleteMin();
        }
        return res;
    }
    /** return the numbers of tree in the heap
     The complexity is O(1)
     */
    public int getTreeCounter(){
        return this.treeCounter;
    }
    /** return the first node of the heap
     The complexity is O(1)
     */
    public HeapNode getFirst(){
        return this.first;
    }
    /**
     * private HeapNode linking(HeapNode first)
     *
     * linking the given node with the heap in the degree bucket
     *The complexity is O(log n) n is the size of the fibonacci heap
     */
    private HeapNode linking(HeapNode first) {
        int j=0;
        HeapNode[] buckets = new HeapNode[50];
        int k = first.getRank();
        buckets[k] = first;
        HeapNode curr = this.first.getNext();
        first.setPrev(first);
        first.setNext(first);
        while (curr.getKey() != first.getKey()) {
            k = curr.getRank();
            HeapNode union = curr;
            curr = curr.getNext();
            while (buckets[k] != null) {
                 union = this.join(buckets[k], union);
                buckets[k] = null;
                k = union.getRank();
            }
            if (buckets[k] == null) {
                buckets[k] = union;
            }

        }
        // linking all the buckets starting by finding first
        treeCounter = 0;
        //find the first non-empty bucket
        for (int i=0;i<buckets.length;i++){
            if (buckets[i]!=null) {
                treeCounter++;
                first = buckets[i];
                this.min= first;
                j=i+1;
                break;
            }
        }
        curr= first;
        while(j<buckets.length){
            if (buckets[j]!=null){
                treeCounter++;
                // update the heap min
                if (buckets[j].getKey()<this.min.getKey()) this.min= buckets[j];
                curr.setNext(buckets[j]);
                buckets[j].setPrev(curr);
                curr= curr.getNext();
            }
            j++;
        }
        first.setPrev(curr);
        curr.setNext(first);
        return first;
    }
    /**
     * private HeapNode join(HeapNode old, HeapNode curr)
     *
     * return new heap that contain the old and new heaps as binomial tree
     *The complexity is O(1)
     */
    private HeapNode join(HeapNode old, HeapNode curr) {
        HeapNode smaller;
        HeapNode bigger;
        linkCounter++;
        if (old.getKey()>curr.getKey()) {
            bigger=old;
            smaller=curr;
        }
        else {
            bigger = curr;
            smaller = old;
        }
        bigger.setParent(smaller);
        HeapNode currChild= smaller.getChild();
        smaller.setChild(bigger);
        smaller.setNext(smaller);
        smaller.setPrev(smaller);
        if(currChild!=null){
               bigger.setNext(currChild);
            bigger.setPrev(currChild.getPrev());
            currChild.getPrev().setNext(bigger);
            currChild.setPrev(bigger);
        }
        else {
            bigger.setNext(bigger);
            bigger.setPrev(bigger);
        }
        smaller.setRank(smaller.getRank()+1);
        return smaller;
    }
    /**
     * private void cascadingCuts(HeapNode x,HeapNode parent)
     * generating x heapNode as a root in the fibo heap
     *The complexity is O(1)
     */
    private void cascadingCuts(HeapNode x,HeapNode parent) {
        cutCounter++;
        treeCounter++;
        x.setParent(null);
        // if x is the only child
        if(parent.getRank()==1) parent.setChild(null);
        else{
            x.getPrev().setNext(x.getNext());
            x.getNext().setPrev(x.getPrev());
            // if x is the left child
            if (parent.getChild().getKey()==x.getKey()) parent.setChild(x.getNext());
        }
        parent.setRank(parent.getRank()-1);
        // make x as root
        if(x.isMark()){
            x.setMark(false);
            markedCounter--;
        }
        x.setNext(first);
        x.setPrev(first.getPrev());
        first.getPrev().setNext(x);
        first.setPrev(x);
        first=x;
    }
    /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{

	public int key;
    private int rank;
    private boolean mark;
    private HeapNode child;
    private HeapNode next;
    private HeapNode prev;
    private HeapNode parent;
    private HeapNode fibChild; //helper field for the k-min method

  	public HeapNode(int key) {
	    this.key = key;
	    this.rank = 0;
	    this.mark = false;
        this.child = null;
        this.next = null;
        this.prev = null;
        this.parent = null;
        this.fibChild = null;
      }
    public HeapNode(int k, HeapNode prevChild) {
        this.key = key;
        this.rank = 0;
        this.mark = false;
        this.child = null;
        this.next = null;
        this.prev = null;
        this.parent = null;
        this.fibChild = prevChild;
    }
    // the complexity of all the functions below is O(1)
  	public int getKey() { return this.key; }
  	public int getRank() { return this.rank; }
  	public boolean isMark(){ return this.mark;}
  	public HeapNode getChild(){ return this.child;}
  	public HeapNode getNext(){ return this.next;}
  	public HeapNode getPrev(){return this.prev;}
  	public HeapNode getParent() { return parent;}
  	public HeapNode getFibChild() {return this.fibChild;};
  	public void setKey(int k) {this.key = k;}
  	public void setRank(int r) {this.rank = r;}
  	public void setMark(boolean m) { this.mark = m;}
  	public void setChild(HeapNode c) { this.child = c;}
  	public void setNext(HeapNode n) {this.next = n;}
  	public void setPrev(HeapNode p) {this.prev = p;}
  	public void setParent(HeapNode p) {this.parent = p;}
    public void setFibChild(HeapNode c){
  	    this.fibChild= c;
    }
   }
}
