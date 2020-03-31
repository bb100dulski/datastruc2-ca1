//https://www.geeksforgeeks.org/union-find-algorithm-set-2-union-by-rank/

package sample;

public class unionFind {

    public static int find(int[] parent, int i)
    {
        if (parent[i] == -1)
            return -1;

        while (parent[i] != i)
            i = parent[i];
        return i;
    }

    public static void union(int[] parent, int x, int y)
    {
        int xSet = find(parent, x);
        int ySet = find(parent, y);
        parent[xSet] = ySet;
    }
}
