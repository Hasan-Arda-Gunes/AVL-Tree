import java.util.ArrayList;

public class MemberTree{
    public static class Node{
        String name;
        double gms;
        int height;
        Node right;
        Node left;
        int activity;
        // Node constructor
        Node(String memberName, double gms, Node right, Node left){
            this.name = memberName;
            this.gms = gms;
            this.left = left;
            this.right = right;
            height = 0;
            activity = 1;
        }
        Node(String name, double gms){this(name,gms,null,null);}
    }
    Node root;
    // adds a new node to the tree
    public void insert(String name, double x ){root = insert(name, x, root);}
    // removes a node from the tree
    public void remove(double x){root = remove(x,root);}
    private Node remove(double x, Node t){
        if (t == null)
            return t;
        if (x<t.gms)
            t.left = remove(x,t.left);
        else if (x>t.gms)
            t.right = remove(x,t.right);
        else if( t.left != null && t.right != null ){
            t.gms = findMin(t.right).gms;
            t.name = findMin(t.right).name;
            t.right = remove(t.gms,t.right);
        }
        else
            t = ( t.left != null ) ? t.left : t.right;
        return balance(t);
    }
    // finds the node with the min value that is reachable from the given node
    public Node findMin( Node t ){

        if( t == null )
            return t;

        while( t.left != null )
            t = t.left;
        return t;
    }
    private Node rotateWithLeftChild( Node k2 ){
        Node k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = Math.max( height( k2.left ), height( k2.right ) ) + 1;
        k1.height = Math.max( height( k1.left ), k2.height ) + 1;
        if (k2 == root)
            root = k1.right;
        return k1;
    }
    private Node rotateWithRightChild( Node k1 ){
        Node k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = Math.max( height( k1.left ), height( k1.right ) ) + 1;
        k2.height = Math.max( height( k2.right ), k1.height ) + 1;
        if (k1 == root)
            root = k2.left;
        return k2;
    }
    private Node doubleWithLeftChild( Node k3 ){
        k3.left = rotateWithRightChild( k3.left );
        return rotateWithLeftChild( k3 );
    }
    private Node doubleWithRightChild(Node k1 ){

        k1.right = rotateWithLeftChild( k1.right );
        return rotateWithRightChild( k1 );
    }
    private int height(Node t ){
        return t == null ? -1 : t.height;
    }
    public static final int ALLOWED_IMBALANCE = 1;
    // use the rotates to keep the avl form
    private Node balance(Node t ){
        if( t == null )
            return t;

        if( height( t.left ) - height( t.right ) > ALLOWED_IMBALANCE )
            if( height( t.left.left ) >= height( t.left.right ) )
                t = rotateWithLeftChild( t );
            else
                t = doubleWithLeftChild( t );
        else
        if( height( t.right ) - height( t.left ) > ALLOWED_IMBALANCE )
            if( height( t.right.right ) >= height( t.right.left ) )
                t = rotateWithRightChild( t );
            else
                t = doubleWithRightChild( t );

        t.height = Math.max( height( t.left ), height( t.right ) ) + 1;
        return t;
    }
    private Node insert( String memberName,double x,Node t )
    {
        if( t == null )
            return new Node(memberName,x, null, null );

        if( x < t.gms )
            t.left = insert( memberName,x, t.left );
        else if( x > t.gms )
            t.right = insert(memberName,x, t.right );
        else if (x == t.gms)
            t.name = memberName;

        return balance(t);
    }
    // find the node with its given value
    public  Node find(double gms, Node t){
        if (t.gms == gms)
            return t;
        if (gms<t.gms)
            return find(gms,t.left);
        else
            return find(gms,t.right);

    }
    // finds the lowest common superior of two given node
    public static Node lowestSuperior(Node a, Node b, Node superior){
        if ((a.gms<= superior.gms && superior.gms<=b.gms) || (b.gms<= superior.gms && superior.gms <= a.gms))
            return superior;
        if (a.gms < superior.gms)
            return lowestSuperior(a,b,superior.left);

        return lowestSuperior(a,b,superior.right);

    }
    // start from rank 0 boss and rank increases by 1 every time it goes to a lower node
    public static int findRank(double gms, Node t, int rank){
        if (t.gms == gms)
            return rank;
        if (gms<t.gms)
            return findRank(gms,t.left,rank+1);
        else
            return findRank(gms,t.right,rank+1);
    }
    // finds the nodes with the specified rank with the same principles as the previous one
    public static ArrayList<Node> rankList(Node t, ArrayList<Node> list, int rank){
        if (rank == 0)
            list.add(t);
        if (t.left!= null)
            rankList(t.left,list,rank-1);
        if (t.right!= null)
            rankList(t.right,list,rank-1);
        return list;
    }
    // restore activity of nodes to 1
    public static void restoreActivity(Node t){
        t.activity = 1;
        if (t.right != null)
            restoreActivity(t.right);
        if (t.left != null)
            restoreActivity(t.left);
    }
    // used with while loop
    public static int intelDivide(Node t){
        if (t == null)
            return 0;
        // if left and right are null it means leaf and we count the leaves
        if (t.left == null && t.right == null){
            t.activity = -1;
            return 1;
        }
        if (t.left != null && t.right != null){
            // if one of them is counted and the other is not we continue with the other one
            if (t.left.activity == -1 && t.right.activity == 1)
                return intelDivide(t.right);
            if (t.left.activity == 1 && t.right.activity == -1)
                return intelDivide(t.left);
            // if either of them is counted we mark these nodes with 0

            if (t.left.activity == -1 ||t.right.activity == -1){
                t.activity = 0;
                return 0;
            }
            // if both of the nodes are marked with 0 we collect these nodes
            if (t.left.activity == 0 && t.right.activity == 0){
                t.activity = -1;
                return 1;
            }
        }
        if (t.left != null && t.right == null){
            // if the left node is counted we mark this node with 0
            if (t.left.activity == -1){
                t.activity = 0;
                return 0;
            }

            else if (t.left.activity == 0){
                //if the left node is marked with 0 we collect the left node
                t.activity = -1;
                return 1;
            }
        }
        if (t.right != null && t.left == null){
            // if the right node is counted we mark this node with 0
            if (t.right.activity == -1){
                t.activity = 0;
                return 0;
            }

            else if (t.right.activity == 0){
                //if the right node is marked with 0 we collect the right node
                t.activity = -1;
                return 1;
            }
        }
        return intelDivide(t.left) + intelDivide(t.right);


    }




    }