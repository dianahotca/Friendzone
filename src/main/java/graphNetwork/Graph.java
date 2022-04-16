package graphNetwork;

import java.util.*;

class Graph<T>
{
    //creating an object of the Map class that stores the edges of the graph
    private Map<T, List<T>> map = new HashMap<>();

    //the method adds a new vertex to the graph
    public void addNewVertex(T s)
    {
        map.put(s, new LinkedList<T>());
    }

    //the method adds an edge between source and destination
    public void addNewEdge(T source, T destination, boolean bidirectional)
    {
        if (!map.containsKey(source))
            addNewVertex(source);
        if (!map.containsKey(destination))
            addNewVertex(destination);
        map.get(source).add(destination);
        if (bidirectional == true)
        {
            map.get(destination).add(source);
        }
    }

    //the method counts the number of vertices
    public int countVertices()
    {
        return map.keySet().size();
    }

    //the method counts the number of edges
    public int countEdges(boolean bidirection)
    {
        int count = 0;
        for (T v : map.keySet())
        {
            count = count + map.get(v).size();
        }
        if (bidirection == true)
        {
            count = count / 2;
        }
        return count;
    }

    //checks if a graph has vertex or not
    public boolean containsVertex(T s)
    {
        if (map.containsKey(s))
        {
            return true;
        }return false;
    }

    //checks if a graph has edge or not
    //where s and d are the two parameters that represent source(vertex) and destination (vertex)
    public boolean containsEdge(T s, T d)
    {
        if (map.get(s).contains(d))
        {
            return true;
        }
       return false;
    }

    //returns the adjacencyS list of each vertex
    //here we have overridden the toString() method of the StringBuilder class
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for (T v : map.keySet())
        {
            builder.append(v.toString() + ": ");
            for (T w : map.get(v))
            {
                builder.append(w.toString() + " ");
            }
            builder.append("\n");
        }
        return (builder.toString());
    }

    /**
     * Calculates the number of communities using a DFS algorithm
     *
     * @return integer, number of communities
     */
    public int numberOfCommunities(){
        Map<T,Boolean> visited = new HashMap<T,Boolean>();

        for (T node:map.keySet()){
            visited.put(node,false);
        }
        int count = 0;
        for (T key : visited.keySet()) {
            if (!visited.get(key)) {
                count ++;
                dfs(map, key, visited);
            }
        }
        return count;
    }

    private void dfs(Map<T, List<T>> adj, T key, Map<T,Boolean> visited) {
        visited.put(key, true);
        for (T j:adj.get(key)) {
            if (!visited.get(j)) {
                dfs(adj, j, visited);
            }
        }
    }

    /**
     * Calculates the most sociable community
     *
     * @return List of long that represents the ids of the users in the most sociable community
     */

    public List<T> mostSociableCommunity() {
        Map<T,Boolean> visited = new HashMap<T,Boolean>();
        for (T node:map.keySet()){
            visited.put(node,false);
        }
        List<T> rez = new ArrayList<>();
        int max = 0;
        for (T node : map.keySet())
            if (!visited.get(node)) {
                List<T> intermediate = new ArrayList<>();
                intermediate.add(node);
                dfs2(map,node,visited,intermediate);
                if (max < intermediate.size()) {
                    max = intermediate.size();
                    rez = List.copyOf(intermediate);
                }
            }
        return rez;
    }


    public void dfs2(Map<T, List<T>> adj,T key,Map<T,Boolean> visited, List<T> intermidiate) {
        visited.put(key,true);
        for (T currentNode:adj.get(key)) {
            if (visited.get(currentNode) == false) {
                intermidiate.add(currentNode);
                dfs2(adj,currentNode,visited,intermidiate);
            }
        }
    }
}