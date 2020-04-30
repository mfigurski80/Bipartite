import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class BipartiteGraph {
    // to get the edges incident with a vertex v, just call adjacencyMap.get(v)
    private HashMap<Vertex, ArrayList<Edge>> adjacencyMap;
    private ArrayList<Vertex> leftVertices;
    private ArrayList<Vertex> rightVertices;
    public BipartiteGraph(int nLeft, int nRight, ArrayList<int[]> edges){
        leftVertices = new ArrayList<>();
        rightVertices = new ArrayList<>();
        adjacencyMap = new HashMap<>();
        for (int i = 0; i < nLeft; i++){
            Vertex v = new Vertex(i, true);
            leftVertices.add(v);
            adjacencyMap.put(v, new ArrayList<Edge>());
        }
        for (int i = 0; i < nRight; i++){
            Vertex v = new Vertex(i, false);
            rightVertices.add(v);
            adjacencyMap.put(v, new ArrayList<Edge>());
        }
        for (int[] edge : edges){
            Vertex u = leftVertices.get(edge[0]);
            Vertex v = rightVertices.get(edge[1]);
            Edge e = new Edge(leftVertices.get(edge[0]), rightVertices.get(edge[1]));
            adjacencyMap.get(u).add(e);
            adjacencyMap.get(v).add(e);
        }
    }

    public ArrayList<Vertex> getVertices(){
        ArrayList<Vertex> verts = new ArrayList<>();
        for (Vertex v : leftVertices)
            verts.add(v);
        for (Vertex v : rightVertices)
            verts.add(v);
        return verts;
    }

    public ArrayList<Edge> getEdges(){
        ArrayList<Edge> edgeList = new ArrayList<>();
        for (Vertex v : leftVertices){
            for (Edge e : adjacencyMap.get(v)){
                edgeList.add(e);
            }
        }
        return edgeList;
    }

    public boolean augmentFlow(ArrayList<Edge> augmentingPath){
        if (augmentingPath == null || augmentingPath.size() == 0){
            return false;
        }
        for (int i = 0; i < augmentingPath.size(); i++){
            if ((i%2==0) == augmentingPath.get(i).isInMatching()){
                // just a sanity check to make sure it alternates blue/red
                return false;
            }
        }
        //as long as the order of the edges is correct, augmenting a flow
        //can be done in this way
        for (int i = 0; i < augmentingPath.size(); i++){
            augmentingPath.get(i).setInMatching(i%2==0);
        }
        return true;
    }

    public ArrayList<Edge> findAugmentingPath(){
        HashMap<Vertex, Boolean> R = new HashMap<Vertex, Boolean>(); // mark left nodes without any match
        HashMap<Vertex, Boolean> T = new HashMap<Vertex, Boolean>(); // mark right nodes without any match

        // build R, R_vert, T
        for (Vertex v : rightVertices) T.put(v, true);
        for (Vertex v : leftVertices) {
            boolean seenMatch = false;
            for (Edge e : adjacencyMap.get(v)) {
                if (e.isInMatching()) {
                    seenMatch = true;
                    T.remove(e.getTail());
                    break;
                }
            }
            if (!seenMatch) {
                R.put(v, true);
            }
        }

        System.out.print("R set: "); System.out.println(R.keySet());
        System.out.print("T set: "); System.out.println(T.keySet());


        for (Vertex v : R.keySet()) {
            ArrayList<Edge> path = findAugmentingPathFrom(v, T);
            if (path != null) return path;
            System.out.println();
        }

        return null;
    }

    private ArrayList<Edge> findAugmentingPathFrom(Vertex start, HashMap<Vertex, Boolean> T) {
        System.out.println(start);

        for (Edge e : adjacencyMap.get(start)) {
            System.out.print(e); System.out.println(e.isInMatching());
            if (e.isInMatching()) continue;

            Vertex right = e.getTail();
            System.out.println(right);
            if (T.containsKey(right)) {
                System.out.println("FOUND!!!!");
                ArrayList<Edge> path = new ArrayList<Edge>();
                path.add(e);
                return path;
            } else {
                for (Edge e2 : adjacencyMap.get(right)) {
                    if (e2.isInMatching()) {
                        ArrayList<Edge> path = findAugmentingPathFrom(e2.getHead(), T);
                        path.add(0, e2);
                        path.add(0, e);
                        return path;
                    }
                }
            }

        }

        return null;
    }


}
